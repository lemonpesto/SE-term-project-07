package service;

import model.*;

import java.util.ArrayList;
import java.util.List;


public class MoveActionService {
    private final RuleEngine ruleEngine;

    public MoveActionService(RuleEngine ruleEngine) {
        this.ruleEngine = ruleEngine;
    }

    /**
     * 단일 Piece 이동 메서드
     */
    public void movePiece(Piece piece, ThrowResult throwResult, Game game) {
        // piece가 그룹으로 묶여 있는지 확인
        PieceGroup group = piece.getGroup();

        if (group != null && group.size() > 1) {
            // 이미 그룹 상태면 그룹 전체를 이동
            movePiece(group, throwResult, game);
        } else {
            // 그룹에 속해있지 않다면
            PieceGroup singleGroup = new PieceGroup(piece.getOwner()); // 크기 1 그룹을 임시로 만들어 처리
            singleGroup.grouping(piece);
            movePiece(singleGroup, throwResult, game);
        }
    }

    // PieceGroup 전체를 이동시키는 오버로드 메서드: 그룹 이동시킨 후 룰 적용
    public void movePiece(PieceGroup group, ThrowResult throwResult, Game game) {
        Cell start = group.getCurrentCell(); // 현재 그룹이 올라가 있는 Cell
        int steps = throwResult.getSteps();     // 이동할 칸 수

        // 전진/후진에 따라 target Cell 결정
        Cell target;
        if (throwResult == ThrowResult.BACK_DO) { // 뒤로 한 칸 이동한 타겟 셀 계산
            target = group.backToPrevious();
        } else {
            target = moveForward(group, start, steps); // 앞으로 steps 만큼 이동한 타겟 셀 계산
        }

        // 룰 적용
        applyRules(group, target, game);

        // 5) 만약 해당 그룹 소유 플레이어의 모든 말이 Finish 상태라면 순위에 추가
//        if (group.getOwner().hasAllPiecesFinished()) {
//            game.addFinishedPlayer(group.getOwner());
//        }
    }

    /**
     * steps만큼 순방향 이동 후 도착 cell 반환
     */
    private Cell moveForward(PieceGroup group, Cell from, int steps) {
        Cell current = from;
        // 출발점에 있으면서 이미 ON_BOARD 상태인 말이 윷을 던졌을 때 --> 탈출 처리
        if(from.isStartCell() && group.getPieces().get(0).getState() == PieceState.ON_BOARD){
            // 남은 이동 칸을 1 이상이라 가정하여 탈출 처리
            updatePiecesState(group, current, 1);
            return current;
        }


        for (int i = 0; i < steps; i++) {
            List<Cell> nextList = current.getNextCells(); // 현재 셀의 다음 셀 목록

            Cell next;
            if (i == 0 && nextList.size() >= 2) {
                // 첫 시작 Cell이 분기점(즉, 다음 셀이 2개 이상)이면
                next = nextList.get(1); // 대각길(지름길/인덱스 1) 경로로 들어가고
            } else {
                next = nextList.get(0); // 그 외는 항상 인덱스 0 따라감
            }

            // Piece들 상태 갱신 & 탈출 체크
            int remainingSteps = steps - (i + 1);
            boolean finished = updatePiecesState(group, current, remainingSteps);

            // 한 칸 이동
            group.moveGroupTo(next);

            current = next;

            // 탈출한 말은 이동 더 이상 이동할 필요 없으니 종료
            if (finished) {
                break;
            }
        }
        return current;
    }

    /**
     * 이동 후 적용할 룰들을 분리된 메서드로 구현합니다.
     */
    private void applyRules(PieceGroup movingGroup, Cell cell, Game game) {
        // 말 업기
        if (ruleEngine.applyGrouping(cell)) {
            PieceGroup group = new PieceGroup(movingGroup.getOwner());
            for (Piece p : cell.getOccupants()) {
                group.grouping(p);
            }
        }
        // 상대 말 잡기
        if (ruleEngine.applyCapture(cell)) {
            // capture 처리 로직 호출 (추가 구현 필요)
            List<Piece> occupants = new ArrayList<>(cell.getOccupants());
            Cell startCell = game.getBoard().getStartCell();

            for (Piece occupant : occupants) {
                Player owner = occupant.getOwner();
                if (!owner.equals(movingGroup.getOwner())) {
                    // 1) 속해 있던 그룹에서 제거
                    PieceGroup ownerGroup = occupant.getGroup();
                    if (ownerGroup != null) {
                        ownerGroup.remove(occupant);
                    }
                    // 상대말: 상태 변경 후 출발 셀로 이동
                    occupant.setState(PieceState.NOT_STARTED);
                    occupant.moveTo(startCell);
                }
            }
        }
    }

    private boolean updatePiecesState(PieceGroup group, Cell currCell, int remainingSteps) {
        // 이동할 칸이 남아 있고 && 지금 위치가 출발점이라면 탈출
        if (remainingSteps > 0 && currCell.isStartCell() && group.getPath().size() > 1) {
            // FINISHED 상태로 변경
            group.setPiecesState(PieceState.FINISHED);
            group.breakUp(); // 그룹 해체

            // START 셀 위의 FINISHED 피스를 일괄 제거
            List<Piece> toRemove = new ArrayList<>();
            for (Piece occupant : currCell.getOccupants()) {
                if (occupant.getState() == PieceState.FINISHED) {
                    toRemove.add(occupant);
                }
            }
            for (Piece p : toRemove) {
                currCell.removePiece(p);
            }
            return true;
        }
        // 아직 탈출되지 않았다면 ON_BOARD 상태로 설정
        group.setPiecesState(PieceState.ON_BOARD);
        return false;
    }
}
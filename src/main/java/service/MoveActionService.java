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
            System.out.println("전진 이후 경로: ");
            for(Cell c : group.getPath()){
                System.out.print(c.getId() + " ");
            }
            System.out.println();
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

        for (int i = 0; i < steps; i++) {
            List<Cell> nextList = current.getNextCells(); // 현재 셀의 다음 셀 목록

            // 분기점 처리: 첫 이동(i==0)이고 nextList가 2개 이상일 때 지름길(인덱스1) 선택
            Cell next;
            if (i == 0 && nextList.size() >= 2) {
                next = nextList.get(1);
            } else {
                next = nextList.get(0);
            }

            // 이동 후 “현재 셀이 출발점(START)”인지 확인 (남은 칸 수와 상관없이)
            if (current.isStartCell() && group.getPath().size() > 1) {
                // FINISHED 처리: 그룹 내 모든 말 상태를 FINISHED로 변경
                group.setPiecesState(PieceState.FINISHED);
                group.breakUp();

                // 출발점에 올라간 FINISHED 상태의 말들을 제거
                List<Piece> toRemove = new ArrayList<>();
                for (Piece occupant : current.getOccupants()) {
                    if (occupant.getState() == PieceState.FINISHED) {
                        toRemove.add(occupant);
                    }
                }
                for (Piece p : toRemove) {
                    current.removePiece(p);
                }
                // 탈출했으므로 더 이상 이동할 필요 없음
                break;
            }
            // 한 칸 이동
            group.moveGroupTo(next);
            current = next;

            // 탈출하지 않았으면 ON_BOARD 상태 유지
            group.setPiecesState(PieceState.ON_BOARD);
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
}
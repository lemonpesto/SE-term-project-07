package service;

import java.util.Random;
import model.ThrowResult;

/**
 * 윷 던지기 서비스.
 * -- 랜덤 윷 던지기: 네 개의 윷을 랜덤으로 던져 ThrowResult 반환
 */
public class YutThrowService {
    private static final Random RANDOM = new Random();

    /**
     * 네 개의 윷을 던져 나온 스텝 수 반환
     * 빽도: -1, 도:1, 개:2, 걸:3, 윷:4, 모:5
     */
    public ThrowResult throwRandom() {
        // 1: 뒤집어짐, 0: 안 뒤집어짐.
        int yut1 = RANDOM.nextInt(2);
        int yut2 = RANDOM.nextInt(2);
        int yut3 = RANDOM.nextInt(2);
        int yut_backdo = RANDOM.nextInt(2); // 빽도 표시 가진 윷

        int sum = yut1 + yut2 + yut3 + yut_backdo;
        int steps = sum;

        if(sum == 0) steps = 5; // 모
        if(sum == 1 && yut_backdo == 1) steps = -1;

        return ThrowResult.fromSteps(steps);
    }
}

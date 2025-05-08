package model;

public enum ThrowResult {
    BACK_DO(-1, false),
    DO(1, false),
    GAE(2, false),
    GEOL(3, false),
    YUT(4, true),
    MO(5, true);

    private final int steps;
    private final boolean extraTurn;

    ThrowResult(int steps, boolean extraTurn) {
        this.steps = steps;
        this.extraTurn = extraTurn;
    }

    public int getSteps() { return steps; }
    public boolean isExtraTurn() { return extraTurn; }

    public static ThrowResult fromSteps(int s) {
        for (ThrowResult r : values()) {
            if (r.steps == s) return r;
        }
        throw new IllegalArgumentException("Invalid steps: " + s);
    }
}
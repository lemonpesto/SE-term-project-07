package model;

/**
 * 윷놀이판의 형태를 나타내는 enum
 */
public enum BoardShape {
    SQUARE(4),
    PENTAGON(5),
    HEXAGON(6);

    private final int vertexCount;
    BoardShape(int vertexCount) {
        this.vertexCount = vertexCount;
    }
    public int getVertexCount() {
        return vertexCount;
    }
}
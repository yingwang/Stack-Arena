package com.stackarena.game;

import android.graphics.Color;

public class TetrisPiece {
    public enum PieceType {
        A, B, C, D, E, F, G
    }

    private PieceType type;
    private int[][] shape;
    private int color;
    private int x, y;

    public TetrisPiece(PieceType type) {
        this.type = type;
        this.x = 3;
        this.y = -1;  // Start above the board for proper game over detection
        initializeShape();
        initializeColor();
    }

    private void initializeShape() {
        switch (type) {
            case A:
                // XX
                // 2-block line
                shape = new int[][]{
                    {1, 1}
                };
                break;
            case B:
                // XX
                //  X
                // 3-block L
                shape = new int[][]{
                    {1, 1},
                    {0, 1}
                };
                break;
            case C:
                // XX
                // XX
                //  X
                // 5-block
                shape = new int[][]{
                    {1, 1},
                    {1, 1},
                    {0, 1}
                };
                break;
            case D:
                // XX
                //  X
                // XX
                // 5-block zigzag
                shape = new int[][]{
                    {1, 1},
                    {0, 1},
                    {1, 1}
                };
                break;
            case E:
                // XXXXX
                // 5-block line
                shape = new int[][]{
                    {1, 1, 1, 1, 1}
                };
                break;
            case F:
                // XXX
                //   X
                //   X
                // 5-block inverted L
                shape = new int[][]{
                    {1, 1, 1},
                    {0, 0, 1},
                    {0, 0, 1}
                };
                break;
            case G:
                //  X
                // XXX
                //  X
                // 5-block plus/cross
                shape = new int[][]{
                    {0, 1, 0},
                    {1, 1, 1},
                    {0, 1, 0}
                };
                break;
        }
    }

    private void initializeColor() {
        switch (type) {
            case A:
                color = Color.rgb(0, 217, 255); // Neon Cyan
                break;
            case B:
                color = Color.rgb(57, 255, 20); // Neon Green
                break;
            case C:
                color = Color.rgb(176, 38, 255); // Neon Purple
                break;
            case D:
                color = Color.rgb(255, 0, 110); // Neon Pink
                break;
            case E:
                color = Color.rgb(255, 158, 0); // Neon Orange
                break;
            case F:
                color = Color.rgb(0, 255, 245); // Electric Cyan
                break;
            case G:
                color = Color.rgb(255, 20, 147); // Deep Pink
                break;
        }
    }

    public void rotate() {
        int rows = shape.length;
        int cols = shape[0].length;
        int[][] rotated = new int[cols][rows];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                rotated[j][rows - 1 - i] = shape[i][j];
            }
        }
        shape = rotated;
    }

    public int[][] getShape() {
        return shape;
    }

    public int getColor() {
        return color;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void moveLeft() {
        x--;
    }

    public void moveRight() {
        x++;
    }

    public void moveDown() {
        y++;
    }

    public TetrisPiece copy() {
        TetrisPiece copy = new TetrisPiece(this.type);
        copy.x = this.x;
        copy.y = this.y;
        copy.shape = new int[this.shape.length][this.shape[0].length];
        for (int i = 0; i < this.shape.length; i++) {
            System.arraycopy(this.shape[i], 0, copy.shape[i], 0, this.shape[i].length);
        }
        return copy;
    }
}

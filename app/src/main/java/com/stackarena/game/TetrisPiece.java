package com.stackarena.game;

import android.graphics.Color;

public class TetrisPiece {
    public enum PieceType {
        A, B, C, D, E, F, G, H
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
                //  X
                //  X
                //  X
                shape = new int[][]{
                    {1, 1, 0},
                    {0, 1, 0},
                    {0, 1, 0},
                    {0, 1, 0}
                };
                break;
            case B:
                // XXX
                // X X
                shape = new int[][]{
                    {1, 1, 1},
                    {1, 0, 1}
                };
                break;
            case C:
                // XX
                // XX
                //  X
                shape = new int[][]{
                    {1, 1, 0},
                    {1, 1, 0},
                    {0, 1, 0}
                };
                break;
            case D:
                //   X
                // XXX
                // X
                shape = new int[][]{
                    {0, 0, 1},
                    {1, 1, 1},
                    {1, 0, 0}
                };
                break;
            case E:
                // XXXX
                //  X
                shape = new int[][]{
                    {1, 1, 1, 1},
                    {0, 1, 0, 0}
                };
                break;
            case F:
                //  X
                // XXX
                //  X
                shape = new int[][]{
                    {0, 1, 0},
                    {1, 1, 1},
                    {0, 1, 0}
                };
                break;
            case G:
                //  XX
                // XX
                //  X
                shape = new int[][]{
                    {0, 1, 1},
                    {1, 1, 0},
                    {0, 1, 0}
                };
                break;
            case H:
                // XXXXX
                shape = new int[][]{
                    {1, 1, 1, 1, 1}
                };
                break;
        }
    }

    private void initializeColor() {
        switch (type) {
            case A:
                color = Color.CYAN;
                break;
            case B:
                color = Color.YELLOW;
                break;
            case C:
                color = Color.MAGENTA;
                break;
            case D:
                color = Color.GREEN;
                break;
            case E:
                color = Color.RED;
                break;
            case F:
                color = Color.BLUE;
                break;
            case G:
                color = Color.rgb(255, 165, 0); // Orange
                break;
            case H:
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

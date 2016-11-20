package com.csong.tetris;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;

import java.lang.annotation.Retention;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import static java.lang.annotation.RetentionPolicy.SOURCE;

/**
 * Created by csong on 10/21/16.
 */

public class Block {
    @Retention(SOURCE)
    @IntDef({L, INVERTED_L, SQUARE, T, LINE, Z, S})
    public @interface Type {}
    public static final int SQUARE = 0;
    public static final int L = 1;
    public static final int INVERTED_L = 2;
    public static final int T = 3;
    public static final int LINE = 4;
    public static final int Z = 5;
    public static final int S = 6;

    @Retention(SOURCE)
    @IntDef({ZERO, CLOCKWISE, FLIPPED, COUNTER_CLOCKWISE})
    public @interface Rotation {}
    public static final int ZERO = 10;
    public static final int CLOCKWISE = 11;
    public static final int FLIPPED = 12;
    public static final int COUNTER_CLOCKWISE = 13;

    // Color constants
    public static final int CYAN = Color.rgb(128, 255, 255);
    public static final int YELLOW = Color.rgb(255, 255, 150);
    public static final int BLUE = Color.rgb(0, 128, 255);
    public static final int DARK_RED = Color.rgb(170, 0, 0);
    public static final int DARK_GREEN = Color.rgb(5, 100, 5);
    public static final int ORANGE = Color.rgb(255, 128, 64);
    public static final int LIME_GREEN = Color.rgb(75, 255, 75);

    private final int tileSize;
    @Type private final int blockType;
    @Block.Rotation private int rotation;

    // Indicates the top left tile
    private int boardX;
    private int boardY;

    private Set<Point> occupiedTiles;

    public Block(@Type int blockType, int tileSize, int boardX, int boardY) {
        this.blockType = blockType;
        this.tileSize = tileSize;
        this.boardX = boardX;
        this.boardY = boardY;
        this.rotation = ZERO;

        occupiedTiles = new HashSet<>();
        updateOccupiedTiles();
    }

    // Returns true if the block has just stopped falling
    public boolean update(@NonNull List<Block> blocks) {
        // This block falls normally
        boardY++;
        updateOccupiedTiles();

        // Check for collision with other blocks
        for (Block block : blocks) {
            if (block == this) {
                continue;
            }

            if (this.collidesWith(block)) {
                boardY--;
                updateOccupiedTiles();
                return true;
            }
        }

        // Check for collision with the ground
        if (this.onGround()) {
            boardY--;
            updateOccupiedTiles();
            return true;
        }

        return false;
    }

    public void draw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(getBlockColor());

        for (Point p : occupiedTiles) {
            int x = p.x * tileSize;
            int y = p.y * tileSize;
            Rect rect = new Rect(x, y, x + tileSize, y + tileSize);
            canvas.drawRect(rect, paint);
        }
    }

    public void rotate() {
        switch (rotation) {
            case ZERO:
                rotation = CLOCKWISE;
                break;
            case CLOCKWISE:
                rotation = FLIPPED;
                break;
            case FLIPPED:
                rotation = COUNTER_CLOCKWISE;
                break;
            case COUNTER_CLOCKWISE:
                rotation = ZERO;
                break;
        }
    }

    private boolean collidesWith(@NonNull Block other) {
        for (Point p : this.occupiedTiles) {
            if (other.occupiedTiles.contains(p)) {
                return true;
            }
        }
        return false;
    }

    private boolean onGround() {
        int maxY = 0;
        for (Point p : this.occupiedTiles) {
            maxY = Math.max(maxY, p.y);
        }

        boolean b = maxY >= GamePanel.GRID_HEIGHT;
        return b;
    }

    private void updateOccupiedTiles() {
        occupiedTiles.clear();
        switch (blockType) {
            case L:
                switch (rotation) {
                    case ZERO:
                        occupiedTiles.add(new Point(boardX, boardY));
                        occupiedTiles.add(new Point(boardX, boardY + 1));
                        occupiedTiles.add(new Point(boardX, boardY + 2));
                        occupiedTiles.add(new Point(boardX + 1, boardY + 2));
                        break;
                    case CLOCKWISE:
                        occupiedTiles.add(new Point(boardX, boardY));
                        occupiedTiles.add(new Point(boardX + 1, boardY));
                        occupiedTiles.add(new Point(boardX + 2, boardY));
                        occupiedTiles.add(new Point(boardX, boardY + 1));
                        break;
                    case FLIPPED:
                        occupiedTiles.add(new Point(boardX, boardY));
                        occupiedTiles.add(new Point(boardX + 1, boardY));
                        occupiedTiles.add(new Point(boardX + 1, boardY + 1));
                        occupiedTiles.add(new Point(boardX + 1, boardY + 2));
                        break;
                    case COUNTER_CLOCKWISE:
                        occupiedTiles.add(new Point(boardX + 2, boardY));
                        occupiedTiles.add(new Point(boardX, boardY + 1));
                        occupiedTiles.add(new Point(boardX + 1, boardY + 1));
                        occupiedTiles.add(new Point(boardX + 2, boardY + 1));
                        break;
                }
                break;
            case INVERTED_L:
                switch (rotation) {
                    case ZERO:
                        occupiedTiles.add(new Point(boardX + 1, boardY));
                        occupiedTiles.add(new Point(boardX + 1, boardY + 1));
                        occupiedTiles.add(new Point(boardX + 1, boardY + 2));
                        occupiedTiles.add(new Point(boardX, boardY + 2));
                        break;
                    case CLOCKWISE:
                        occupiedTiles.add(new Point(boardX, boardY));
                        occupiedTiles.add(new Point(boardX, boardY + 1));
                        occupiedTiles.add(new Point(boardX + 1, boardY + 1));
                        occupiedTiles.add(new Point(boardX + 2, boardY + 1));
                        break;
                    case FLIPPED:
                        occupiedTiles.add(new Point(boardX, boardY));
                        occupiedTiles.add(new Point(boardX + 1, boardY));
                        occupiedTiles.add(new Point(boardX, boardY + 1));
                        occupiedTiles.add(new Point(boardX, boardY + 2));
                        break;
                    case COUNTER_CLOCKWISE:
                        occupiedTiles.add(new Point(boardX, boardY));
                        occupiedTiles.add(new Point(boardX + 1, boardY));
                        occupiedTiles.add(new Point(boardX + 2, boardY));
                        occupiedTiles.add(new Point(boardX + 2, boardY + 1));
                        break;
                }
                break;
            case SQUARE:
                occupiedTiles.add(new Point(boardX, boardY));
                occupiedTiles.add(new Point(boardX + 1, boardY));
                occupiedTiles.add(new Point(boardX, boardY + 1));
                occupiedTiles.add(new Point(boardX + 1, boardY + 1));
                break;
            case T:
                switch (rotation) {
                    case ZERO:
                        occupiedTiles.add(new Point(boardX + 1, boardY));
                        occupiedTiles.add(new Point(boardX, boardY + 1));
                        occupiedTiles.add(new Point(boardX + 1, boardY + 1));
                        occupiedTiles.add(new Point(boardX + 2, boardY + 1));
                        break;
                    case CLOCKWISE:
                        occupiedTiles.add(new Point(boardX, boardY));
                        occupiedTiles.add(new Point(boardX, boardY + 1));
                        occupiedTiles.add(new Point(boardX + 1, boardY + 1));
                        occupiedTiles.add(new Point(boardX, boardY + 2));
                        break;
                    case FLIPPED:
                        occupiedTiles.add(new Point(boardX, boardY));
                        occupiedTiles.add(new Point(boardX + 1, boardY));
                        occupiedTiles.add(new Point(boardX + 2, boardY));
                        occupiedTiles.add(new Point(boardX + 1, boardY + 1));
                        break;
                    case COUNTER_CLOCKWISE:
                        occupiedTiles.add(new Point(boardX + 1, boardY));
                        occupiedTiles.add(new Point(boardX, boardY + 1));
                        occupiedTiles.add(new Point(boardX + 1, boardY + 1));
                        occupiedTiles.add(new Point(boardX + 1, boardY + 2));
                        break;
                }
                break;
            case LINE:
                switch (rotation) {
                    case ZERO:
                        occupiedTiles.add(new Point(boardX, boardY));
                        occupiedTiles.add(new Point(boardX, boardY + 1));
                        occupiedTiles.add(new Point(boardX, boardY + 2));
                        occupiedTiles.add(new Point(boardX, boardY + 3));
                        break;
                    case CLOCKWISE:
                        occupiedTiles.add(new Point(boardX, boardY));
                        occupiedTiles.add(new Point(boardX + 1, boardY));
                        occupiedTiles.add(new Point(boardX + 2, boardY));
                        occupiedTiles.add(new Point(boardX + 3, boardY));
                        break;
                    case FLIPPED:
                        occupiedTiles.add(new Point(boardX, boardY));
                        occupiedTiles.add(new Point(boardX, boardY + 1));
                        occupiedTiles.add(new Point(boardX, boardY + 2));
                        occupiedTiles.add(new Point(boardX, boardY + 3));
                        break;
                    case COUNTER_CLOCKWISE:
                        occupiedTiles.add(new Point(boardX, boardY));
                        occupiedTiles.add(new Point(boardX + 1, boardY));
                        occupiedTiles.add(new Point(boardX + 2, boardY));
                        occupiedTiles.add(new Point(boardX + 3, boardY));
                        break;
                }
                break;
            case Z:
                switch (rotation) {
                    case ZERO:
                        occupiedTiles.add(new Point(boardX, boardY));
                        occupiedTiles.add(new Point(boardX + 1, boardY));
                        occupiedTiles.add(new Point(boardX + 1, boardY + 1));
                        occupiedTiles.add(new Point(boardX + 2, boardY + 1));
                        break;
                    case CLOCKWISE:
                        occupiedTiles.add(new Point(boardX + 1, boardY));
                        occupiedTiles.add(new Point(boardX, boardY + 1));
                        occupiedTiles.add(new Point(boardX + 1, boardY + 1));
                        occupiedTiles.add(new Point(boardX, boardY + 2));
                        break;
                    case FLIPPED:
                        occupiedTiles.add(new Point(boardX, boardY));
                        occupiedTiles.add(new Point(boardX + 1, boardY));
                        occupiedTiles.add(new Point(boardX + 1, boardY + 1));
                        occupiedTiles.add(new Point(boardX + 2, boardY + 1));
                        break;
                    case COUNTER_CLOCKWISE:
                        occupiedTiles.add(new Point(boardX + 1, boardY));
                        occupiedTiles.add(new Point(boardX, boardY + 1));
                        occupiedTiles.add(new Point(boardX + 1, boardY + 1));
                        occupiedTiles.add(new Point(boardX, boardY + 2));
                        break;
                }
                break;
            case S:
                switch (rotation) {
                    case ZERO:
                        occupiedTiles.add(new Point(boardX + 1, boardY));
                        occupiedTiles.add(new Point(boardX + 2, boardY));
                        occupiedTiles.add(new Point(boardX, boardY + 1));
                        occupiedTiles.add(new Point(boardX + 1, boardY + 1));
                        break;
                    case CLOCKWISE:
                        occupiedTiles.add(new Point(boardX, boardY));
                        occupiedTiles.add(new Point(boardX, boardY + 1));
                        occupiedTiles.add(new Point(boardX + 1, boardY + 1));
                        occupiedTiles.add(new Point(boardX + 1, boardY + 2));
                        break;
                    case FLIPPED:
                        occupiedTiles.add(new Point(boardX + 1, boardY));
                        occupiedTiles.add(new Point(boardX + 2, boardY));
                        occupiedTiles.add(new Point(boardX, boardY + 1));
                        occupiedTiles.add(new Point(boardX + 1, boardY + 1));
                        break;
                    case COUNTER_CLOCKWISE:
                        occupiedTiles.add(new Point(boardX, boardY));
                        occupiedTiles.add(new Point(boardX, boardY + 1));
                        occupiedTiles.add(new Point(boardX + 1, boardY + 1));
                        occupiedTiles.add(new Point(boardX + 1, boardY + 2));
                        break;
                }
                break;
            default:
                break;
        }
    }

    private int getBlockColor() {
        switch (blockType) {
            case T:
                return CYAN;
            case L:
                return DARK_GREEN;
            case INVERTED_L:
                return YELLOW;
            case LINE:
                return DARK_RED;
            case S:
                return BLUE;
            case SQUARE:
                return ORANGE;
            case Z:
                return LIME_GREEN;
            default:
                // Shouldn't happen
                Random random = new Random();
                return Color.rgb(random.nextInt(255), random.nextInt(255), random.nextInt(255));
        }
    }
}

package com.csong.tetris;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by csong on 10/21/16.
 */

public class GamePanel extends SurfaceView implements SurfaceHolder.Callback, GestureDetector.OnGestureListener {
    // dimensions of the grid in blocks
    public static final int GRID_WIDTH = 10;
    public static final int GRID_HEIGHT = 20;

    private static final String TAG = GamePanel.class.getSimpleName();
    private static final int FLING_THRESHOLD = 1000;

    private List<Block> blocks;
    private Block activeBlock;

    public int tileSize;
    private int fallSpeed;
    private int frameCounter;
    private MainThread mainThread;
    private GestureDetectorCompat gestureDetector;

    public GamePanel(Context context) {
        super(context);

        frameCounter = 0;
        fallSpeed = 2;

        getHolder().addCallback(this);

        mainThread = new MainThread(getHolder(), this);
        setFocusable(true);
        blocks = new ArrayList<>((GRID_HEIGHT * GRID_WIDTH) / 4);

        gestureDetector = new GestureDetectorCompat(context, this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        frameCounter = 0;
        fallSpeed = 2;
        tileSize = getHeight() / GRID_HEIGHT;

        activeBlock = generateNewBlock();

        mainThread.setRunning(true);
        mainThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        frameCounter = 0;
        fallSpeed = 2;
        boolean retry = true;
        while (retry) {
            try {
                mainThread.setRunning(false);
                mainThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            retry = false;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        activeBlock.rotate();
        if (gestureDetector.onTouchEvent(event)) {
            return true;
        }
        return super.onTouchEvent(event);
    }

    public void update() {
        frameCounter++;
        if (frameCounter >= MainThread.FPS / fallSpeed) {
            boolean blockLanded = activeBlock.update(blocks);

            if (blockLanded) {
                activeBlock = generateNewBlock();
            }
            frameCounter = 0;
        }
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        for (Block block : blocks) {
            block.draw(canvas);
        }

        drawGrid(canvas);
    }

    // region GestureDetector callbacks
    @Override
    public boolean onDown(MotionEvent motionEvent) {
        return true;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) { }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        activeBlock.rotate();
        return true;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) { }

    @Override
    public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX, float velocityY) {
        Log.d(TAG, "onFling: " + event1.toString() + event2.toString());
        if (Math.abs(velocityX) > FLING_THRESHOLD) {
            if (velocityX < 0) {    // left swipe

            } else {    // right swipe

            }
        }
        return true;
    }

    // endregion

    private Block generateNewBlock() {
        Random random = new Random();
        @Block.Type int block = random.nextInt(7);
        Block blockToAdd = new Block(block, tileSize, random.nextInt(GRID_WIDTH - 2), 0);
        blocks.add(blockToAdd);

        return blockToAdd;
    }

    private void drawGrid(@NonNull Canvas canvas) {
        for (int i = 0; i < GRID_WIDTH + 1; i++) {
            Paint paint = new Paint();
            paint.setColor(Color.GRAY);
            canvas.drawLine(i * tileSize, 0, i * tileSize, tileSize * (GRID_HEIGHT), paint);
        }

        for (int i = 0; i < GRID_HEIGHT + 1; i++) {
            Paint paint = new Paint();
            paint.setColor(Color.GRAY);
            canvas.drawLine(0, i * tileSize, tileSize * GRID_WIDTH, i * tileSize, paint);
        }
    }
}

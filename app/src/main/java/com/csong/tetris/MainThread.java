package com.csong.tetris;

import android.graphics.Canvas;
import android.support.annotation.NonNull;
import android.view.SurfaceHolder;

/**
 * Created by csong on 10/21/16.
 */

public class MainThread extends Thread {
    public static final int FPS = 10;
    private double averageFPS;
    private SurfaceHolder surfaceHolder;
    private GamePanel gamePanel;
    private boolean running;
    public static Canvas canvas;

    public MainThread(@NonNull SurfaceHolder surfaceHolder, @NonNull GamePanel gamePanel) {
        super();
        this.surfaceHolder = surfaceHolder;
        this.gamePanel = gamePanel;
    }

    @Override
    public void run() {
        long startTime;
        long timeMillis;
        long waitTime;
        long totalTime = 0;
        long frameCount = 0;

        // in millis
        long targetTime = 1000/FPS;

        while(running) {
            startTime = System.nanoTime();
            canvas = null;

            canvas = this.surfaceHolder.lockCanvas();
            if (canvas != null) {
                synchronized (surfaceHolder) {
                    this.gamePanel.update();
                    this.gamePanel.draw(canvas);
                }
                surfaceHolder.unlockCanvasAndPost(canvas);
            }

            timeMillis = (System.nanoTime() - startTime) / 1000000;
            waitTime = targetTime - timeMillis;

            if (waitTime > 0) {
                try {
                    Thread.sleep(waitTime);
                } catch (InterruptedException e) {

                }
            }

            totalTime += System.nanoTime() - startTime;
            frameCount++;

            if (frameCount == FPS) {
                averageFPS = (frameCount * 1000000) / (totalTime / 1000);
                frameCount = 0;
                totalTime = 0;
                System.out.println(averageFPS);
            }
        }
    }

    public void setRunning(boolean isRunning) {
        running = isRunning;
    }
}

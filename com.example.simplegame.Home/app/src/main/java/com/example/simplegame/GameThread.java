package com.example.simplegame;

import android.graphics.Canvas;

public class GameThread extends Thread {
	GameView view;
	boolean running = false;

	int fps = 10;

	public GameThread(GameView view) {
		this.view = view;
	}

	public void setRunning(boolean run) {
		running = run;
	}

	@Override
	public void run() {
		
		Canvas canvas = null;
		while (running) {
			try {
				canvas = view.getHolder().lockCanvas();
				synchronized (view.getHolder()) {
					view.onDraw(canvas);
				}
			} finally {
				if (canvas != null) {
					view.getHolder().unlockCanvasAndPost(canvas);
				}
				try {
					
						sleep(10);
				} catch (Exception e) {
				}

			}

		}
	}
}
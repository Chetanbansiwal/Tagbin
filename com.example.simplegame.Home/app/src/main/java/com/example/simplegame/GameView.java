package com.example.simplegame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

public class GameView extends SurfaceView {
	Bitmap bmp,bar,brick;
	SurfaceHolder holder;
	int bary;
	float barx;

	private GameThread thread;
	Sprite sprite;

	public GameView(Context context) {
		super(context);
		thread=new GameThread(this);
		holder=getHolder();
		holder.addCallback(new Callback() {
			
			public void surfaceDestroyed(SurfaceHolder holder) {
				thread.setRunning(false);
				boolean retry=true;
				
					while(retry)
					{
						try {
							thread.join();
							retry=false;
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
			}
			
			public void surfaceCreated(SurfaceHolder holder) {
				bar=BitmapFactory.decodeResource(getResources(), R.drawable.bar);
				bmp=BitmapFactory.decodeResource(getResources(), R.drawable.ball_animation);
				brick=BitmapFactory.decodeResource(getResources(), R.drawable.brick);
//				bricksprite=new BrickSprite(GameView.this, brick);
				sprite=new Sprite(GameView.this,thread,bmp,bar,brick);
				bary=getHeight()-100;
				thread.setRunning(true);
				thread.start();
				
			}
			
			public void surfaceChanged(SurfaceHolder holder, int format, int width,
					int height) {
				// TODO Auto-generated method stub
				
			}
		});
	
	
		
	
		
	}




	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawColor(Color.BLACK);
		sprite.onDraw(canvas);
//		bricksprite.onDraw(canvas);
	
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		sprite.onTouch(event);
		return true;
	}
}

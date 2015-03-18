package com.example.simplegame;

import java.util.Random;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

public class Sprite {
	private static final int NO_COLUMNS = 12;
	Bitmap bmp,bar,brick,scalebit;
	GameView gameview;
	int x,speedx=0,speedy=0;
	int no_bricksperline;
	private int currentFrame=0;
	private int width;
	private int height;
	int brwidth;
	int brheight;
	int init=0;
	float tempwidth;
	String[][] bricks;
	GameThread thread;
	private int y;
	Paint paint=new Paint();
	private int barx;
	private int bary;
    Bitmap bg;

    long initialTime = System.currentTimeMillis();

	@SuppressLint("FloatMath")
	public Sprite(GameView gameview,GameThread thread,Bitmap bmp,Bitmap bar,Bitmap brick)
	{
		this.gameview=gameview;
		this.bmp=bmp;
		this.bar=bar;
		this.brick=brick;
		this.thread=thread;

        bg = BitmapFactory.decodeResource(gameview.getResources(), R.drawable.home_screen);

        this.no_bricksperline=gameview.getWidth()/brick.getWidth()-2;
		bricks=new String[no_bricksperline][4];
		brwidth=brick.getWidth();
		brheight=brick.getHeight();
		paint.setColor(Color.BLACK);
		for (int i = 0; i < no_bricksperline; ++i)
			for (int j = 0; j < 4; ++j)
				bricks[i][j] = "B";
	
		String strwidth=String.valueOf(((float)(bmp.getWidth())/NO_COLUMNS));
		if(strwidth.contains("."))
		{
			scalebit=Bitmap.createScaledBitmap(bmp, (int)(Math.ceil(((float)bmp.getWidth())/NO_COLUMNS))*NO_COLUMNS, bmp.getHeight(), true);
		}
		else
		{
			scalebit=bmp;
		}
		this.width=scalebit.getWidth()/NO_COLUMNS;
		this.bary=gameview.getHeight()-100;
		this.height=scalebit.getHeight();
		Random random=new Random();
		 x = barx -5;//random.nextInt(gameview.getWidth() - width);
         y = bary - bar.getHeight();//random.nextInt(gameview.getHeight() - height)-100;
         if(y<=100)
         {
        	 y=120;
         }
//		speedx=random.nextInt(10)-5;
//		speedy=random.nextInt(10)-5;
		speedx=10;
		speedy=10;
	}
	
	public void update()
	{
		
		if(x>gameview.getWidth()-width-speedx || x+speedx<0)
		{
			speedx=-speedx;
		}
		if(y>gameview.getHeight()-height-speedy || y+speedy<0)
		{
			speedy=-speedy;
		}
		if(y+height >= bary-bar.getHeight()) {
			
			if((x+width>barx-bar.getWidth()/2 && barx>x) ||  (x<barx+bar.getWidth()/2 && x>barx)) {
				//speedy=-speedy;
			}
		}
		
		if(y>0 && y<=(5*brheight) && init==1)
		{
			if(x!=0){
				int x1=x;
				int y1=y;
				if(x1>(no_bricksperline+1)*brwidth)
				{
					x1=(no_bricksperline)*brwidth;
				}
				if(x1<=brwidth)
				{
					x1=brwidth;
				}
				if(y1<=brheight)
				{
					y1=brheight;
				}
				if(y1>=(4*brheight) && y1<=(5*brheight))
				{
					y1=4*brheight;
				}
				int posi=(int)Math.floor(x1/brwidth)-1;
				int posj=(int)Math.floor(y1/brheight)-1;
				if(bricks[posi][posj]=="B")
				{
						bricks[posi][posj]="K";
						speedy=-speedy;
				}	
				
			}
		}
		if(y+height>bary+bar.getHeight())
		{
		}
		
		
		
		x=x+speedx;
		y=y+speedy;
		currentFrame=++currentFrame%NO_COLUMNS;
	}



	@SuppressLint("DrawAllocation")
	public void onDraw(Canvas canvas)
	{
        // Ia all break is gone then stop the thread
		checkFinish();

		update();
        canvas.drawBitmap(bg, 0,0,null);
		for (int i = 0; i < no_bricksperline; ++i){
			for (int j = 0; j < 4; ++j) {
				// ourHolder.lockCanvas();

				if (bricks[i][j].contains("B"))
					canvas.drawBitmap(brick, (i+1)*brick.getWidth(),(j+1)*brick.getHeight(),
							null);
				init=1;
			}
		}
		
		int srcX=currentFrame*width;
		int srcY=0;
		Rect src=new Rect(srcX, srcY, srcX+width, srcY+height);
		Rect dest=new Rect(x,y,x+width,y+width);
		canvas.drawBitmap(brick,x,y,null);

        //canvas.drawBitmap(bar, barx-bar.getWidth()/2, bary-bar.getHeight(),null);

        /* For rotating Bat */
        Matrix matrix = new Matrix();
        matrix.postRotate(20, bar.getWidth()/2, bar.getHeight()/2);
        matrix.postTranslate(barx - bar.getWidth()/2, bary-bar.getHeight());
        canvas.drawBitmap(bar,matrix,null);

        /* For drawing Score */
        Paint paint = new Paint();
        paint.setColor(Color.YELLOW);
        paint.setTextSize(60);
        long timer = System.currentTimeMillis() - initialTime;
        canvas.drawText(timer+" ",20,50,paint);



	}
	
	private void checkFinish() {
		int totalbricks=0;
		for (int i = 0; i < no_bricksperline; ++i){
			for (int j = 0; j < 4; ++j){
				if(bricks[i][j] == "K"){
					totalbricks++;
				}
			}
		}
            if(totalbricks==(no_bricksperline*4))
            {
                thread.setRunning(false);
            }
		}

	public void onTouch(MotionEvent event) {
		barx= (int) event.getX();
        Log.d("Touch",barx+"");
		
	}
	
}

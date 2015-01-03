package com.tagplug.app;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

public class Splash extends Activity {

	private static final int RightToLeft = 1;
	private static final int LeftToRight = 2;
	private static final int DURATION = 18000;
	private ValueAnimator mCurrentAnimator;
	private final Matrix mMatrix = new Matrix();
	private ImageView mImageView;
	private float mScaleFactor;
	private int mDirection = RightToLeft;
	private RectF mDisplayRect = new RectF();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.splash);

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		mImageView = (ImageView) findViewById(R.id.splash_background);

		mImageView.post(new Runnable() {
			@Override
			public void run() {
				mScaleFactor = (float) mImageView.getHeight()
						/ (float) mImageView.getDrawable().getIntrinsicHeight();
				mMatrix.postScale(mScaleFactor, mScaleFactor);
				mImageView.setImageMatrix(mMatrix);
				animate();
			}
		});

		Thread logoTimer = new Thread() {
			public void run() {
				try {
					int logoTimer = 0;
					while (logoTimer < 5000) {
						sleep(100);
						logoTimer = logoTimer + 100;
					}
					;
					startActivity(new Intent(getApplicationContext(),
							MainActivity.class));
				}

				catch (InterruptedException e) {
					e.printStackTrace();
				}

				finally {
					finish();
				}
			}
		};

		logoTimer.start();
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void animate() {
		updateDisplayRect();
		if (mDirection == RightToLeft) {
			animate(mDisplayRect.left, mDisplayRect.left
					- (mDisplayRect.right - mImageView.getWidth()));
		} else {
			animate(mDisplayRect.left, 0.0f);
		}
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@SuppressLint("NewApi")
	private void animate(float from, float to) {
		mCurrentAnimator = ValueAnimator.ofFloat(from, to);
		mCurrentAnimator
				.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
					@TargetApi(Build.VERSION_CODES.HONEYCOMB)
					@SuppressLint("NewApi")
					@Override
					public void onAnimationUpdate(ValueAnimator animation) {
						float value = (Float) animation.getAnimatedValue();

						mMatrix.reset();
						mMatrix.postScale(mScaleFactor, mScaleFactor);
						mMatrix.postTranslate(value, 0);

						mImageView.setImageMatrix(mMatrix);

					}
				});
		mCurrentAnimator.setDuration(DURATION);
		mCurrentAnimator.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				if (mDirection == RightToLeft)
					mDirection = LeftToRight;
				else
					mDirection = RightToLeft;

				animate();
			}
		});
		mCurrentAnimator.start();
	}

	private void updateDisplayRect() {
		mDisplayRect.set(0, 0, mImageView.getDrawable().getIntrinsicWidth(),
				mImageView.getDrawable().getIntrinsicHeight());
		mMatrix.mapRect(mDisplayRect);
	}

}

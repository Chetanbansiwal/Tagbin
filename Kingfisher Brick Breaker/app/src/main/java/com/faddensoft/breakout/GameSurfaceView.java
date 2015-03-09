/*
 * Copyright 2012 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.faddensoft.breakout;

import android.content.Context;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.os.ConditionVariable;
import android.util.Log;
import android.view.MotionEvent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import java.util.ArrayList;
import java.util.List;

/**
 * View object for the GL surface.  Wraps the renderer.
 */
public class GameSurfaceView extends GLSurfaceView implements SensorEventListener {
    private static final String TAG = BreakoutActivity.TAG;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    List<Float>[] rollingAverage = new List[2];
    private static final int MAX_SAMPLE_SIZE = 30;

    private double initialTime = 0.0;
    private double finalTime = 0.0;
    private GameSurfaceRenderer mRenderer;
    private final ConditionVariable syncObj = new ConditionVariable();

    /**
     * Prepares the OpenGL context and starts the Renderer thread.
     */
    public GameSurfaceView(Context context, GameState gameState,
            TextResources.Configuration textConfig) {
        super(context);
        rollingAverage[0] = new ArrayList<Float>();
        rollingAverage[1] = new ArrayList<Float>();
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);


        setEGLContextClientVersion(2);      // Request OpenGL ES 2.0

        getHolder().setFormat(PixelFormat.TRANSLUCENT);

        setEGLConfigChooser(8, 8, 8, 8, 16, 0);

        // Create our Renderer object, and tell the GLSurfaceView code about it.  This also
        // starts the renderer thread, which will be calling the various callback methods
        // in the GameSurfaceRenderer class.
        mRenderer = new GameSurfaceRenderer(gameState, this, textConfig,context);
        setRenderer(mRenderer);

    }

    @Override
    public void onPause() {
        /*
         * We call a "pause" function in our Renderer class, which tells it to save state and
         * go to sleep.  Because it's running in the Renderer thread, we call it through
         * queueEvent(), which doesn't wait for the code to actually execute.  In theory the
         * application could be killed shortly after we return from here, which would be bad if
         * it happened while the Renderer thread was still saving off important state.  We need
         * to wait for it to finish.
         */

        super.onPause();

        //Log.d(TAG, "asking renderer to pause");
        syncObj.close();
        queueEvent(new Runnable() {
            @Override public void run() {
                mRenderer.onViewPause(syncObj);
            }});
        syncObj.block();

        //Log.d(TAG, "renderer pause complete");
    }

    public List<Float> roll(List<Float> list, float newMember){
        if(list.size() == MAX_SAMPLE_SIZE){
            list.remove(0);
        }
        list.add(newMember);
        return list;
    }

    public float averageList(List<Float> tallyUp){

        float total=0;
        for(float item : tallyUp ){
            total+=item;
        }
        total = total/tallyUp.size();

        return total;
    }



    public void onSensorChanged(SensorEvent sensorEvent)
    {

        finalTime = System.currentTimeMillis();
        final float x,y;

        if(finalTime - initialTime  > 5) {


        initialTime = finalTime;
            if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

                rollingAverage[0] = roll(rollingAverage[0], -sensorEvent.values[0]);
                rollingAverage[1] = roll(rollingAverage[1], sensorEvent.values[1]);
                if(averageList(rollingAverage[0])*420 > 900){
                    x = 900;
                }else if(averageList(rollingAverage[0])*420 < 0){
                    x = 0;
                }else x = averageList(rollingAverage[0])*420;

                y = averageList(rollingAverage[1])*420;

                //Log.d(TAG, "GameSurfaceView onAcceloEvent x=" + x + " y=" + y);
                queueEvent(new Runnable() {
                    @Override public void run() {
                        mRenderer.touchEvent(x, y);
                    }});

            }
        }
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ORIENTATION) {

         }

    }

    // I've chosen to not implement this method
    public void onAccuracyChanged(Sensor arg0, int arg1)
    {
        // TODO Auto-generated method stub


    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        /*
         * Forward touch events to the game loop.  We don't want to call Renderer methods
         * directly, because they manipulate state that is "owned" by a different thread.  We
         * use the GLSurfaceView queueEvent() function to execute it there.
         *
         * This increases the latency of our touch response slightly, but it shouldn't be
         * noticeable.
         */

        switch (e.getAction()) {
            case MotionEvent.ACTION_MOVE:
                final float x, y;
                x = e.getX();
                y = e.getY();
                //Log.d(TAG, "GameSurfaceView onTouchEvent x=" + x + " y=" + y);
                queueEvent(new Runnable() {
                    @Override public void run() {
                        mRenderer.touchEvent(x, y);
                    }});
                break;
            default:
                break;
        }

        return true;
    }
}

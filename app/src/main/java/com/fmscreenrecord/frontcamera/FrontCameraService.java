package com.fmscreenrecord.frontcamera;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.view.Surface;
import android.view.WindowManager;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class FrontCameraService extends Service
{

	private CameraViewManager windowManager;

	private ScheduledExecutorService threadPool;

	public static int screenRotation = 0;

	@Override
	public void onCreate() {
		super.onCreate();
//		 mSensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
//		 sensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION); 
//		 mSensorManager.registerListener(mSensorEventListener, sensor, 
//                 SensorManager.SENSOR_DELAY_NORMAL); 
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (windowManager == null) {
			windowManager = CameraViewManager
					.getInstance(getApplicationContext());
		}

		if (threadPool == null) {
			threadPool = Executors.newScheduledThreadPool(1);
			threadPool.scheduleAtFixedRate(command, 0, 1, TimeUnit.SECONDS);
		}
		windowManager.showContent();
		return super.onStartCommand(intent, flags, startId);
	}
	private Runnable command = new Runnable() {
		@Override
		public void run() {


			int rotation = ((WindowManager)getSystemService(Context.WINDOW_SERVICE))
					.getDefaultDisplay().getRotation();
			switch (rotation)
			{
				case Surface.ROTATION_0:
					screenRotation = 0;
					break;
				case Surface.ROTATION_90:
					screenRotation = 1;
					break;
				case Surface.ROTATION_180:
					screenRotation = 2;
					break;
				case Surface.ROTATION_270:
					screenRotation = 3;
					break;
			}


		}
	};
	
	/*
	private Sensor sensor;
	 private SensorManager mSensorManager; 
   private  final SensorEventListener mSensorEventListener =  
       new SensorEventListener() { 
          
       public void onSensorChanged(SensorEvent event) { 
           // TODO Auto-generated method stub 
           if(event.sensor.getType() == Sensor.TYPE_ORIENTATION){ 
               float fPitchAngle = event.values[SensorManager.DATA_X]; 
               System.out.print("======fpa======="+fPitchAngle+"\n");
               if(fPitchAngle<10 && fPitchAngle>-10)
               { 
            	   System.out.print("=============0");
               }
               else if(fPitchAngle<80 && fPitchAngle>100)
               { 
            	   System.out.print("=============1");
               } 
               else if(fPitchAngle<170 && fPitchAngle>190)
               { 
            	   System.out.print("=============2");
               } 
               else if(fPitchAngle<260 && fPitchAngle>280)
               { 
            	   System.out.print("=============3");
               } 
           } 
       } 
          
       public void onAccuracyChanged(Sensor sensor, int accuracy) { 
           // TODO Auto-generated method stub 
              
       } 
   }; */


	public void onDestroy() {
		super.onDestroy();

		if (threadPool != null) {
			threadPool.shutdown();
			threadPool = null;
		}
	}
}

package com.example.wifiinfoapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.SurfaceHolder.Callback;

public class SurfaceTest  extends SurfaceView implements  Callback{
	private SurfaceHolder mSurfaceHolder;
	private Canvas canvas;
	
	
	public SurfaceTest(Context context, AttributeSet attrs) {
		super(context, attrs);
		mSurfaceHolder = getHolder();
		mSurfaceHolder.addCallback(this);
		mSurfaceHolder.setFormat(PixelFormat.TRANSLUCENT);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void surfaceCreated(SurfaceHolder arg0) {
		// TODO Auto-generated method stub
		canvas = mSurfaceHolder.lockCanvas(); 
        //2. 在畫布上貼圖
		Paint paint = new Paint();
		paint.setStyle(Paint.Style.FILL_AND_STROKE);
		paint.setColor(Color.WHITE);
        canvas.drawLine(100,100,200,300, paint);
        //3. 解鎖並po出畫布
        mSurfaceHolder.unlockCanvasAndPost(canvas);
		
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		// TODO Auto-generated method stub
		
	}

}

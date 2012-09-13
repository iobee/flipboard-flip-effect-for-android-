package com.iobee.ui;

import java.util.Timer;
import java.util.TimerTask;

import com.iobee.activity.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.Region;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * 采用src更改点的位置，进行拉伸类似实现3d翻转效果。
 * @author iobee
 *
 */
public class flipboardviewSrc extends View{
	private final static String TAG = "flipboardViewSrc";
	
	//当开始翻转时，激发翻转程序，生成翻转效果用的上下两张bitmap
	private Bitmap downBitmap;
	private Bitmap upBitmap;
	private Bitmap currentBitmap;
	private Bitmap bitmap;
	private Bitmap grayBitmap;
	
	//对翻转时需要进行设置的一些类
	private Matrix UpMatrix; //矩阵
	private Matrix DownMatrix;
	private Paint paint; //绘制bitmap时的一些属性设计
	private Paint paintGray;
	private Canvas canvas; //画布
	private Path path;
	
	//翻转时，需要固定的一些点
	private int centerX,centerY;
	private int mscreenY,mscreenX; //当前手机屏幕的宽和高
	private float deltaX,deltaY;
	private float firstX,firstY;
	
	private int testY;
	
	//shadow
	private int[] gradientShadow;
	private GradientDrawable gradientdrawable;
	private ColorMatrix colormatrix;
	
	//private float[] src = {0,480,640,480,60,960,580,960};
	//private float[] dst = {0,480,640,480,5,560,640,560};
	private float baseY = 0;
	private int UpOrDown; //判断是上翻还是下翻
	
	private Timer time;

	public flipboardviewSrc(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		creatBitmap();
		initCanvas();
		
	}
	

	/**
	 * 生成bitmap
	 */
	public void creatBitmap(){
		
		upBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.background_right);
		downBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bg_960_640);
		grayBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.mask_gray);
		currentBitmap =	BitmapFactory.decodeResource(getResources(), R.drawable.test2);
		bitmap = currentBitmap;
		
	}
	
	/**
	 * 初始化画布绘制的一些数据
	 */
	public void initCanvas(){
		mscreenX = 640;
		mscreenY = 960;
		centerX = mscreenX/2;
		centerY = mscreenY/2;
		
		paint = new Paint();
		paint.setAntiAlias(true);
		paintGray = new Paint();
		paintGray.setAntiAlias(true);
		paintGray.setAlpha(0);
		
		//src = new Float[]{0,480};
		UpMatrix = new Matrix();
		DownMatrix = new Matrix();
		colormatrix = new ColorMatrix();
		colormatrix.set(new float[] {
                1, 0, 0, 0, 0,
                0, 1, 0, 0, 0,
                0, 0, 1, 0, 0,
                0, 0, 0, 1, 0 });
		
		//paint.setColorFilter(new ColorMatrixColorFilter(colormatrix));
		
		gradientShadow = new int[]{0xFF000000,0xFF000000};
		gradientdrawable = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, gradientShadow);
		gradientdrawable.setGradientType(GradientDrawable.LINEAR_GRADIENT);
		
		
		//设置矩形path,分隔图片用
		path = new Path();
		path.moveTo(0, 0);
		path.lineTo(mscreenX, 0);
		path.lineTo(mscreenX,centerY);
		path.lineTo(0, centerY);
		path.close();
		
	}
	
		
	/**
	 * 自动翻转
	 * @param delta
	 */
	public void autoUpdate(){		
		time = new Timer();
		
		TimerTask task = new TimerTask() {			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				
				if(Math.abs(testY) < mscreenY){
					if(UpOrDown == 1)
						testY += 10;
					else if(UpOrDown == 2)
						testY -=10;
					Log.i(TAG, "testY-->"+testY);
					taskManager(testY);
					}
				else
					time.cancel();
			}
		};		
		
		time.schedule(task, 0, 10);
	}
	
	
	
	/**
	 * 对用户操作行为进行分析并作出相应行为反馈
	 */
	public void taskManager(float deltaY){
		float multiple,currentY = 0,currentX = 0,initY;
		int currentAlpha = 0;
		if(UpOrDown == 0)
			if(deltaY >0)
				UpOrDown = 1;
			else if(deltaY <0)
				UpOrDown = 2;
			
		
		if(UpOrDown == 1){ //如果下翻，计算Y点的坐标和X点坐标
			multiple = (mscreenY - baseY)/(firstY - baseY); //获取初始距离的倍数，用来计算Y方向到哪里
			currentY = mscreenY - deltaY*multiple;
			
			if(currentY > mscreenY)
				currentY = mscreenY;
			
			if(currentY < 0)
				currentY = 0;
				
		}
		else if(UpOrDown == 2 ){	
			multiple = (mscreenY - baseY)/(mscreenY-baseY-firstY);
			
			if(deltaY > 0)
				deltaY = 0;			
			currentY = Math.abs(deltaY)*multiple;
			
			if(currentY > mscreenY)
				currentY = mscreenY;
			Log.i(TAG, "currentY-->"+currentY);
		}
		
		//如果Y点在下半个屏幕
		if(currentY > centerY){
			initY = mscreenY;
			
			if(UpOrDown == 1)
				bitmap = currentBitmap;
			else if(UpOrDown == 2)
				bitmap = upBitmap;
				
			currentX = (currentY - centerY)/7;
			currentAlpha = (int) ((mscreenY - currentY)/2);
			gradientdrawable.setBounds(0, centerY, mscreenX, mscreenY);
		}
		
		else{ //如果Y点在上半个屏幕
			initY = 0;
			
			if(UpOrDown == 1)
				bitmap = downBitmap;
			else if(UpOrDown == 2)
				bitmap = currentBitmap;
			
			currentX = (centerY - currentY)/7;
			currentAlpha = (int) (currentY/2);
			gradientdrawable.setBounds(0, 0, mscreenX, centerY);
		}
		
		float[] dst = {0,centerY,mscreenX,centerY,currentX,currentY,mscreenX-currentX,currentY};
		float[] src = {0,centerY,mscreenX,centerY,61,initY,mscreenX-61,initY};
			
		UpMatrix.setPolyToPoly(src, 0, dst, 0, src.length>>1);
			
		//设置透明度
		//int currentAlpha = (int) ((960 - currentY)/2);
		paintGray.setAlpha(currentAlpha);
		gradientdrawable.setAlpha(255 - currentAlpha);
			
		postInvalidate();	
		
	}
	
	
	public void updateView(Canvas canvas){
		if(UpOrDown == 1 || UpOrDown ==0){
			canvas.save();
			canvas.clipPath(path,Region.Op.DIFFERENCE);				
			canvas.drawBitmap(downBitmap, DownMatrix, paint);
			
			if(bitmap == currentBitmap){
				gradientdrawable.draw(canvas);
				canvas.drawBitmap(bitmap, UpMatrix, paint);
				canvas.drawBitmap(grayBitmap, UpMatrix, paintGray);
			}
			
			canvas.restore();
				
			canvas.save();
			canvas.clipPath(path,Region.Op.INTERSECT);
				
			canvas.drawBitmap(currentBitmap, DownMatrix, paint);
			
			if(bitmap == downBitmap){
				gradientdrawable.draw(canvas);
				canvas.drawBitmap(bitmap, UpMatrix, paint);
				canvas.drawBitmap(grayBitmap, DownMatrix, paintGray);
			}
			
			canvas.restore();
		}
		
		if(UpOrDown == 2){
			canvas.save();
			canvas.clipPath(path,Region.Op.DIFFERENCE);				
			canvas.drawBitmap(currentBitmap, DownMatrix, paint);
			
			if(bitmap == upBitmap){
				gradientdrawable.draw(canvas);
				canvas.drawBitmap(bitmap, UpMatrix, paint);
				canvas.drawBitmap(grayBitmap, UpMatrix, paintGray);
			}
			
			canvas.restore();
				
			canvas.save();
			canvas.clipPath(path,Region.Op.INTERSECT);
				
			canvas.drawBitmap(upBitmap, DownMatrix, paint);
			if(bitmap == currentBitmap){
				gradientdrawable.draw(canvas);
				canvas.drawBitmap(bitmap, UpMatrix, paint);
				canvas.drawBitmap(grayBitmap, UpMatrix, paintGray);
			}
			canvas.restore();
		}
	}


	@Override
	protected void dispatchDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.dispatchDraw(canvas);
		canvas.drawColor(Color.YELLOW);
		canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG));
		
		updateView(canvas);       
	}


	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		Log.i(TAG, "ontouchevent");
		float mLastMotionX = event.getX();
		float mLastMotionY = event.getY();
		Log.i(TAG, "mLastMotionY-->"+mLastMotionY);
		Log.i(TAG, "firstY-->"+firstY);
		
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			Log.i(TAG, "action_down");
			firstY = mLastMotionY;
			UpOrDown = 0;
			break;
		
		case MotionEvent.ACTION_MOVE:
			Log.i(TAG, "action_move");
			deltaY = firstY - mLastMotionY;
			Log.i(TAG, deltaY+"");
			taskManager(deltaY);
			break;
			
		case MotionEvent.ACTION_UP:
			Log.i(TAG, "event.up");
			testY = (int) deltaY;
			autoUpdate();
			//taskManager(0);
			break;

		default:
			break;
		}
		return true;
	}
	
	
}

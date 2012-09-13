	package com.iobee.ui;	
	
	import com.iobee.activity.R;

import android.content.Context;  
    import android.graphics.Bitmap;  
    import android.graphics.BitmapFactory;  
    import android.graphics.Camera;  
    import android.graphics.Canvas;  
    import android.graphics.Color;
    import android.graphics.Matrix;  
    import android.graphics.Paint;  
    import android.view.MotionEvent;  
    import android.view.View;  
 
    /** 
     * ͼƬ��ά��ת 
     * @author chroya 
     */  
    public class flipboardviewCamera extends View {  
        //�����  
        private Camera mCamera;  
 
        //��ת�õ�ͼƬ  
        private Bitmap face;      
        private Matrix mMatrix = new Matrix();  
        private Paint mPaint = new Paint();  
 
        private int mLastMotionX, mLastMotionY;  
 
        //ͼƬ�����ĵ�����  
        private int centerX, centerY;  
        //ת�����ܾ��룬����������1:1  
        private int deltaX, deltaY;  
        //ͼƬ��ȸ߶�  
        private int bWidth, bHeight;  
 
        public flipboardviewCamera(Context context) {  
            super(context);  
            setWillNotDraw(false);  
            mCamera = new Camera();   
            mPaint.setAntiAlias(true);  
            face = BitmapFactory.decodeResource(getResources(), R.drawable.test);  
            bWidth = face.getWidth();  
            bHeight = face.getHeight();  
            centerX = bWidth>>1;  
            centerY = bHeight>>1;  
        }     
 
        /** 
         * ת�� 
         * @param degreeX 
         * @param degreeY 
         */  
        void rotate(int degreeX, int degreeY) {  
            deltaX += degreeX;  
            deltaY += degreeY;  
 
            mCamera.save();  
            //mCamera.rotateY(deltaX);  
            mCamera.rotateX(-deltaY);  
            //mCamera.translate(0, 0, -centerX);  
            mCamera.getMatrix(mMatrix);  
            mCamera.restore();    
            //��ͼƬ�����ĵ�Ϊ��ת����,������������䣬�����ԣ�0,0����Ϊ��ת����  
            mMatrix.preTranslate(-centerX, -centerY);  
            mMatrix.postTranslate(centerX, centerY);          
            mCamera.save();   
 
            postInvalidate();  
        }     
 
        @Override  
        public boolean onTouchEvent(MotionEvent event) {  
            int x = (int) event.getX();  
            int y = (int) event.getY();  
 
            switch(event.getAction()) {  
            case MotionEvent.ACTION_DOWN:  
                mLastMotionX = x;  
                mLastMotionY = y;  
                break;  
            case MotionEvent.ACTION_MOVE:  
                int dx = x - mLastMotionX;  
                int dy = y - mLastMotionY;  
                rotate(dx, dy);  
                mLastMotionX = x;  
                mLastMotionY = y;  
                break;  
            case MotionEvent.ACTION_UP:  
                break;  
            }  
            return true;  
        }  
 
        @Override  
        public void dispatchDraw(Canvas canvas) {  
            super.dispatchDraw(canvas);
            canvas.drawColor(Color.WHITE);
            canvas.drawBitmap(face, mMatrix, mPaint);         
        }  
    }
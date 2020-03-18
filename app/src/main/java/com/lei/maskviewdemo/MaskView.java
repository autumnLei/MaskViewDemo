package com.lei.maskviewdemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class MaskView extends View {
    private static final String TAG = "MaskView";
    private int width;//设置高
    private int height;//设置高
    private Paint mPaint;
    private Paint mBgPaint;

    //设置一个Bitmap
    private Bitmap bitmap;
    //创建该Bitmap的画布
    private Canvas bitmapCanvas;
    private Paint mPaintCover;
    private Paint mPaintRect;

    //定义一样个背景的Bitmap
    private Bitmap mBitmapBackground;
    private Matrix matrix;
    private Path mPath;

    //这里设置初始值是为了不点击屏幕时 ，不显示路径
    private float down_x = -100;
    private float down_y = -100;
    private float move_x = -100;
    private float move_y = -100;

    public MaskView(Context context) {
        super(context);
    }

    public MaskView(Context context, AttributeSet attributes) {
        super(context, attributes);
        Log.i(TAG, "MaskView: ");
        //设置背景
        mBitmapBackground = BitmapFactory.decodeResource(getResources(), R.drawable.w);

        mPaintCover = new Paint();
        mPaintCover.setAntiAlias(true);
        mPaintCover.setColor(Color.TRANSPARENT);
        mPaintCover.setStrokeWidth(50);
        //设置图形混合方式，这里使用PorterDuff.Mode.XOR模式，与底层重叠部分设为透明
        PorterDuffXfermode mode = new PorterDuffXfermode(PorterDuff.Mode.CLEAR);
        mPaintCover.setXfermode(mode);
        mPaintCover.setStyle(Paint.Style.STROKE);
        //设置笔刷的样式，默认为BUTT，如果设置为ROUND(圆形),SQUARE(方形)，需要将填充类型Style设置为STROKE或者FILL_AND_STROKE
        mPaintCover.setStrokeCap(Paint.Cap.ROUND);
        //设置画笔的结合方式
        mPaintCover.setStrokeJoin(Paint.Join.ROUND);

        mBgPaint = new Paint();//背景图片的paint
        PorterDuffXfermode bgMode = new PorterDuffXfermode(PorterDuff.Mode.XOR);
        mBgPaint.setXfermode(bgMode);
        mBgPaint.setAntiAlias(true);

        //路径记录滑动屏幕的路径。
        mPath = new Path();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.i(TAG, "onMeasure: ");
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        height = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        setMeasuredDimension(width, height);//设置宽和高

        //创建一个Bitmap，用于绘图。
        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmapCanvas = new Canvas(bitmap);//该画布为bitmap。

        //绘制背景BitmapBackground大小的矩阵
        matrix = new Matrix();//如果在构造器中初始化，需要使用reset()方法
        matrix.postScale((float) width / mBitmapBackground.getWidth(), (float) height / mBitmapBackground.getHeight());

    }

    @Override
    protected void onDraw(Canvas canvas) {
        Log.i(TAG, "onDraw: ");
        super.onDraw(canvas);
        //将bitmapBackground设置该View画布的背景
//        canvas.drawBitmap(mBitmapBackground, matrix, null);
        //然后画布添加背景的基础上添加bitmap。
//        canvas.drawBitmap(bitmap, 0, 0, mPaint);
//        bitmapCanvas.drawRect(0, 0, width, height, mPaintRect);//bitmap上绘制一个蒙版
        canvas.drawPath(mPath, mPaintCover);//bitmap上绘制手 划过的路径
        canvas.drawBitmap(mBitmapBackground, matrix, mBgPaint);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        Log.i(TAG, "onLayout: ");
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.i(TAG, "onTouchEvent: event-" + MotionEvent.actionToString(event.getAction()));
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //获得点击屏幕时的坐标
                down_x = event.getX();
                down_y = event.getY();
                //将Path移动到点击点
                mPath.moveTo(down_x, down_y);
                invalidate();//更新画面
                break;
            case MotionEvent.ACTION_MOVE:
                //获得在屏幕上移动的坐标
                move_x = event.getX();
                move_y = event.getY();
                //将移动的轨迹画成直线
                mPath.lineTo(move_x, move_y);
                invalidate();//更新画面
                break;
            default:
                break;
        }
        return true;
    }

}
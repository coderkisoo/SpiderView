package com.example.shs1329.viewtest;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;

import static android.R.attr.angle;

/**
 * Created by shs1329 on 2017/9/4.
 */

public class SpiderView extends View {

    private static final int POINT_DEF = 0;
    private static final int BACKGROUND_CIRCLE_MIN = 20;
    // 自定义view中的属性
    float radius = 0;
    int pointNum = 0;//蛛网图点的数目
    double radians = 0;
    int levelNum = 0;//层级数目
    float pointRadius = 0;//点半径
    int powerColor = Color.GREEN;

    //自定义view中的paint
    Paint pointPaint;
    Paint linePaint;
    Paint powerPaint;
    Canvas canvas;
    ArrayList<Float> datas = new ArrayList<>();

    public SpiderView(Context context) {
        super(context);
    }

    public SpiderView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SpiderView);
        try {
            pointNum = typedArray.getInteger(R.styleable.SpiderView_point_num, POINT_DEF);
            radius = typedArray.getDimension(R.styleable.SpiderView_radius, 0.0f);
            pointRadius = typedArray.getDimension(R.styleable.SpiderView_pointRadius, 0.0f);
            levelNum = typedArray.getInteger(R.styleable.SpiderView_levelNum, 0);
            powerColor = typedArray.getColor(R.styleable.SpiderView_powerColor, powerColor);
        } finally {
            typedArray.recycle();
        }
        initData();
    }

    private void initData() {
        radians = Math.PI * 2 / pointNum;
        pointPaint = new Paint();
        pointPaint.setColor(Color.BLUE);
        pointPaint.setAntiAlias(true);
        pointPaint.setStrokeWidth(8);
        pointPaint.setStyle(Paint.Style.FILL);

        linePaint = new Paint();
        linePaint.setAntiAlias(true);
        linePaint.setStrokeWidth(1);
        linePaint.setColor(Color.BLACK);
        linePaint.setStyle(Paint.Style.STROKE);

        powerPaint = new Paint();
        powerPaint.setColor(powerColor);
        powerPaint.setAntiAlias(true);
        powerPaint.setStrokeWidth(4);
        powerPaint.setStyle(Paint.Style.STROKE);

        for (int i = 0; i < pointNum; i++) {
            datas.add((float) Math.random());
        }
    }

    /**
     * 我们要画出来的是一个正n边形 n = {@link SpiderView#pointNum}
     * 要决定出一个圆形 宽度 高度必须相等
     */
//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        int widthMeasureMode = MeasureSpec.getMode(widthMeasureSpec);
//        int widthMeasureSize = MeasureSpec.getSize(widthMeasureSpec);
//        int heightMeasureMode = MeasureSpec.getMode(heightMeasureSpec);
//        int heightMeasureSize = MeasureSpec.getSize(heightMeasureSpec);
//        if (MeasureSpec.UNSPECIFIED == widthMeasureMode) {
//            widthMeasureSize = BACKGROUND_CIRCLE_MIN;
//        }
//        if (MeasureSpec.UNSPECIFIED == heightMeasureMode) {
//            heightMeasureSize = BACKGROUND_CIRCLE_MIN;
//        }
//        widthMeasureSize = Math.min(widthMeasureSize, heightMeasureSize);
//        setMeasuredDimension(getDefaultSize(widthMeasureSize, widthMeasureSpec),
//                getDefaultSize(widthMeasureSize, heightMeasureSpec));
//    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    /**
     * 画出来的是一个正n边形 n = {@link SpiderView#pointNum}
     * 起始点，x/2,0
     * 画n个点
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.canvas = canvas;
        int centerX = canvas.getWidth() / 2;
        int centerY = canvas.getHeight() / 2;
        System.out.println("radius :" + radius + " angle:" + angle);
        ArrayList<Path> pathList = new ArrayList<>();
        for (int i = 0; i < levelNum; i++) {
            pathList.add(new Path());
        }
        for (int i = 0; i < pointNum; i++) {
            double currentRadians = radians * i;
            float pointX = (float) (Math.sin(currentRadians) * radius);//坐标轴上的点
            float pointY = -(float) (Math.cos(currentRadians) * radius);

            float realPointX = centerX + pointX;
            float realPointY = centerY + pointY;
            for (int currentLevel = 0; currentLevel < levelNum; currentLevel++) {
                float levelPointX = centerX + pointX * (currentLevel + 1) / levelNum;
                float levelPointY = centerY + pointY * (currentLevel + 1) / levelNum;
                Path path = pathList.get(currentLevel);
                if (0 == i) {
                    path.moveTo(levelPointX, levelPointY);
                } else {
                    path.lineTo(levelPointX, levelPointY);
                }
            }

            canvas.drawLine(centerX, centerY, realPointX, realPointY, linePaint);
            canvas.drawCircle(realPointX, realPointY, pointRadius, pointPaint);
        }

        for (int currentLevel = 0; currentLevel < levelNum; currentLevel++) {
            Path path = pathList.get(currentLevel);
            path.close();
            canvas.drawPath(path, linePaint);
        }

        canvas.drawCircle(centerX, centerY, pointRadius, pointPaint);
        drawPowerLine();
    }

    public void setDatas(ArrayList<Float> floatList) {
        this.datas = floatList;
        invalidate();
    }

    private void drawPowerLine() {
        if (datas.size() != pointNum) {
            return;
        }
        float centerX = canvas.getWidth() / 2;
        float centerY = canvas.getHeight() / 2;
        Path path = new Path();
        for (int i = 0; i < pointNum; i++) {
            double currentRadians = radians * i;
            float pointX = (float) (Math.sin(currentRadians) * radius * datas.get(i));//坐标轴上的点
            float pointY = -(float) (Math.cos(currentRadians) * radius * datas.get(i));

            float realPointX = centerX + pointX;
            float realPointY = centerY + pointY;
            if (i == 0) {
                path.moveTo(realPointX, realPointY);
            } else {
                path.lineTo(realPointX, realPointY);
            }
//            canvas.drawCircle(realPointX, realPointY, pointRadius, pointPaint);
        }
        path.close();
        canvas.drawPath(path, powerPaint);
    }

}

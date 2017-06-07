package com.yuweng.drawtest;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

/**
 * Created by yuweng on 2017/6/6.
 */

public class Draw extends View {
    public Paint paint;
    public Canvas canvas;
    Bitmap bitmap;
    Draw(Context context,int display_height,int display_width){
        super(context);
        paint = new Paint(Paint.DITHER_FLAG);
        bitmap = Bitmap.createBitmap(display_width,display_height,Bitmap.Config.RGB_565);
        canvas = new Canvas(bitmap);
        paint.setColor(Color.RED);
        paint.setDither(true);
        paint.setAntiAlias(true);
        paint.setStrokeCap(Paint.Cap.ROUND);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(bitmap,0,0,paint);
    }

    public void drawPoint(double x, double y){
        canvas.drawPoint((float)x,(float)y,paint);
        invalidate();
    }
    public void drawLine(final double start_x,final double start_y,final double end_x,final double end_y){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                canvas.drawLine((float)start_x,(float)start_y,(float)end_x,(float)end_y,paint);
            }
        });
        thread.start();
        invalidate();
    }
    public void drawLineNoThread(final double start_x,final double start_y,final double end_x,final double end_y){
        canvas.drawLine((float)start_x,(float)start_y,(float)end_x,(float)end_y,paint);
        invalidate();
    }

    public void drawCircle(final double center_x,final double center_y,final double raidus){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                canvas.drawCircle((float)center_x,(float)center_y,(float)raidus,paint);
            }
        });
        thread.start();
        invalidate();
    }

    public void drawCircleNoThread(final double center_x,final double center_y,final double raidus){
        canvas.drawCircle((float)center_x,(float)center_y,(float)raidus,paint);
        invalidate();
    }

}

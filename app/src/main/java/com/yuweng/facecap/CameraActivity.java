package com.yuweng.facecap;

/**
 * Created by yuweng on 2017/6/6.
 */


import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.androidhiddencamera.CameraConfig;
import com.androidhiddencamera.CameraError;
import com.androidhiddencamera.HiddenCameraActivity;
import com.androidhiddencamera.config.CameraFacing;
import com.androidhiddencamera.config.CameraImageFormat;
import com.androidhiddencamera.config.CameraResolution;
import com.androidhiddencamera.config.CameraRotation;
import com.yuweng.drawtest.*;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class CameraActivity extends HiddenCameraActivity {
    Draw draw;
    Timer timer;
    List<Point> list;
    int dis_height;
    int dis_width;
    int i ;//List 的索引
    private CameraConfig mCameraConfig;
    Button cap_button;

    public static class MyHandler extends Handler {
        private final WeakReference<Activity> myActivity;
        public MyHandler(CameraActivity act){
            myActivity = new WeakReference<Activity>(act);
        }
        @Override
        public void handleMessage(Message msg) {
            Bundle data;
            switch(msg.what){
                case 0://Line
                    data = msg.getData();
                    double x1 = data.getDouble("x1");
                    double y1 = data.getDouble("y1");
                    double x2 = data.getDouble("x2");
                    double y2 = data.getDouble("y2");
                    //((MainActivity)myActivity.get()).draw.canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                    ((CameraActivity)myActivity.get()).draw.drawLineNoThread(x1,y1,x2,y2);
                case 1://Point
                    data = msg.getData();
                    double x = data.getDouble("x");
                    double y = data.getDouble("y");
                    ((CameraActivity)myActivity.get()).draw.canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                    ((CameraActivity)myActivity.get()).draw.drawCircleNoThread(x,y,4);
            }
            super.handleMessage(msg);
        }
    }
    MyHandler handler = new MyHandler(this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);//隐藏状态栏
        WindowManager wm = this.getWindowManager();
        dis_height = wm.getDefaultDisplay().getHeight();
        dis_width = wm.getDefaultDisplay().getWidth();
        draw = new Draw(this,dis_height,dis_width);

        setContentView(R.layout.activity_camera);

        RelativeLayout relative_layout = (RelativeLayout)findViewById(R.id.relative_layout);
        relative_layout.addView(draw);

        cap_button = (Button)findViewById(R.id.cap_button);
        mCameraConfig = new CameraConfig()
                .getBuilder(this)
                .setCameraFacing(CameraFacing.FRONT_FACING_CAMERA)
                .setCameraResolution(CameraResolution.HIGH_RESOLUTION)
                .setImageFormat(CameraImageFormat.FORMAT_JPEG)
                .setImageRotation(CameraRotation.ROTATION_270)
                .build();
        //Check for the camera permission for the runtime
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            //Start camera preview
            startCamera(mCameraConfig);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 101);
        }
    }

    public void capture(View v){
        String file_save_img = dis_height+"x"+dis_width;
        final File file_= new File(Environment.getExternalStorageDirectory(),file_save_img);
        if(!file_.exists()){
            file_.mkdir();
        }

        Button btn = (Button)findViewById(R.id.cap_button);
        btn.setVisibility(View.INVISIBLE);
        Random ran = new Random();
        int num = 1;
        while (num>0){
            num--;
            //statue = false;
            int x1 = ran.nextInt(dis_width);
            int y1 = ran.nextInt(dis_height);
            int x2 = ran.nextInt(dis_width);
            int y2 = ran.nextInt(dis_height);
            draw.drawCircle(x1,y1,4);
            draw.drawLineNoThread(x1,y1,x2,y2);
            Toast.makeText(this,x1+" "+y1+" "+x2+" "+y2,Toast.LENGTH_LONG).show();
            Point p1 = new Point(x1,y1);
            Point p2 = new Point(x2,y2);
            list = Point.getLinePoints(p1,p2);
            i = 0;
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Message msg = handler.obtainMessage();
                    Bundle data = new Bundle();
                    msg.what = 1;
                    data.putDouble("x",list.get(i).x);
                    data.putDouble("y",list.get(i).y);
                    msg.setData(data);
                    handler.sendMessage(msg);
                    String img_name = list.get(i).x+"_"+list.get(i).y+".jpg";
                    File img_file = new File(file_,img_name);
                    takePicture(img_file);
                    i++;
                    if (i ==list.size()){
                        timer.cancel();
                        timer = null;
                        list.clear();
                        list = null;
                        System.gc();
                    }
                }
            },1000,500);
        }
        btn.setVisibility(View.VISIBLE);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 101) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //noinspection MissingPermission
                startCamera(mCameraConfig);
            } else {
                Toast.makeText(this, "Camera permission denied.", Toast.LENGTH_LONG).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onImageCapture(@NonNull File imageFile) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath(), options);

        //Display the image to the image view
        //((ImageView) findViewById(R.id.cam_prev)).setImageBitmap(bitmap);
    }

    @Override
    public void onCameraError(@CameraError.CameraErrorCodes int errorCode) {
        switch (errorCode) {
            case CameraError.ERROR_CAMERA_OPEN_FAILED:
                //Camera open failed. Probably because another application
                //is using the camera
                Toast.makeText(this, "Cannot open camera.", Toast.LENGTH_LONG).show();
                break;
            case CameraError.ERROR_IMAGE_WRITE_FAILED:
                //Image write failed. Please check if you have provided WRITE_EXTERNAL_STORAGE permission
                Toast.makeText(this, "Cannot write image captured by camera.", Toast.LENGTH_LONG).show();
                break;
            case CameraError.ERROR_CAMERA_PERMISSION_NOT_AVAILABLE:
                //camera permission is not available
                //Ask for the camra permission before initializing it.
                Toast.makeText(this, "Camera permission not available.", Toast.LENGTH_LONG).show();
                break;
            case CameraError.ERROR_DOES_NOT_HAVE_OVERDRAW_PERMISSION:
                //This error will never happen while hidden camera is used from activity or fragment
                break;
            case CameraError.ERROR_DOES_NOT_HAVE_FRONT_CAMERA:
                Toast.makeText(this, "Your device does not have front camera.", Toast.LENGTH_LONG).show();
                break;
        }
    }
}

package com.yuweng.facecap;

import android.content.Intent;
import android.os.Environment;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    Timer timer ;
    int i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);//隐藏标题栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);//隐藏状态栏
        setContentView(R.layout.activity_main);
    }

    public void toService(View v){
        final Intent intent = new Intent(MainActivity.this,CameraService.class);
        final String file_pre = Environment.getExternalStorageDirectory().getPath()+"/Cap/";
        timer = new Timer();
        i = 5;
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                intent.putExtra("img_name",file_pre+System.currentTimeMillis()+".jpg");
                startService(intent);
                i--;
                if (i ==0){
                    timer.cancel();
                    timer = null;
                }
            }
        },1000,500);

    }
    public void toActivity(View v){
        Intent intent = new Intent(MainActivity.this,CameraActivity.class);
        startActivity(intent);

    }
}

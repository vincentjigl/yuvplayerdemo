package com.example.administrator.yuvplayerdemo;

import android.Manifest;
import android.app.Activity;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends Activity {
    private static final String TAG = "jgl";
    private GLSurface mglsuface = null;
    private MyGLRender mrender = null;
    int width = 320;
    int height = 240;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "requestPermissions");
            // here, Permission is not granted
            ActivityCompat.requestPermissions(this, new String[] {
                    android.Manifest.permission.CAMERA,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE }, 50);
        } else if (savedInstanceState == null) {
            Log.i(TAG, "onCreate");
        }
        setContentView(R.layout.activity_main);

        mglsuface = (GLSurface)findViewById(R.id.preview);
        mrender = new MyGLRender(mglsuface);
        mglsuface.setRenderer(mrender);

        mrender.update(width, height);
        new Thread(){
            @Override
            public void run() {
                super.run();
                File sdcard = Environment.getExternalStorageDirectory();
                final String fileName = sdcard + "/test.yuv";
                Log.i("jgl", "file path" + fileName);

                File yuvFile = new File(fileName);
                FileInputStream fis = null;
                try {
                    fis   = new FileInputStream(yuvFile);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                int size = width * height * 3 / 2;

                byte[] input = new byte[size];
                int hasRead = 0;
                while (true){

                    try {
                        hasRead = fis.read(input);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (hasRead == -1){
                        break;
                    }

                    mrender.update(input);
                    Log.i("thread", "thread is executing hasRead: " + hasRead);
                    try {
                        sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }
}

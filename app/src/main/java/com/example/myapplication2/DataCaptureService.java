package com.example.myapplication2;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.myapplication2.Register.Register7Activity;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DataCaptureService extends Service {
    private static final String TAG = "DataCaptureService";
    private ExecutorService executorService;
    private boolean isRunning = false;
    private ConectInsole conect;
    private ConectInsole2 conect2;
    private SharedPreferences sharedPreferences;
    private String followInRight, followInLeft;

    @Override
    public void onCreate() {
        super.onCreate();
        executorService = Executors.newSingleThreadExecutor();
        isRunning = true;
        sharedPreferences = getSharedPreferences("My_Appinsolesamount", MODE_PRIVATE);
        followInRight = sharedPreferences.getString("Sright", "default");
        followInLeft = sharedPreferences.getString("Sleft", "default");
        startReceivingData();


        sharedPreferences = getSharedPreferences("My_Appinsolesamount", MODE_PRIVATE);
        followInRight = sharedPreferences.getString("Sright", "default");
        followInLeft = sharedPreferences.getString("Sleft", "default");


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isRunning = false;
        executorService.shutdownNow();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @NonNull
    private void startReceivingData() {

        sharedPreferences = getSharedPreferences("My_Appcalibrar", MODE_PRIVATE);




        executorService.execute(new Runnable() {
            @Override
            public void run() {
                while (isRunning) {
                    conect = new ConectInsole(DataCaptureService.this);
                    conect2 = new ConectInsole2(DataCaptureService.this);
                    if ("true".equals(followInRight)) {
                        conect.checkForNewData(DataCaptureService.this);
                    }
                    if ("true".equals(followInLeft)) {
                        conect2.checkForNewData(DataCaptureService.this);
                    }
                    try {
                        Thread.sleep(250);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

}
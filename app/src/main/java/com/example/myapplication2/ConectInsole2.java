package com.example.myapplication2;

import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ConectInsole2 {
    private static final String CHANNEL_ID = "notify_pressure";

    private final OkHttpClient client = new OkHttpClient();
    private final FirebaseHelper firebaseHelper;
    private final SharedPreferences prefsConfig;
    private final String baseUrl;

    private Calendar calendar;
    private SendData receivedData = new SendData();

    public static class ConfigData {
        public int cmd, freq;
        public int[] thresholds = new int[9];

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("ConfigData{");
            for (int i = 0; i < thresholds.length; i++) {
                sb.append("S").append(i + 1).append("=").append(thresholds[i]);
                if (i < thresholds.length - 1) sb.append(", ");
            }
            sb.append('}');
            return sb.toString();
        }
    }

    public static class SendData {
        public int cmd, battery, sensorTrigger;
        public int hour, minute, second, millisecond;
        public ArrayList<Integer> SR1 = new ArrayList<>();
        public ArrayList<Integer> SR2 = new ArrayList<>();
        public ArrayList<Integer> SR3 = new ArrayList<>();
        public ArrayList<Integer> SR4 = new ArrayList<>();
        public ArrayList<Integer> SR5 = new ArrayList<>();
        public ArrayList<Integer> SR6 = new ArrayList<>();
        public ArrayList<Integer> SR7 = new ArrayList<>();
        public ArrayList<Integer> SR8 = new ArrayList<>();
        public ArrayList<Integer> SR9 = new ArrayList<>();
    }

    public ConectInsole2(@NonNull Context context) {
        Log.d(TAG, "ConectInsole2: initializing");
        firebaseHelper = new FirebaseHelper(context);
        prefsConfig = context.getSharedPreferences("My_Appips", MODE_PRIVATE);
        baseUrl = "http://" + prefsConfig.getString("IP2", "");
        Log.d(TAG, "Base URL: " + baseUrl);
    }

    public void createAndSendConfigData(byte cmd, byte freq, short... sensors) {
        Log.d(TAG, "createAndSendConfigData: cmd=" + cmd + ", freq=" + freq);
        ConfigData cfg = new ConfigData();
        cfg.cmd = cmd;
        cfg.freq = freq;
        for (int i = 0; i < cfg.thresholds.length && i < sensors.length; i++) {
            cfg.thresholds[i] = sensors[i];
        }
        Log.d(TAG, "ConfigData: " + cfg);
        sendConfigData(cfg);
    }

    private void sendConfigData(ConfigData cfg) {
        Log.d(TAG, "sendConfigData: building payload");
        StringBuilder payload = new StringBuilder()
                .append(cfg.cmd).append(',')
                .append(cfg.freq);
        for (int val : cfg.thresholds) payload.append(',').append(val);
        Log.d(TAG, "Payload: " + payload);

        RequestBody body = new FormBody.Builder()
                .add("config_data", payload.toString())
                .build();

        Request request = new Request.Builder()
                .url(baseUrl + "/config")
                .post(body)
                .build();
        client.newCall(request).enqueue(new LoggingCallback("sendConfigData"));
    }

    public void checkForNewData(Context ctx) {
        Log.d(TAG, "checkForNewData: " + baseUrl + "/check");
        Request request = new Request.Builder()
                .url(baseUrl + "/check")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "checkForNewData error", e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d(TAG, "checkForNewData response: " + response.code());
                if (response.isSuccessful()) {
                    try {
                        boolean hasNew = new JSONObject(response.body().string())
                                .getBoolean("newData");
                        Log.d(TAG, "New data: " + hasNew);
                        if (hasNew) receiveData(ctx);
                    } catch (JSONException e) {
                        Log.e(TAG, "checkForNewData JSON error", e);
                    }
                } else {
                    Log.e(TAG, "checkForNewData failed: " + response.message());
                }
            }
        });
    }

    public void receiveData(Context ctx) {
        Log.d(TAG, "receiveData: " + baseUrl + "/data");
        Request request = new Request.Builder()
                .url(baseUrl + "/data")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "receiveData failure", e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d(TAG, "receiveData response: " + response.code());
                if (!response.isSuccessful()) {
                    Log.e(TAG, "receiveData failed: " + response.message());
                    return;
                }
                try {
                    String json = response.body().string();
                    Log.d(TAG, "Raw JSON: " + json);
                    parseJson(json);

                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user != null) {
                        if (isNetworkAvailable(ctx)) {
                            firebaseHelper.saveSendData2(receivedData, getEventList(ctx));
                            Log.d(TAG, "Saved to Firebase");
                        } else {
                            String today = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
                            firebaseHelper.saveSendData2Locally(receivedData, today);
                            runToast(ctx, "Sem conexão. Dados salvos localmente.");
                            Log.d(TAG, "Saved locally due to no network");
                        }
                    } else {
                        Log.d(TAG, "User not authenticated: skip save");
                    }

                    storeReadings(ctx);
                    Log.d(TAG, "Readings stored");
                    if (receivedData.cmd == 0x3D) handlePressureEvent(ctx);
                } catch (JSONException e) {
                    Log.e(TAG, "receiveData JSON error", e);
                }
            }
        });
    }

    private void parseJson(String json) throws JSONException {
        Log.d(TAG, "parseJson: start");
        JSONObject j = new JSONObject(json);
        receivedData.cmd = j.getInt("cmd");
        calendar = Calendar.getInstance();
        receivedData.hour = calendar.get(Calendar.HOUR_OF_DAY);
        receivedData.minute = calendar.get(Calendar.MINUTE);
        receivedData.second = calendar.get(Calendar.SECOND);
        receivedData.millisecond = calendar.get(Calendar.MILLISECOND);
        receivedData.battery = j.getInt("battery");
        Log.d(TAG, "Metadata - cmd:" + receivedData.cmd + ", bat:" + receivedData.battery);
        JSONArray sensorsReads = j.getJSONArray("sensors_reads");

        // Clear previous data
        receivedData.SR1.clear();
        receivedData.SR2.clear();
        receivedData.SR3.clear();
        receivedData.SR4.clear();
        receivedData.SR5.clear();
        receivedData.SR6.clear();
        receivedData.SR7.clear();
        receivedData.SR8.clear();
        receivedData.SR9.clear();

        for (int i = 0; i < sensorsReads.length(); i++) {
            JSONObject sensorRead = sensorsReads.getJSONObject(i);
            receivedData.SR1.add( sensorRead.getInt("S1"));
            receivedData.SR2.add( sensorRead.getInt("S2"));
            receivedData.SR3.add( sensorRead.getInt("S3"));
            receivedData.SR4.add( sensorRead.getInt("S4"));
            receivedData.SR5.add( sensorRead.getInt("S5"));
            receivedData.SR6.add( sensorRead.getInt("S6"));
            receivedData.SR7.add( sensorRead.getInt("S7"));
            receivedData.SR8.add( sensorRead.getInt("S8"));
            receivedData.SR9.add( sensorRead.getInt("S9"));
        }
        Log.d(TAG, "Sensor count:" + receivedData.SR1+","+ receivedData.SR2+","+ receivedData.SR3+","+ receivedData.SR4+","+ receivedData.SR5+","+ receivedData.SR6+","+ receivedData.SR7+","+ receivedData.SR8+","+ receivedData.SR9);
    }

    private void storeReadings(Context ctx) {
        String receivedS1 = receivedData.SR1.toString();
        String receivedS2 = receivedData.SR2.toString();
        String receivedS3 = receivedData.SR3.toString();
        String receivedS4 = receivedData.SR4.toString();
        String receivedS5 = receivedData.SR5.toString();
        String receivedS6 = receivedData.SR6.toString();
        String receivedS7 = receivedData.SR7.toString();
        String receivedS8 = receivedData.SR8.toString();
        String receivedS9 = receivedData.SR9.toString();
        SharedPreferences sharedPreferences = ctx.getSharedPreferences("My_Appinsolereadings2", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("S1_2", receivedS1);
        editor.putString("S2_2", receivedS2);
        editor.putString("S3_2", receivedS3);
        editor.putString("S4_2", receivedS4);
        editor.putString("S5_2", receivedS5);
        editor.putString("S6_2", receivedS6);
        editor.putString("S7_2", receivedS7);
        editor.putString("S8_2", receivedS8);
        editor.putString("S9_2", receivedS9);
        editor.apply();
    }


    private List<String> getEventList(Context ctx) {
        Log.d(TAG, "getEventList: checking thresholds");
        /*SharedPreferences reg = ctx.getSharedPreferences("My_Appregions", MODE_PRIVATE);
        SharedPreferences thr = ctx.getSharedPreferences("Treshold_insole2", MODE_PRIVATE);
        List<String> events = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            boolean on = reg.getBoolean("S" + (i + 1), false);
            int lim = thr.getInt("Lim" + (i + 1) + "I2", 8191);
            int val = getLastReading(i);
            if (on && val > lim) {
                events.add(String.valueOf(i + 1));
                Log.d(TAG, "Event sensor" + (i + 1) + ":" + val);
            }
        }
        return events;*/
        return java.util.Collections.emptyList();
    }

    private void handlePressureEvent(Context ctx) {
        Log.d(TAG, "handlePressureEvent: event detected");
        SharedPreferences vib = ctx.getSharedPreferences("My_Appvibra", MODE_PRIVATE);
        conectarVibraSend((byte) 0x1A, vib);
        createNotificationChannel(ctx);
        NotificationManagerCompat nm = NotificationManagerCompat.from(ctx);
        if (ActivityCompat.checkSelfPermission(ctx, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED)
            return;
        nm.notify(2, buildNotification(ctx));
        Log.d(TAG, "Notification dispatched");
    }

    private void conectarVibraSend(byte cmd, SharedPreferences vib) {
        Log.d(TAG, "conectarVibraSend: sending vibra config");
        byte pulse = Byte.parseByte(vib.getString("pulse", "0"));
        short interval = Short.parseShort(vib.getString("interval", "0"));
        short time = Short.parseShort(vib.getString("time", "0"));
        byte freq = Byte.parseByte(vib.getString("int", "0"));
        new ConectVibra(null).SendConfigData(cmd, pulse, freq, time, interval);
    }

    private Notification buildNotification(Context ctx) {
        Log.d(TAG, "buildNotification: creating notif");
        Bitmap bmp = BitmapFactory.decodeResource(ctx.getResources(), R.drawable.leftfoot2);
        String txt = "Sensor(es): " + String.join(", ", getEventList(ctx));
        return new NotificationCompat.Builder(ctx, CHANNEL_ID)
                .setSmallIcon(R.drawable.alert_triangle_svgrepo_com)
                .setContentTitle("Pico de Pressão Plantar detectado!")
                .setContentText(txt)
                .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(bmp).bigLargeIcon(null))
                .setLargeIcon(bmp)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build();
    }

    private void createNotificationChannel(Context ctx) {
        Log.d(TAG, "createNotificationChannel: init");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel chan = new NotificationChannel(CHANNEL_ID, "Alertas de Pressão", NotificationManager.IMPORTANCE_HIGH);
            chan.setDescription("Notificações de pressão plantar");
            NotificationManager nm = ctx.getSystemService(NotificationManager.class);
            if (nm != null) nm.createNotificationChannel(chan);
        }
    }

    private boolean isNetworkAvailable(Context ctx) {
        ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo ni = cm.getActiveNetworkInfo();
            return ni != null && ni.isConnected();
        }
        return false;
    }

    private void runToast(Context ctx, String msg) {
        Handler h = new Handler(Looper.getMainLooper());
        h.post(() -> Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show());
    }

    private static class LoggingCallback implements Callback {
        private final String name;

        LoggingCallback(String name) {
            this.name = name;
        }

        @Override
        public void onFailure(Call call, IOException e) {
            Log.e(TAG, name + " failed", e);
        }

        @Override
        public void onResponse(Call call, Response response) {
            if (!response.isSuccessful()) Log.e(TAG, name + " error: " + response.message());
            else Log.d(TAG, name + " success");
        }
    }
}
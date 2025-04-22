package com.example.myapplication2.Settings;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication2.ConectInsole;
import com.example.myapplication2.ConectInsole2;
import com.example.myapplication2.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.Calendar;


public class ParametersActivity extends AppCompatActivity{

    SeekBar limup, limdown;
    TextView limdownText, limupText;
    String InRight, InLeft;
    FloatingActionButton mBackBtn;
    Button saveChanges;
    Float percentageAdjustLeft, percentageAdjustRight;
    ConectInsole conectInsole;
    ConectInsole2 conectInsole2;
    private Calendar calendar;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parameters);
    }

    @Override
    public void onStart() {
        super.onStart();

        limupText = findViewById(R.id.limupText);
        limdownText = findViewById(R.id.limdownText);
        mBackBtn = findViewById(R.id.buttonback2);
        saveChanges = findViewById(R.id.savechanges);
        limup = findViewById(R.id.limup);
        limdown = findViewById(R.id.limdown);

        sharedPreferences = getSharedPreferences("My_Appinsolesamount", MODE_PRIVATE);
        InRight = sharedPreferences.getString("Sright", "default");
        InLeft = sharedPreferences.getString("Sleft", "default");


        if (InRight.equals("false") && InLeft.equals("true")) {
            limdown.setVisibility(View.GONE);
            limdownText.setVisibility(View.GONE);
            limupText.setText("Limiar: 0%");

            limup.setProgress(0);
            limup.incrementProgressBy(50);
            limup.setMax(200);

            limup.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    limupText.setText("Limiar em:" + String.valueOf(progress));
                    percentageAdjustLeft = (float) (progress/100);

                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });

        }
        if (InLeft.equals("false") && InRight.equals("true")) {
            limdown.setVisibility(View.GONE);
            limdownText.setVisibility(View.GONE);
            limupText.setText("Limiar: 0%");

            limup.setProgress(0);
            limup.incrementProgressBy(50);
            limup.setMax(200);

            limup.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    limupText.setText("Limiar em:" + String.valueOf(progress));
                    percentageAdjustRight = (float) (progress/100);

                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });


        }

        else {

            //configura palmilha direita
            limupText.setText("Limiar palmilha direita: 0%");

            limup.setProgress(0);
            limup.incrementProgressBy(50);
            limup.setMax(200);

            limup.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    limupText.setText("Limiar palmilha direita:" + String.valueOf(progress));
                    percentageAdjustRight = (float) (progress/100);

                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
            //configura palmilha esquerda
            limdownText.setText("Limiar palmilha esquerda: 0%");

            limdown.setProgress(0);
            limdown.incrementProgressBy(50);
            limdown.setMax(200);

            limdown.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    limdownText.setText("Limiar palmilha esquerda:" + String.valueOf(progress));
                    percentageAdjustLeft = (float) (progress/100);

                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });

        }

        saveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(InRight.equals("true")){
                    //recalcular limiar e enviar a palmilha direita
                    loadTreshData(percentageAdjustRight);
                }
                if(InLeft.equals("true")){
                    //recalcular limiar e enviar a palmilha esquerda
                    loadTreshData2(percentageAdjustLeft);

                }


            }
        });

        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }


    private void loadTreshData(Float treshold) {

        calendar = Calendar.getInstance();
        byte hora = (byte) calendar.get(Calendar.HOUR_OF_DAY);
        byte min = (byte) calendar.get(Calendar.MINUTE);
        byte seg = (byte) calendar.get(Calendar.SECOND);
        byte mSeg = (byte) calendar.get(Calendar.MILLISECOND);
        byte cmd = 0x2A;
        byte freq = 1;

        short tnumbers[] = new short[9];
        sharedPreferences = getSharedPreferences("ConfigPrefsR", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("percentageR", String.valueOf(treshold));

        tnumbers[0] = (short) sharedPreferences.getInt("S1", 0);
        tnumbers[1] = (short) sharedPreferences.getInt("S2", 0);
        tnumbers[2] = (short) sharedPreferences.getInt("S3", 0);
        tnumbers[3] = (short) sharedPreferences.getInt("S4", 0);
        tnumbers[4] = (short) sharedPreferences.getInt("S5", 0);
        tnumbers[5] = (short) sharedPreferences.getInt("S6", 0);
        tnumbers[6] = (short) sharedPreferences.getInt("S7", 0);
        tnumbers[7] = (short) sharedPreferences.getInt("S8", 0);
        tnumbers[8] = (short) sharedPreferences.getInt("S9", 0);


        for (int i = 0; i < tnumbers.length; i++) {
            tnumbers[i] = (short) (tnumbers[i]*treshold);
        }
        conectInsole.createAndSendConfigData(cmd, freq, tnumbers[0], tnumbers[1], tnumbers[2], tnumbers[3], tnumbers[4],  tnumbers[5], tnumbers[6], tnumbers[7], tnumbers[8]);
    }

    private void loadTreshData2(Float treshold) {

        calendar = Calendar.getInstance();
        byte hora = (byte) calendar.get(Calendar.HOUR_OF_DAY);
        byte min = (byte) calendar.get(Calendar.MINUTE);
        byte seg = (byte) calendar.get(Calendar.SECOND);
        byte mSeg = (byte) calendar.get(Calendar.MILLISECOND);
        byte cmd = 0x2A;
        byte freq = 1;

        short tnumbers[] = new short[9];
        sharedPreferences = getSharedPreferences("ConfigPrefsL", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("percentageL", String.valueOf(treshold));

        tnumbers[0] = (short) sharedPreferences.getInt("S1", 0);
        tnumbers[1] = (short) sharedPreferences.getInt("S2", 0);
        tnumbers[2] = (short) sharedPreferences.getInt("S3", 0);
        tnumbers[3] = (short) sharedPreferences.getInt("S4", 0);
        tnumbers[4] = (short) sharedPreferences.getInt("S5", 0);
        tnumbers[5] = (short) sharedPreferences.getInt("S6", 0);
        tnumbers[6] = (short) sharedPreferences.getInt("S7", 0);
        tnumbers[7] = (short) sharedPreferences.getInt("S8", 0);
        tnumbers[8] = (short) sharedPreferences.getInt("S9", 0);


        for (int i = 0; i < tnumbers.length; i++) {
            tnumbers[i] = (short) (tnumbers[i]*treshold);
        }
        conectInsole2.createAndSendConfigData(cmd, freq, tnumbers[0], tnumbers[1], tnumbers[2], tnumbers[3], tnumbers[4],  tnumbers[5], tnumbers[6], tnumbers[7], tnumbers[8]);
    }




}
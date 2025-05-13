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
import com.example.myapplication2.Home.HomeActivity;
import com.example.myapplication2.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


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
    List<Short> resultList;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parameters);
    }

    @Override
    public void onStart() {
        super.onStart();

        conectInsole = new ConectInsole(ParametersActivity.this);
        conectInsole2 = new ConectInsole2(ParametersActivity.this);
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
            limupText.setText("Limiar: 0x");

            limup.setProgress(0);
            limup.incrementProgressBy(1);
            limup.setMin(-10);
            limup.setMax(10);

            limup.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    limupText.setText("Limiar em: " + String.valueOf(progress));
                    if(progress>0){
                        percentageAdjustLeft = (float) progress;
                    }
                    else{
                        percentageAdjustLeft = (11+progress)/10f;
                    }

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
            limupText.setText("Limiar: 0x");

            limup.setProgress(0);
            limup.incrementProgressBy(1);
            limup.setMin(-10);
            limup.setMax(10);

            limup.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    limupText.setText("Limiar em: " + String.valueOf(progress));
                    if(progress>0){
                        percentageAdjustRight = (float) progress;
                    }
                    else{
                        percentageAdjustRight = (11+progress)/10f;
                    }
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
            limupText.setText("Limiar palmilha direita: 0x");

            limup.setProgress(0);
            limup.incrementProgressBy(1);
            limup.setMin(-10);
            limup.setMax(10);

            limup.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    limupText.setText("Limiar palmilha direita: " + String.valueOf(progress));
                    if(progress>0){
                        percentageAdjustRight = (float) progress;
                    }
                    else{
                        percentageAdjustRight = (11+progress)/10f;
                    }

                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
            //configura palmilha esquerda
            limdownText.setText("Limiar palmilha esquerda: 0x");

            limdown.setProgress(0);
            limdown.incrementProgressBy(1);
            limdown.setMin(-10);
            limdown.setMax(10);

            limdown.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    limdownText.setText("Limiar palmilha esquerda: " + String.valueOf(progress));
                    if(progress>0){
                        percentageAdjustLeft = (float) progress;
                    }
                    else{
                        percentageAdjustLeft = (11+progress)/10f;
                    }

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

        byte cmd = 0x2A;
        byte freq = 1;

        List<Short> tnumbers = new ArrayList<>();
        SharedPreferences sharedPreferences = getSharedPreferences("ConfigPrefs1", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("percentageR", String.valueOf(treshold));

        tnumbers.add( (short) sharedPreferences.getInt("S1", 0));
        tnumbers.add( (short) sharedPreferences.getInt("S2", 0));
        tnumbers.add( (short) sharedPreferences.getInt("S3", 0));
        tnumbers.add( (short) sharedPreferences.getInt("S4", 0));
        tnumbers.add( (short) sharedPreferences.getInt("S5", 0));
        tnumbers.add( (short) sharedPreferences.getInt("S6", 0));
        tnumbers.add( (short) sharedPreferences.getInt("S7", 0));
        tnumbers.add( (short) sharedPreferences.getInt("S8", 0));
        tnumbers.add( (short) sharedPreferences.getInt("S9", 0));

        resultList = processList(tnumbers, treshold);
        System.out.println("Resultados de limiar" + resultList);
        System.out.println(resultList.get(0));
        System.out.println(resultList.get(1));
        System.out.println(resultList.get(8));
        conectInsole.createAndSendConfigData(cmd, freq, resultList.get(0), resultList.get(1), resultList.get(2), resultList.get(3), resultList.get(4),  resultList.get(5), resultList.get(6), resultList.get(7), resultList.get(8));
    }

    private void loadTreshData2(Float treshold) {

        byte cmd = 0x2A;
        byte freq = 1;

        List<Short> tnumbers = new ArrayList<>();
        sharedPreferences = getSharedPreferences("ConfigPrefs2", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("percentageL", String.valueOf(treshold));

        tnumbers.add( (short) sharedPreferences.getInt("S1", 0));
        tnumbers.add( (short) sharedPreferences.getInt("S2", 0));
        tnumbers.add( (short) sharedPreferences.getInt("S3", 0));
        tnumbers.add( (short) sharedPreferences.getInt("S4", 0));
        tnumbers.add( (short) sharedPreferences.getInt("S5", 0));
        tnumbers.add( (short) sharedPreferences.getInt("S6", 0));
        tnumbers.add( (short) sharedPreferences.getInt("S7", 0));
        tnumbers.add( (short) sharedPreferences.getInt("S8", 0));
        tnumbers.add( (short) sharedPreferences.getInt("S9", 0));

        resultList = processList(tnumbers, treshold);
        conectInsole2.createAndSendConfigData(cmd, freq, resultList.get(0), resultList.get(1), resultList.get(2), resultList.get(3), resultList.get(4),  resultList.get(5), resultList.get(6), resultList.get(7), resultList.get(8));
    }


    public static List<Short> processList(List<Short> inputList, Float treshold) {
            List<Short> resultList = new ArrayList<>();

            // Iterar sobre a lista original
            for (short number : inputList) {
                // Converter o número para hexadecimal
                String hex = Integer.toHexString(Short.toUnsignedInt(number)); // Converte short para int e depois para hex

                // Verificar se o número em hexadecimal começa com "3"
                if (hex.startsWith("3")) {
                    // Remover o prefixo "3" (remover o "3" inicial)
                    String hexWithoutPrefix = hex.substring(1);  // Remove o "3"

                    // Converter o valor restante de volta para int
                    int newValue = Integer.parseInt(hexWithoutPrefix, 16);

                    // Soma 1 ao valor
                    newValue = (int) (newValue*treshold);

                    // Reconstruir o valor com o prefixo "3" de volta
                    String newHex = "3" + Integer.toHexString(newValue);

                    // Converter o valor de volta para short e adicionar à lista resultante
                    resultList.add((short) Integer.parseInt(newHex, 16));
                } else {
                    // Se o número não começar com "3", apenas adicioná-lo à lista sem modificações
                    resultList.add(number);
                }
            }


        return resultList;
    }


}


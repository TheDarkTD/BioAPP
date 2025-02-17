package com.example.myapplication2.Settings;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication2.ConectInsole;
import com.example.myapplication2.FirebaseHelper;
import com.example.myapplication2.Home.HomeActivity;
import com.example.myapplication2.LoginActivity;
import com.example.myapplication2.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;


public class ParametersActivity extends AppCompatActivity{

    SeekBar ajustTresh;
    TextView tresholdajust;
    FloatingActionButton mBackBtn;
    Button saveChanges;
    Float percentageAdjust;
    FirebaseAuth fAuth;
    DatabaseReference databaseReference;
    String uid = null;
    ConectInsole conectInsole;
    private FirebaseHelper firebasehelper;
    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parameters);
    }

    @Override
    public void onStart() {
        super.onStart();

        //gifimagewait.setVisibility(View.GONE);
        ajustTresh = findViewById(R.id.seekBarajust);
        //ajustTresh2 = findViewById(R.id.seekBarajust);

        tresholdajust = findViewById(R.id.limiarajustado);
        mBackBtn = findViewById(R.id.buttonback2);
        saveChanges = findViewById(R.id.savechanges);

        ajustTresh.setProgress(0);
        ajustTresh.incrementProgressBy(50);
        ajustTresh.setMax(200);

        ajustTresh.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tresholdajust.setText("Limiar em:" + String.valueOf(progress));
                percentageAdjust = (float) (progress/100);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        saveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //recalcular limiar, salvar no banco de dados e enviar novos limiares a palmilha

                FirebaseUser user = fAuth.getCurrentUser();
                if (user != null) {
                    uid = user.getUid();  // Pega o UID do usuário autenticado
                    loadTreshData(uid, percentageAdjust);
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


    private void loadTreshData(String uid, Float treshold) {
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(uid);

        databaseReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DataSnapshot dataSnapshot = task.getResult();
                if (dataSnapshot.exists()) {
                    // Recuperar os dados do usuário e ConfigData
                    ConectInsole.ConfigData configData = dataSnapshot.child("config").getValue(ConectInsole.ConfigData.class);
                    int cmd = 0x2A;
                    calendar = Calendar.getInstance();
                    byte hora = (byte) calendar.get(Calendar.HOUR_OF_DAY);
                    byte min = (byte) calendar.get(Calendar.MINUTE);
                    byte seg = (byte) calendar.get(Calendar.SECOND);
                    byte mSeg  = (byte) calendar.get(Calendar.MILLISECOND);;
                    int freq = configData.freq;
                    int S1 = (int) (configData.S1 * treshold);
                    int S2 = (int) (configData.S2*treshold);
                    int S3 = (int) (configData.S3*treshold);
                    int S4 = (int) (configData.S1*treshold);
                    int S5 = (int) (configData.S5*treshold);
                    int S6 = (int) (configData.S6*treshold);
                    int S7 = (int) (configData.S7*treshold);
                    int S8 = (int) (configData.S8*treshold);
                    int S9 = (int) (configData.S9*treshold);

                    conectInsole.createAndSendConfigData( (byte) cmd, hora, min, seg, mSeg, (byte) freq, (short) S1, (short) S2, (short) S3,  (short) S4,  (short) S5,  (short) S6,  (short) S7, (short) S8, (short) S9);

                    configData = new ConectInsole.ConfigData();
                    configData.cmd = cmd;
                    configData.hora = hora;
                    configData.min = min;
                    configData.seg = seg;
                    configData.mSeg = mSeg;
                    configData.freq = freq;
                    configData.S1 = S1;
                    configData.S2 = S2;
                    configData.S3 = S3;
                    configData.S4 = S4;
                    configData.S5 = S5;
                    configData.S6 = S6;
                    configData.S7 = S7;
                    configData.S8 = S8;
                    configData.S9 = S9;

                    firebasehelper.saveConfigData(configData);


                } else {
                    Toast.makeText(ParametersActivity.this, "Nenhum dado encontrado.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(ParametersActivity.this, "Falha ao carregar os dados.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
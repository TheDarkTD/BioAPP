package com.example.myapplication2.Home;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication2.ConectInsole;
import com.example.myapplication2.ConectInsole2;
import com.example.myapplication2.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class Pop extends AppCompatActivity{

    FloatingActionButton mCloseBtn;
    private String followInRight, followInLeft;
    TextView atualiza;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.popupwindow);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);


        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width*.8),(int)(height*.8));

        ConectInsole conectar = new ConectInsole(this);
        ConectInsole2 conectar2 = new ConectInsole2(this);

        SharedPreferences sharedPreferences = getSharedPreferences("My_Appinsolesamount", MODE_PRIVATE);
        followInRight = sharedPreferences.getString("Sright", "default");
        followInLeft = sharedPreferences.getString("Sleft", "default");

        String senddatainsole1=conectar.getSendDataAsString();
        String senddatainsole2=conectar2.getSendDataAsString();

        if (followInRight.equals("true") && followInLeft.equals("false")){
            atualiza.setText(senddatainsole1);

        } else if (followInRight.equals("false") && followInLeft.equals("true")) {
            atualiza.setText(senddatainsole2);

        }else {
            atualiza.setText(senddatainsole1 + senddatainsole2);
        }

        mCloseBtn =(FloatingActionButton) findViewById(R.id.buttonclose);
        mCloseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


}

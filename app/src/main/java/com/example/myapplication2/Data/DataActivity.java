package com.example.myapplication2.Data;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;

import com.example.myapplication2.Connection.ConnectionActivity;
import com.example.myapplication2.Home.HomeActivity;
import com.example.myapplication2.R;
import com.example.myapplication2.Settings.SettingsActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Calendar;


public class DataActivity extends AppCompatActivity {

    Button mExportBtn;
    Button mDocumentBtn;
    DatePickerDialog datePickerDialogInicio, datePickerDialogFim;
    Button mInicio, mFim;



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);

        mInicio = findViewById(R.id.btnInicio);
        mFim = findViewById(R.id.btnFim);
        mDocumentBtn = findViewById(R.id.btndocument);
        mExportBtn = findViewById(R.id.btnexport);

        //seleçao do periodo dos dados
        initDatePickers();

        mInicio.setText(getTodaysDate());
        mFim.setText(getTodaysDate());

        mInicio.setOnClickListener(v -> datePickerDialogInicio.show());
        mFim.setOnClickListener(v -> datePickerDialogFim.show());

        //buscar dados com base no periodo selecionado

        //exportar dados de acordo com o tipo selecionado
        mExportBtn.setOnClickListener(v -> showExportDialog());

        //gerar documento de acordo com o tipo selecionado
        mDocumentBtn.setOnClickListener( v -> showDocumentDialog());

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomnavview4);
        bottomNavigationView.setSelectedItemId(R.id.data);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.home:
                    startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                    finish();
                    return true;
                case R.id.settings:
                    startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                    finish();
                    return true;
                case R.id.connection:
                    startActivity(new Intent(getApplicationContext(), ConnectionActivity.class));
                    finish();
                    return true;
                case R.id.data:
                    return true;
            }
            return false;
        });


    }


    private void showExportDialog() {
        String[] fileTypes = {"PDF", "CSV", "TXT"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Escolha o tipo de arquivo:")
                .setItems(fileTypes, (dialog, which) -> {
                    String selected = fileTypes[which];
                    Toast.makeText(this, "Exportando como: " + selected, Toast.LENGTH_SHORT).show();
                    // Aqui você pode chamar a função para exportar os dados conforme o formato escolhido
                });

        builder.create().show();
    }

    private void showDocumentDialog() {
        String[] reportTypes = {"Resumo", "Detalhado", "Gráficos"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Escolha o tipo de relatório:")
                .setItems(reportTypes, (dialog, which) -> {
                    String selected = reportTypes[which];
                    Toast.makeText(this, "Gerando relatório: " + selected, Toast.LENGTH_SHORT).show();
                    // Aqui você pode chamar a função para gerar o relatório conforme a escolha
                });

        builder.create().show();
    }


    private void initDatePickers() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        datePickerDialogInicio = new DatePickerDialog(this, (view, year1, month1, dayOfMonth) -> {
            month1++;
            String date = makeDateString(dayOfMonth, month1, year1);
            mInicio.setText(date);
            SharedPreferences sharedPreferences = getSharedPreferences("Data_period", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("StartD", day);
            editor.putInt("StartM", month);
            editor.putInt("StartY", year);
        }, year, month, day);



        datePickerDialogFim = new DatePickerDialog(this, (view, year1, month1, dayOfMonth) -> {
            month1++;
            String date = makeDateString(dayOfMonth, month1, year1);
            mFim.setText(date);
            SharedPreferences sharedPreferences = getSharedPreferences("Data_period", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("EndD", day);
            editor.putInt("EndM", month);
            editor.putInt("EndY", year);
        }, year, month, day);
    }

    // Mesmos métodos auxiliares de antes...
    private String getTodaysDate() {
        Calendar cal = Calendar.getInstance();
        return makeDateString(cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.YEAR));
    }

    private String makeDateString(int day, int month, int year) {
        return day + " " + getMonthFormat(month) + " " + year;
    }

    private String getMonthFormat(int month) {
        switch (month) {
            case 1: return "JAN";
            case 2: return "FEB";
            case 3: return "MAR";
            case 4: return "ABR";
            case 5: return "MAI";
            case 6: return "JUN";
            case 7: return "JUL";
            case 8: return "AGO";
            case 9: return "SET";
            case 10: return "OUT";
            case 11: return "NOV";
            case 12: return "DEZ";
            default: return "JAN";
        }
    }
}
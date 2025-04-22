package com.example.myapplication2.Data;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import okhttp3.*;
import org.json.JSONObject;
import java.io.IOException;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myapplication2.ConectInsole;
import com.example.myapplication2.ConectInsole2;
import com.example.myapplication2.Connection.ConnectionActivity;
import com.example.myapplication2.Home.HomeActivity;
import com.example.myapplication2.R;
import com.example.myapplication2.Settings.SettingsActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;


public class DataActivity extends AppCompatActivity {

    Button mExportBtn;
    Button mDocumentBtn;
    DatePickerDialog datePickerDialogInicio, datePickerDialogFim;
    Button mInicio, mFim;
    DatabaseReference ref;
    String dataInicio, dataFim, followInRight, followInLeft;
    String uid;
    FirebaseAuth fAuth;
    DatabaseReference databaseReference;
    private SharedPreferences sharedPreferences;
    private static final OkHttpClient client = new OkHttpClient();


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            uid = user.getUid();  // Pega o UID do usuário logado
            databaseReference = FirebaseDatabase.getInstance("https://bioapp-496ae-default-rtdb.firebaseio.com/")
                    .getReference()
                    .child("Users")
                    .child(uid);  // Salvar dados no nó "Users/{UID}"

            // Inicializando SharedPreferences para armazenar dados localmente
            sharedPreferences = getSharedPreferences("offline_data", Context.MODE_PRIVATE);
        }


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

        mInicio = findViewById(R.id.btnInicio);
        mFim = findViewById(R.id.btnFim);
        mDocumentBtn = findViewById(R.id.btndocument);
        mExportBtn = findViewById(R.id.btnexport);

        //seleçao do periodo desejado para visualizacao dos dados
        initDatePickers();

        mInicio.setText(getTodaysDate());
        mFim.setText(getTodaysDate());

        mInicio.setOnClickListener(v -> datePickerDialogInicio.show());
        mFim.setOnClickListener(v -> datePickerDialogFim.show());

        SharedPreferences sharedPreferences = getSharedPreferences("Data_periodI", MODE_PRIVATE);
        String Iyear = sharedPreferences.getString("StartY", "0000");
        String Imonth = sharedPreferences.getString("StartM", "00");
        String Iday = sharedPreferences.getString("StartD", "00");

        dataInicio = Iyear + "-" + Imonth + "-" + Iday;

        sharedPreferences = getSharedPreferences("Data_periodF", MODE_PRIVATE);
        String Fyear = sharedPreferences.getString("EndY", "0000");
        String Fmonth = sharedPreferences.getString("EndM", "00");
        String Fday = sharedPreferences.getString("EndD", "00");

        dataFim = Fyear + "-" + Fmonth + "-" + Fday;

        //buscar dados com base no periodo selecionado - de acordo com cada palmilha

        sharedPreferences = getSharedPreferences("My_Appinsolesamount", MODE_PRIVATE);
        followInRight = sharedPreferences.getString("Sright", "default");
        followInLeft = sharedPreferences.getString("Sleft", "default");



        //exportar dados de acordo com o tipo selecionado
        mExportBtn.setOnClickListener(v -> showExportDialog(followInLeft, followInRight, dataInicio, dataFim));

        //gerar documento de acordo com o tipo selecionado
        mDocumentBtn.setOnClickListener( v -> showDocumentDialog(followInLeft, followInRight, dataFim, dataInicio));



    }


    private void showExportDialog(String Left, String Right, String inicio, String fim) {
        String[] fileTypes = {"PDF", "CSV", "TXT"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        AlertDialog.Builder builder1 = builder.setTitle("Escolha o tipo de arquivo:")
                .setItems(fileTypes, (dialog, which) -> {
                    String selected = fileTypes[which];

                    // Chama a função correta com base na escolha
                    if (selected.equals("PDF")) {
                        enviarDados("PDF", uid, inicio, fim, Left, Right);
                    } else if (selected.equals("CSV")) {
                        enviarDados("CSV", uid, inicio, fim, Left, Right);
                    } else if (selected.equals("TXT")) {
                        enviarDados("TXT", uid, inicio, fim, Left, Right);
                    }

                    Toast.makeText(this, "Exportando como: " + selected, Toast.LENGTH_SHORT).show();
                });
        builder.create().show();
    }

    private void showDocumentDialog(String Left, String Right, String inicio, String fim) {
        String[] reportTypes = {"Resumo", "Gráficos"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Escolha o tipo de relatório:")
                .setItems(reportTypes, (dialog, which) -> {
                    String selected = reportTypes[which];
                    Toast.makeText(this, "Gerando relatório: " + selected, Toast.LENGTH_SHORT).show();

                    switch (selected) {
                        case "Resumo":
                            enviarDados("resumo", uid, inicio, fim, Left, Right);
                            break;
                        case "Gráficos":
                            enviarDados("grafs", uid, inicio, fim, Left, Right);
                            break;
                    }
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
            SharedPreferences sharedPreferences = getSharedPreferences("Data_periodI", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("StartD", day);
            editor.putInt("StartM", month);
            editor.putInt("StartY", year);
            editor.apply();
        }, year, month, day);



        datePickerDialogFim = new DatePickerDialog(this, (view, year1, month1, dayOfMonth) -> {
            month1++;
            String date = makeDateString(dayOfMonth, month1, year1);
            mFim.setText(date);
            SharedPreferences sharedPreferences = getSharedPreferences("Data_periodF", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("EndD", day);
            editor.putInt("EndM", month);
            editor.putInt("EndY", year);
            editor.apply();
        }, year, month, day);
    }
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
    private void saveToFile(String content, String filename) {
        try {
            File file = new File(getExternalFilesDir(null), filename);
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(content.getBytes());
            fos.close();
            Toast.makeText(this, "Arquivo salvo em: " + file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Erro ao salvar arquivo", Toast.LENGTH_SHORT).show();
        }
    }


    public static void enviarDados(String typeReport,String login, String dataInicio, String dataFim, String Left, String Right) {

        //envia para processar dados palmilha direita
        if (Right.equals("true") && Left.equals("false")) {
            try {
                // Cria o JSON
                JSONObject json = new JSONObject();
                json.put("amount", "R");
                json.put("typereport", typeReport);
                json.put("login", login);
                json.put("datainicio", dataInicio);
                json.put("datafim", dataFim);

                // Corpo da requisição
                RequestBody body = RequestBody.create(
                        json.toString(),
                        MediaType.parse("application/json; charset=utf-8")
                );

                // Endereço da API no Render
                String url = "https://insoleapi.onrender.com";

                // Requisição POST
                Request request = new Request.Builder()
                        .url(url)
                        .post(body)
                        .build();

                // Envia a requisição de forma assíncrona
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                        // Aqui você pode notificar erro na UI
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.isSuccessful()) {
                            String responseBody = response.body().string();
                            System.out.println("Resposta da API: " + responseBody);
                            // Aqui você pode extrair o gráfico base64 do JSON
                        } else {
                            System.err.println("Erro da API: " + response.code());
                        }
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        if (Left.equals("true") && Right.equals("false")) {
            try {
                // Cria o JSON
                JSONObject json = new JSONObject();
                json.put("amount", "L");
                json.put("typereport", typeReport);
                json.put("login", login);
                json.put("datainicio", dataInicio);
                json.put("datafim", dataFim);

                // Corpo da requisição
                RequestBody body = RequestBody.create(
                        json.toString(),
                        MediaType.parse("application/json; charset=utf-8")
                );

                // Endereço da API no Render
                String url = "https://sua-api.onrender.com/processar_dados";

                // Requisição POST
                Request request = new Request.Builder()
                        .url(url)
                        .post(body)
                        .build();

                // Envia a requisição de forma assíncrona
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                        // Aqui você pode notificar erro na UI
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.isSuccessful()) {
                            String responseBody = response.body().string();
                            System.out.println("Resposta da API: " + responseBody);
                            // Aqui você pode extrair o gráfico base64 do JSON
                        } else {
                            System.err.println("Erro da API: " + response.code());
                        }
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        else {
            try {
                // Cria o JSON
                JSONObject json = new JSONObject();
                json.put("amount", "B");
                json.put("typereport", typeReport);
                json.put("login", login);
                json.put("datainicio", dataInicio);
                json.put("datafim", dataFim);

                // Corpo da requisição
                RequestBody body = RequestBody.create(
                        json.toString(),
                        MediaType.parse("application/json; charset=utf-8")
                );

                // Endereço da API no Render
                String url = "https://sua-api.onrender.com/processar_dados";

                // Requisição POST
                Request request = new Request.Builder()
                        .url(url)
                        .post(body)
                        .build();

                // Envia a requisição de forma assíncrona
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                        // Aqui você pode notificar erro na UI
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.isSuccessful()) {
                            String responseBody = response.body().string();
                            System.out.println("Resposta da API: " + responseBody);
                            // Aqui você pode extrair o gráfico base64 do JSON
                        } else {
                            System.err.println("Erro da API: " + response.code());
                        }
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

}
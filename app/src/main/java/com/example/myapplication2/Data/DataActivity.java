package com.example.myapplication2.Data;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Environment;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication2.Connection.ConnectionActivity;
import com.example.myapplication2.Home.HomeActivity;
import com.example.myapplication2.R;
import com.example.myapplication2.Settings.SettingsActivity;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;


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

        /*String[][] selected_dataS;

        //exportar dados de acordo com o tipo selecionado
    private void exportToTXT(String[][] x) {
        mExportBtn.setOnClickListener(v -> showExportDialog(selected_dataS));

        //gerar documento de acordo com o tipo selecionado
        mDocumentBtn.setOnClickListener( v -> showDocumentDialog(selected_dataS));*/

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


    private void showExportDialog(String[][] selected_data) {
        String[] fileTypes = {"PDF", "CSV", "TXT"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        AlertDialog.Builder builder1 = builder.setTitle("Escolha o tipo de arquivo:")
                .setItems(fileTypes, (dialog, which) -> {
                    String selected = fileTypes[which];

                    // Chama a função correta com base na escolha
                    if (selected.equals("PDF")) {
                        //exportToPDF(selected_data); // Certifique-se de que a variável `x` está acessível
                    } else if (selected.equals("CSV")) {
                        //exportToCSV(selected_data);
                    } else if (selected.equals("TXT")) {
                        //exportToTXT(selected_data);
                    }

                    Toast.makeText(this, "Exportando como: " + selected, Toast.LENGTH_SHORT).show();
                });
        builder.create().show();
    }

    private void showDocumentDialog(String[][] dados) {
        String[] reportTypes = {"Resumo", "Detalhado", "Gráficos"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Escolha o tipo de relatório:")
                .setItems(reportTypes, (dialog, which) -> {
                    String selected = reportTypes[which];
                    Toast.makeText(this, "Gerando relatório: " + selected, Toast.LENGTH_SHORT).show();

                    switch (selected) {
                        case "Resumo":
                            //generateSummaryPDF(dados, this); // gera tabela resumida
                            break;
                        case "Detalhado":
                            //generateDetailedPDF(dados, this); // mais informações por item
                            break;
                        case "Gráficos":
                            //generateChartPDF(dados, this); // plota gráficos
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
            SharedPreferences sharedPreferences = getSharedPreferences("Data_period", MODE_PRIVATE);
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
            SharedPreferences sharedPreferences = getSharedPreferences("Data_period", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("EndD", day);
            editor.putInt("EndM", month);
            editor.putInt("EndY", year);
            editor.apply();
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

    private void exportToCSV(String[][] x) {
        StringBuilder data = new StringBuilder();
        data.append("Valor,Tempo\n"); // Cabeçalho

        for (String[] array : x) {
            for (String item : array) {
                String[] partes = item.split(" - ");
                if (partes.length == 2) {
                    data.append(partes[0]).append(",").append(partes[1]).append("\n");
                }
            }
        }

        saveToFile(data.toString(), "export.csv");
    }
    private void exportToTXT(String[][] x) {
        StringBuilder data = new StringBuilder();

        for (int i = 0; i < x.length; i++) {
            data.append("Conjunto ").append(i + 1).append(":\n");
            for (String item : x[i]) {
                data.append(item.replace(" - ", " às ")).append("\n");
            }
            data.append("\n");
        }

        saveToFile(data.toString(), "export.txt");
    }

    /*private void exportToPDF(String[][] x) {
        PdfDocument pdfDoc = new PdfDocument();
        Paint paint = new Paint();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(300, 600, 1).create();
        PdfDocument.Page page = pdfDoc.startPage(pageInfo);
        Canvas canvas = page.getCanvas();

        int xPos = 10;
        int yPos = 25;

        paint.setTextSize(12f);
        canvas.drawText("Valor       Tempo", xPos, yPos, paint);
        yPos += 15;

        for (String[] array : x) {
            for (String item : array) {
                String[] partes = item.split(" - ");
                if (partes.length == 2) {
                    canvas.drawText(partes[0] + "        " + partes[1], xPos, yPos, paint);
                    yPos += 15;
                }
            }
            yPos += 10;
        }

        pdfDoc.finishPage(page);

        File file = new File(getExternalFilesDir(null), "export.pdf");

        try {
            pdfDoc.writeTo(new FileOutputStream(file));
            Toast.makeText(this, "PDF salvo em: " + file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Erro ao salvar PDF", Toast.LENGTH_SHORT).show();
        }

        pdfDoc.close();
    }*/
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

    /*private void generateSummaryPDF(String[][] dados, Context context) {
        PdfDocument pdfDocument = new PdfDocument();
        Paint paint = new Paint();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);
        Canvas canvas = page.getCanvas();

        int x = 40, y = 50;
        paint.setTextSize(14);

        for (int i = 0; i < dados.length; i++) {
            for (int j = 0; j < dados[i].length; j++) {
                canvas.drawText(dados[i][j], x + j * 100, y, paint);
            }
            y += 25;
        }

        pdfDocument.finishPage(page);

        File file = new File(getExternalFilesDir(null), "resumo_dados.pdf");
        try {
            pdfDocument.writeTo(new FileOutputStream(file));
            Toast.makeText(this, "PDF salvo em: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Erro ao salvar PDF", Toast.LENGTH_SHORT).show();
        }

        pdfDocument.close();
    }

    public void generateDetailedPDF(String[][] data, Context context) {
        try {
            File pdfFile = new File(context.getExternalFilesDir(null), "relatorio_detalhado.pdf");
            PdfWriter writer = new PdfWriter(new FileOutputStream(pdfFile));
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            document.add(new Paragraph("Relatório Detalhado").setBold().setFontSize(16));

            if (data.length > 0) {
                int cols = data[0].length;
                Table table = new Table(cols);

                // Adiciona cabeçalhos
                for (int i = 0; i < cols; i++) {
                    table.addCell("Coluna " + (i + 1));
                }

                // Adiciona dados
                for (String[] row : data) {
                    for (String value : row) {
                        table.addCell(value);
                    }
                }

                document.add(table);
            }

            document.close();
            Toast.makeText(context, "PDF gerado em: " + pdfFile.getAbsolutePath(), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Erro ao gerar PDF: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }


    public void generateChartPDF(String[][] data, Context context) {
        try {
            // Simula um gráfico usando os dados
            LineChart chart = new LineChart(context);
            List<Entry> entries = new ArrayList<>();

            for (int i = 0; i < data.length; i++) {
                float x = Float.parseFloat(data[i][0]);
                float y = Float.parseFloat(data[i][1]);
                entries.add(new Entry(x, y));
            }

            LineDataSet dataSet = new LineDataSet(entries, "Dados");
            LineData lineData = new LineData(dataSet);
            chart.setData(lineData);
            chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
            chart.invalidate();

            // Renderizar o gráfico como bitmap
            chart.measure(View.MeasureSpec.makeMeasureSpec(800, View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(600, View.MeasureSpec.EXACTLY));
            chart.layout(0, 0, chart.getMeasuredWidth(), chart.getMeasuredHeight());

            Bitmap chartBitmap = Bitmap.createBitmap(chart.getWidth(), chart.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(chartBitmap);
            chart.draw(canvas);

            // Salvar o gráfico no PDF
            File pdfFile = new File(context.getExternalFilesDir(null), "grafico_dados.pdf");
            PdfWriter writer = new PdfWriter(new FileOutputStream(pdfFile));
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            com.itextpdf.layout.element.Image pdfImage = new com.itextpdf.layout.element.Image(
                    com.itextpdf.io.image.ImageDataFactory.create(chartBitmap));
            document.add(new Paragraph("Gráfico de Dados").setBold().setFontSize(16));
            document.add(pdfImage);

            document.close();
            Toast.makeText(context, "PDF com gráfico gerado em: " + pdfFile.getAbsolutePath(), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Erro ao gerar gráfico PDF: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }*/



}
package com.example.komertzial_aplikazioa;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AgendaActivity extends AppCompatActivity {
    private CalendarView calendarView;
    private RecyclerView rvVisitas;
    private VisitasAdapter visitasAdapter;
    private List<Visita> visitasList;
    private Button btnAgregarVisita;
    private String selectedDate;
    private static DatabaseHelper db;
    private int usuarioId;
    private Button btnesportatu;
    private String erabizena;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agenda);

        db = new DatabaseHelper(this);
        Intent intent = getIntent();

        usuarioId = intent.getIntExtra("user_id", usuarioId);
        erabizena = intent.getStringExtra("user_name");

        if (usuarioId == -1) {
            Toast.makeText(this, "Erabiltzaile ID-a ez da aurkitu", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        visitasList = new ArrayList<>();
        calendarView = findViewById(R.id.calendarView);
        rvVisitas = findViewById(R.id.rvVisitas);
        btnAgregarVisita = findViewById(R.id.btnAgregarVisita);

        // RecyclerView konfiguratu
        rvVisitas.setLayoutManager(new LinearLayoutManager(this));
        visitasAdapter = new VisitasAdapter(visitasList, this::Bisitaezabatu);
        rvVisitas.setAdapter(visitasAdapter);

        selectedDate = getTodayDate();
        Bisitakargatu(selectedDate);

        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            selectedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth);
            Bisitakargatu(selectedDate);
        });

        btnAgregarVisita.setOnClickListener(v -> Bisitagehitu());
        btnesportatu = findViewById(R.id.BtnEsportatu);


        btnesportatu.setOnClickListener(view -> {
            txtesportatu(this);
        });
    }

    // Bisita bat ezabatzen du
    private void Bisitaezabatu(Visita visita) {
        db.BisitaEzabatu(visita.getId());
        Bisitakargatu(selectedDate);
    }

    // Bisita bat gehitzeko laguntzailea irekitzen du
    private void Bisitagehitu() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Bisita berria");

        View viewInflated = LayoutInflater.from(this).inflate(R.layout.dialog_add_visita, null);
        EditText inputTitulo = viewInflated.findViewById(R.id.inputTitulo);
        EditText inputDetalles = viewInflated.findViewById(R.id.inputDetalles);
        builder.setView(viewInflated);

        builder.setPositiveButton("Gorde", (dialog, which) -> {
            String titulo = inputTitulo.getText().toString().trim();
            String detalles = inputDetalles.getText().toString().trim();

            if (!titulo.isEmpty() && !detalles.isEmpty()) {
                db.BisitaGorde(titulo, detalles, selectedDate, usuarioId);
                Bisitakargatu(selectedDate);
            } else {
                Toast.makeText(this, "Datu guztiak sartu behar dira", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Atzera", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    // Iada gordetako bisitak kargatzen ditu
    private void Bisitakargatu(String fecha) {
        visitasList.clear();
        visitasList.addAll(db.BisitakErabiltzaileDataArabera(usuarioId,fecha));
        visitasAdapter.notifyDataSetChanged();
    }

    // Gaurko data lortzen da
    private String getTodayDate() {
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        return String.format(Locale.getDefault(), "%04d-%02d-%02d",
                calendar.get(java.util.Calendar.YEAR),
                calendar.get(java.util.Calendar.MONTH) + 1,
                calendar.get(java.util.Calendar.DAY_OF_MONTH));
    }

    //Hilabeteko bisita guztien Goiburua eta xehetasuna txt batean idazten eta gordetzendu(/storage/emulated/0/Documents/XML-ak/Bidaltzeko/)
    public void txtesportatu(Context context) {
        DatabaseHelper db = new DatabaseHelper(context);
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM", Locale.getDefault());
        String currentMonth = dateFormat.format(calendar.getTime());

        List<Visita> meetings = db.HilabetekoBilerak(currentMonth,usuarioId);
        if (meetings.isEmpty()) {
            new Handler(Looper.getMainLooper()).post(() ->
                    Toast.makeText(context, "Hilabete honetan ez da bilerarik egon", Toast.LENGTH_SHORT).show()
            );
            Log.e("ExportMeetings", "Ez daude exportatzeko bilerak.");
            return;
        }

        File directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "XML-ak/Bidaltzeko");
        if (directory != null && !directory.exists()) {
            boolean dirCreated = directory.mkdirs();
            Log.d("ExportMeetings", "Fitxategia sortu da: " + dirCreated);
        }

        File file = new File(directory, "Bilerak_" + currentMonth +"_"+erabizena+ ".txt");

        try (FileWriter writer = new FileWriter(file)) {
            for (Visita meeting : meetings) {
                writer.write("Data: " + meeting.getDate() + " - Goiburua: " + meeting.getTitulo() + "\n");
            }
            writer.flush();
            Log.d("ExportMeetings", "Fitxategia gordeta: " + file.getAbsolutePath());

            new Handler(Looper.getMainLooper()).post(() ->
                    Toast.makeText(context, "Fitxategia gordeta: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show()
            );

        } catch (IOException e) {
            Log.e("ExportMeetings", "Errorea fitxategia gordetzean", e);
            new Handler(Looper.getMainLooper()).post(() ->
                    Toast.makeText(context, "Errorea fitxategia gordetzean", Toast.LENGTH_SHORT).show()
            );
        }
    }


}

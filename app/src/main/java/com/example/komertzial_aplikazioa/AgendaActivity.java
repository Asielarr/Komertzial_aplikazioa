package com.example.komertzial_aplikazioa;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
            Toast.makeText(this, "ID de usuario no encontrado", Toast.LENGTH_SHORT).show();
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
        db.eliminarVisita(visita.getId());
        Bisitakargatu(selectedDate);
    }

    // Bisita bat gehitzeko laguntzailea irekitzen du
    private void Bisitagehitu() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Nueva Actividad");

        View viewInflated = LayoutInflater.from(this).inflate(R.layout.dialog_add_visita, null);
        EditText inputTitulo = viewInflated.findViewById(R.id.inputTitulo);
        EditText inputDetalles = viewInflated.findViewById(R.id.inputDetalles);
        builder.setView(viewInflated);

        builder.setPositiveButton("Gorde", (dialog, which) -> {
            String titulo = inputTitulo.getText().toString().trim();
            String detalles = inputDetalles.getText().toString().trim();

            if (!titulo.isEmpty() && !detalles.isEmpty()) {
                db.insertarVisita(titulo, detalles, selectedDate, usuarioId);
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
        visitasList.addAll(db.obtenerEventosPorUsuarioYFecha(usuarioId,fecha)); // Cargamos las visitas para un usuario específico y fecha
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

    public void txtesportatu(Context context) {
        DatabaseHelper db = new DatabaseHelper(context); // CORREGIDO: Pasar context
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM", Locale.getDefault());
        String currentMonth = dateFormat.format(calendar.getTime());

        List<Visita> meetings = db.getMeetingsForMonth(currentMonth);
        if (meetings.isEmpty()) {
            new Handler(Looper.getMainLooper()).post(() ->
                    Toast.makeText(context, "No hay reuniones para este mes", Toast.LENGTH_SHORT).show()
            );
            Log.e("ExportMeetings", "No hay reuniones para exportar.");
            return;
        }

        File directory = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        if (directory != null && !directory.exists()) {
            boolean dirCreated = directory.mkdirs();
            Log.d("ExportMeetings", "Fitxategia sortu da: " + dirCreated);
        }

        File file = new File(directory, "Bilerak_" + currentMonth + ".txt");

        try (FileWriter writer = new FileWriter(file)) {
            for (Visita meeting : meetings) {
                writer.write("Data: " + meeting.getDate() + " - Goiburua: " + meeting.getTitulo() + "\n");
            }
            writer.flush();
            Log.d("ExportMeetings", "Archivo guardado en: " + file.getAbsolutePath());

            new Handler(Looper.getMainLooper()).post(() ->
                    Toast.makeText(context, "Archivo guardado en: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show()
            );

        } catch (IOException e) {
            Log.e("ExportMeetings", "Error al guardar el archivo", e);
            new Handler(Looper.getMainLooper()).post(() ->
                    Toast.makeText(context, "Error al guardar el archivo", Toast.LENGTH_SHORT).show()
            );
        }
    }


}

package com.example.komertzial_aplikazioa;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
    private DatabaseHelper db;
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
            try {
                txtesportatu();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
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

    private void txtesportatu() throws IOException {
        // Verificar si el nombre de usuario es válido
        if (erabizena == null || erabizena.isEmpty()) {
            Toast.makeText(this, "Error: Usuario no encontrado.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Verificar si el usuarioID es válido
        if (usuarioId == -1) {
            Toast.makeText(this, "No se encontró el usuario en la base de datos.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Suponiendo que tienes un CalendarView en tu layout
        CalendarView calendarView = findViewById(R.id.calendarView);  // Asegúrate de que el ID del calendario sea correcto
        long fechaSeleccionada = calendarView.getDate();  // Obtener la fecha seleccionada

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(fechaSeleccionada);  // Establecer la fecha seleccionada

        int mesSeleccionadoNumero = calendar.get(Calendar.MONTH) + 1;  // El mes en Calendar empieza desde 0, por eso sumamos 1

        // Convertir el número del mes a formato de dos dígitos
        String mesSeleccionado = String.format("%02d", mesSeleccionadoNumero);


        // Obtener las reuniones del usuario para el mes seleccionado
        List<String> reuniones = db.obtenerReunionesPorUsuarioYMes(usuarioId, mesSeleccionado);

        if (reuniones.isEmpty()) {
            Toast.makeText(this, "No hay reuniones para exportar.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Crear el archivo TXT
        String nombreArchivo = erabizena + "_" + mesSeleccionado + ".txt";
        File directorio = getExternalFilesDir(null);  // Directorio externo, si no existe, usa interno
        if (directorio == null) {
            directorio = getFilesDir();  // Usar almacenamiento interno si el externo no está disponible
        }

        // Crear directorio si no existe
        if (!directorio.exists()) {
            directorio.mkdirs();
        }

        File archivoTXT = new File(directorio, nombreArchivo);

        // Escribir las reuniones en el archivo TXT
        try (FileWriter writer = new FileWriter(archivoTXT)) {
            for (String reunion : reuniones) {
                writer.write(reunion + "\n");
            }
            writer.flush();

            // Mostrar un mensaje de éxito
            Toast.makeText(this, "Archivo guardado en: " + archivoTXT.getAbsolutePath(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            // Si ocurre un error al guardar el archivo, mostrar el error
            Toast.makeText(this, "Error al guardar el archivo: " + e.getMessage(), Toast.LENGTH_LONG).show();
            Log.e("Error", "Error al guardar el archivo", e);
        }
    }


}

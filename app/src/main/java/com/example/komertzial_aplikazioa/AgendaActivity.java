package com.example.komertzial_aplikazioa;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
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
    private int usuarioId; // Para almacenar el ID del usuario que ha iniciado sesión

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agenda);

        // Inicializar base de datos
        db = new DatabaseHelper(this);
        Intent intent = getIntent();

        usuarioId = intent.getIntExtra("user_id", usuarioId);

        if (usuarioId == -1) {
            Toast.makeText(this, "ID de usuario no encontrado", Toast.LENGTH_SHORT).show();
            finish(); // Finalizar si no se tiene un ID de usuario válido
            return;
        }

        // Inicializar UI
        visitasList = new ArrayList<>();
        calendarView = findViewById(R.id.calendarView);
        rvVisitas = findViewById(R.id.rvVisitas);
        btnAgregarVisita = findViewById(R.id.btnAgregarVisita);

        // Configurar RecyclerView
        rvVisitas.setLayoutManager(new LinearLayoutManager(this));
        visitasAdapter = new VisitasAdapter(visitasList, this::eliminarVisita);
        rvVisitas.setAdapter(visitasAdapter);

        // Configurar fecha inicial
        selectedDate = getTodayDate();
        cargarVisitas(selectedDate);

        // Listener para seleccionar fecha
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            selectedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth);
            cargarVisitas(selectedDate);
        });

        // Botón para agregar nueva visita
        btnAgregarVisita.setOnClickListener(v -> mostrarDialogoAgregarVisita());
    }

    // Método para eliminar visita
    private void eliminarVisita(Visita visita) {
        db.eliminarVisita(visita.getId());
        cargarVisitas(selectedDate);
    }

    // Mostrar diálogo para agregar visita
    private void mostrarDialogoAgregarVisita() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Nueva Actividad");

        View viewInflated = LayoutInflater.from(this).inflate(R.layout.dialog_add_visita, null);
        EditText inputTitulo = viewInflated.findViewById(R.id.inputTitulo);
        EditText inputDetalles = viewInflated.findViewById(R.id.inputDetalles);
        builder.setView(viewInflated);

        builder.setPositiveButton("Guardar", (dialog, which) -> {
            String titulo = inputTitulo.getText().toString().trim();
            String detalles = inputDetalles.getText().toString().trim();

            if (!titulo.isEmpty() && !detalles.isEmpty()) {
                db.insertarVisita(titulo, detalles, selectedDate, usuarioId); // Usamos el ID de usuario dinámicamente
                cargarVisitas(selectedDate);
            } else {
                Toast.makeText(this, "Debes llenar ambos campos", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    // Cargar visitas desde SQLite
    private void cargarVisitas(String fecha) {
        visitasList.clear();
        visitasList.addAll(db.obtenerEventosPorUsuarioYFecha(usuarioId,fecha)); // Cargamos las visitas para un usuario específico y fecha
        visitasAdapter.notifyDataSetChanged();
    }

    // Obtener la fecha actual
    private String getTodayDate() {
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        return String.format(Locale.getDefault(), "%04d-%02d-%02d",
                calendar.get(java.util.Calendar.YEAR),
                calendar.get(java.util.Calendar.MONTH) + 1,
                calendar.get(java.util.Calendar.DAY_OF_MONTH));
    }

}

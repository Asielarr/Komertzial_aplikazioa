package com.example.komertzial_aplikazioa;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.komertzial_aplikazioa.Visita;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class AgendaActivity extends AppCompatActivity {

    private CalendarView calendarView;
    private RecyclerView rvVisitas;
    private VisitasAdapter visitasAdapter;
    private List<Visita> visitasList;
    private Button btnAgregarVisita;
    private String selectedDate;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agenda);

        // Inicializar SharedPreferences
        sharedPreferences = getSharedPreferences("AgendaPrefs", Context.MODE_PRIVATE);

        // Inicializar lista de visitas
        visitasList = new ArrayList<>();

        // Inicializar elementos UI
        calendarView = findViewById(R.id.calendarView);
        rvVisitas = findViewById(R.id.rvVisitas);
        btnAgregarVisita = findViewById(R.id.btnAgregarVisita);

        // Configurar RecyclerView
        rvVisitas.setLayoutManager(new LinearLayoutManager(this));
        visitasAdapter = new VisitasAdapter(visitasList, this::eliminarVisita);
        rvVisitas.setAdapter(visitasAdapter);

        // Configurar fecha inicial en el calendario
        selectedDate = getTodayDate();
        loadActivitiesForDate(selectedDate);

        // Listener del CalendarView
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            selectedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth);
            Toast.makeText(AgendaActivity.this, "Fecha seleccionada: " + selectedDate, Toast.LENGTH_SHORT).show();
            loadActivitiesForDate(selectedDate);
        });

        // Listener para agregar una nueva visita
        btnAgregarVisita.setOnClickListener(v -> showAddVisitaDialog());
    }

    // Método para eliminar una visita
    private void eliminarVisita(Visita visita) {
        visitasList.remove(visita);
        saveActivitiesForDate(selectedDate);
        visitasAdapter.notifyDataSetChanged();
    }



    // Método para mostrar un diálogo y agregar una nueva visita
    @SuppressLint("NotifyDataSetChanged")
    private void showAddVisitaDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Nueva Actividad");

        // Inflar layout personalizado
        View viewInflated = LayoutInflater.from(this).inflate(R.layout.dialog_add_visita, null);
        EditText inputTitulo;
        inputTitulo= viewInflated.findViewById(R.id.inputTitulo);
        EditText inputDetalles = viewInflated.findViewById(R.id.inputDetalles);
        builder.setView(viewInflated);

        // Botón de confirmación
        builder.setPositiveButton("Guardar", (dialog, which) -> {
            String titulo = inputTitulo.getText().toString().trim();
            String detalles = inputDetalles.getText().toString().trim();

            if (!titulo.isEmpty() && !detalles.isEmpty()) {
                Visita nuevaVisita = new Visita(titulo, detalles);
                visitasList.add(nuevaVisita);
                saveActivitiesForDate(selectedDate);  // Guardar en SharedPreferences
                visitasAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(AgendaActivity.this, "Debes llenar ambos campos", Toast.LENGTH_SHORT).show();
            }
        });

        // Botón de cancelar
        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    // Método para obtener la fecha actual
    private String getTodayDate() {
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        return String.format(Locale.getDefault(), "%04d-%02d-%02d",
                calendar.get(java.util.Calendar.YEAR),
                calendar.get(java.util.Calendar.MONTH) + 1,
                calendar.get(java.util.Calendar.DAY_OF_MONTH));
    }

    // Cargar actividades guardadas para una fecha específica
    @SuppressLint("NotifyDataSetChanged")
    private void loadActivitiesForDate(String selectedDate) {
        visitasList.clear();
        visitasList.addAll(getActivitiesForDate(selectedDate));
        visitasAdapter.notifyDataSetChanged();
    }

    // Obtener actividades guardadas de SharedPreferences
    private List<Visita> getActivitiesForDate(String selectedDate) {
        List<Visita> visitasForDate = new ArrayList<>();
        Set<String> visitasSet = sharedPreferences.getStringSet(selectedDate, new HashSet<>());

        for (String visitaStr : visitasSet) {
            String[] parts = visitaStr.split("\\|");
            if (parts.length == 2) {
                visitasForDate.add(new Visita(parts[0], parts[1]));
            }
        }

        return visitasForDate;
    }

    // Guardar actividades en SharedPreferences
    private void saveActivitiesForDate(String selectedDate) {
        Set<String> visitasSet = new HashSet<>();
        for (Visita visita : visitasList) {
            visitasSet.add(visita.getTitulo() + "|" + visita.getDetalles());
        }

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet(selectedDate, visitasSet);
        editor.apply();
    }
}

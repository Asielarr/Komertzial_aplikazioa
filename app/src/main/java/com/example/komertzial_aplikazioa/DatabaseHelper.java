package com.example.komertzial_aplikazioa;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "agendaDB";
    private static final int DATABASE_VERSION = 3;

    // Tablas
    private static final String TABLE_ERABILTZAILEA = "erabiltzailea";
    private static final String TABLE_AGENDA = "agenda";

    // Columnas de la tabla erabiltzailea
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NOMBRE = "nombre";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_TELEFONO = "telefono";
    private static final String COLUMN_PASAHITZA = "pasahitza";  // Nueva columna

    // Columnas de la tabla agenda
    private static final String COLUMN_ID_AGENDA = "id";
    private static final String COLUMN_TITULO = "titulo";
    private static final String COLUMN_DETALLES = "detalles";
    private static final String COLUMN_FECHA = "fecha";
    private static final String COLUMN_ERABILTZAILEA_ID = "erabiltzailea_id";


    // Sentencias SQL para crear las tablas
    private static final String CREATE_TABLE_ERABILTZAILEA =
            "CREATE TABLE " + TABLE_ERABILTZAILEA + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_NOMBRE + " TEXT, " +
                    COLUMN_EMAIL + " TEXT, " +
                    COLUMN_TELEFONO + " TEXT, " +
                    COLUMN_PASAHITZA + " TEXT)";  // Nueva columna

    private static final String CREATE_TABLE_AGENDA =
            "CREATE TABLE " + TABLE_AGENDA + " (" +
                    COLUMN_ID_AGENDA + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_TITULO + " TEXT, " +
                    COLUMN_DETALLES + " TEXT, " +
                    COLUMN_FECHA + " TEXT, " +
                    COLUMN_ERABILTZAILEA_ID + " INTEGER, " +
                    "FOREIGN KEY (" + COLUMN_ERABILTZAILEA_ID + ") REFERENCES " +
                    TABLE_ERABILTZAILEA + "(" + COLUMN_ID + "))";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Verificar si las tablas existen antes de crearlas
        verificarTablas(db);

        // Verificar si la tabla 'erabiltzailea' está vacía
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_ERABILTZAILEA, null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();

        // Si no hay usuarios en la tabla, insertar el usuario admin
        if (count == 0) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_NOMBRE, "admin");
            values.put(COLUMN_EMAIL, "admin@admin.com");
            values.put(COLUMN_TELEFONO, "000000000");
            values.put(COLUMN_PASAHITZA, "admin");

            // Insertar el usuario admin en la tabla
            db.insert(TABLE_ERABILTZAILEA, null, values);
        }
    }

    // Este método se llamará para verificar la existencia de las tablas
    private void verificarTablas(SQLiteDatabase db) {
        try {
            // Verificar si la tabla 'erabiltzailea' existe
            Cursor cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='" + TABLE_ERABILTZAILEA + "'", null);
            if (cursor.getCount() == 0) {
                // Si no existe la tabla, crearla
                db.execSQL(CREATE_TABLE_ERABILTZAILEA);
            }
            cursor.close();

            // Verificar si la tabla 'agenda' existe
            cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='" + TABLE_AGENDA + "'", null);
            if (cursor.getCount() == 0) {
                // Si no existe la tabla, crearla
                db.execSQL(CREATE_TABLE_AGENDA);
            }
            cursor.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("Range")
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            // Si la base de datos se está actualizando a una versión mayor
            try {
                Cursor cursor = db.rawQuery("PRAGMA table_info(" + TABLE_ERABILTZAILEA + ")", null);
                boolean columnExists = false;
                while (cursor.moveToNext()) {
                    if (cursor.getString(cursor.getColumnIndex("name")).equals(COLUMN_PASAHITZA)) {
                        columnExists = true;
                        break;
                    }
                }
                cursor.close();

                // Si la columna no existe, agregarla
                if (!columnExists) {
                    // Agregar la nueva columna 'pasahitza' en la tabla existente
                    db.execSQL("ALTER TABLE " + TABLE_ERABILTZAILEA + " ADD COLUMN " + COLUMN_PASAHITZA + " TEXT");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Otros métodos como insertar, obtener eventos, etc. seguirán como estaban, pero recuerda no cerrar las conexiones aquí.
    public List<Visita> obtenerEventosPorUsuarioYFecha(int usuarioId, String fecha) {
        List<Visita> visitas = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Consulta para obtener las visitas de un usuario específico en una fecha determinada
        Cursor cursor = db.query(TABLE_AGENDA, null,
                COLUMN_ERABILTZAILEA_ID + " = ? AND " + COLUMN_FECHA + " = ?",
                new String[]{String.valueOf(usuarioId), fecha},
                null, null, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID_AGENDA));
                @SuppressLint("Range") String title = cursor.getString(cursor.getColumnIndex(COLUMN_TITULO));
                @SuppressLint("Range") String details = cursor.getString(cursor.getColumnIndex(COLUMN_DETALLES));
                Visita visita = new Visita(id, title, details, usuarioId);
                visitas.add(visita);
            }
        }

        return visitas;
    }

    // Método para insertar un nuevo evento en la tabla agenda
    public long insertarVisita(String titulo, String detalles, String fecha, int idUsuario) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITULO, titulo);
        values.put(COLUMN_DETALLES, detalles);
        values.put(COLUMN_FECHA, fecha);
        values.put(COLUMN_ERABILTZAILEA_ID, idUsuario);

        long result = db.insert(TABLE_AGENDA, null, values);
        return result;
    }
    // Método para obtener un usuario por nombre y contraseña
    public Cursor obtenerUsuarioPorNombreYPass(String nombre, String pasahitza) {
        SQLiteDatabase db = this.getReadableDatabase();

        // Consulta SQL para obtener un usuario basado en su nombre y contraseña
        String query = "SELECT * FROM " + TABLE_ERABILTZAILEA + " WHERE " +
                COLUMN_NOMBRE + " = ? AND " + COLUMN_PASAHITZA + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{nombre, pasahitza});

        // Devolver el cursor con los resultados
        return cursor;
    }
    public void eliminarVisita(int visitaId) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Eliminamos la visita de la tabla agenda usando el ID
        db.delete(TABLE_AGENDA, COLUMN_ID_AGENDA + " = ?", new String[]{String.valueOf(visitaId)});

    }
    @SuppressLint("Range")
    public int obtenerUserIdPorNombre(String nombreUsuario) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT " + DatabaseHelper.COLUMN_ID + " FROM " + DatabaseHelper.TABLE_ERABILTZAILEA +
                " WHERE " + DatabaseHelper.COLUMN_NOMBRE + " = ?", new String[]{nombreUsuario});

        int userId = -1;  // Valor por defecto si no encontramos el usuario
        if (cursor != null && cursor.moveToFirst()) {
            userId = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID));  // Recuperamos el ID
            cursor.close();
        }

        return userId;
    }

}

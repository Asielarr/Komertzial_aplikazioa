package com.example.komertzial_aplikazioa;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "komertzial_aplikazioa"; // Nuevo nombre de la base de datos
    private static final int DATABASE_VERSION = 4; // Incrementado para reflejar cambios en la estructura

    // Tablas
    public static final String TABLE_KOMERTZIALAK = "komertzialak"; // Nuevo nombre de la tabla
    public static final String TABLE_AGENDA = "agenda";

    // Columnas de la tabla komertzialak
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NOMBRE = "nombre";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_TELEFONO = "telefono";
    public static final String COLUMN_PASAHITZA = "pasahitza"; // Nueva columna agregada anteriormente
    public static final String COLUMN_EREMUA = "eremua"; // Nueva columna agregada

    // Columnas de la tabla agenda
    public static final String COLUMN_ID_AGENDA = "id";
    public static final String COLUMN_TITULO = "titulo";
    public static final String COLUMN_DETALLES = "detalles";
    public static final String COLUMN_FECHA = "fecha";
    public static final String COLUMN_KOMERTZIAL_ID = "komertzial_id"; // Nuevo nombre de la columna de clave foránea

    // Sentencias SQL para crear las tablas
    private static final String CREATE_TABLE_KOMERTZIALAK =
            "CREATE TABLE " + TABLE_KOMERTZIALAK + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_NOMBRE + " TEXT, " +
                    COLUMN_EMAIL + " TEXT, " +
                    COLUMN_TELEFONO + " TEXT, " +
                    COLUMN_PASAHITZA + " TEXT, " +
                    COLUMN_EREMUA + " TEXT)"; // Nueva columna

    private static final String CREATE_TABLE_AGENDA =
            "CREATE TABLE " + TABLE_AGENDA + " (" +
                    COLUMN_ID_AGENDA + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_TITULO + " TEXT, " +
                    COLUMN_DETALLES + " TEXT, " +
                    COLUMN_FECHA + " TEXT, " +
                    COLUMN_KOMERTZIAL_ID + " INTEGER, " +
                    "FOREIGN KEY (" + COLUMN_KOMERTZIAL_ID + ") REFERENCES " +
                    TABLE_KOMERTZIALAK + "(" + COLUMN_ID + "))";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_KOMERTZIALAK);
        db.execSQL(CREATE_TABLE_AGENDA);

        // Insertar el usuario admin si la tabla está vacía
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_KOMERTZIALAK, null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();

        if (count == 0) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_NOMBRE, "admin");
            values.put(COLUMN_EMAIL, "admin@admin.com");
            values.put(COLUMN_TELEFONO, "000000000");
            values.put(COLUMN_PASAHITZA, "admin");
            values.put(COLUMN_EREMUA, "administracion"); // Valor por defecto

            db.insert(TABLE_KOMERTZIALAK, null, values);
        }
    }

    @SuppressLint("Range")
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 4) { // Verifica si la base de datos debe actualizarse a la nueva versión
            try {
                // Agregar la nueva columna 'eremua' si no existe
                Cursor cursor = db.rawQuery("PRAGMA table_info(" + TABLE_KOMERTZIALAK + ")", null);
                boolean columnExists = false;
                while (cursor.moveToNext()) {
                    if (cursor.getString(cursor.getColumnIndex("name")).equals(COLUMN_EREMUA)) {
                        columnExists = true;
                        break;
                    }
                }
                cursor.close();

                if (!columnExists) {
                    db.execSQL("ALTER TABLE " + TABLE_KOMERTZIALAK + " ADD COLUMN " + COLUMN_EREMUA + " TEXT");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Obtener el ID del usuario por nombre
    @SuppressLint("Range")
    public int obtenerUserIdPorNombre(String nombreUsuario) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_ID + " FROM " + TABLE_KOMERTZIALAK +
                " WHERE " + COLUMN_NOMBRE + " = ?", new String[]{nombreUsuario});

        int userId = -1;
        if (cursor != null && cursor.moveToFirst()) {
            userId = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
            cursor.close();
        }
        return userId;
    }

    // Método para insertar una nueva visita en la agenda
    public long insertarVisita(String titulo, String detalles, String fecha, int idUsuario) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITULO, titulo);
        values.put(COLUMN_DETALLES, detalles);
        values.put(COLUMN_FECHA, fecha);
        values.put(COLUMN_KOMERTZIAL_ID, idUsuario);

        return db.insert(TABLE_AGENDA, null, values);
    }

    // Método para obtener eventos por usuario y fecha
    public List<Visita> obtenerEventosPorUsuarioYFecha(int usuarioId, String fecha) {
        List<Visita> visitas = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_AGENDA, null,
                COLUMN_KOMERTZIAL_ID + " = ? AND " + COLUMN_FECHA + " = ?",
                new String[]{String.valueOf(usuarioId), fecha},
                null, null, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID_AGENDA));
                @SuppressLint("Range") String title = cursor.getString(cursor.getColumnIndex(COLUMN_TITULO));
                @SuppressLint("Range") String details = cursor.getString(cursor.getColumnIndex(COLUMN_DETALLES));
                visitas.add(new Visita(id, title, details, usuarioId));
            }
            cursor.close();
        }

        return visitas;
    }

    // Método para obtener un usuario por nombre y contraseña
    public Cursor obtenerUsuarioPorNombreYPass(String nombre, String pasahitza) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_KOMERTZIALAK + " WHERE " +
                COLUMN_NOMBRE + " = ? AND " + COLUMN_PASAHITZA + " = ?", new String[]{nombre, pasahitza});
    }

    // Método para eliminar una visita
    public void eliminarVisita(int visitaId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_AGENDA, COLUMN_ID_AGENDA + " = ?", new String[]{String.valueOf(visitaId)});
    }
    // Método para insertar un nuevo komertzial
    public long insertarKomertzial(String nombre, String email, String telefono, String pasahitza, String eremua) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NOMBRE, nombre);
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_TELEFONO, telefono);
        values.put(COLUMN_PASAHITZA, pasahitza);
        values.put(COLUMN_EREMUA, eremua);

        return db.insert(TABLE_KOMERTZIALAK, null, values);
    }

    // Método para actualizar un komertzial existente
    public int actualizarKomertzial(int id, String email, String telefono, String pasahitza, String eremua) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_TELEFONO, telefono);
        values.put(COLUMN_PASAHITZA, pasahitza);
        values.put(COLUMN_EREMUA, eremua);

        return db.update(TABLE_KOMERTZIALAK, values, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
    }

    public List<String> obtenerReunionesPorUsuarioYMes(int userId, String mes) {
        List<String> reuniones = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();

        // Asegúrate de que el mes está en formato 2 dígitos, por ejemplo: "01" para enero
        String mesFormatoDosDigitos = String.format("%02d", Integer.parseInt(mes));

        // Log para depuración
        Log.d("ReunionesManager", "Mes solicitado: " + mes + " convertido a: " + mesFormatoDosDigitos);

        // Consulta SQL para obtener las reuniones
        String query = "SELECT titulo, detalles, fecha FROM " + DatabaseHelper.TABLE_AGENDA +
                " WHERE erabiltzailea_id = ? AND strftime('%m', fecha) = ?";

        // Log de la consulta que se va a ejecutar
        Log.d("ReunionesManager", "Consulta SQL: " + query + " con parámetros: " + userId + ", " + mesFormatoDosDigitos);

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId), mesFormatoDosDigitos});

        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") String titulo = cursor.getString(cursor.getColumnIndex("titulo"));
                @SuppressLint("Range") String detalles = cursor.getString(cursor.getColumnIndex("detalles"));
                @SuppressLint("Range") String fecha = cursor.getString(cursor.getColumnIndex("fecha"));

                // Log para verificar que se encontró una reunión
                Log.d("ReunionesManager", "Reunión encontrada: " + fecha + " | " + titulo + " - " + detalles);

                reuniones.add(fecha + " | " + titulo + " - " + detalles);
            } while (cursor.moveToNext());
        } else {
            // Log si no se encuentran reuniones para el usuario y mes especificados
            Log.d("ReunionesManager", "No se encontraron reuniones para el usuario con ID: " + userId + " en el mes: " + mes);
        }

        cursor.close();
        return reuniones;
    }


    // Método auxiliar para convertir el nombre del mes a su número correspondiente (01-12)
    private int convertirMesANumero(String mes) {
        switch (mes.toLowerCase(Locale.getDefault())) {
            case "enero": return 1;
            case "febrero": return 2;
            case "marzo": return 3;
            case "abril": return 4;
            case "mayo": return 5;
            case "junio": return 6;
            case "julio": return 7;
            case "agosto": return 8;
            case "septiembre": return 9;
            case "octubre": return 10;
            case "noviembre": return 11;
            case "diciembre": return 12;
            default: return -1; // Si el mes no es válido
        }
    }



}

package com.example.komertzial_aplikazioa;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "komertzial_aplikazioa"; //Datu base izena
    private static final int DATABASE_VERSION = 5;

    // Taulak
    public static final String TABLE_KOMERTZIALAK = "komertzialak";
    public static final String TABLE_AGENDA = "agenda";

    // Komertzial taularen zutabeak
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NOMBRE = "nombre";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_TELEFONO = "telefono";
    public static final String COLUMN_PASAHITZA = "pasahitza";
    public static final String COLUMN_EREMUA = "eremua";

    // Agenda taularen zutabeak
    public static final String COLUMN_ID_AGENDA = "id";
    public static final String COLUMN_TITULO = "titulo";
    public static final String COLUMN_DETALLES = "detalles";
    public static final String COLUMN_FECHA = "fecha";
    public static final String COLUMN_KOMERTZIAL_ID = "komertzial_id";

    //Partner taularen zutabeak
    public static final String TABLE_PARTNER = "partnerra";
    public static final String COLUMN_PARTNER_ID = "Partner_id";
    public static final String COLUMN_NOMBRE_PART = "Nombre";
    public static final String COLUMN_DIRECCION = "Direccion";
    public static final String COLUMN_TELEFONO_PART = "Telefono";
    public static final String COLUMN_ESTADO = "Estado";
    public static final String COLUMN_ID_COMERCIAL = "Id_Comercial";

    //Taulak sortzeko aginduak
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

    String CREATE_TABLE_PARTNER = "CREATE TABLE " + TABLE_PARTNER + " (" +
            COLUMN_PARTNER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_NOMBRE_PART + " TEXT NOT NULL, " +
            COLUMN_DIRECCION + " TEXT NOT NULL, " +
            COLUMN_TELEFONO_PART + " TEXT NOT NULL, " +
            COLUMN_ESTADO + " INTEGER NOT NULL, " + // 0 = No es partner, 1 = Es partner
            COLUMN_ID_COMERCIAL + " INTEGER, " +
            "FOREIGN KEY (" + COLUMN_ID_COMERCIAL + ") REFERENCES komertzialak(id));";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //Aplikazioa abiaraztean taulak ez badaude sortuta, sortu egiten ditu
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_KOMERTZIALAK);
        db.execSQL(CREATE_TABLE_AGENDA);
        db.execSQL(CREATE_TABLE_PARTNER);

        // Taula sortzean, komertzial bat sortzen du admin izenekoa
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_KOMERTZIALAK, null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();

        if (count == 0) {
            ContentValues comercialValues = new ContentValues();
            comercialValues.put(COLUMN_NOMBRE, "admin");
            comercialValues.put(COLUMN_EMAIL, "admin@admin.com");
            comercialValues.put(COLUMN_TELEFONO, "000000000");
            comercialValues.put(COLUMN_PASAHITZA, "admin");
            comercialValues.put(COLUMN_EREMUA, "administracion"); // Valor por defecto

            long comercialId = db.insert(TABLE_KOMERTZIALAK, null, comercialValues);

            // Partner bat gehitzen du
            ContentValues partnerValues = new ContentValues();
            partnerValues.put(COLUMN_NOMBRE, "Partner de prueba");
            partnerValues.put(COLUMN_DIRECCION, "Calle Ficticia 123");
            partnerValues.put(COLUMN_TELEFONO, "123456789");
            partnerValues.put(COLUMN_ESTADO, 1);
            partnerValues.put(COLUMN_ID_COMERCIAL, comercialId);

            db.insert(TABLE_PARTNER, null, partnerValues);
        }
    }


    //Datu basea bertsio zaharra badu, hau eguneratzen du
    @SuppressLint("Range")
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 5) {
            try {

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

    // Erabiltzailearen ID-a lortzen du izenaren bidez
    @SuppressLint("Range")
    public int ErabiltzaileIDlortu(String nombreUsuario) {
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

    // Bisita bat gordetzen du
    public long BisitaGorde(String titulo, String detalles, String fecha, int idUsuario) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITULO, titulo);
        values.put(COLUMN_DETALLES, detalles);
        values.put(COLUMN_FECHA, fecha);
        values.put(COLUMN_KOMERTZIAL_ID, idUsuario);

        return db.insert(TABLE_AGENDA, null, values);
    }

    // Bisitak lortzen ditu erabiltzaile eta data arabera
    public List<Visita> BisitakErabiltzaileDataArabera(int usuarioId, String fecha) {
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

    // Login-a egiteko kontsulta
    public Cursor LoginKontsulta(String nombre, String pasahitza) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_KOMERTZIALAK + " WHERE " +
                COLUMN_NOMBRE + " = ? AND " + COLUMN_PASAHITZA + " = ?", new String[]{nombre, pasahitza});
    }

    // Bisita bat ezabatzeko kontsulta
    public void BisitaEzabatu(int visitaId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_AGENDA, COLUMN_ID_AGENDA + " = ?", new String[]{String.valueOf(visitaId)});
    }
    // Komertzial berri bat gordetzen du
    public long KomertzialaGehitu(String nombre, String email, String telefono, String pasahitza, String eremua) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NOMBRE, nombre);
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_TELEFONO, telefono);
        values.put(COLUMN_PASAHITZA, pasahitza);
        values.put(COLUMN_EREMUA, eremua);

        return db.insert(TABLE_KOMERTZIALAK, null, values);
    }

    // Komertzial baten datuak eguneratzen ditu
    public int KomertzialaEguneratu(int id, String email, String telefono, String pasahitza, String eremua) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_TELEFONO, telefono);
        values.put(COLUMN_PASAHITZA, pasahitza);
        values.put(COLUMN_EREMUA, eremua);

        return db.update(TABLE_KOMERTZIALAK, values, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
    }

    //Hilabete osoko bisitak lortzen ditu logeatuta dagoen erabiltzailearen arabera
    public List<Visita> HilabetekoBilerak(String month, int idUsuario) {
        List<Visita> meetings = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT fecha, titulo FROM agenda WHERE fecha LIKE ? AND komertzial_id = ?",
                new String[]{month + "%", String.valueOf(idUsuario)}
        );

        if (cursor.moveToFirst()) {
            do {
                String date = cursor.getString(0);
                String title = cursor.getString(1);
                meetings.add(new Visita(date, title));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return meetings;
    }


    //Partner guztien zerrenda bat itzultzen du
    public List<Partner> PartnerrakLortu() {
        List<Partner> partnerList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT p.Partner_id, p.Nombre, p.Direccion, p.Telefono, p.Estado, k.Nombre AS NombreComercial " +
                "FROM " + TABLE_PARTNER + " p " +
                "LEFT JOIN " + TABLE_KOMERTZIALAK + " k " +
                "ON p.Id_Comercial = k.id";

        Cursor cursor = db.rawQuery(query, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    @SuppressLint("Range") int partnerId = cursor.getInt(cursor.getColumnIndex(COLUMN_PARTNER_ID));
                    @SuppressLint("Range") String nombre = cursor.getString(cursor.getColumnIndex(COLUMN_NOMBRE_PART));
                    @SuppressLint("Range") String direccion = cursor.getString(cursor.getColumnIndex(COLUMN_DIRECCION));
                    @SuppressLint("Range") String telefono = cursor.getString(cursor.getColumnIndex(COLUMN_TELEFONO_PART));
                    @SuppressLint("Range") int estado = cursor.getInt(cursor.getColumnIndex(COLUMN_ESTADO));
                    @SuppressLint("Range") String nombreComercial = cursor.getString(cursor.getColumnIndex("NombreComercial")); // Obtener nombre del comercial

                    Partner partner = new Partner(partnerId, nombre, direccion, telefono, estado, nombreComercial);
                    partnerList.add(partner);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        db.close();
        return partnerList;
    }

    //Partner berria gehitzen du
    public void PartnerraGehitu(String nombre, String direccion, String telefono, int estado, int idComercial) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("Nombre", nombre);
        values.put("Direccion", direccion);
        values.put("Telefono", telefono);
        values.put("Estado", estado);
        values.put("Id_Comercial", idComercial); // Se usa el ID del usuario logueado

        db.insert(TABLE_PARTNER, null, values);
        db.close();
    }

    //Partner bat ezabatzen du
    public void PartnerEzabatu(int partnerId, Context context) {
        SQLiteDatabase db = this.getWritableDatabase();

        int rowsDeleted = db.delete(TABLE_PARTNER, "Partner_id = ?", new String[]{String.valueOf(partnerId)});
        db.close();

        if (rowsDeleted > 0) {
            Toast.makeText(context, "Partner-a ezabatu da", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Errorea: Ez da aurkitu partner-ik ID horrekin", Toast.LENGTH_SHORT).show();
        }

        if (context instanceof Activity) {
            ((Activity) context).finish();
        }
    }

    //Jasotako datuekin,iada dauden partnerren datuak eguneratzen ditu(ID berdina badu) edo berri bat gehitzen du
    public void PartnerEguneratuSortu(int partnerId, String nombre, String direccion, String telefono, int estado, int idComercial) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        //Partner ID autoincrement, beraz ez da gehitzen
        values.put(COLUMN_NOMBRE_PART, nombre);
        values.put(COLUMN_DIRECCION, direccion);
        values.put(COLUMN_TELEFONO_PART, telefono);
        values.put(COLUMN_ESTADO, estado);
        values.put(COLUMN_ID_COMERCIAL, idComercial);

        int rowsUpdated = db.update(TABLE_PARTNER, values, COLUMN_PARTNER_ID + " = ?", new String[]{String.valueOf(partnerId)});

        if (rowsUpdated == 0) {
            db.insert(TABLE_PARTNER, null, values);
        }

        db.close();
    }

    //Partnerraren izena lortzen da bere ID-aren bitartez
    public Partner PartnerLortuIDbidez(int partnerId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_PARTNER, null, COLUMN_PARTNER_ID + " = ?", new String[]{String.valueOf(partnerId)}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            @SuppressLint("Range") Partner partner = new Partner(
                    cursor.getInt(cursor.getColumnIndex(COLUMN_PARTNER_ID)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_NOMBRE_PART)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_DIRECCION)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_TELEFONO_PART)),
                    cursor.getInt(cursor.getColumnIndex(COLUMN_ESTADO)),
                    cursor.getInt(cursor.getColumnIndex(COLUMN_ID_COMERCIAL))
            );
            cursor.close();
            return partner;
        } else {
            return null;
        }
    }

    //Partner-aren datuak eguneratzen ditu
    public void updatePartner(int partnerId, String nombre, String direccion, String telefono, int estado, int idComercial) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_NOMBRE_PART, nombre);
        values.put(COLUMN_DIRECCION, direccion);
        values.put(COLUMN_TELEFONO_PART, telefono);
        values.put(COLUMN_ESTADO, estado);
        values.put(COLUMN_ID_COMERCIAL, idComercial);

        db.update(TABLE_PARTNER, values, COLUMN_PARTNER_ID + " = ?", new String[]{String.valueOf(partnerId)});
        db.close();
    }









}

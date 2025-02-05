package com.example.komertzial_aplikazioa;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class xml_inportatu extends AppCompatActivity {
    private static final int PICK_XML_FILE = 1;
    private DatabaseHelper databaseHelper;
    private Button btnSeleccionarXML;
    private ListView listaKomertzialak;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xml_inportatu);

        databaseHelper = new DatabaseHelper(this);
        btnSeleccionarXML = findViewById(R.id.btnSeleccionarXML);
        listaKomertzialak = findViewById(R.id.listaKomertzialak);

        btnSeleccionarXML.setOnClickListener(view -> XMLAukeratu());

        cargarKomertzialak();
    }

    // XML-a aukeratzeko lehioa ireki
    private void XMLAukeratu() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("text/xml");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intent, "Aukeratu XML-a"), PICK_XML_FILE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_XML_FILE && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                XMLirakurri(uri);
            }
        }
    }

    // Irekitako XML fitxategia irakurri
    private void XMLirakurri(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            if (inputStream == null) {
                Toast.makeText(this, "No se pudo abrir el archivo", Toast.LENGTH_SHORT).show();
                return;
            }

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(inputStream);
            document.getDocumentElement().normalize();

            NodeList nodeList = document.getElementsByTagName("komertzial");

            int count = 0;
            for (int i = 0; i < nodeList.getLength(); i++) {
                Element element = (Element) nodeList.item(i);

                String nombre = element.getElementsByTagName("nombre").item(0).getTextContent();
                String email = element.getElementsByTagName("email").item(0).getTextContent();
                String telefono = element.getElementsByTagName("telefono").item(0).getTextContent();
                String pasahitza = element.getElementsByTagName("pasahitza").item(0).getTextContent();
                String eremua = element.getElementsByTagName("eremua").item(0).getTextContent();

                KomertzialaSortuEguneratu(nombre, email, telefono, pasahitza, eremua);
                count++;
            }

            Toast.makeText(this, count + "Erregistroak eguneratu/gehitu dira.", Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            Log.e("XMLImport", "Errorea irakurtzean", e);
            Toast.makeText(this, "Errorea irakurtzean", Toast.LENGTH_SHORT).show();
        }
    }

    // Erregistro aldatuak edo berriak sortzen ditu
    private void KomertzialaSortuEguneratu(String nombre, String email, String telefono, String pasahitza, String eremua) {
        int userId = databaseHelper.ErabiltzaileIDlortu(nombre);

        if (userId == -1) {
            databaseHelper.KomertzialaGehitu(nombre, email, telefono, pasahitza, eremua);
            Log.d("XMLImport", "Berria gehitua: " + nombre);
        } else {
            // Actualizar existente
            databaseHelper.KomertzialaEguneratu(userId, email, telefono, pasahitza, eremua);
            Log.d("XMLImport", "Eguneratua: " + nombre);
        }
    }
    private void cargarKomertzialak() {
        List<String> komertzialakList = new ArrayList<>();
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_KOMERTZIALAK, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                @SuppressLint("Range") String nombre = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_NOMBRE));
                @SuppressLint("Range") String email = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_EMAIL));
                @SuppressLint("Range") String telefono = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_TELEFONO));
                @SuppressLint("Range") String eremua = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_EREMUA));

                komertzialakList.add(nombre + " | " + email + " | " + telefono + " | " + eremua);
            }
            cursor.close();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, komertzialakList);
        listaKomertzialak.setAdapter(adapter);
    }
}

package com.example.komertzial_aplikazioa;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import android.widget.Toast;

import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class PartnerActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private PartnerAdapter adapter;
    private List<Partner> partnerList;
    private DatabaseHelper dbHelper;
    int usuarioId = 0;
    private Button btnAlta;
    private Button BtnBaja;
    private Button Btninportatu;
    private static final int PICK_XML_FILE = 1;
    private EditText etPartnerId, etNombre, etDireccion, etTelefono, etEstado, etIdComercial;
    private Button btnActualizar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_partner);

        dbHelper = new DatabaseHelper(this);
        recyclerView = findViewById(R.id.recypartner);
        btnAlta = findViewById(R.id.BtnAlta);
        BtnBaja = findViewById(R.id.BtnBaja);
        Btninportatu = findViewById(R.id.BtnInportatu);

        Intent intent = getIntent();
        usuarioId = intent.getIntExtra("user_id", usuarioId);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        loadPartners();

        btnAlta.setOnClickListener(v -> showAddPartnerDialog());

        BtnBaja.setOnClickListener(v -> {
            // Crear un AlertDialog para pedir el ID del partner a eliminar
            AlertDialog.Builder builder = new AlertDialog.Builder(PartnerActivity.this);
            builder.setTitle("Eliminar Partner");
            builder.setMessage("Introduce el ID del partner que deseas eliminar:");

            // EditText para que el usuario ingrese el ID
            final EditText input = new EditText(PartnerActivity.this);
            input.setInputType(InputType.TYPE_CLASS_NUMBER);
            builder.setView(input);

            // Botón "Aceptar"
            builder.setPositiveButton("Aceptar", (dialog, which) -> {
                String partnerIdStr = input.getText().toString().trim();

                // Validar que el ID no esté vacío
                if (partnerIdStr.isEmpty()) {
                    Toast.makeText(PartnerActivity.this, "Debes ingresar un ID", Toast.LENGTH_SHORT).show();
                    return;
                }

                int partnerId = Integer.parseInt(partnerIdStr);

                // Confirmación final antes de eliminar
                new AlertDialog.Builder(PartnerActivity.this)
                        .setTitle("Confirmación")
                        .setMessage("¿Seguro que quieres eliminar el partner con ID " + partnerId + "?")
                        .setPositiveButton("Sí", (confirmDialog, whichConfirm) -> {
                            // Crear un objeto de DatabaseHelper
                            DatabaseHelper dbHelper = new DatabaseHelper(PartnerActivity.this);

                            // Llamar al método eliminarPartner y pasarle el contexto
                            dbHelper.eliminarPartner(partnerId, PartnerActivity.this);
                        })
                        .setNegativeButton("No", null)
                        .show();
            });

            // Botón "Cancelar"
            builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());

            // Mostrar el diálogo
            builder.show();
        });

        Button btnImportar = findViewById(R.id.BtnInportatu);
        btnImportar.setOnClickListener(v -> XMLAukeratu());

        // Referencias a los EditText y Button
        btnActualizar = findViewById(R.id.BtnAldaketa);

        // Al hacer click en el botón de actualización
        btnActualizar.setOnClickListener(v -> showPartnerIdDialog());


    }

    private void loadPartners() {
        partnerList = dbHelper.getAllPartners();
        adapter = new PartnerAdapter(partnerList);
        recyclerView.setAdapter(adapter);
    }

    private void showAddPartnerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Añadir nuevo Partner");

        // Layout para el diálogo
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 20, 50, 20);

        final EditText inputNombre = new EditText(this);
        inputNombre.setHint("Nombre");
        layout.addView(inputNombre);

        final EditText inputDireccion = new EditText(this);
        inputDireccion.setHint("Dirección");
        layout.addView(inputDireccion);

        final EditText inputTelefono = new EditText(this);
        inputTelefono.setHint("Teléfono");
        layout.addView(inputTelefono);

        final EditText inputEstado = new EditText(this);
        inputEstado.setHint("Estado (0 o 1)");
        layout.addView(inputEstado);


        builder.setView(layout);

        // Botón de confirmación
        builder.setPositiveButton("Añadir", (dialog, which) -> {
            String nombre = inputNombre.getText().toString();
            String direccion = inputDireccion.getText().toString();
            String telefono = inputTelefono.getText().toString();
            int estado = Integer.parseInt(inputEstado.getText().toString());

            // Insertar en la base de datos
            dbHelper.addPartner(nombre, direccion, telefono, estado, usuarioId);

            // Recargar la lista
            loadPartners();
        });

        // Botón de cancelar
        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());

        builder.show();
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
                XMLirakurri(uri);  // Llamar al método para leer el XML
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

            // Crea un DocumentBuilderFactory y un DocumentBuilder
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(inputStream);
            document.getDocumentElement().normalize();  // Normaliza el documento

            // Obtén los elementos "partner" del XML
            NodeList nodeList = document.getElementsByTagName("partner");

            int count = 0;
            // Itera sobre todos los elementos "partner" en el XML
            for (int i = 0; i < nodeList.getLength(); i++) {
                Element element = (Element) nodeList.item(i);  // Obtén cada "partner"

                // Extrae los valores de cada "partner"
                int partnerId = Integer.parseInt(element.getElementsByTagName("Partner_ID").item(0).getTextContent());
                String nombre = element.getElementsByTagName("nombre").item(0).getTextContent();
                String direccion = element.getElementsByTagName("direccion").item(0).getTextContent();
                String telefono = element.getElementsByTagName("telefono").item(0).getTextContent();
                int estado = Integer.parseInt(element.getElementsByTagName("estado").item(0).getTextContent());
                int idComercial = Integer.parseInt(element.getElementsByTagName("id_comercial").item(0).getTextContent());

                // Llamamos al método para insertar o actualizar el partner
                dbHelper.PartnerSortuEguneratu(partnerId, nombre, direccion, telefono, estado, idComercial);
                count++;
            }

            Toast.makeText(this, count + " Erregistroak eguneratu/gehitu dira.", Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            Log.e("XMLImport", "Errorea irakurtzean", e);
            Toast.makeText(this, "Errorea irakurtzean", Toast.LENGTH_SHORT).show();
        }
    }

    public void showPartnerIdDialog() {
        // Crear un diálogo para pedir el ID del partner
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Ingrese el ID del Partner");

        // Crear un EditText para ingresar el Partner ID
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);

        builder.setPositiveButton("Aceptar", (dialog, which) -> {
            String partnerIdStr = input.getText().toString();
            if (!partnerIdStr.isEmpty()) {
                int partnerId = Integer.parseInt(partnerIdStr);
                // Verificar si el Partner existe en la base de datos
                Partner partner = dbHelper.getPartnerById(partnerId);
                if (partner != null) {
                    // Si el Partner existe, mostrar el segundo diálogo
                    showEditPartnerDialog(partner);
                } else {
                    Toast.makeText(this, "Partner no encontrado", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Por favor, ingrese un ID válido", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());

        builder.show();
    }

    public void showEditPartnerDialog(Partner partner) {
        // Crear un nuevo diálogo para editar los datos del Partner
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Editar datos del Partner");

        // Crear un layout para el diálogo
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        // Crear los EditTexts para los campos
        final EditText etNombre = new EditText(this);
        etNombre.setText(partner.getNombre());

        final EditText etDireccion = new EditText(this);
        etDireccion.setText(partner.getDireccion());

        final EditText etTelefono = new EditText(this);
        etTelefono.setText(partner.getTelefono());

        final EditText etEstado = new EditText(this);
        etEstado.setText(String.valueOf(partner.getEstado()));

        final EditText etIdComercial = new EditText(this);
        etIdComercial.setText(String.valueOf(partner.getIdComercial()));

        // Añadir los EditTexts al layout
        layout.addView(etNombre);
        layout.addView(etDireccion);
        layout.addView(etTelefono);
        layout.addView(etEstado);
        layout.addView(etIdComercial);

        builder.setView(layout);

        builder.setPositiveButton("Guardar", (dialog, which) -> {
            // Obtener los datos modificados
            String nombre = etNombre.getText().toString();
            String direccion = etDireccion.getText().toString();
            String telefono = etTelefono.getText().toString();
            int estado = Integer.parseInt(etEstado.getText().toString());
            int idComercial = Integer.parseInt(etIdComercial.getText().toString());

            // Actualizar los datos del partner en la base de datos
            dbHelper.updatePartner(partner.getPartnerId(), nombre, direccion, telefono, estado, idComercial);
            Toast.makeText(this, "Datos del partner actualizados", Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());

        builder.show();
    }








}

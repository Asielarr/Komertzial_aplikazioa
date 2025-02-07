package com.example.komertzial_aplikazioa;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.InputType;
import android.util.Log;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import java.io.File;
import java.io.FileInputStream;
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

        btnAlta.setOnClickListener(v -> PartnerGehituDialog());

        BtnBaja.setOnClickListener(v -> {
            // AlertDialog bat sortzen du, partner-aren ID-a sartzeko
            AlertDialog.Builder builder = new AlertDialog.Builder(PartnerActivity.this);
            builder.setTitle("Partner-ari baja eman");
            builder.setMessage("Sartu ezabatuko den partner-aren ID-a:");

            // Edit text bat sortzen du, ID-a idazteko
            final EditText input = new EditText(PartnerActivity.this);
            input.setInputType(InputType.TYPE_CLASS_NUMBER);
            builder.setView(input);

            // Botoi bat sortzen du
            builder.setPositiveButton("Onartu", (dialog, which) -> {
                String partnerIdStr = input.getText().toString().trim();

                // ID-a hustik ez dagoela egiaztatu
                if (partnerIdStr.isEmpty()) {
                    Toast.makeText(PartnerActivity.this, "ID bat sartu behar duzu", Toast.LENGTH_SHORT).show();
                    return;
                }

                int partnerId = Integer.parseInt(partnerIdStr);

                // Ziurtatu ezabatu nahi duela
                new AlertDialog.Builder(PartnerActivity.this)
                        .setTitle("Egiaztapena")
                        .setMessage("Ziur zaude hurrengo ID-a duen partner-a ezabatu nahi duzula?" + partnerId + "?")
                        .setPositiveButton("Bai", (confirmDialog, whichConfirm) -> {
                            DatabaseHelper dbHelper = new DatabaseHelper(PartnerActivity.this);

                            dbHelper.PartnerEzabatu(partnerId, PartnerActivity.this);
                        })
                        .setNegativeButton("Ez", null)
                        .show();
            });

            // Atzera botoia
            builder.setNegativeButton("Atzera", (dialog, which) -> dialog.dismiss());

            builder.show();
        });

        //Inportatzeko balioko duen botoia
        Button btnImportar = findViewById(R.id.BtnInportatu);
        btnImportar.setOnClickListener(v -> XMLAukeratu());

        //Eguneratzeko balioko duen botoia
        btnActualizar = findViewById(R.id.BtnAldaketa);
        btnActualizar.setOnClickListener(v -> showPartnerIdDialog());

    }

    //Momentu honetan dauden partner-ak kargatuko duen metodoa
    private void loadPartners() {
        partnerList = dbHelper.PartnerrakLortu();
        adapter = new PartnerAdapter(partnerList);
        recyclerView.setAdapter(adapter);
    }

    //Partner berri bat gehitzeko erabiliko den metodoa
    private void PartnerGehituDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Partner berri bat gehitu");

        // Layout-a dialog-arentzat
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 20, 50, 20);

        final EditText inputNombre = new EditText(this);
        inputNombre.setHint("Izena");
        layout.addView(inputNombre);

        final EditText inputDireccion = new EditText(this);
        inputDireccion.setHint("Helbidea");
        layout.addView(inputDireccion);

        final EditText inputTelefono = new EditText(this);
        inputTelefono.setHint("Mugikorra");
        layout.addView(inputTelefono);

        final EditText inputEstado = new EditText(this);
        inputEstado.setHint("Mota(0 edo 1)");
        layout.addView(inputEstado);

        builder.setView(layout);

        // Egiaztapen botoia
        builder.setPositiveButton("Gehitu", (dialog, which) -> {
            String nombre = inputNombre.getText().toString();
            String direccion = inputDireccion.getText().toString();
            String telefono = inputTelefono.getText().toString();
            int estado = Integer.parseInt(inputEstado.getText().toString());

            // Datu basean gorde
            dbHelper.PartnerraGehitu(nombre, direccion, telefono, estado, usuarioId);

            // XML-an gorde
            savePartnerToXML(nombre, direccion, telefono, estado, usuarioId);

            loadPartners();
        });

        // Atzera botoia
        builder.setNegativeButton("Atzera", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    // XML-a aukeratzeko lehioa ireki
    private void XMLAukeratu() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("text/xml");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intent, "Aukeratu XML-a"), PICK_XML_FILE);
    }

    //XML-a aukeratu ondorengo gertakaria
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
                Toast.makeText(this, "Ezin izan da fitxategia ireki", Toast.LENGTH_SHORT).show();
                return;
            }

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(inputStream);
            document.getDocumentElement().normalize();

            // "partner" Elementuak lortu
            NodeList nodeList = document.getElementsByTagName("partner");

            //Partner bakoitzeko datuak ateratzen ditu eta datu basean gorde
            int count = 0;
            for (int i = 0; i < nodeList.getLength(); i++) {
                Element element = (Element) nodeList.item(i);

                int partnerId = Integer.parseInt(element.getElementsByTagName("Partner_ID").item(0).getTextContent());
                String nombre = element.getElementsByTagName("nombre").item(0).getTextContent();
                String direccion = element.getElementsByTagName("direccion").item(0).getTextContent();
                String telefono = element.getElementsByTagName("telefono").item(0).getTextContent();
                int estado = Integer.parseInt(element.getElementsByTagName("estado").item(0).getTextContent());
                int idComercial = Integer.parseInt(element.getElementsByTagName("id_comercial").item(0).getTextContent());

                dbHelper.PartnerEguneratuSortu(partnerId, nombre, direccion, telefono, estado, idComercial);
                count++;
            }

            Toast.makeText(this, count + " Erregistroak eguneratu/gehitu dira.", Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            Log.e("XMLImport", "Errorea irakurtzean", e);
            Toast.makeText(this, "Errorea irakurtzean", Toast.LENGTH_SHORT).show();
        }
    }

    //Dialog bat sortzen du, partner baten datuak editatzeko
    public void showPartnerIdDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Sartu partner baten ID-a");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);

        builder.setPositiveButton("Jarraitu", (dialog, which) -> {
            String partnerIdStr = input.getText().toString();
            if (!partnerIdStr.isEmpty()) {
                int partnerId = Integer.parseInt(partnerIdStr);
                Partner partner = dbHelper.PartnerLortuIDbidez(partnerId);
                if (partner != null) {
                    showEditPartnerDialog(partner);
                } else {
                    Toast.makeText(this, "Partner-a ez da aurkitu", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Sartu ID baliodun bat", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Atzera", (dialog, which) -> dialog.dismiss());

        builder.show();
    }

    //Editatzeko dialog-a irekitzen du
    public void showEditPartnerDialog(Partner partner) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Partner-aren datuak eguneratu");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

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

        layout.addView(etNombre);
        layout.addView(etDireccion);
        layout.addView(etTelefono);
        layout.addView(etEstado);
        layout.addView(etIdComercial);

        builder.setView(layout);

        builder.setPositiveButton("Gorde", (dialog, which) -> {
            // Aldaketak lortu
            String nombre = etNombre.getText().toString();
            String direccion = etDireccion.getText().toString();
            String telefono = etTelefono.getText().toString();
            int estado = Integer.parseInt(etEstado.getText().toString());
            int idComercial = Integer.parseInt(etIdComercial.getText().toString());

            //Eguneratu
            dbHelper.updatePartner(partner.getPartnerId(), nombre, direccion, telefono, estado, idComercial);
            Toast.makeText(this, "Datuak eguneratu dira", Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton("Atzera", (dialog, which) -> dialog.dismiss());

        builder.show();
    }

    //Partner berriaren datuak XML batean gordetzen dira
    private void savePartnerToXML(String nombre, String direccion, String telefono, int estado, int idComercial) {
        try {
            // Partnerraren fitxategia gordetzeko helbidea(emulated/0/documents/XML-ak/Bidaltzeko)
            File baseDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "XML-ak/Bidaltzeko");

            //karpeta ez badago, sortu
            if (!baseDir.exists()) {
                baseDir.mkdirs();
            }

            //Fitxategiaren izena
            File file = new File(baseDir, "partner_berriak.xml");

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document;

            if (file.exists()) {
                FileInputStream fis = new FileInputStream(file);
                document = builder.parse(fis);
                fis.close();

                Element root = document.getDocumentElement();

                Element partner = createPartnerElement(document, nombre, direccion, telefono, estado, idComercial);
                root.appendChild(partner);

            } else {
                document = builder.newDocument();

                Element root = document.createElement("partners");
                document.appendChild(root);

                Element partner = createPartnerElement(document, nombre, direccion, telefono, estado, idComercial);
                root.appendChild(partner);
            }

            // Aldaketak gorde
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");

            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(file);
            transformer.transform(source, result);

            Toast.makeText(this, "Partnerberria godrde da: " + file.getAbsolutePath(), Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Errorea gorderzean", Toast.LENGTH_SHORT).show();
        }
    }


    //XML fitxategiko nodoak sortzen ditu
    private Element createPartnerElement(Document document, String nombre, String direccion, String telefono, int estado, int idComercial) {
        Element partner = document.createElement("partner");

        Element nombreElement = document.createElement("nombre");
        nombreElement.appendChild(document.createTextNode(nombre));
        partner.appendChild(nombreElement);

        Element direccionElement = document.createElement("direccion");
        direccionElement.appendChild(document.createTextNode(direccion));
        partner.appendChild(direccionElement);

        Element telefonoElement = document.createElement("telefono");
        telefonoElement.appendChild(document.createTextNode(telefono));
        partner.appendChild(telefonoElement);

        Element estadoElement = document.createElement("estado");
        estadoElement.appendChild(document.createTextNode(String.valueOf(estado)));
        partner.appendChild(estadoElement);

        Element idComercialElement = document.createElement("idComercial");
        idComercialElement.appendChild(document.createTextNode(String.valueOf(idComercial)));
        partner.appendChild(idComercialElement);

        return partner;
    }










}

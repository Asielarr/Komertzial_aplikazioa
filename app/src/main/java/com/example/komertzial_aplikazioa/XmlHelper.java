package com.example.komertzial_aplikazioa;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.FileOutputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class XmlHelper {

    // Método para guardar los pedidos en un archivo XML
    public static String guardarPedidosEnXml(Context context, List<Eskaera> nuevosPedidos) {
        try {
            File downloadsDirectory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "XML-ak/Bidaltzeko");
            if (!downloadsDirectory.exists()) {
                downloadsDirectory.mkdirs();
            }

            File file = new File(downloadsDirectory, "pedidos.xml");

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc;

            // Si el archivo existe, cargar pedidos previos
            if (file.exists()) {
                doc = builder.parse(file);
                doc.getDocumentElement().normalize();
            } else {
                doc = builder.newDocument();
                Element root = doc.createElement("Pedidos");
                doc.appendChild(root);
            }

            Element root = doc.getDocumentElement();

            for (Eskaera pedido : nuevosPedidos) {
                Element pedidoElement = doc.createElement("Pedido");

                Element idElement = doc.createElement("ID");
                idElement.appendChild(doc.createTextNode(String.valueOf(pedido.getCodigoProducto())));
                pedidoElement.appendChild(idElement);

                Element nombreElement = doc.createElement("Nombre");
                nombreElement.appendChild(doc.createTextNode(pedido.getNombreProducto()));
                pedidoElement.appendChild(nombreElement);

                Element precioElement = doc.createElement("Precio");
                precioElement.appendChild(doc.createTextNode(String.valueOf(pedido.getPrecio())));
                pedidoElement.appendChild(precioElement);

                Element cantidadElement = doc.createElement("Cantidad");
                cantidadElement.appendChild(doc.createTextNode(String.valueOf(pedido.getCantidad())));
                pedidoElement.appendChild(cantidadElement);

                // Redondear el total para evitar problemas con la precisión decimal
                double totalRedondeado = Math.round(pedido.getTotal() * 100.0) / 100.0;

                // Verificar si el total es diferente de cero antes de agregarlo al XML
                if (totalRedondeado != 0) {
                    Log.d("PedidoData", "Total: " + totalRedondeado); // Verificar valor
                    Element totalElement = doc.createElement("Total");
                    totalElement.appendChild(doc.createTextNode(String.valueOf(totalRedondeado)));
                    pedidoElement.appendChild(totalElement);
                }

                // Agregar otros elementos solo si no son nulos
                if (pedido.getEstadoPedido() != null) {
                    Element estadoPedidoElement = doc.createElement("EstadoPedido");
                    estadoPedidoElement.appendChild(doc.createTextNode(pedido.getEstadoPedido()));
                    pedidoElement.appendChild(estadoPedidoElement);
                }

                if (pedido.getIdComercial() != 0) {
                    Element idComercialElement = doc.createElement("IDComercial");
                    idComercialElement.appendChild(doc.createTextNode(String.valueOf(pedido.getIdComercial())));
                    pedidoElement.appendChild(idComercialElement);
                }

                if (pedido.getIdPartner() != 0) {
                    Element idPartnerElement = doc.createElement("IDPartner");
                    idPartnerElement.appendChild(doc.createTextNode(String.valueOf(pedido.getIdPartner())));
                    pedidoElement.appendChild(idPartnerElement);
                }

                if (pedido.getDireccionEnvio() != null && !pedido.getDireccionEnvio().isEmpty()) {
                    Log.d("PedidoData", "Direccion de Envio: " + pedido.getDireccionEnvio()); // Verificar valor
                    Element direccionEnvioElement = doc.createElement("DireccionEnvio");
                    direccionEnvioElement.appendChild(doc.createTextNode(pedido.getDireccionEnvio()));
                    pedidoElement.appendChild(direccionEnvioElement);
                }

                if (pedido.getFechaPedido() != null && !pedido.getFechaPedido().isEmpty()) {
                    Log.d("PedidoData", "Fecha: " + pedido.getFechaPedido()); // Verificar valor
                    Element fechaElement = doc.createElement("Fecha");
                    fechaElement.appendChild(doc.createTextNode(pedido.getFechaPedido()));
                    pedidoElement.appendChild(fechaElement);
                }

                root.appendChild(pedidoElement);
            }

            // Guardar el archivo XML
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            FileOutputStream fos = new FileOutputStream(file);
            transformer.transform(new DOMSource(doc), new StreamResult(fos));
            fos.close();

            // Imprimir el XML en consola para depuración
            transformerFactory = TransformerFactory.newInstance();
            transformer = transformerFactory.newTransformer();
            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(doc), new StreamResult(writer));
            Log.d("XML_Debug", writer.toString());

            return file.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            return "Error al guardar XML: " + e.getMessage();
        }
    }

    // Método para importar pedidos desde un archivo XML
    public static List<Eskaera> importarPedidosDesdeXml(File file) {
        List<Eskaera> listaPedidos = new ArrayList<>();

        try {
            if (!file.exists()) {
                return listaPedidos;
            }

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(file);
            doc.getDocumentElement().normalize();

            NodeList nodeList = doc.getElementsByTagName("Pedido");

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;

                    int codigoProducto = Integer.parseInt(getTagValue("ID", element));
                    String nombreProducto = getTagValue("Nombre", element);
                    double precio = Double.parseDouble(getTagValue("Precio", element));
                    int cantidad = Integer.parseInt(getTagValue("Cantidad", element));
                    String estadoPedido = getTagValue("EstadoPedido", element);
                    int idComercial = getTagValue("IDComercial", element) != null ? Integer.parseInt(getTagValue("IDComercial", element)) : 0;
                    int idPartner = getTagValue("IDPartner", element) != null ? Integer.parseInt(getTagValue("IDPartner", element)) : 0;
                    String direccionEnvio = getTagValue("DireccionEnvio", element);
                    String fecha = getTagValue("Fecha", element);
                    double total = getTagValue("Total", element) != null ? Double.parseDouble(getTagValue("Total", element)) : 0;

                    Eskaera pedido = new Eskaera(codigoProducto, nombreProducto, precio, cantidad, estadoPedido, idComercial, idPartner, direccionEnvio, fecha, (float) total);
                    listaPedidos.add(pedido);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listaPedidos;
    }

    // Método auxiliar para obtener el valor de un elemento XML
    public static String getTagValue(String tag, Element element) {
        NodeList nodeList = element.getElementsByTagName(tag);
        if (nodeList.getLength() > 0) {
            Node node = nodeList.item(0);
            return node.getTextContent();
        }
        return null;
    }
}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.proyectosistemainmobiliario.facturacion;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;

import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Exporta facturas y listados a PDF usando OpenPDF. Diseño coherente con los
 * colores de la app (azul oscuro + acentos).
 */
public class ExportadorPDF {

    // ── Colores (mismos que la app Swing) ──────────────────────────
    private static final Color AZUL_OSCURO = new Color(22, 60, 110);
    private static final Color AZUL_MED = new Color(41, 98, 170);
    private static final Color GRIS_TEXTO = new Color(50, 60, 80);
    private static final Color GRIS_CLARO = new Color(230, 235, 245);
    private static final Color GRIS_BORDE = new Color(220, 220, 220);
    private static final Color FONDO_TOTAL = new Color(225, 235, 250);

    private static final NumberFormat FORMATO_MONEDA
            = NumberFormat.getCurrencyInstance(new Locale("es", "CO"));

    // ── Fuentes ────────────────────────────────────────────────────
    private static final Font FUENTE_TITULO = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, Color.WHITE);
    private static final Font FUENTE_SUBTITULO = FontFactory.getFont(FontFactory.HELVETICA, 11, new Color(200, 220, 255));
    private static final Font FUENTE_SECCION = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, Color.WHITE);
    private static final Font FUENTE_LABEL = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, GRIS_TEXTO);
    private static final Font FUENTE_VALOR = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.BLACK);
    private static final Font FUENTE_TOTAL = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, AZUL_OSCURO);
    private static final Font FUENTE_FOOTER = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 10, GRIS_TEXTO);

    // ═══════════ API PÚBLICA ═══════════════════════════════════════
    /**
     * Exporta una factura (venta o arriendo) a un archivo PDF.
     */
    public static void exportarFactura(Factura f, File destino) throws IOException {
        Document doc = new Document(PageSize.A4, 40, 40, 40, 40);
        try {
            PdfWriter.getInstance(doc, new FileOutputStream(destino));
            doc.open();

            doc.add(buildHeader(f));
            doc.add(Chunk.NEWLINE);

            doc.add(buildSeccion("DATOS DEL CLIENTE"));
            doc.add(buildTablaDatos(new String[][]{
                {"Nombre:", f.getNombreCliente()},
                {"Documento:", f.getDocumentoCliente()},
                {"Correo:", f.getCorreoCliente()},
                {"Teléfono:", f.getTelefonoCliente()}
            }));

            doc.add(buildSeccion("DATOS DE LA PROPIEDAD"));
            doc.add(buildTablaDatos(new String[][]{
                {"Tipo:", f.getTipoPropiedad()},
                {"Dirección:", f.getDireccionPropiedad()},
                {"Área:", String.format("%.2f m²", f.getAreaPropiedad())},
                {"Agente:", f.getNombreAgente()}
            }));

            doc.add(buildSeccion("DETALLE ECONÓMICO"));
            doc.add(buildTablaEconomica(f));

            doc.add(buildFooter());

        } catch (DocumentException e) {
            throw new IOException("Error generando PDF: " + e.getMessage(), e);
        } finally {
            if (doc.isOpen()) {
                doc.close();
            }
        }
    }

    // ═══════════ HELPERS PRIVADOS ══════════════════════════════════
    /**
     * Caja azul oscura con título grande, tipo de factura y número/fecha
     */
    private static PdfPTable buildHeader(Factura f) {
        PdfPTable tabla = new PdfPTable(1);
        tabla.setWidthPercentage(100);

        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(AZUL_OSCURO);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPadding(18);

        Paragraph titulo = new Paragraph("SISTEMA INMOBILIARIO", FUENTE_TITULO);
        titulo.setAlignment(Element.ALIGN_LEFT);

        String tipo = "VENTA".equals(f.getTipoTransaccion())
                ? "Factura de compraventa de propiedad"
                : "Factura de contrato de arriendo";
        Paragraph sub = new Paragraph(tipo, FUENTE_SUBTITULO);
        sub.setAlignment(Element.ALIGN_LEFT);
        sub.setSpacingBefore(4);

        Paragraph numero = new Paragraph(
                "N° " + f.getNumeroFactura() + "   |   Fecha: " + f.getFechaEmision(),
                FUENTE_SUBTITULO);
        numero.setAlignment(Element.ALIGN_LEFT);

        cell.addElement(titulo);
        cell.addElement(sub);
        cell.addElement(numero);

        tabla.addCell(cell);
        return tabla;
    }

    /**
     * Barra azul mediana con el nombre de la sección en blanco
     */
    private static PdfPTable buildSeccion(String nombre) {
        PdfPTable tabla = new PdfPTable(1);
        tabla.setWidthPercentage(100);
        tabla.setSpacingBefore(12);
        tabla.setSpacingAfter(4);

        PdfPCell cell = new PdfPCell(new Phrase(nombre, FUENTE_SECCION));
        cell.setBackgroundColor(AZUL_MED);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPadding(7);
        tabla.addCell(cell);
        return tabla;
    }

    /**
     * Tabla de 2 columnas (etiqueta + valor) con filas alternadas
     */
    private static PdfPTable buildTablaDatos(String[][] filas) {
        PdfPTable tabla = new PdfPTable(2);
        tabla.setWidthPercentage(100);
        try {
            tabla.setWidths(new float[]{1.5f, 4f});
        } catch (DocumentException ignored) {
        }

        boolean alternar = false;
        for (String[] fila : filas) {
            Color fondo = alternar ? GRIS_CLARO : Color.WHITE;
            tabla.addCell(celdaPlana(fila[0], FUENTE_LABEL, fondo, Element.ALIGN_LEFT));
            tabla.addCell(celdaPlana(fila[1] != null ? fila[1] : "", FUENTE_VALOR, fondo, Element.ALIGN_LEFT));
            alternar = !alternar;
        }
        return tabla;
    }

    /**
     * Tabla del detalle económico — subtotal, IVA y total con énfasis
     */
    private static PdfPTable buildTablaEconomica(Factura f) {
        PdfPTable tabla = new PdfPTable(2);
        tabla.setWidthPercentage(100);
        try {
            tabla.setWidths(new float[]{3f, 2f});
        } catch (DocumentException ignored) {
        }

        // Si es arriendo, agrega valor mensual y meses arriba
        if ("ARRIENDO".equals(f.getTipoTransaccion())) {
            addFilaEconomica(tabla, "Valor mensual:", FORMATO_MONEDA.format(f.getValorMensual()), false);
            addFilaEconomica(tabla, "Duración:", f.getMeses() + " meses", false);
        }
        addFilaEconomica(tabla, "Subtotal:", FORMATO_MONEDA.format(f.getSubtotal()), false);
        addFilaEconomica(tabla, "IVA (19%):", FORMATO_MONEDA.format(f.getIva()), false);
        addFilaEconomica(tabla, "TOTAL A PAGAR:", FORMATO_MONEDA.format(f.getTotal()), true);

        return tabla;
    }

    private static void addFilaEconomica(PdfPTable tabla, String etiqueta, String valor, boolean esTotal) {
        Font fe = esTotal ? FUENTE_TOTAL : FUENTE_LABEL;
        Font fv = esTotal ? FUENTE_TOTAL : FUENTE_VALOR;
        Color fondo = esTotal ? FONDO_TOTAL : Color.WHITE;

        PdfPCell c1 = new PdfPCell(new Phrase(etiqueta, fe));
        c1.setBackgroundColor(fondo);
        c1.setBorder(Rectangle.TOP);
        c1.setBorderColor(GRIS_BORDE);
        c1.setPadding(8);

        PdfPCell c2 = new PdfPCell(new Phrase(valor, fv));
        c2.setBackgroundColor(fondo);
        c2.setBorder(Rectangle.TOP);
        c2.setBorderColor(GRIS_BORDE);
        c2.setPadding(8);
        c2.setHorizontalAlignment(Element.ALIGN_RIGHT);

        tabla.addCell(c1);
        tabla.addCell(c2);
    }

    private static PdfPCell celdaPlana(String texto, Font f, Color fondo, int align) {
        PdfPCell c = new PdfPCell(new Phrase(texto, f));
        c.setBackgroundColor(fondo);
        c.setBorder(Rectangle.NO_BORDER);
        c.setPadding(6);
        c.setHorizontalAlignment(align);
        return c;
    }

    private static Paragraph buildFooter() {
        Paragraph p = new Paragraph("Gracias por contar con nosotros", FUENTE_FOOTER);
        p.setAlignment(Element.ALIGN_CENTER);
        p.setSpacingBefore(25);
        return p;
    }
    // ═══════════ HELPER UI ═════════════════════════════════════════

    /**
     * Muestra un diálogo "Guardar como" y exporta la factura al destino
     * elegido. Si el guardado fue exitoso, intenta abrir el PDF con la app por
     * defecto del SO.
     *
     * @param f Factura a exportar
     * @param parent Componente padre del diálogo (típicamente 'this' o el
     * panel)
     * @return true si se guardó (y opcionalmente abrió) el PDF, false si el
     * usuario canceló
     */
    public static boolean exportarFacturaConDialogo(Factura f, java.awt.Component parent) {
        javax.swing.JFileChooser chooser = new javax.swing.JFileChooser();
        chooser.setDialogTitle("Guardar factura PDF");

        // Nombre sugerido: "Factura_VTA-20260522-1530-0001.pdf"
        String nombreSugerido = "Factura_" + f.getNumeroFactura() + ".pdf";
        chooser.setSelectedFile(new File(nombreSugerido));

        // Filtro: solo .pdf
        chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Archivos PDF (*.pdf)", "pdf"));

        int resultado = chooser.showSaveDialog(parent);
        if (resultado != javax.swing.JFileChooser.APPROVE_OPTION) {
            return false; // el usuario canceló
        }

        File destino = chooser.getSelectedFile();
        // Si el usuario no escribió la extensión, la agregamos
        if (!destino.getName().toLowerCase().endsWith(".pdf")) {
            destino = new File(destino.getAbsolutePath() + ".pdf");
        }

        try {
            exportarFactura(f, destino);

            // Intentar abrir el PDF con la app por defecto
            if (java.awt.Desktop.isDesktopSupported() && destino.exists()) {
                try {
                    java.awt.Desktop.getDesktop().open(destino);
                } catch (IOException ignored) {
                    // Si no se puede abrir automáticamente, no es error fatal —
                    // el archivo ya está guardado.
                }
            }

            javax.swing.JOptionPane.showMessageDialog(parent,
                    "PDF guardado:\n" + destino.getAbsolutePath(),
                    "Éxito", javax.swing.JOptionPane.INFORMATION_MESSAGE);
            return true;

        } catch (IOException ex) {
            javax.swing.JOptionPane.showMessageDialog(parent,
                    "Error al generar PDF:\n" + ex.getMessage(),
                    "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public static boolean exportarListadoPropiedadesConDialogo(
            java.util.List<com.mycompany.proyectosistemainmobiliario.modelos.Propiedad> props,
            String subtituloFiltros,
            java.awt.Component parent) {
        File destino = pedirDestino(parent, "listado_propiedades.pdf");
        if (destino == null) {
            return false;
        }
        try {
            exportarListadoPropiedades(props, destino, subtituloFiltros);
            abrirYNotificar(destino, parent);
            return true;
        } catch (IOException ex) {
            mostrarErrorPDF(parent, ex);
            return false;
        }
    }

    public static boolean exportarListadoVentasConDialogo(
            java.util.List<com.mycompany.proyectosistemainmobiliario.modelos.Venta> ventas,
            java.awt.Component parent) {
        File destino = pedirDestino(parent, "listado_ventas.pdf");
        if (destino == null) {
            return false;
        }
        try {
            exportarListadoVentas(ventas, destino);
            abrirYNotificar(destino, parent);
            return true;
        } catch (IOException ex) {
            mostrarErrorPDF(parent, ex);
            return false;
        }
    }

    public static boolean exportarListadoArriendosConDialogo(
            java.util.List<com.mycompany.proyectosistemainmobiliario.modelos.Arriendo> arriendos,
            java.awt.Component parent) {
        File destino = pedirDestino(parent, "listado_arriendos.pdf");
        if (destino == null) {
            return false;
        }
        try {
            exportarListadoArriendos(arriendos, destino);
            abrirYNotificar(destino, parent);
            return true;
        } catch (IOException ex) {
            mostrarErrorPDF(parent, ex);
            return false;
        }
    }

// ── Helpers privados compartidos ──────────────────────────────
    private static File pedirDestino(java.awt.Component parent, String nombreSugerido) {
        javax.swing.JFileChooser chooser = new javax.swing.JFileChooser();
        chooser.setDialogTitle("Guardar PDF");
        chooser.setSelectedFile(new File(nombreSugerido));
        chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Archivos PDF (*.pdf)", "pdf"));

        if (chooser.showSaveDialog(parent) != javax.swing.JFileChooser.APPROVE_OPTION) {
            return null;
        }

        File destino = chooser.getSelectedFile();
        if (!destino.getName().toLowerCase().endsWith(".pdf")) {
            destino = new File(destino.getAbsolutePath() + ".pdf");
        }
        return destino;
    }

    private static void abrirYNotificar(File destino, java.awt.Component parent) {
        if (java.awt.Desktop.isDesktopSupported() && destino.exists()) {
            try {
                java.awt.Desktop.getDesktop().open(destino);
            } catch (IOException ignored) {
            }
        }
        javax.swing.JOptionPane.showMessageDialog(parent,
                "PDF guardado:\n" + destino.getAbsolutePath(),
                "Éxito", javax.swing.JOptionPane.INFORMATION_MESSAGE);
    }

    private static void mostrarErrorPDF(java.awt.Component parent, Exception ex) {
        javax.swing.JOptionPane.showMessageDialog(parent,
                "Error al generar PDF:\n" + ex.getMessage(),
                "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
    }
// ═══════════ LISTADOS ══════════════════════════════════════════

    /**
     * Exporta un listado de propiedades a PDF (orientación apaisada para que
     * quepan las columnas).
     */
    public static void exportarListadoPropiedades(java.util.List<com.mycompany.proyectosistemainmobiliario.modelos.Propiedad> propiedades,
            File destino,
            String subtituloFiltros) throws IOException {
        Document doc = new Document(PageSize.A4.rotate(), 30, 30, 30, 30);
        try {
            PdfWriter.getInstance(doc, new FileOutputStream(destino));
            doc.open();

            doc.add(buildHeaderListado("LISTADO DE PROPIEDADES", subtituloFiltros, propiedades.size()));
            doc.add(Chunk.NEWLINE);

            String[] columnas = {"ID", "Tipo", "Dirección", "Ciudad", "Barrio", "Área m²", "Estado", "Precio"};
            float[] anchos = {0.6f, 1.2f, 4f, 1.5f, 1.5f, 1f, 1.2f, 2f};

            PdfPTable tabla = buildTablaListado(columnas, anchos);

            for (com.mycompany.proyectosistemainmobiliario.modelos.Propiedad p : propiedades) {
                addFilaListado(tabla, new String[]{
                    String.valueOf(p.getId()),
                    p.getClass().getSimpleName(),
                    p.getDireccion(),
                    p.getCiudad(),
                    p.getBarrio(),
                    String.format("%.1f", p.getArea()),
                    p.getEstado(),
                    FORMATO_MONEDA.format(p.calcularPrecio())
                });
            }
            doc.add(tabla);
            doc.add(buildFooter());

        } catch (DocumentException e) {
            throw new IOException("Error generando PDF: " + e.getMessage(), e);
        } finally {
            if (doc.isOpen()) {
                doc.close();
            }
        }
    }

    /**
     * Exporta un listado de ventas a PDF.
     */
    public static void exportarListadoVentas(java.util.List<com.mycompany.proyectosistemainmobiliario.modelos.Venta> ventas,
            File destino) throws IOException {
        Document doc = new Document(PageSize.A4.rotate(), 30, 30, 30, 30);
        try {
            PdfWriter.getInstance(doc, new FileOutputStream(destino));
            doc.open();

            doc.add(buildHeaderListado("LISTADO DE VENTAS", null, ventas.size()));
            doc.add(Chunk.NEWLINE);

            String[] columnas = {"ID", "Fecha", "Cliente", "Propiedad", "Agente", "Precio final"};
            float[] anchos = {0.6f, 1.3f, 2.5f, 4f, 2f, 2f};

            PdfPTable tabla = buildTablaListado(columnas, anchos);

            double totalAcumulado = 0;
            for (com.mycompany.proyectosistemainmobiliario.modelos.Venta v : ventas) {
                addFilaListado(tabla, new String[]{
                    String.valueOf(v.getId()),
                    v.getFecha(),
                    v.getCliente().getNombre(),
                    v.getPropiedad().getDireccion(),
                    v.getAgente().getNombre(),
                    FORMATO_MONEDA.format(v.getPrecioFinal())
                });
                totalAcumulado += v.getPrecioFinal();
            }
            doc.add(tabla);
            doc.add(buildTotalAcumulado("Total ventas:", totalAcumulado));
            doc.add(buildFooter());

        } catch (DocumentException e) {
            throw new IOException("Error generando PDF: " + e.getMessage(), e);
        } finally {
            if (doc.isOpen()) {
                doc.close();
            }
        }
    }

    /**
     * Exporta un listado de arriendos a PDF.
     */
    public static void exportarListadoArriendos(java.util.List<com.mycompany.proyectosistemainmobiliario.modelos.Arriendo> arriendos,
            File destino) throws IOException {
        Document doc = new Document(PageSize.A4.rotate(), 30, 30, 30, 30);
        try {
            PdfWriter.getInstance(doc, new FileOutputStream(destino));
            doc.open();

            doc.add(buildHeaderListado("LISTADO DE ARRIENDOS", null, arriendos.size()));
            doc.add(Chunk.NEWLINE);

            String[] columnas = {"ID", "Fecha inicio", "Cliente", "Propiedad", "Agente", "Valor mensual", "Meses", "Total"};
            float[] anchos = {0.6f, 1.3f, 2.3f, 3.5f, 1.8f, 1.6f, 0.7f, 1.8f};

            PdfPTable tabla = buildTablaListado(columnas, anchos);

            double totalAcumulado = 0;
            for (com.mycompany.proyectosistemainmobiliario.modelos.Arriendo a : arriendos) {
                double totalArr = a.calcularTotalArriendo();
                addFilaListado(tabla, new String[]{
                    String.valueOf(a.getId()),
                    a.getFechaInicio(),
                    a.getCliente().getNombre(),
                    a.getPropiedad().getDireccion(),
                    a.getAgente().getNombre(),
                    FORMATO_MONEDA.format(a.getValorMensual()),
                    String.valueOf(a.getMeses()),
                    FORMATO_MONEDA.format(totalArr)
                });
                totalAcumulado += totalArr;
            }
            doc.add(tabla);
            doc.add(buildTotalAcumulado("Total arriendos (acumulado contratos):", totalAcumulado));
            doc.add(buildFooter());

        } catch (DocumentException e) {
            throw new IOException("Error generando PDF: " + e.getMessage(), e);
        } finally {
            if (doc.isOpen()) {
                doc.close();
            }
        }
    }

// ═══════════ HELPERS DE LISTADO ═══════════════════════════════
    /**
     * Header de listado: caja azul con título, fecha de generación y total de
     * registros
     */
    private static PdfPTable buildHeaderListado(String titulo, String subtituloFiltros, int totalRegistros) {
        PdfPTable tabla = new PdfPTable(1);
        tabla.setWidthPercentage(100);

        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(AZUL_OSCURO);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPadding(16);

        Paragraph tit = new Paragraph("SISTEMA INMOBILIARIO", FUENTE_TITULO);
        Paragraph sub = new Paragraph(titulo, FUENTE_SUBTITULO);
        sub.setSpacingBefore(4);

        String fecha = java.time.LocalDateTime.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        Paragraph meta = new Paragraph(
                "Generado: " + fecha + "   |   Total registros: " + totalRegistros, FUENTE_SUBTITULO);

        cell.addElement(tit);
        cell.addElement(sub);
        cell.addElement(meta);

        if (subtituloFiltros != null && !subtituloFiltros.isBlank()) {
            Paragraph filtros = new Paragraph("Filtros aplicados: " + subtituloFiltros, FUENTE_SUBTITULO);
            cell.addElement(filtros);
        }

        tabla.addCell(cell);
        return tabla;
    }

    /**
     * Construye la tabla del listado con encabezados azul medio
     */
    private static PdfPTable buildTablaListado(String[] columnas, float[] anchos) {
        PdfPTable tabla = new PdfPTable(columnas.length);
        tabla.setWidthPercentage(100);
        tabla.setSpacingBefore(6);
        try {
            tabla.setWidths(anchos);
        } catch (DocumentException ignored) {
        }

        Font fuenteHeader = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.WHITE);
        for (String col : columnas) {
            PdfPCell c = new PdfPCell(new Phrase(col, fuenteHeader));
            c.setBackgroundColor(AZUL_MED);
            c.setBorderColor(AZUL_MED);
            c.setPadding(7);
            c.setHorizontalAlignment(Element.ALIGN_LEFT);
            tabla.addCell(c);
        }
        return tabla;
    }

    private static boolean alternarFila = false;

    /**
     * Agrega una fila a la tabla de listado, con colores alternados
     */
    private static void addFilaListado(PdfPTable tabla, String[] valores) {
        Color fondo = alternarFila ? GRIS_CLARO : Color.WHITE;
        Font fuenteCelda = FontFactory.getFont(FontFactory.HELVETICA, 9, GRIS_TEXTO);

        for (String v : valores) {
            PdfPCell c = new PdfPCell(new Phrase(v != null ? v : "", fuenteCelda));
            c.setBackgroundColor(fondo);
            c.setBorder(Rectangle.BOTTOM);
            c.setBorderColor(GRIS_BORDE);
            c.setPadding(6);
            tabla.addCell(c);
        }
        alternarFila = !alternarFila;
    }

    /**
     * Fila resumen con el total acumulado de un listado
     */
    private static Paragraph buildTotalAcumulado(String etiqueta, double total) {
        Paragraph p = new Paragraph(etiqueta + "  " + FORMATO_MONEDA.format(total), FUENTE_TOTAL);
        p.setAlignment(Element.ALIGN_RIGHT);
        p.setSpacingBefore(10);
        return p;
    }
// ═══════════ LISTADO DE CLIENTES ══════════════════════════════

    public static void exportarListadoClientes(
            java.util.List<com.mycompany.proyectosistemainmobiliario.modelos.Cliente> clientes,
            File destino) throws IOException {
        Document doc = new Document(PageSize.A4, 30, 30, 30, 30);
        try {
            PdfWriter.getInstance(doc, new FileOutputStream(destino));
            doc.open();

            doc.add(buildHeaderListado("LISTADO DE CLIENTES", null, clientes.size()));
            doc.add(Chunk.NEWLINE);

            String[] columnas = {"ID", "Nombre", "Documento", "Correo", "Teléfono"};
            float[] anchos = {0.6f, 3f, 1.8f, 3f, 1.6f};

            PdfPTable tabla = buildTablaListado(columnas, anchos);

            for (com.mycompany.proyectosistemainmobiliario.modelos.Cliente c : clientes) {
                String doc_ = c.getTipoDocumento() + " " + c.getNumeroDocumento();
                addFilaListado(tabla, new String[]{
                    String.valueOf(c.getId()),
                    c.getNombre(),
                    doc_,
                    c.getCorreo(),
                    c.getTelefono()
                });
            }
            doc.add(tabla);
            doc.add(buildFooter());

        } catch (DocumentException e) {
            throw new IOException("Error generando PDF: " + e.getMessage(), e);
        } finally {
            if (doc.isOpen()) {
                doc.close();
            }
        }
    }

    public static boolean exportarListadoClientesConDialogo(
            java.util.List<com.mycompany.proyectosistemainmobiliario.modelos.Cliente> clientes,
            java.awt.Component parent) {
        File destino = pedirDestino(parent, "listado_clientes.pdf");
        if (destino == null) {
            return false;
        }
        try {
            exportarListadoClientes(clientes, destino);
            abrirYNotificar(destino, parent);
            return true;
        } catch (IOException ex) {
            mostrarErrorPDF(parent, ex);
            return false;
        }
    }
}

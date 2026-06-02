package com.mycompany.proyectosistemainmobiliario.vistas;

import com.mycompany.proyectosistemainmobiliario.facturacion.GeneradorFactura;
import com.mycompany.proyectosistemainmobiliario.modelos.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;

/**
 * CLASE FacturaSwing (Vista / GUI)
 * ───────────────────────────────────────────────────────────────── Ventana
 * Swing (JFrame) para generar facturas de ventas y arriendos.
 *
 * SEPARACIÓN DE RESPONSABILIDADES (POO): Esta clase SOLO maneja la interfaz
 * visual. Toda la lógica de cálculo vive en GeneradorFactura (que implementa
 * IFacturable). La vista no calcula nada: llama al generador y muestra el
 * resultado.
 *
 * FLUJO: 1. El usuario elige "Venta" o "Arriendo" con los radio buttons. 2.
 * Llena los campos del formulario. 3. Pulsa "Generar Factura". 4. La vista
 * construye los objetos del dominio (Venta o Arriendo). 5. Pasa esos objetos a
 * GeneradorFactura. 6. Muestra el texto devuelto por generarFactura() en el
 * área de texto.
 *
 * Archivo: vistas/FacturaSwing.java
 */
public class FacturaSwing extends JFrame {

    // ── Constantes de estilo ───────────────────────────────────────
    private static final Color COLOR_PRIMARIO = new Color(33, 97, 140);
    private static final Color COLOR_SECUNDARIO = new Color(52, 152, 219);
    private static final Color COLOR_FONDO = new Color(245, 248, 250);
    private static final Color COLOR_BOTON = new Color(39, 174, 96);
    private static final Color COLOR_LIMPIAR = new Color(192, 57, 43);
    private static final Font FUENTE_LABEL = new Font("Segoe UI", Font.BOLD, 12);
    private static final Font FUENTE_CAMPO = new Font("Segoe UI", Font.PLAIN, 12);
    private static final Font FUENTE_RESULTADO = new Font("Monospaced", Font.PLAIN, 12);

    // ── Componentes — Selector de tipo ────────────────────────────
    private JRadioButton rbVenta;
    private JRadioButton rbArriendo;

    // ── Componentes — Datos del Cliente ───────────────────────────
    private JTextField txtNombreCliente;
    private JComboBox<String> cmbTipoDocumento;
    private JTextField txtNumeroDocumento;
    private JTextField txtCorreo;
    private JTextField txtTelefono;

    // ── Componentes — Datos de la Propiedad ───────────────────────
    private JComboBox<String> cmbTipoPropiedad;
    private JTextField txtDireccion;
    private JTextField txtArea;
    private JTextField txtEstado;

    // ── Componentes — Datos del Agente ────────────────────────────
    private JTextField txtNombreAgente;

    // ── Componentes — Datos de Venta ──────────────────────────────
    private JTextField txtPrecioVenta;
    private JLabel lblPrecioVenta;

    // ── Componentes — Datos de Arriendo ───────────────────────────
    private JTextField txtMeses;
    private JLabel lblMeses;

    // ── Componentes — Resultado ────────────────────────────────────
    private JTextArea txtResultado;
    private JButton btnGenerar;
    private JButton btnLimpiar;
    private JButton btnCopiar;
    private JButton btnExportarPDF;
    private JLabel lblEstado;

    // Última factura generada — se guarda aquí para que el botón PDF la use
    private com.mycompany.proyectosistemainmobiliario.facturacion.Factura facturaActual;

    // ── Panel dinámico que muestra/oculta campos ───────────────────
    private JPanel panelCamposDinamicos;

    // ══════════════════════════════════════════════════════════════
    //  CONSTRUCTOR
    // ══════════════════════════════════════════════════════════════
    public FacturaSwing() {
        super("Sistema Inmobiliario - Generador de Facturas");
        configurarVentana();
        construirUI();
        aplicarListeners();
        actualizarCamposDinamicos(); // Estado inicial
    }

    // ── 1. Configuración base del JFrame ──────────────────────────
    private void configurarVentana() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(900, 750);
        setMinimumSize(new Dimension(800, 650));
        setLocationRelativeTo(null); // Centrar en pantalla
        getContentPane().setBackground(COLOR_FONDO);
        setLayout(new BorderLayout(10, 10));
    }

    // ── 2. Construcción de la interfaz ────────────────────────────
    private void construirUI() {
        add(crearEncabezado(), BorderLayout.NORTH);
        add(crearPanelCentral(), BorderLayout.CENTER);
        add(crearPanelBotones(), BorderLayout.SOUTH);
    }

    /**
     * Encabezado azul con el título del sistema
     */
    private JPanel crearEncabezado() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(COLOR_PRIMARIO);
        panel.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel titulo = new JLabel("Generador de Facturas Inmobiliarias");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titulo.setForeground(Color.WHITE);

        JLabel subtitulo = new JLabel("Complete los datos y presione «Generar Factura»");
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitulo.setForeground(new Color(200, 220, 240));

        JPanel textos = new JPanel(new GridLayout(2, 1));
        textos.setOpaque(false);
        textos.add(titulo);
        textos.add(subtitulo);
        panel.add(textos, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Panel central dividido: formulario (izq) | resultado (der)
     */
    private JSplitPane crearPanelCentral() {
        JSplitPane split = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                crearFormulario(),
                crearPanelResultado()
        );
        split.setDividerLocation(420);
        split.setResizeWeight(0.45);
        split.setBorder(new EmptyBorder(5, 10, 5, 10));
        return split;
    }

    // ── 2a. Formulario (lado izquierdo) ───────────────────────────
    private JScrollPane crearFormulario() {
        JPanel formulario = new JPanel();
        formulario.setLayout(new BoxLayout(formulario, BoxLayout.Y_AXIS));
        formulario.setBackground(COLOR_FONDO);
        formulario.setBorder(new EmptyBorder(5, 5, 5, 5));

        formulario.add(crearSelectorTipo());
        formulario.add(Box.createVerticalStrut(8));
        formulario.add(crearSeccionCliente());
        formulario.add(Box.createVerticalStrut(8));
        formulario.add(crearSeccionPropiedad());
        formulario.add(Box.createVerticalStrut(8));
        formulario.add(crearSeccionAgente());
        formulario.add(Box.createVerticalStrut(8));
        panelCamposDinamicos = crearSeccionDinamica();
        formulario.add(panelCamposDinamicos);

        JScrollPane scroll = new JScrollPane(formulario);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.setBorder(null);
        return scroll;
    }

    /**
     * RadioButtons para elegir Venta o Arriendo
     */
    private JPanel crearSelectorTipo() {
        JPanel panel = crearPanelSeccion("Tipo de Transacción");
        panel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));

        rbVenta = new JRadioButton("🔑  Venta", true);
        rbArriendo = new JRadioButton("🏡  Arriendo", false);

        estilizarRadio(rbVenta);
        estilizarRadio(rbArriendo);

        ButtonGroup grupo = new ButtonGroup();
        grupo.add(rbVenta);
        grupo.add(rbArriendo);

        panel.add(rbVenta);
        panel.add(rbArriendo);
        return panel;
    }

    /**
     * Sección con campos del cliente
     */
    private JPanel crearSeccionCliente() {
        JPanel panel = crearPanelSeccion("Datos del Cliente");
        panel.setLayout(new GridLayout(0, 2, 8, 6));

        txtNombreCliente = crearCampo();
        cmbTipoDocumento = new JComboBox<>(new String[]{"CC", "CE", "NIT", "PP"});
        txtNumeroDocumento = crearCampo();
        txtCorreo = crearCampo();
        txtTelefono = crearCampo();

        estilizarCombo(cmbTipoDocumento);

        panel.add(label("Nombre completo:"));
        panel.add(txtNombreCliente);
        panel.add(label("Tipo documento:"));
        panel.add(cmbTipoDocumento);
        panel.add(label("Número documento:"));
        panel.add(txtNumeroDocumento);
        panel.add(label("Correo:"));
        panel.add(txtCorreo);
        panel.add(label("Teléfono:"));
        panel.add(txtTelefono);
        return panel;
    }

    /**
     * Sección con campos de la propiedad
     */
    private JPanel crearSeccionPropiedad() {
        JPanel panel = crearPanelSeccion("Datos de la Propiedad");
        panel.setLayout(new GridLayout(0, 2, 8, 6));

        cmbTipoPropiedad = new JComboBox<>(new String[]{"Casa", "Apartamento"});
        txtDireccion = crearCampo();
        txtArea = crearCampo();
        txtEstado = new JTextField("Disponible");
        txtEstado.setFont(FUENTE_CAMPO);

        estilizarCombo(cmbTipoPropiedad);

        panel.add(label("Tipo propiedad:"));
        panel.add(cmbTipoPropiedad);
        panel.add(label("Dirección:"));
        panel.add(txtDireccion);
        panel.add(label("Área (m²):"));
        panel.add(txtArea);
        panel.add(label("Estado:"));
        panel.add(txtEstado);
        return panel;
    }

    /**
     * Sección del agente
     */
    private JPanel crearSeccionAgente() {
        JPanel panel = crearPanelSeccion("Datos del Agente");
        panel.setLayout(new GridLayout(0, 2, 8, 6));

        txtNombreAgente = crearCampo();
        panel.add(label("Nombre agente:"));
        panel.add(txtNombreAgente);
        return panel;
    }

    /**
     * Sección dinámica: muestra campo "Precio" para ventas y campo "Meses" para
     * arriendos.
     */
    private JPanel crearSeccionDinamica() {
        JPanel panel = crearPanelSeccion("Datos de la Transacción");
        panel.setLayout(new GridLayout(0, 2, 8, 6));

        lblPrecioVenta = label("Precio de venta ($):");
        txtPrecioVenta = crearCampo();

        lblMeses = label("Duración (meses):");
        txtMeses = crearCampo();

        panel.add(lblPrecioVenta);
        panel.add(txtPrecioVenta);
        panel.add(lblMeses);
        panel.add(txtMeses);
        return panel;
    }

    // ── 2b. Panel de resultado (lado derecho) ─────────────────────
    private JPanel crearPanelResultado() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBackground(COLOR_FONDO);
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(COLOR_SECUNDARIO),
                "  Factura generada",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 13), COLOR_PRIMARIO
        ));

        txtResultado = new JTextArea();
        txtResultado.setFont(FUENTE_RESULTADO);
        txtResultado.setEditable(false);
        txtResultado.setBackground(new Color(250, 252, 255));
        txtResultado.setForeground(new Color(30, 30, 30));
        txtResultado.setBorder(new EmptyBorder(8, 10, 8, 10));
        txtResultado.setText("Aquí aparecerá la factura generada...");
        txtResultado.setForeground(Color.GRAY);

        JScrollPane scroll = new JScrollPane(txtResultado);
        scroll.setBorder(null);

        btnCopiar = crearBoton("📋 Copiar", new Color(108, 117, 125));
        btnCopiar.setToolTipText("Copiar la factura al portapapeles");

        JPanel barraInferior = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        barraInferior.setBackground(COLOR_FONDO);
        barraInferior.add(btnCopiar);

        panel.add(scroll, BorderLayout.CENTER);
        panel.add(barraInferior, BorderLayout.SOUTH);
        return panel;
    }

    // ── 2c. Barra de botones inferior ─────────────────────────────
    private JPanel crearPanelBotones() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(COLOR_FONDO);
        panel.setBorder(new EmptyBorder(5, 10, 10, 10));

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        botones.setBackground(COLOR_FONDO);

        btnGenerar = crearBoton("✅  Generar Factura", COLOR_BOTON);
        btnLimpiar = crearBoton("🗑  Limpiar", COLOR_LIMPIAR);
        btnExportarPDF = crearBoton("📄  Exportar PDF", COLOR_SECUNDARIO);
        btnGenerar.setPreferredSize(new Dimension(200, 40));
        btnLimpiar.setPreferredSize(new Dimension(130, 40));
        btnExportarPDF.setPreferredSize(new Dimension(170, 40));
        btnExportarPDF.setEnabled(false); // Se habilita al generar una factura

        botones.add(btnGenerar);
        botones.add(btnLimpiar);
        botones.add(btnExportarPDF);

        lblEstado = new JLabel(" ");
        lblEstado.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblEstado.setHorizontalAlignment(SwingConstants.CENTER);
        lblEstado.setForeground(new Color(80, 80, 80));

        panel.add(botones, BorderLayout.CENTER);
        panel.add(lblEstado, BorderLayout.SOUTH);
        return panel;
    }

    // ── 3. Listeners (eventos) ────────────────────────────────────
    private void aplicarListeners() {

        // Cambiar tipo de transacción → mostrar/ocultar campos
        rbVenta.addActionListener(e -> actualizarCamposDinamicos());
        rbArriendo.addActionListener(e -> actualizarCamposDinamicos());

        // Botón principal
        btnGenerar.addActionListener(this::accionGenerarFactura);

        // Botón limpiar
        btnLimpiar.addActionListener(e -> limpiarFormulario());

        // Botón copiar
        btnCopiar.addActionListener(e -> copiarAlPortapapeles());

        // Botón exportar PDF
        btnExportarPDF.addActionListener(e -> accionExportarPDF());
    }

    /**
     * Muestra u oculta los campos según el tipo seleccionado. Venta → muestra
     * "Precio de venta", oculta "Meses". Arriendo→ muestra "Meses", oculta
     * "Precio de venta".
     */
    private void actualizarCamposDinamicos() {
        boolean esVenta = rbVenta.isSelected();
        lblPrecioVenta.setVisible(esVenta);
        txtPrecioVenta.setVisible(esVenta);
        lblMeses.setVisible(!esVenta);
        txtMeses.setVisible(!esVenta);
        panelCamposDinamicos.revalidate();
        panelCamposDinamicos.repaint();
    }

    // ── 4. Lógica del botón "Generar Factura" ─────────────────────
    /**
     * Método principal que se ejecuta al pulsar el botón. Valida los datos,
     * construye los objetos del dominio, invoca GeneradorFactura y muestra el
     * resultado.
     */
    private void accionGenerarFactura(ActionEvent e) {
        try {
            // 4a. Validar campos obligatorios
            if (!validarCampos()) {
                return;
            }

            // 4b. Construir objetos del dominio desde los campos del formulario
            Cliente cliente = construirCliente();
            Propiedad propiedad = construirPropiedad();
            Agente agente = construirAgente();

            // 4c. Crear el generador según el tipo (polimorfismo vía Factory Method)
            GeneradorFactura generador;

            if (rbVenta.isSelected()) {
                double precio = Double.parseDouble(txtPrecioVenta.getText().trim());
                // Usamos el constructor desde BD (precio ya conocido)
                Venta venta = new Venta(1, cliente, propiedad, agente, precio, "Hoy");
                generador = GeneradorFactura.desde(venta);
            } else {
                int meses = Integer.parseInt(txtMeses.getText().trim());
                Arriendo arriendo = new Arriendo(1, cliente, propiedad, agente, meses, "Hoy");
                generador = GeneradorFactura.desde(arriendo);
            }

            // 4d. Llamar a generarFactura() de la interface IFacturable
            String factura = generador.generarFactura();

            // 4e. Mostrar en el área de texto
            txtResultado.setFont(FUENTE_RESULTADO);
            txtResultado.setForeground(new Color(30, 30, 30));
            txtResultado.setText(factura);
            txtResultado.setCaretPosition(0);

            lblEstado.setForeground(new Color(39, 174, 96));
            lblEstado.setText("✔  Factura generada correctamente - N°: "
                    + generador.obtenerNumeroFactura());

            // 4f. Guardar la factura como objeto y habilitar el botón PDF
            facturaActual = generador.obtenerFactura();
            btnExportarPDF.setEnabled(true);

        } catch (NumberFormatException ex) {
            mostrarError("Revisa los campos numéricos (Área, Precio, Meses).");
        } catch (Exception ex) {
            mostrarError("Error inesperado: " + ex.getMessage());
        }
    }
    //NUEVO AGREGADO

    /**
     * Exporta la factura actual a PDF, mostrando "Guardar como" y abriéndolo.
     * Solo se llama si hay una factura generada (el botón está deshabilitado en
     * caso contrario).
     */
    private void accionExportarPDF() {
        if (facturaActual == null) {
            mostrarError("Primero genera una factura antes de exportar.");
            return;
        }
        boolean ok = com.mycompany.proyectosistemainmobiliario.facturacion.ExportadorPDF.exportarFacturaConDialogo(facturaActual, this);
        if (ok) {
            lblEstado.setForeground(new Color(39, 174, 96));
            lblEstado.setText("📄 Factura exportada a PDF correctamente.");
        }
    }

    // ── 5. Construcción de objetos del dominio ────────────────────
    private Cliente construirCliente() {
        return new Cliente(
                1,
                txtNombreCliente.getText().trim(),
                txtCorreo.getText().trim(),
                txtTelefono.getText().trim(),
                (String) cmbTipoDocumento.getSelectedItem(),
                txtNumeroDocumento.getText().trim()
        );
    }

    private Propiedad construirPropiedad() {
        double area = Double.parseDouble(txtArea.getText().trim());
        String dir = txtDireccion.getText().trim();
        String estado = txtEstado.getText().trim();

        // Constructor real de Apartamento: (String numeroApa, int piso,
        //   double administracion, int id, String direccion, double area, String estado)
        // Constructor real de Casa: (boolean tienePatio, int id,
        //   String direccion, double area, String estado)
        if ("Apartamento".equals(cmbTipoPropiedad.getSelectedItem())) {
            return new Apartamento("101", 1, 0.0, 1, dir, area, estado);
        } else {
            return new Casa(false, 1, dir, area, estado);
        }
    }

    private Agente construirAgente() {
        // Constructor real de Agente: (int id, String nombre, String correo,
        //   String telefono, double salario, String codigoEmpleado)
        return new Agente(1, txtNombreAgente.getText().trim(),
                "agente@inmobiliaria.com", "000-000-0000",
                0.0, "EMP-001");
    }

    // ── 6. Validación ─────────────────────────────────────────────
    private boolean validarCampos() {
        if (txtNombreCliente.getText().isBlank()) {
            mostrarError("El nombre del cliente es obligatorio.");
            txtNombreCliente.requestFocus();
            return false;
        }
        if (txtNumeroDocumento.getText().isBlank()) {
            mostrarError("El número de documento es obligatorio.");
            txtNumeroDocumento.requestFocus();
            return false;
        }
        if (txtDireccion.getText().isBlank()) {
            mostrarError("La dirección de la propiedad es obligatoria.");
            txtDireccion.requestFocus();
            return false;
        }
        if (txtArea.getText().isBlank()) {
            mostrarError("El área de la propiedad es obligatoria.");
            txtArea.requestFocus();
            return false;
        }
        if (txtNombreAgente.getText().isBlank()) {
            mostrarError("El nombre del agente es obligatorio.");
            txtNombreAgente.requestFocus();
            return false;
        }
        if (rbVenta.isSelected() && txtPrecioVenta.getText().isBlank()) {
            mostrarError("El precio de venta es obligatorio.");
            txtPrecioVenta.requestFocus();
            return false;
        }
        if (rbArriendo.isSelected() && txtMeses.getText().isBlank()) {
            mostrarError("La duración en meses es obligatoria.");
            txtMeses.requestFocus();
            return false;
        }
        return true;
    }

    // ── 7. Acciones auxiliares ────────────────────────────────────
    private void limpiarFormulario() {
        txtNombreCliente.setText("");
        txtNumeroDocumento.setText("");
        txtCorreo.setText("");
        txtTelefono.setText("");
        txtDireccion.setText("");
        txtArea.setText("");
        txtEstado.setText("Disponible");
        txtNombreAgente.setText("");
        txtPrecioVenta.setText("");
        txtMeses.setText("");
        txtResultado.setText("Aquí aparecerá la factura generada...");
        txtResultado.setForeground(Color.GRAY);
        lblEstado.setText(" ");
        rbVenta.setSelected(true);
        actualizarCamposDinamicos();
        //NUEVO AGREGADO PDF
        rbVenta.setSelected(true);
        facturaActual = null;
        btnExportarPDF.setEnabled(false);

        actualizarCamposDinamicos();
    }

    private void copiarAlPortapapeles() {
        String texto = txtResultado.getText();
        if (!texto.isBlank() && !texto.startsWith("Aquí")) {
            Toolkit.getDefaultToolkit()
                    .getSystemClipboard()
                    .setContents(new StringSelection(texto), null);
            lblEstado.setForeground(new Color(39, 174, 96));
            lblEstado.setText("📋  Factura copiada al portapapeles.");
        }
    }

    private void mostrarError(String mensaje) {
        lblEstado.setForeground(COLOR_LIMPIAR);
        lblEstado.setText("⚠  " + mensaje);
        JOptionPane.showMessageDialog(this, mensaje, "Error de validación",
                JOptionPane.WARNING_MESSAGE);
    }

    // ── 8. Fábrica de componentes con estilo uniforme ─────────────
    private JTextField crearCampo() {
        JTextField campo = new JTextField();
        campo.setFont(FUENTE_CAMPO);
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 190, 200)),
                new EmptyBorder(4, 6, 4, 6)
        ));
        return campo;
    }

    private JLabel label(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(FUENTE_LABEL);
        lbl.setForeground(new Color(50, 60, 70));
        return lbl;
    }

    private JButton crearBoton(String texto, Color color) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(8, 18, 8, 18));
        return btn;
    }

    private JPanel crearPanelSeccion(String titulo) {
        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(new Color(200, 210, 220)),
                        "  " + titulo,
                        TitledBorder.LEFT, TitledBorder.TOP,
                        new Font("Segoe UI", Font.BOLD, 12), COLOR_PRIMARIO
                ),
                new EmptyBorder(6, 8, 8, 8)
        ));
        return panel;
    }

    private void estilizarRadio(JRadioButton rb) {
        rb.setFont(new Font("Segoe UI", Font.BOLD, 13));
        rb.setBackground(Color.WHITE);
        rb.setForeground(COLOR_PRIMARIO);
        rb.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private void estilizarCombo(JComboBox<?> combo) {
        combo.setFont(FUENTE_CAMPO);
        combo.setBackground(Color.WHITE);
    }

    // ══════════════════════════════════════════════════════════════
    //  MAIN — Punto de entrada para probar la ventana de forma aislada
    // ══════════════════════════════════════════════════════════════
    public static void main(String[] args) {
        // Usar el Look & Feel del sistema operativo
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        // Lanzar en el hilo de eventos de Swing (buena práctica)
        SwingUtilities.invokeLater(() -> {
            FacturaSwing ventana = new FacturaSwing();
            ventana.setVisible(true);
        });
    }
}

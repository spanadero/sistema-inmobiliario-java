package com.mycompany.proyectosistemainmobiliario.vistas;

import com.mycompany.proyectosistemainmobiliario.auth.Sesion;
import com.mycompany.proyectosistemainmobiliario.db.Conexion;
import com.mycompany.proyectosistemainmobiliario.facturacion.GeneradorFactura;
import com.mycompany.proyectosistemainmobiliario.modelos.*;
import com.mycompany.proyectosistemainmobiliario.servicios.*;
import java.time.LocalDate;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import org.mindrot.jbcrypt.BCrypt;
//nuevo agregado
import com.mycompany.proyectosistemainmobiliario.modelos.FiltroPropiedad;

public class MenuSwing extends JFrame {

    private final PropiedadService propiedadService = new PropiedadService();
    private final VentaService ventaService = new VentaService();
    private final ArriendoService arriendoService = new ArriendoService();
    private final ClienteService clienteService = new ClienteService();
    private final AgenteService agenteService = new AgenteService();

    // Cola FIFO en memoria (igual que Menu.java original)
    private final Queue<Cliente> colaClientes = new LinkedList<>();

    // Arreglo polimórfico (igual que Menu.java original)
    private final Propiedad[] arregloPropiedades = new Propiedad[5];

    // Colores
    private static final Color AZUL_OSCURO = new Color(22, 60, 110);
    private static final Color AZUL_MED = new Color(41, 98, 170);
    private static final Color AZUL_CLARO = new Color(214, 229, 255);
    private static final Color VERDE = new Color(39, 174, 96);
    private static final Color ROJO = new Color(192, 57, 43);
    private static final Color NARANJA = new Color(211, 84, 0);
    private static final Color MORADO = new Color(125, 60, 152);
    private static final Color FONDO = new Color(242, 245, 252);
    private static final Color BLANCO = Color.WHITE;
    private static final Color GRIS_TEXTO = new Color(50, 60, 80);

    private JPanel panelContenido;

    public MenuSwing() {
        setTitle("Sistema Inmobiliario - BD MySQL");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1050, 700);
        setMinimumSize(new Dimension(860, 580));
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(FONDO);
        root.add(buildHeader(), BorderLayout.NORTH);
        JPanel sidebar = buildSidebar();

        JScrollPane sidebarScroll = new JScrollPane(sidebar);
        sidebarScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        sidebarScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        sidebarScroll.setBorder(null);
        sidebarScroll.getVerticalScrollBar().setUnitIncrement(16);
        sidebar.setPreferredSize(new Dimension(220, 900));

        sidebarScroll.setMinimumSize(new Dimension(220, 100));
        sidebarScroll.setMaximumSize(new Dimension(220, Integer.MAX_VALUE));

        root.add(sidebarScroll, BorderLayout.WEST);

        panelContenido = new JPanel(new BorderLayout());
        panelContenido.setBackground(FONDO);
        panelContenido.setBorder(new EmptyBorder(20, 20, 20, 20));
        panelContenido.add(buildPanelBienvenida(), BorderLayout.CENTER);
        root.add(panelContenido, BorderLayout.CENTER);
        root.add(buildFooter(), BorderLayout.SOUTH);
        setContentPane(root);
        configurarPorRol();
    }

    // ═══════════ HEADER ═══════════
    private JPanel buildHeader() {
        JPanel h = new JPanel(new BorderLayout());
        h.setBackground(AZUL_OSCURO);
        h.setBorder(new EmptyBorder(16, 24, 16, 24));
        JLabel titulo = new JLabel("🏠  Sistema Inmobiliario");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 24));
        titulo.setForeground(BLANCO);
        JLabel sub = new JLabel("Conectado a MySQL · inmobiliaria");
        sub.setFont(new Font("SansSerif", Font.PLAIN, 13));
        sub.setForeground(new Color(160, 195, 240));
        JPanel col = new JPanel();
        col.setOpaque(false);
        col.setLayout(new BoxLayout(col, BoxLayout.Y_AXIS));
        col.add(titulo);
        col.add(sub);
        h.add(col, BorderLayout.WEST);

        // Indicador de conexión
        boolean ok = Conexion.probar();
        JLabel conn = new JLabel(ok ? "  ● BD " : "  ● BD");
        conn.setFont(new Font("SansSerif", Font.BOLD, 12));
        conn.setForeground(ok ? new Color(100, 240, 130) : new Color(255, 100, 100));
        h.add(conn, BorderLayout.EAST);
        return h;
    }

    // ═══════════ SIDEBAR ═══════════
    private JPanel buildSidebar() {
        JPanel sb = new JPanel();
        sb.setPreferredSize(null); // dejará que BoxLayout calcule el alto real
        sb.setLayout(new BoxLayout(sb, BoxLayout.Y_AXIS));
        sb.setBackground(new Color(28, 50, 88));
        sb.setPreferredSize(new Dimension(220, 0));
        sb.setBorder(new EmptyBorder(10, 0, 10, 0));

        addGroup(sb, "CLIENTES");
        addBtn(sb, "1. Registrar cliente", VERDE, () -> cargar(panelRegistrarCliente()));
        addBtn(sb, "2. Agregar cliente a cola", AZUL_MED, () -> cargar(panelAgregarCola()));
        addBtn(sb, "3. Atender cliente", NARANJA, () -> cargar(panelAtenderCliente()));
        addBtn(sb, "Ver todos los clientes", AZUL_MED, () -> cargar(panelVerClientes()));

        addGroup(sb, "PROPIEDADES");
        addBtn(sb, "4. Registrar apartamento", AZUL_MED, () -> cargar(panelRegistrarApartamento()));
        addBtn(sb, "5. Registrar casa", AZUL_MED, () -> cargar(panelRegistrarCasa()));
        addBtn(sb, "6. Mostrar propiedades", AZUL_MED, () -> cargar(panelMostrarPropiedades()));
        addBtn(sb, "🔍 Búsqueda avanzada", VERDE, () -> cargar(panelBusquedaAvanzada()));

        addGroup(sb, "VENTAS");
        addBtn(sb, "7. Realizar venta", VERDE, () -> cargar(panelRealizarVenta()));
        addBtn(sb, "8. Mostrar ventas", AZUL_MED, () -> cargar(panelMostrarVentas()));

        addGroup(sb, "ARRIENDOS");
        addBtn(sb, "9. Registrar arriendo", VERDE, () -> cargar(panelRegistrarArriendo()));
        addBtn(sb, "10. Mostrar arriendos", AZUL_MED, () -> cargar(panelMostrarArriendos()));

        addGroup(sb, "POLIMORFISMO");
        addBtn(sb, "11. Arreglo polimórfico", MORADO, () -> cargar(panelArregloPolimorfico()));

        addGroup(sb, "FACTURAS");
        addBtn(sb, "📜 Historial de facturas", NARANJA, () -> cargar(panelHistorialFacturas()));

        addGroup(sb, "USUARIOS");
        addBtn(sb, "🔑 Cambiar contraseña", MORADO, () -> cargar(panelCambiarContraseña()));
        if (Sesion.getInstance().esAdmin()) {
            addBtn(sb, "👥 Gestionar usuarios", MORADO, () -> cargar(panelUsuarios()));
        }

        sb.add(Box.createVerticalStrut(20));

        // Cerrar sesión — vuelve al login sin cerrar el proceso
        JButton btnCerrarSesion = boton("🔓 Cerrar sesión", NARANJA);
        btnCerrarSesion.setMaximumSize(new Dimension(220, 38));
        btnCerrarSesion.setAlignmentX(CENTER_ALIGNMENT);
        btnCerrarSesion.addActionListener(e -> {
            int r = JOptionPane.showConfirmDialog(this, "¿Cerrar sesión?", "Confirmar", JOptionPane.YES_NO_OPTION);
            if (r == JOptionPane.YES_OPTION) {
                Sesion.getInstance().cerrar();
                dispose();
                SwingUtilities.invokeLater(() -> {
                    LoginDialog login = new LoginDialog(null);
                    login.setVisible(true);
                    if (login.isAutenticado()) {
                        new MenuSwing().setVisible(true);
                    } else {
                        System.exit(0);
                    }
                });
            }
        });

        JButton btnSalir = boton(" Salir", ROJO);
        btnSalir.setMaximumSize(new Dimension(220, 38));
        btnSalir.setAlignmentX(CENTER_ALIGNMENT);
        btnSalir.addActionListener(e -> {
            int r = JOptionPane.showConfirmDialog(this, "¿Salir de la aplicación?", "Confirmar", JOptionPane.YES_NO_OPTION);
            if (r == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        });

        sb.add(Box.createVerticalStrut(6));
        sb.add(btnCerrarSesion);
        sb.add(Box.createVerticalStrut(4));
        sb.add(btnSalir);
        // Forzar que el panel reporte su altura real para que el JScrollPane sepa cuándo mostrar la barra
        sb.setPreferredSize(new Dimension(220, sb.getPreferredSize().height));
        return sb;

    }

    private void addGroup(JPanel sb, String label) {
        sb.add(Box.createVerticalStrut(8));
        JLabel lbl = new JLabel("  " + label);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 10));
        lbl.setForeground(new Color(120, 155, 210));
        lbl.setMaximumSize(new Dimension(220, 20));
        sb.add(lbl);
    }

    private void addBtn(JPanel sb, String texto, Color accent, Runnable accion) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("SansSerif", Font.PLAIN, 13));
        btn.setForeground(new Color(15, 35, 70));
        btn.setBackground(new Color(28, 50, 88));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(new EmptyBorder(9, 18, 9, 10));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(220, 42));
        btn.setMinimumSize(new Dimension(220, 42));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(accent.darker());
                btn.setForeground(new Color(15, 35, 70));
            }

            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(new Color(28, 50, 88));
                btn.setForeground(new Color(15, 35, 70));
            }
        });
        btn.addActionListener(e -> accion.run());
        sb.add(btn);
    }

    private void cargar(JPanel panel) {
        panelContenido.removeAll();
        panelContenido.add(panel, BorderLayout.CENTER);
        panelContenido.revalidate();
        panelContenido.repaint();
    }

    // ═══════════ FOOTER ═══════════
    private JPanel buildFooter() {
        JPanel f = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 6));
        f.setBackground(AZUL_OSCURO);
        JLabel lbl = new JLabel("MySQL JDBC · Cola FIFO · Pila Stack · Arreglo Polimórfico");
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lbl.setForeground(new Color(140, 175, 225));
        f.add(lbl);
        return f;
    }

    // ═══════════ BIENVENIDA ═══════════
    private JPanel buildPanelBienvenida() {
        JPanel wrap = new JPanel(new GridBagLayout());
        wrap.setOpaque(false);
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(BLANCO);
        card.setBorder(new CompoundBorder(new LineBorder(new Color(200, 215, 240), 1, true), new EmptyBorder(36, 52, 36, 52)));

        card.add(lbl("🏠", 52, Font.PLAIN, AZUL_OSCURO, CENTER_ALIGNMENT));
        card.add(Box.createVerticalStrut(10));
        card.add(lbl("Bienvenido al Sistema Inmobiliario", 22, Font.BOLD, AZUL_OSCURO, CENTER_ALIGNMENT));
        card.add(Box.createVerticalStrut(6));
        card.add(lbl("Datos en tiempo real desde MySQL · inmobiliaria", 14, Font.PLAIN, new Color(90, 100, 130), CENTER_ALIGNMENT));
        card.add(Box.createVerticalStrut(24));

        JPanel stats = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 0));
        stats.setOpaque(false);
        stats.add(statCard("Propiedades", String.valueOf(propiedadService.contarPropiedades()), "📦"));
        stats.add(statCard("Clientes", String.valueOf(clienteService.contar()), "👥"));
        stats.add(statCard("Ventas", String.valueOf(ventaService.contar()), "💰"));
        stats.add(statCard("Arriendos", String.valueOf(arriendoService.contar()), "📋"));
        stats.add(statCard("Cola", String.valueOf(colaClientes.size()), "⌛"));
        card.add(stats);
        wrap.add(card);
        return wrap;
    }

    private JPanel statCard(String label, String valor, String icon) {
        JPanel c = new JPanel();
        c.setLayout(new BoxLayout(c, BoxLayout.Y_AXIS));
        c.setBackground(FONDO);
        c.setBorder(new CompoundBorder(new LineBorder(new Color(200, 215, 240), 1, true), new EmptyBorder(14, 20, 14, 20)));
        c.add(lbl(icon, 22, Font.PLAIN, AZUL_OSCURO, CENTER_ALIGNMENT));
        c.add(lbl(valor, 28, Font.BOLD, AZUL_MED, CENTER_ALIGNMENT));
        c.add(lbl(label, 12, Font.PLAIN, GRIS_TEXTO, CENTER_ALIGNMENT));
        return c;
    }

    // ═══════════ 1. REGISTRAR CLIENTE ═══════════
    private JPanel panelRegistrarCliente() {
        JPanel p = card("👤 Registrar Cliente", VERDE);
        JTextField fNombre = campo(), fCorreo = campo(), fTelefono = campo(), fTipoDoc = campo("CC"), fNumDoc = campo();
        JPanel form = formGrid("Nombre:", fNombre, "Correo:", fCorreo, "Teléfono:", fTelefono, "Tipo doc.:", fTipoDoc, "Número doc.:", fNumDoc);
        JButton btn = boton("Guardar en BD", VERDE);
        JLabel res = res();
        btn.addActionListener(e -> {
            String nom = fNombre.getText().trim();
            if (nom.isEmpty()) {
                res.setText("❌ Nombre obligatorio.");
                res.setForeground(ROJO);
                return;
            }
            Cliente c = new Cliente(0, nom, fCorreo.getText().trim(), fTelefono.getText().trim(), fTipoDoc.getText().trim(), fNumDoc.getText().trim());
            clienteService.registrarCliente(c);
            res.setText("✅ Cliente \"" + nom + "\" guardado en BD. ID asignado: " + c.getId());
            res.setForeground(VERDE);
            limpiar(fNombre, fCorreo, fTelefono, fNumDoc);
            fTipoDoc.setText("CC");
        });
        p.add(form, BorderLayout.CENTER);
        p.add(sur(btn, res), BorderLayout.SOUTH);
        return p;
    }

    // ═══════════ Ver clientes ═══════════
    // ═══════════ Ver clientes ═══════════
    private JPanel panelVerClientes() {
        JPanel p = card("👥 Clientes en BD", AZUL_MED);
        String[] cols = {"ID", "Nombre", "Correo", "Teléfono", "Tipo Doc", "Número Doc"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        Runnable recargar = () -> {
            model.setRowCount(0);
            for (Cliente c : clienteService.obtenerTodos()) {
                model.addRow(new Object[]{c.getId(), c.getNombre(), c.getCorreo(), c.getTelefono(), c.getTipoDocumento(), c.getNumeroDocumento()});
            }
        };
        recargar.run();

        JButton ref = boton("🔄 Actualizar", AZUL_MED);
        ref.addActionListener(e -> recargar.run());

        JButton btnPDF = boton("📄 Exportar PDF", AZUL_MED);
        btnPDF.addActionListener(e -> {
            java.util.List<Cliente> todos = clienteService.obtenerTodos();
            com.mycompany.proyectosistemainmobiliario.facturacion.ExportadorPDF
                    .exportarListadoClientesConDialogo(todos, this);
        });

        JPanel sur = new JPanel(new FlowLayout(FlowLayout.LEFT));
        sur.setOpaque(false);
        sur.add(ref);
        sur.add(btnPDF);
        p.add(new JScrollPane(tabla(model)), BorderLayout.CENTER);
        p.add(sur, BorderLayout.SOUTH);
        return p;
    }

    // ═══════════ 2. AGREGAR A COLA ═══════════
    private JPanel panelAgregarCola() {
        JPanel p = card("👥 Agregar Cliente a Cola (FIFO)", AZUL_MED);
        // Combo con clientes de la BD
        List<Cliente> clientes = clienteService.obtenerTodos();
        JComboBox<String> combo = new JComboBox<>();
        for (Cliente c : clientes) {
            combo.addItem("[" + c.getId() + "] " + c.getNombre());
        }

        DefaultListModel<String> colaModel = new DefaultListModel<>();
        JList<String> colaList = new JList<>(colaModel);
        colaList.setFont(new Font("Monospaced", Font.PLAIN, 12));
        actualizarCola(colaModel);

        JButton btn = boton("Agregar a cola", AZUL_MED);
        JLabel res = res();
        btn.addActionListener(e -> {
            int idx = combo.getSelectedIndex();
            if (idx < 0) {
                res.setText("❌ Selecciona un cliente.");
                res.setForeground(ROJO);
                return;
            }
            Cliente c = clientes.get(idx);
            colaClientes.add(c);
            arriendoService.agregarCliente(c);
            res.setText("✅ \"" + c.getNombre() + "\" en cola. Total: " + colaClientes.size());
            res.setForeground(VERDE);
            actualizarCola(colaModel);
        });

        JPanel izq = new JPanel(new BorderLayout(0, 8));
        izq.setOpaque(false);
        izq.add(lbl("Selecciona cliente de la BD:", 13, Font.BOLD, AZUL_OSCURO, LEFT_ALIGNMENT), BorderLayout.NORTH);
        izq.add(combo, BorderLayout.CENTER);

        JPanel der = new JPanel(new BorderLayout(0, 6));
        der.setOpaque(false);
        der.add(lbl("Cola actual:", 13, Font.BOLD, AZUL_OSCURO, LEFT_ALIGNMENT), BorderLayout.NORTH);
        der.add(new JScrollPane(colaList), BorderLayout.CENTER);

        JPanel centro = new JPanel(new GridLayout(1, 2, 16, 0));
        centro.setOpaque(false);
        centro.add(izq);
        centro.add(der);
        p.add(centro, BorderLayout.CENTER);
        p.add(sur(btn, res), BorderLayout.SOUTH);
        return p;
    }

    private void actualizarCola(DefaultListModel<String> m) {
        m.clear();
        if (colaClientes.isEmpty()) {
            m.addElement("  (vacía)");
            return;
        }
        int i = 1;
        for (Cliente c : colaClientes) {
            m.addElement("  " + i++ + ". " + c.getNombre() + " [ID:" + c.getId() + "]");
        }
    }

    // ═══════════ 3. ATENDER CLIENTE ═══════════
    private JPanel panelAtenderCliente() {
        JPanel p = card("🎯 Atender Cliente (FIFO)", NARANJA);
        DefaultListModel<String> colaModel = new DefaultListModel<>();
        JList<String> colaList = new JList<>(colaModel);
        colaList.setFont(new Font("Monospaced", Font.PLAIN, 13));
        actualizarCola(colaModel);
        JButton btn = boton("Atender siguiente", NARANJA);
        JLabel res = res();
        btn.addActionListener(e -> {
            Cliente c = colaClientes.poll();
            arriendoService.atenderCliente();
            if (c != null) {
                res.setText("✅ Atendiendo: " + c.getNombre() + " [ID:" + c.getId() + "]");
                res.setForeground(VERDE);
            } else {
                res.setText("⚠️ No hay clientes en espera.");
                res.setForeground(NARANJA);
            }
            actualizarCola(colaModel);
        });
        JPanel centro = new JPanel(new BorderLayout(0, 8));
        centro.setOpaque(false);
        centro.add(lbl("Cola de espera:", 13, Font.BOLD, AZUL_OSCURO, LEFT_ALIGNMENT), BorderLayout.NORTH);
        centro.add(new JScrollPane(colaList), BorderLayout.CENTER);
        p.add(centro, BorderLayout.CENTER);
        p.add(sur(btn, res), BorderLayout.SOUTH);
        return p;
    }

    // ═══════════ 4. REGISTRAR APARTAMENTO ═══════════
    private JPanel panelRegistrarApartamento() {
        JPanel p = card("🏢 Registrar Apartamento", AZUL_MED);
        JTextField fDir = campo(), fArea = campo(), fPiso = campo(), fAdmin = campo(), fNumApa = campo("A1");
        JPanel form = formGrid("Dirección:", fDir, "Área (m²):", fArea, "Piso:", fPiso, "Administración $:", fAdmin, "Número apto.:", fNumApa);
        JButton btn = boton("Guardar en BD", AZUL_MED);
        JLabel res = res();
        btn.addActionListener(e -> {
            try {
                double area = Double.parseDouble(fArea.getText().trim());
                int piso = Integer.parseInt(fPiso.getText().trim());
                double admin = Double.parseDouble(fAdmin.getText().trim());
                Apartamento a = new Apartamento(fNumApa.getText().trim(), piso, admin, 0, fDir.getText().trim(), area, "disponible");
                propiedadService.registrarApartamento(a);
                res.setText(String.format("✅ Apartamento guardado. Precio calculado: $%,.0f", a.calcularPrecio()));
                res.setForeground(VERDE);
                limpiar(fDir, fArea, fPiso, fAdmin);
                fNumApa.setText("A1");
            } catch (NumberFormatException ex) {
                res.setText("❌ Verifica números.");
                res.setForeground(ROJO);
            }
        });
        p.add(form, BorderLayout.CENTER);
        p.add(sur(btn, res), BorderLayout.SOUTH);
        return p;
    }

    // ═══════════ 5. REGISTRAR CASA ═══════════
    private JPanel panelRegistrarCasa() {
        JPanel p = card("🏡 Registrar Casa", AZUL_MED);
        JTextField fDir = campo(), fArea = campo();
        JCheckBox cbPatio = new JCheckBox("¿Tiene patio?");
        cbPatio.setOpaque(false);
        cbPatio.setFont(new Font("SansSerif", Font.PLAIN, 13));
        JPanel form = new JPanel(new GridLayout(0, 2, 10, 10));
        form.setOpaque(false);
        form.add(lbl("Dirección:", 13, Font.PLAIN, GRIS_TEXTO, LEFT_ALIGNMENT));
        form.add(fDir);
        form.add(lbl("Área (m²):", 13, Font.PLAIN, GRIS_TEXTO, LEFT_ALIGNMENT));
        form.add(fArea);
        form.add(new JLabel());
        form.add(cbPatio);
        JButton btn = boton("Guardar en BD", AZUL_MED);
        JLabel res = res();
        btn.addActionListener(e -> {
            try {
                double area = Double.parseDouble(fArea.getText().trim());
                Casa c = new Casa(cbPatio.isSelected(), 0, fDir.getText().trim(), area, "disponible");
                propiedadService.registrarCasa(c);
                res.setText(String.format("✅ Casa guardada. Precio calculado: $%,.0f", c.calcularPrecio()));
                res.setForeground(VERDE);
                limpiar(fDir, fArea);
                cbPatio.setSelected(false);
            } catch (NumberFormatException ex) {
                res.setText("❌ Verifica el área.");
                res.setForeground(ROJO);
            }
        });
        p.add(form, BorderLayout.CENTER);
        p.add(sur(btn, res), BorderLayout.SOUTH);
        return p;
    }

    // ═══════════ 6. MOSTRAR PROPIEDADES ═══════════
    private JPanel panelMostrarPropiedades() {
        JPanel p = card("📦 Propiedades en BD", AZUL_MED);
        String[] cols = {"ID", "Tipo", "Dirección", "Área m²", "Estado", "Precio $"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        cargarTablaProp(model);

        JButton ref = boton("🔄 Actualizar", AZUL_MED);
        ref.addActionListener(e -> cargarTablaProp(model));

        JButton btnPDF = boton("📄 Exportar PDF", AZUL_MED);
        btnPDF.addActionListener(e -> {
            java.util.List<Propiedad> todas = propiedadService.obtenerTodas();
            com.mycompany.proyectosistemainmobiliario.facturacion.ExportadorPDF
                    .exportarListadoPropiedadesConDialogo(todas, "Ninguno (todas las propiedades)", this);
        });

        JPanel sur = new JPanel(new FlowLayout(FlowLayout.LEFT));
        sur.setOpaque(false);
        sur.add(ref);
        sur.add(btnPDF);
        p.add(new JScrollPane(tabla(model)), BorderLayout.CENTER);
        p.add(sur, BorderLayout.SOUTH);
        return p;
    }

    // ═══════════ 7. REALIZAR VENTA ═══════════
    private JPanel panelRealizarVenta() {
        JPanel p = card("💰 Realizar Venta", VERDE);

        // Solo propiedades disponibles para venta
        List<Propiedad> props = propiedadService.obtenerTodas().stream()
                .filter(pr -> "disponible".equals(pr.getEstado()))
                .collect(java.util.stream.Collectors.toList());
        List<Agente> agentes = agenteService.obtenerTodos();

        JComboBox<String> comboProp = new JComboBox<>();
        JComboBox<String> comboAgente = new JComboBox<>();
        JTextField fFecha = campo(LocalDate.now().toString());

        if (props.isEmpty()) {
            comboProp.addItem("— No hay propiedades disponibles -");
        }
        for (Propiedad prop : props) {
            comboProp.addItem("[" + prop.getId() + "] " + prop.getDireccion());
        }
        for (Agente a : agentes) {
            comboAgente.addItem("[" + a.getId() + "] " + a.getNombre());
        }

        // Cola visual
        DefaultListModel<String> colaModel = new DefaultListModel<>();
        JList<String> colaList = new JList<>(colaModel);
        colaList.setFont(new Font("Monospaced", Font.PLAIN, 12));
        actualizarCola(colaModel);

        JPanel form = new JPanel(new GridLayout(0, 2, 10, 10));
        form.setOpaque(false);
        form.add(lbl("Propiedad:", 13, Font.PLAIN, GRIS_TEXTO, LEFT_ALIGNMENT));
        form.add(comboProp);
        form.add(lbl("Agente:", 13, Font.PLAIN, GRIS_TEXTO, LEFT_ALIGNMENT));
        form.add(comboAgente);
        form.add(lbl("Fecha (yyyy-mm-dd):", 13, Font.PLAIN, GRIS_TEXTO, LEFT_ALIGNMENT));
        form.add(fFecha);

        JButton btn = boton("Registrar venta (toma 1° cliente de cola)", VERDE);
        JLabel res = res();
        btn.addActionListener(e -> {
            if (props.isEmpty()) {
                res.setText("❌ No hay propiedades disponibles para vender.");
                res.setForeground(ROJO);
                return;
            }
            Cliente cliente = colaClientes.poll();
            if (cliente == null) {
                res.setText("⚠️ No hay clientes en cola. Usa opción 2 primero.");
                res.setForeground(NARANJA);
                actualizarCola(colaModel);
                return;
            }
            if (comboProp.getSelectedIndex() < 0 || comboAgente.getSelectedIndex() < 0) {
                res.setText("❌ Selecciona propiedad y agente.");
                res.setForeground(ROJO);
                colaClientes.add(cliente);
                return;
            }
            Propiedad prop = props.get(comboProp.getSelectedIndex());
            Agente agente = agentes.get(comboAgente.getSelectedIndex());
            String fecha = fFecha.getText().trim().isEmpty() ? LocalDate.now().toString() : fFecha.getText().trim();
            Venta venta = new Venta(0, cliente, prop, agente, fecha);
            boolean exito = ventaService.realizarVenta(venta);

            if (!exito) {
                // La propiedad ya no está disponible — devolver cliente a cola
                colaClientes.add(cliente);
                actualizarCola(colaModel);
                res.setText("❌ La propiedad ya no está disponible. Refrescando lista...");
                res.setForeground(ROJO);
                // Recargar lista de propiedades disponibles
                comboProp.removeAllItems();
                props.clear();
                props.addAll(propiedadService.obtenerTodas().stream()
                        .filter(pr -> "disponible".equals(pr.getEstado()))
                        .collect(java.util.stream.Collectors.toList()));
                if (props.isEmpty()) {
                    comboProp.addItem("- No hay propiedades disponibles -");
                }
                for (Propiedad pr : props) {
                    comboProp.addItem("[" + pr.getId() + "] " + pr.getDireccion());
                }
                return;
            }

            res.setText(String.format("✅ Venta registrada. Cliente: %s | $%,.0f", cliente.getNombre(), venta.getPrecioFinal()));
            res.setForeground(VERDE);
            // Recargar lista propiedades (solo disponibles)
            comboProp.removeAllItems();
            props.clear();
            props.addAll(propiedadService.obtenerTodas().stream()
                    .filter(pr -> "disponible".equals(pr.getEstado()))
                    .collect(java.util.stream.Collectors.toList()));
            if (props.isEmpty()) {
                comboProp.addItem("- No hay propiedades disponibles -");
            }
            for (Propiedad pr : props) {
                comboProp.addItem("[" + pr.getId() + "] " + pr.getDireccion());
            }
            actualizarCola(colaModel);
        });

        JPanel der = new JPanel(new BorderLayout(0, 6));
        der.setOpaque(false);
        der.add(lbl("Cola:", 13, Font.BOLD, AZUL_OSCURO, LEFT_ALIGNMENT), BorderLayout.NORTH);
        der.add(new JScrollPane(colaList), BorderLayout.CENTER);
        JPanel centro = new JPanel(new GridLayout(1, 2, 16, 0));
        centro.setOpaque(false);
        centro.add(form);
        centro.add(der);
        p.add(centro, BorderLayout.CENTER);
        p.add(sur(btn, res), BorderLayout.SOUTH);
        return p;
    }

    // ═══════════ 8. MOSTRAR VENTAS ═══════════
    private JPanel panelMostrarVentas() {
        JPanel p = card("📊 Historial de Ventas", AZUL_MED);
        String[] cols = {"ID", "Cliente", "Propiedad", "Agente", "Precio $", "Fecha"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        cargarTablaVentas(model);

        JLabel lblUltima = new JLabel();
        lblUltima.setFont(new Font("SansSerif", Font.ITALIC, 13));
        lblUltima.setForeground(AZUL_MED);
        Venta u = ventaService.getUltimaVentaPila();
        lblUltima.setText(u != null ? "🔝 Última (pila): " + u.getCliente().getNombre() + " - $" + String.format("%,.0f", u.getPrecioFinal()) : "(sin ventas en pila aún)");

        JButton ref = boton("🔄 Actualizar", AZUL_MED);
        ref.addActionListener(e -> {
            cargarTablaVentas(model);
            Venta uu = ventaService.getUltimaVentaPila();
            if (uu != null) {
                lblUltima.setText("🔝 Última (pila): " + uu.getCliente().getNombre() + " - $" + String.format("%,.0f", uu.getPrecioFinal()));
            }
        });

        JButton btnPDF = boton("📄 Exportar PDF", AZUL_MED);
        btnPDF.addActionListener(e -> {
            java.util.List<Venta> todas = ventaService.obtenerTodas();
            com.mycompany.proyectosistemainmobiliario.facturacion.ExportadorPDF
                    .exportarListadoVentasConDialogo(todas, this);
        });

        JPanel sur = new JPanel(new BorderLayout());
        sur.setOpaque(false);
        sur.add(lblUltima, BorderLayout.WEST);
        JPanel bp = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bp.setOpaque(false);
        bp.add(ref);
        bp.add(btnPDF);
        sur.add(bp, BorderLayout.EAST);
        p.add(new JScrollPane(tabla(model)), BorderLayout.CENTER);
        p.add(sur, BorderLayout.SOUTH);
        return p;
    }

    // ═══════════ 9. REGISTRAR ARRIENDO ═══════════
    private JPanel panelRegistrarArriendo() {
        JPanel p = card("📋 Registrar Arriendo", VERDE);

        // Solo propiedades disponibles para arrendar
        List<Propiedad> props = propiedadService.obtenerTodas().stream()
                .filter(pr -> "disponible".equals(pr.getEstado()))
                .collect(java.util.stream.Collectors.toList());
        List<Cliente> clientes = clienteService.obtenerTodos();
        List<Agente> agentes = agenteService.obtenerTodos();

        JComboBox<String> comboProp = new JComboBox<>();
        JComboBox<String> comboCliente = new JComboBox<>();
        JComboBox<String> comboAgente = new JComboBox<>();
        JTextField fMeses = campo(), fFecha = campo(LocalDate.now().toString());

        if (props.isEmpty()) {
            comboProp.addItem("- No hay propiedades disponibles -");
        }
        for (Propiedad pr : props) {
            comboProp.addItem("[" + pr.getId() + "] " + pr.getDireccion());
        }
        for (Cliente c : clientes) {
            comboCliente.addItem("[" + c.getId() + "] " + c.getNombre());
        }
        for (Agente a : agentes) {
            comboAgente.addItem("[" + a.getId() + "] " + a.getNombre());
        }

        JPanel form = new JPanel(new GridLayout(0, 2, 10, 10));
        form.setOpaque(false);
        form.add(lbl("Propiedad:", 13, Font.PLAIN, GRIS_TEXTO, LEFT_ALIGNMENT));
        form.add(comboProp);
        form.add(lbl("Cliente:", 13, Font.PLAIN, GRIS_TEXTO, LEFT_ALIGNMENT));
        form.add(comboCliente);
        form.add(lbl("Agente:", 13, Font.PLAIN, GRIS_TEXTO, LEFT_ALIGNMENT));
        form.add(comboAgente);
        form.add(lbl("Meses:", 13, Font.PLAIN, GRIS_TEXTO, LEFT_ALIGNMENT));
        form.add(fMeses);
        form.add(lbl("Fecha inicio (yyyy-mm-dd):", 13, Font.PLAIN, GRIS_TEXTO, LEFT_ALIGNMENT));
        form.add(fFecha);

        JButton btn = boton("Registrar arriendo en BD", VERDE);
        JLabel res = res();
        btn.addActionListener(e -> {
            try {
                if (props.isEmpty()) {
                    res.setText("❌ No hay propiedades disponibles para arrendar.");
                    res.setForeground(ROJO);
                    return;
                }
                if (comboProp.getSelectedIndex() < 0 || comboCliente.getSelectedIndex() < 0 || comboAgente.getSelectedIndex() < 0) {
                    res.setText("❌ Selecciona propiedad, cliente y agente.");
                    res.setForeground(ROJO);
                    return;
                }
                int meses = Integer.parseInt(fMeses.getText().trim());
                Propiedad prop = props.get(comboProp.getSelectedIndex());
                Cliente cliente = clientes.get(comboCliente.getSelectedIndex());
                Agente agente = agentes.get(comboAgente.getSelectedIndex());
                String fecha = fFecha.getText().trim().isEmpty() ? LocalDate.now().toString() : fFecha.getText().trim();
                Arriendo a = new Arriendo(0, cliente, prop, agente, meses, fecha);
                boolean exito = arriendoService.registrarArriendo(a);

                if (!exito) {
                    res.setText("❌ La propiedad ya no está disponible. Refrescando lista...");
                    res.setForeground(ROJO);
                    comboProp.removeAllItems();
                    props.clear();
                    props.addAll(propiedadService.obtenerTodas().stream()
                            .filter(pr -> "disponible".equals(pr.getEstado()))
                            .collect(java.util.stream.Collectors.toList()));
                    if (props.isEmpty()) {
                        comboProp.addItem("- No hay propiedades disponibles -");
                    }
                    for (Propiedad pr : props) {
                        comboProp.addItem("[" + pr.getId() + "] " + pr.getDireccion());
                    }
                    return;
                }

                res.setText(String.format("✅ Arriendo registrado. Mensual: $%,.0f | Total: $%,.0f", a.getValorMensual(), a.calcularTotalArriendo()));
                res.setForeground(VERDE);
                limpiar(fMeses);
                fFecha.setText(LocalDate.now().toString());

                // Recargar propiedades disponibles
                comboProp.removeAllItems();
                props.clear();
                props.addAll(propiedadService.obtenerTodas().stream()
                        .filter(pr -> "disponible".equals(pr.getEstado()))
                        .collect(java.util.stream.Collectors.toList()));
                if (props.isEmpty()) {
                    comboProp.addItem("- No hay propiedades disponibles -");
                }
                for (Propiedad pr : props) {
                    comboProp.addItem("[" + pr.getId() + "] " + pr.getDireccion());
                }
            } catch (NumberFormatException ex) {
                res.setText("❌ Meses debe ser número.");
                res.setForeground(ROJO);
            }
        });
        p.add(form, BorderLayout.CENTER);
        p.add(sur(btn, res), BorderLayout.SOUTH);
        return p;
    }

    // ═══════════ 10. MOSTRAR ARRIENDOS ═══════════
    private JPanel panelMostrarArriendos() {
        JPanel p = card("📋 Historial de Arriendos", AZUL_MED);
        String[] cols = {"ID", "Cliente", "Propiedad", "Agente", "Valor/mes $", "Meses", "Total $", "Inicio"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        cargarTablaArriendos(model);

        JButton ref = boton("🔄 Actualizar", AZUL_MED);
        ref.addActionListener(e -> cargarTablaArriendos(model));

        JButton btnPDF = boton("📄 Exportar PDF", AZUL_MED);
        btnPDF.addActionListener(e -> {
            java.util.List<Arriendo> todos = arriendoService.obtenerTodos();
            com.mycompany.proyectosistemainmobiliario.facturacion.ExportadorPDF
                    .exportarListadoArriendosConDialogo(todos, this);
        });

        JPanel sur = new JPanel(new FlowLayout(FlowLayout.LEFT));
        sur.setOpaque(false);
        sur.add(ref);
        sur.add(btnPDF);
        p.add(new JScrollPane(tabla(model)), BorderLayout.CENTER);
        p.add(sur, BorderLayout.SOUTH);
        return p;
    }

    private void cargarTablaArriendos(DefaultTableModel m) {
        m.setRowCount(0);
        for (Arriendo a : arriendoService.obtenerTodos()) {
            m.addRow(new Object[]{a.getId(), a.getCliente().getNombre(), a.getPropiedad().getDireccion(), a.getAgente().getNombre(), String.format("%,.0f", a.getValorMensual()), a.getMeses(), String.format("%,.0f", a.calcularTotalArriendo()), a.getFechaInicio()});
        }
    }

    // ═══════════ 11. ARREGLO POLIMÓRFICO ═══════════
    private JPanel panelArregloPolimorfico() {
        JPanel p = card("🔷 Demostración de Arreglo Polimórfico", MORADO);
        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setFont(new Font("Monospaced", Font.PLAIN, 13));
        area.setBackground(new Color(245, 240, 255));
        area.setBorder(new EmptyBorder(12, 12, 12, 12));
        JButton btn = boton("Llenar y mostrar arreglo", MORADO);
        JLabel res = res();
        btn.addActionListener(e -> {
            arregloPropiedades[0] = new Casa(true, 1, "Calle 1", 120, "disponible");
            arregloPropiedades[1] = new Apartamento("A1", 5, 200000, 2, "Calle 2", 80, "disponible");
            StringBuilder sb = new StringBuilder("Recorrido con polimorfismo (calcularPrecio()):\n");
            sb.append("=".repeat(55)).append("\n");
            for (int i = 0; i < arregloPropiedades.length; i++) {
                Propiedad prop = arregloPropiedades[i];
                if (prop != null) {
                    sb.append(String.format("[%d] %s\n", i, (prop instanceof Casa) ? "Casa" : "Apartamento"));
                    sb.append(String.format("    Dirección : %s\n", prop.getDireccion()));
                    sb.append(String.format("    Área      : %.1f m²\n", prop.getArea()));
                    sb.append(String.format("    Precio    : $%,.0f\n", prop.calcularPrecio()));
                    sb.append("-".repeat(45)).append("\n");
                } else {
                    sb.append(String.format("[%d] null\n", i));
                }
            }
            area.setText(sb.toString());
            res.setText("✅ Arreglo ejecutado.");
            res.setForeground(MORADO);
        });
        p.add(new JScrollPane(area), BorderLayout.CENTER);
        p.add(sur(btn, res), BorderLayout.SOUTH);
        return p;
    }

    //PANELES EXTRA(SOLO ADMIN)
    private JPanel panelUsuarios() {
        JPanel p = card("👥 Usuarios en el sistema", AZUL_MED);
        UsuarioService svc = new UsuarioService();

        String[] cols = {"ID", "Username", "Nombre", "Rol", "Activo"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        Runnable recargar = () -> {
            model.setRowCount(0);
            for (Usuario u : svc.listarTodos()) {
                model.addRow(new Object[]{u.getId(), u.getUsername(), u.getNombre(), u.getRol(), u.isActivo() ? "✅" : "❌"});
            }
        };
        recargar.run();

        JTable tabla = tabla(model);
        JButton btnNuevo = boton("➕ Nuevo usuario", VERDE);
        JButton btnDesactivar = boton("🔕 Desactivar", ROJO);
        JButton btnRefresh = boton("🔄 Actualizar", AZUL_MED);
        JLabel res = res();

        btnNuevo.addActionListener(e -> {
            mostrarDialogUsuario();
            recargar.run();
        });

        btnDesactivar.addActionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (fila < 0) {
                res.setText("❌ Selecciona un usuario.");
                res.setForeground(ROJO);
                return;
            }
            int id = (int) model.getValueAt(fila, 0);
            if (id == Sesion.getInstance().getUsuario().getId()) {
                res.setText("❌ No puedes desactivarte a ti mismo.");
                res.setForeground(ROJO);
                return;
            }
            svc.desactivar(id);
            recargar.run();
            res.setText("✅ Usuario desactivado.");
            res.setForeground(VERDE);
        });

        btnRefresh.addActionListener(e -> recargar.run());

        JPanel sur = new JPanel(new FlowLayout(FlowLayout.LEFT));
        sur.setOpaque(false);
        sur.add(btnNuevo);
        sur.add(btnRefresh);
        sur.add(btnDesactivar);
        sur.add(res);
        p.add(new JScrollPane(tabla), BorderLayout.CENTER);
        p.add(sur, BorderLayout.SOUTH);
        return p;
    }

    private JPanel panelCambiarContraseña() {
        JPanel p = card("🔑 Cambiar Contraseña", MORADO);

        JPasswordField fActual = new JPasswordField();
        JPasswordField fNueva = new JPasswordField();
        JPasswordField fConfirm = new JPasswordField();

        fActual.setFont(new Font("SansSerif", Font.PLAIN, 13));
        fNueva.setFont(new Font("SansSerif", Font.PLAIN, 13));
        fConfirm.setFont(new Font("SansSerif", Font.PLAIN, 13));

        JPanel form = formGrid(
                "Contraseña actual:", fActual,
                "Nueva contraseña:", fNueva,
                "Confirmar nueva:", fConfirm
        );

        JButton btn = boton("Actualizar contraseña", MORADO);
        JLabel res = res();

        btn.addActionListener(e -> {
            String actual = new String(fActual.getPassword()).trim();
            String nueva = new String(fNueva.getPassword()).trim();
            String confirm = new String(fConfirm.getPassword()).trim();

            if (actual.isEmpty() || nueva.isEmpty() || confirm.isEmpty()) {
                res.setText("❌ Completa todos los campos.");
                res.setForeground(ROJO);
                return;
            }
            if (!nueva.equals(confirm)) {
                res.setText("❌ Las contraseñas no coinciden.");
                res.setForeground(ROJO);
                return;
            }
            if (nueva.length() < 6) {
                res.setText("❌ Mínimo 6 caracteres.");
                res.setForeground(ROJO);
                return;
            }

            Usuario u = Sesion.getInstance().getUsuario();
            if (!BCrypt.checkpw(actual, u.getPasswordHash())) {
                res.setText("❌ Contraseña actual incorrecta.");
                res.setForeground(ROJO);
                return;
            }

            new UsuarioService().cambiarContraseña(u.getId(), nueva);
            res.setText("✅ Contraseña actualizada.");
            res.setForeground(VERDE);
            fActual.setText("");
            fNueva.setText("");
            fConfirm.setText("");
        });

        p.add(form, BorderLayout.CENTER);
        p.add(sur(btn, res), BorderLayout.SOUTH);
        return p;
    }

    //Registrar usuario
    private void mostrarDialogUsuario() {
        JTextField fUsername = new JTextField();
        JPasswordField fPassword = new JPasswordField();
        JTextField fNombre = new JTextField();
        JComboBox<Rol> fRol = new JComboBox<>(Rol.values());

        JPanel form = new JPanel(new GridLayout(8, 1, 0, 6));
        form.add(new JLabel("Username:"));
        form.add(fUsername);
        form.add(new JLabel("Contraseña:"));
        form.add(fPassword);
        form.add(new JLabel("Nombre:"));
        form.add(fNombre);
        form.add(new JLabel("Rol:"));
        form.add(fRol);

        int result = JOptionPane.showConfirmDialog(
                this, form, "Nuevo Usuario", JOptionPane.OK_CANCEL_OPTION);

        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        String username = fUsername.getText().trim();
        String password = new String(fPassword.getPassword()).trim();
        String nombre = fNombre.getText().trim();

        if (username.isEmpty() || password.isEmpty() || nombre.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Completa todos los campos.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (password.length() < 6) {
            JOptionPane.showMessageDialog(this, "Mínimo 6 caracteres.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Usuario u = new Usuario(0, username, "", (Rol) fRol.getSelectedItem(), true, nombre);
        new UsuarioService().registrarUsuario(u, password);
        JOptionPane.showMessageDialog(this, "✅ Usuario \"" + nombre + "\" creado correctamente.");
    }

    //CONFIGURAR POR ROL EL MENU SWING
    private void configurarPorRol() {
        boolean esAdmin = Sesion.getInstance().esAdmin();

        Usuario u = Sesion.getInstance().getUsuario();
        setTitle("Sistema Inmobiliario - " + u.getNombre() + " [" + u.getRol() + "]");
    }

    // ═══════════ HELPERS UI ═══════════
    private JPanel card(String titulo, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout(0, 12));
        card.setBackground(BLANCO);
        card.setBorder(new CompoundBorder(new LineBorder(new Color(210, 220, 240), 1, true), new EmptyBorder(20, 24, 20, 24)));
        JPanel tBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        tBar.setOpaque(false);
        JPanel barra = new JPanel();
        barra.setBackground(accentColor);
        barra.setPreferredSize(new Dimension(5, 32));
        JLabel tLbl = new JLabel("  " + titulo);
        tLbl.setFont(new Font("SansSerif", Font.BOLD, 20));
        tLbl.setForeground(AZUL_OSCURO);
        tBar.add(barra);
        tBar.add(tLbl);
        card.add(tBar, BorderLayout.NORTH);
        return card;
    }

    private JPanel formGrid(Object... pairs) {
        JPanel f = new JPanel(new GridLayout(0, 2, 10, 10));
        f.setOpaque(false);
        for (int i = 0; i + 1 < pairs.length; i += 2) {
            f.add(pairs[i] instanceof String ? lbl((String) pairs[i], 13, Font.PLAIN, GRIS_TEXTO, LEFT_ALIGNMENT) : (Component) pairs[i]);
            f.add((Component) pairs[i + 1]);
        }
        return f;
    }

    private JPanel sur(JButton btn, JLabel res) {
        JPanel s = new JPanel(new BorderLayout(0, 8));
        s.setOpaque(false);
        JPanel bp = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bp.setOpaque(false);
        bp.add(btn);
        s.add(bp, BorderLayout.NORTH);
        s.add(res, BorderLayout.CENTER);
        return s;
    }

    private JTable tabla(DefaultTableModel model) {
        JTable t = new JTable(model);
        t.setRowHeight(26);
        t.setFont(new Font("SansSerif", Font.PLAIN, 13));
        t.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        t.getTableHeader().setBackground(AZUL_OSCURO);
        t.getTableHeader().setForeground(BLANCO);
        t.setSelectionBackground(AZUL_CLARO);
        t.setGridColor(new Color(220, 228, 245));
        t.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable tbl, Object val, boolean sel, boolean foc, int row, int col) {
                Component c = super.getTableCellRendererComponent(tbl, val, sel, foc, row, col);
                if (!sel) {
                    c.setBackground(row % 2 == 0 ? BLANCO : new Color(240, 245, 255));
                }
                return c;
            }
        });
        return t;
    }

    private JTextField campo() {
        JTextField f = new JTextField();
        f.setFont(new Font("SansSerif", Font.PLAIN, 13));
        f.setBorder(new CompoundBorder(new LineBorder(new Color(190, 205, 230), 1, true), new EmptyBorder(4, 8, 4, 8)));
        return f;
    }

    private JTextField campo(String v) {
        JTextField f = campo();
        f.setText(v);
        return f;
    }

    private JButton boton(String t, Color c) {
        JButton b = new JButton(t);
        b.setBackground(c);
        b.setForeground(BLANCO);
        b.setFont(new Font("SansSerif", Font.BOLD, 13));
        b.setBorder(new EmptyBorder(9, 18, 9, 18));
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private JLabel res() {
        JLabel l = new JLabel(" ");
        l.setFont(new Font("SansSerif", Font.PLAIN, 13));
        return l;
    }

    private JLabel lbl(String t, int sz, int st, Color c, float al) {
        JLabel l = new JLabel(t);
        l.setFont(new Font("SansSerif", st, sz));
        l.setForeground(c);
        l.setAlignmentX(al);
        return l;
    }

    private void limpiar(JTextField... fs) {
        for (JTextField f : fs) {
            f.setText("");
        }
    }

    // ═══════════ HISTORIAL DE FACTURAS ═══════════
    private JPanel panelHistorialFacturas() {
        JPanel p = card("📜 Historial de Facturas", NARANJA);

        String[] colsV = {"ID", "Cliente", "Propiedad", "Agente", "Total $", "Fecha"};
        DefaultTableModel modelV = new DefaultTableModel(colsV, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        JTable tablaV = tabla(modelV);

        String[] colsA = {"ID", "Cliente", "Propiedad", "Meses", "Total $", "Inicio"};
        DefaultTableModel modelA = new DefaultTableModel(colsA, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        JTable tablaA = tabla(modelA);

        Runnable recargar = () -> {
            modelV.setRowCount(0);
            for (Venta v : ventaService.obtenerTodas()) {
                modelV.addRow(new Object[]{
                    v.getId(), v.getCliente().getNombre(),
                    v.getPropiedad().getDireccion(), v.getAgente().getNombre(),
                    String.format("$%,.0f", v.getPrecioFinal()), v.getFecha()
                });
            }
            modelA.setRowCount(0);
            for (Arriendo a : arriendoService.obtenerTodos()) {
                modelA.addRow(new Object[]{
                    a.getId(), a.getCliente().getNombre(),
                    a.getPropiedad().getDireccion(), a.getMeses(),
                    String.format("$%,.0f", a.calcularTotalArriendo()), a.getFechaInicio()
                });
            }
        };
        recargar.run();

        JLabel res = res();

        JButton btnVerVenta = boton("📜 Ver factura de venta", NARANJA);
        btnVerVenta.addActionListener(e -> {
            int fila = tablaV.getSelectedRow();
            if (fila < 0) {
                res.setText("❌ Selecciona una venta.");
                res.setForeground(ROJO);
                return;
            }
            int idVenta = (int) modelV.getValueAt(fila, 0);
            ventaService.obtenerTodas().stream()
                    .filter(v -> v.getId() == idVenta).findFirst()
                    .ifPresent(v -> {
                        GeneradorFactura gen = GeneradorFactura.desde(v);
                        JTextArea area = new JTextArea(gen.generarFactura());
                        area.setEditable(false);
                        area.setFont(new Font("Monospaced", Font.PLAIN, 12));
                        area.setPreferredSize(new java.awt.Dimension(500, 400));
                        JOptionPane.showMessageDialog(this, new JScrollPane(area),
                                "Factura Venta #" + idVenta, JOptionPane.INFORMATION_MESSAGE);
                    });
        });

        JButton btnVerArriendo = boton("🧾 Ver factura de arriendo", VERDE);
        btnVerArriendo.addActionListener(e -> {
            int fila = tablaA.getSelectedRow();
            if (fila < 0) {
                res.setText("❌ Selecciona un arriendo.");
                res.setForeground(ROJO);
                return;
            }
            int idArr = (int) modelA.getValueAt(fila, 0);
            arriendoService.obtenerTodos().stream()
                    .filter(a -> a.getId() == idArr).findFirst()
                    .ifPresent(a -> {
                        GeneradorFactura gen = GeneradorFactura.desde(a);
                        JTextArea area = new JTextArea(gen.generarFactura());
                        area.setEditable(false);
                        area.setFont(new Font("Monospaced", Font.PLAIN, 12));
                        area.setPreferredSize(new java.awt.Dimension(500, 400));
                        JOptionPane.showMessageDialog(this, new JScrollPane(area),
                                "Factura Arriendo #" + idArr, JOptionPane.INFORMATION_MESSAGE);
                    });
        });

        JButton btnRefresh = boton("🔄 Actualizar", AZUL_MED);
        btnRefresh.addActionListener(e -> recargar.run());

        JPanel izq = new JPanel(new BorderLayout(0, 6));
        izq.setOpaque(false);
        izq.add(lbl("Facturas de Ventas", 13, Font.BOLD, AZUL_OSCURO, LEFT_ALIGNMENT), BorderLayout.NORTH);
        izq.add(new JScrollPane(tablaV), BorderLayout.CENTER);
        izq.add(btnVerVenta, BorderLayout.SOUTH);

        JPanel der = new JPanel(new BorderLayout(0, 6));
        der.setOpaque(false);
        der.add(lbl("Facturas de Arriendos", 13, Font.BOLD, AZUL_OSCURO, LEFT_ALIGNMENT), BorderLayout.NORTH);
        der.add(new JScrollPane(tablaA), BorderLayout.CENTER);
        der.add(btnVerArriendo, BorderLayout.SOUTH);

        JPanel contenedor = new JPanel(new GridLayout(1, 2, 12, 0));
        contenedor.setOpaque(false);
        contenedor.add(izq);
        contenedor.add(der);

        JPanel sur = new JPanel(new FlowLayout(FlowLayout.LEFT));
        sur.setOpaque(false);
        sur.add(btnRefresh);
        sur.add(res);

        p.add(contenedor, BorderLayout.CENTER);
        p.add(sur, BorderLayout.SOUTH);
        return p;
    }

    //NUEVO AGREGADO
    // ═══════════ BÚSQUEDA AVANZADA ═══════════
    private JPanel panelBusquedaAvanzada() {
        JPanel card = card("🔍 Búsqueda avanzada de propiedades", VERDE);

        // ── Campos de filtro ──
        JComboBox<String> cbCiudad = new JComboBox<>();
        JComboBox<String> cbBarrio = new JComboBox<>();
        JComboBox<String> cbTipo = new JComboBox<>(new String[]{"Todos", "Apartamento", "Casa"});
        JTextField txtPrecioMin = campo();
        JTextField txtPrecioMax = campo();
        JTextField txtAreaMin = campo();
        JTextField txtAreaMax = campo();

        // Poblar ciudades
        cbCiudad.addItem("Todas");
        propiedadService.ciudadesDisponibles().forEach(cbCiudad::addItem);

        // Poblar barrios iniciales (todas las ciudades)
        cbBarrio.addItem("Todos");
        propiedadService.barriosDeCiudad(null).forEach(cbBarrio::addItem);

        // Cuando cambia la ciudad → recargar barrios de esa ciudad
        cbCiudad.addActionListener(e -> {
            String ciudadSel = (String) cbCiudad.getSelectedItem();
            cbBarrio.removeAllItems();
            cbBarrio.addItem("Todos");
            String filtroCiudad = "Todas".equals(ciudadSel) ? null : ciudadSel;
            propiedadService.barriosDeCiudad(filtroCiudad).forEach(cbBarrio::addItem);
        });

        // ── Tabla de resultados ──
        String[] cols = {"ID", "Tipo", "Dirección", "Ciudad", "Barrio", "Área m²", "Precio"};
        DefaultTableModel modelo = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        JTable tabla = new JTable(modelo);
        tabla.setRowHeight(24);
        tabla.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        JScrollPane scroll = new JScrollPane(tabla);

        JLabel lblResultado = new JLabel(" ");
        lblResultado.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblResultado.setForeground(AZUL_OSCURO);

        // ── Estado compartido para el botón Exportar PDF (DEBE declararse antes de los listeners) ──
        final java.util.List<Propiedad>[] ultimosResultados = new java.util.List[]{new java.util.ArrayList<>()};
        final String[] ultimosFiltrosTexto = new String[]{""};

        JButton btnExportarPDF = boton("📄 Exportar PDF", AZUL_MED);
        btnExportarPDF.setEnabled(false);
        btnExportarPDF.addActionListener(e -> {
            com.mycompany.proyectosistemainmobiliario.facturacion.ExportadorPDF
                    .exportarListadoPropiedadesConDialogo(ultimosResultados[0], ultimosFiltrosTexto[0], this);
        });

        // ── Acción Buscar ──
        JButton btnBuscar = boton("Buscar", VERDE);
        btnBuscar.addActionListener(e -> {
            FiltroPropiedad f = new FiltroPropiedad();

            String ciudad = (String) cbCiudad.getSelectedItem();
            if (ciudad != null && !"Todas".equals(ciudad)) {
                f.setCiudad(ciudad);
            }

            String barrio = (String) cbBarrio.getSelectedItem();
            if (barrio != null && !"Todos".equals(barrio)) {
                f.setBarrio(barrio);
            }

            String tipo = (String) cbTipo.getSelectedItem();
            if (tipo != null && !"Todos".equals(tipo)) {
                f.setTipo(tipo);
            }

            f.setPrecioMin(parseDoubleOpt(txtPrecioMin.getText()));
            f.setPrecioMax(parseDoubleOpt(txtPrecioMax.getText()));
            f.setAreaMin(parseDoubleOpt(txtAreaMin.getText()));
            f.setAreaMax(parseDoubleOpt(txtAreaMax.getText()));

            List<Propiedad> resultados = propiedadService.buscarDisponibles(f);

            modelo.setRowCount(0);
            for (Propiedad p : resultados) {
                modelo.addRow(new Object[]{
                    p.getId(),
                    p.getClass().getSimpleName(),
                    p.getDireccion(),
                    p.getCiudad(),
                    p.getBarrio(),
                    p.getArea(),
                    String.format("$%,.0f", p.calcularPrecio())
                });
            }
            lblResultado.setText(resultados.size() + " propiedad(es) encontrada(s)");

            // Guardar resultados + descripción de filtros para el PDF
            ultimosResultados[0] = resultados;
            StringBuilder filtros = new StringBuilder();
            if (f.getCiudad() != null) {
                filtros.append("Ciudad=").append(f.getCiudad()).append("  ");
            }
            if (f.getBarrio() != null) {
                filtros.append("Barrio=").append(f.getBarrio()).append("  ");
            }
            if (f.getTipo() != null) {
                filtros.append("Tipo=").append(f.getTipo()).append("  ");
            }
            if (f.getPrecioMin() != null) {
                filtros.append("Precio≥").append(String.format("%,.0f", f.getPrecioMin())).append("  ");
            }
            if (f.getPrecioMax() != null) {
                filtros.append("Precio≤").append(String.format("%,.0f", f.getPrecioMax())).append("  ");
            }
            if (f.getAreaMin() != null) {
                filtros.append("Área≥").append(f.getAreaMin()).append("  ");
            }
            if (f.getAreaMax() != null) {
                filtros.append("Área≤").append(f.getAreaMax()).append("  ");
            }
            ultimosFiltrosTexto[0] = filtros.length() == 0 ? "Ninguno" : filtros.toString().trim();

            btnExportarPDF.setEnabled(!resultados.isEmpty());
        });

        // ── Acción Limpiar ──
        JButton btnLimpiar = boton("Limpiar", NARANJA);
        btnLimpiar.addActionListener(e -> {
            cbCiudad.setSelectedItem("Todas");
            cbTipo.setSelectedItem("Todos");
            txtPrecioMin.setText("");
            txtPrecioMax.setText("");
            txtAreaMin.setText("");
            txtAreaMax.setText("");
            modelo.setRowCount(0);
            lblResultado.setText(" ");
            ultimosResultados[0] = new java.util.ArrayList<>();
            btnExportarPDF.setEnabled(false);
        });

        // ── Layout del formulario ──
        JPanel form = new JPanel(new GridLayout(0, 4, 10, 10));
        form.setOpaque(false);
        form.add(lbl("Ciudad:", 13, Font.PLAIN, GRIS_TEXTO, LEFT_ALIGNMENT));
        form.add(cbCiudad);
        form.add(lbl("Barrio:", 13, Font.PLAIN, GRIS_TEXTO, LEFT_ALIGNMENT));
        form.add(cbBarrio);
        form.add(lbl("Tipo:", 13, Font.PLAIN, GRIS_TEXTO, LEFT_ALIGNMENT));
        form.add(cbTipo);
        form.add(lbl("Precio mín:", 13, Font.PLAIN, GRIS_TEXTO, LEFT_ALIGNMENT));
        form.add(txtPrecioMin);
        form.add(lbl("Precio máx:", 13, Font.PLAIN, GRIS_TEXTO, LEFT_ALIGNMENT));
        form.add(txtPrecioMax);
        form.add(lbl("Área mín:", 13, Font.PLAIN, GRIS_TEXTO, LEFT_ALIGNMENT));
        form.add(txtAreaMin);
        form.add(lbl("Área máx:", 13, Font.PLAIN, GRIS_TEXTO, LEFT_ALIGNMENT));
        form.add(txtAreaMax);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        botones.setOpaque(false);
        botones.add(btnBuscar);
        botones.add(btnLimpiar);
        botones.add(btnExportarPDF);
        botones.add(lblResultado);

        JPanel norte = new JPanel(new BorderLayout(0, 12));
        norte.setOpaque(false);
        norte.add(form, BorderLayout.CENTER);
        norte.add(botones, BorderLayout.SOUTH);

        JPanel contenido = new JPanel(new BorderLayout(0, 12));
        contenido.setOpaque(false);
        contenido.add(norte, BorderLayout.NORTH);
        contenido.add(scroll, BorderLayout.CENTER);

        card.add(contenido, BorderLayout.CENTER);
        return card;
    }
    // ═══════════ HELPERS QUE NO PUEDEN FALTAR ═══════════

    /**
     * Helper para parsear textos opcionales (vacío → null, número → Double)
     */
    private Double parseDoubleOpt(String s) {
        if (s == null || s.isBlank()) {
            return null;
        }
        try {
            return Double.parseDouble(s.trim().replace(",", "").replace(".", ""));
        } catch (NumberFormatException ex) {
            try {
                return Double.parseDouble(s.trim());
            } catch (NumberFormatException ex2) {
                return null;
            }
        }
    }

    /**
     * Carga la tabla de propiedades desde BD
     */
    private void cargarTablaProp(DefaultTableModel m) {
        m.setRowCount(0);
        for (Propiedad prop : propiedadService.obtenerTodas()) {
            String tipo = (prop instanceof Casa) ? "Casa" : "Apartamento";
            m.addRow(new Object[]{
                prop.getId(),
                tipo,
                prop.getDireccion(),
                String.format("%.1f", prop.getArea()),
                prop.getEstado(),
                String.format("%,.0f", prop.calcularPrecio())
            });
        }
    }

    /**
     * Carga la tabla de ventas desde BD
     */
    private void cargarTablaVentas(DefaultTableModel m) {
        m.setRowCount(0);
        for (Venta v : ventaService.obtenerTodas()) {
            m.addRow(new Object[]{
                v.getId(),
                v.getCliente().getNombre(),
                v.getPropiedad().getDireccion(),
                v.getAgente().getNombre(),
                String.format("%,.0f", v.getPrecioFinal()),
                v.getFecha()
            });
        }
    }
}

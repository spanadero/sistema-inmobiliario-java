/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.proyectosistemainmobiliario.vistas;

import com.mycompany.proyectosistemainmobiliario.auth.Sesion;
import com.mycompany.proyectosistemainmobiliario.servicios.UsuarioService;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import static java.awt.Component.LEFT_ALIGNMENT;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.util.Arrays;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Usuario
 */
public class LoginDialog extends JDialog {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private boolean autenticado = false;

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
    
// parte arreglada (loguindialog)
public LoginDialog(Frame parent) {
    super(parent, "Iniciar Sesion", true);

    setTitle("Iniciar sesion - Sistema Inmobiliario");
    setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    setSize(380, 380);
    setLocationRelativeTo(null);
    setResizable(false);

    // Campos
    txtUsername = campo();
    txtPassword = new JPasswordField();
    txtPassword.setFont(new Font("SansSerif", Font.PLAIN, 13));
    txtPassword.setBorder(new CompoundBorder(
            new LineBorder(new Color(190, 205, 230), 1, true),
            new EmptyBorder(6, 8, 6, 8)));

    // Labels claros encima de cada campo
    JLabel lblUser = new JLabel("Usuario");
    lblUser.setFont(new Font("SansSerif", Font.BOLD, 13));
    lblUser.setForeground(GRIS_TEXTO);

    JLabel lblPass = new JLabel("Contraseña");
    lblPass.setFont(new Font("SansSerif", Font.BOLD, 13));
    lblPass.setForeground(GRIS_TEXTO);

    // Panel central — BoxLayout vertical, mucho más predecible que GridLayout aquí
    JPanel centro = new JPanel();
    centro.setLayout(new BoxLayout(centro, BoxLayout.Y_AXIS));
    centro.setOpaque(false);

    lblUser.setAlignmentX(Component.LEFT_ALIGNMENT);
    txtUsername.setAlignmentX(Component.LEFT_ALIGNMENT);
    lblPass.setAlignmentX(Component.LEFT_ALIGNMENT);
    txtPassword.setAlignmentX(Component.LEFT_ALIGNMENT);

    // Que los campos no crezcan en alto pero sí en ancho
    txtUsername.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
    txtPassword.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));

    centro.add(lblUser);
    centro.add(Box.createVerticalStrut(4));
    centro.add(txtUsername);
    centro.add(Box.createVerticalStrut(12));
    centro.add(lblPass);
    centro.add(Box.createVerticalStrut(4));
    centro.add(txtPassword);

    // Título
    JLabel titulo = new JLabel("🏠  Sistema Inmobiliario");
    titulo.setFont(new Font("SansSerif", Font.BOLD, 18));
    titulo.setForeground(AZUL_OSCURO);
    titulo.setHorizontalAlignment(SwingConstants.CENTER);
    titulo.setBorder(new EmptyBorder(0, 0, 10, 0));

    // Botón ingresar
    JButton btnLogin = new JButton("Ingresar");
    btnLogin.setBackground(VERDE);
    btnLogin.setForeground(BLANCO);
    btnLogin.setOpaque(true);
    btnLogin.setBorderPainted(false);
    btnLogin.setFont(new Font("SansSerif", Font.BOLD, 13));
    btnLogin.setBorder(new EmptyBorder(9, 18, 9, 18));
    btnLogin.setFocusPainted(false);
    btnLogin.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    btnLogin.addActionListener(e -> intentarLogin());

    // Enter dispara el login / mueve focus
    txtPassword.addActionListener(e -> intentarLogin());
    txtUsername.addActionListener(e -> txtPassword.requestFocus());

    // Link "¿Olvidaste tu contraseña?"
    JButton btnCambiar = new JButton("¿Olvidaste tu contraseña?");
    btnCambiar.setForeground(AZUL_MED);
    btnCambiar.setBackground(FONDO);
    btnCambiar.setOpaque(false);
    btnCambiar.setBorderPainted(false);
    btnCambiar.setFont(new Font("SansSerif", Font.PLAIN, 12));
    btnCambiar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    btnCambiar.addActionListener(e -> mostrarCambioContraseña());

    // Panel sur con botón + link
    JPanel south = new JPanel(new BorderLayout(0, 4));
    south.setOpaque(false);
    south.add(btnLogin, BorderLayout.NORTH);
    south.add(btnCambiar, BorderLayout.SOUTH);

    // Root — armado UNA sola vez antes de setContentPane
    JPanel root = new JPanel(new BorderLayout(0, 12));
    root.setBorder(new EmptyBorder(24, 32, 24, 32));
    root.setBackground(FONDO);
    root.add(titulo, BorderLayout.NORTH);
    root.add(centro, BorderLayout.CENTER);
    root.add(south, BorderLayout.SOUTH);

    setContentPane(root);

    // Focus inicial en el usuario
    SwingUtilities.invokeLater(() -> txtUsername.requestFocusInWindow());
}

    private void intentarLogin() {
        String user = txtUsername.getText().trim();
        char[] pwd = txtPassword.getPassword();

        UsuarioService svc = new UsuarioService();
        svc.autenticarUsuario(user, new String(pwd)).ifPresentOrElse(
                u -> {
                    Sesion.getInstance().iniciar(u);
                    autenticado = true;
                    dispose();
                },
                () -> JOptionPane.showMessageDialog(this,
                        "Usuario o contraseña incorrectos", "Error", JOptionPane.ERROR_MESSAGE)
        );
        Arrays.fill(pwd, '\0');
    }

    public boolean isAutenticado() {
        return autenticado;
    }

    private void mostrarCambioContraseña() {
        JPasswordField fUsername = new JPasswordField();
        JPasswordField fNueva = new JPasswordField();
        JPasswordField fConfirm = new JPasswordField();

        JPanel form = new JPanel(new GridLayout(6, 1, 0, 6));
        form.add(new JLabel("Usuario:"));
        form.add(fUsername);
        form.add(new JLabel("Nueva contraseña:"));
        form.add(fNueva);
        form.add(new JLabel("Confirmar:"));
        form.add(fConfirm);

        int result = JOptionPane.showConfirmDialog(
                this, form, "Cambiar contraseña", JOptionPane.OK_CANCEL_OPTION);

        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        String username = new String(fUsername.getPassword()).trim();
        String nueva = new String(fNueva.getPassword()).trim();
        String confirm = new String(fConfirm.getPassword()).trim();

        if (username.isEmpty() || nueva.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Completa todos los campos.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!nueva.equals(confirm)) {
            JOptionPane.showMessageDialog(this, "Las contraseñas no coinciden.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (nueva.length() < 6) {
            JOptionPane.showMessageDialog(this, "Mínimo 6 caracteres.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Buscar el usuario por username y actualizar
        UsuarioService svc = new UsuarioService();
        boolean encontrado = svc.cambiarContraseñaPorUsername(username, nueva);

        if (encontrado) {
            JOptionPane.showMessageDialog(this, "✅ Contraseña actualizada.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "❌ Usuario no encontrado.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // HELPERS UI
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
}

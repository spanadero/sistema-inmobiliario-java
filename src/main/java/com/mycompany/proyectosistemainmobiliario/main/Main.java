package com.mycompany.proyectosistemainmobiliario.main;

import com.mycompany.proyectosistemainmobiliario.vistas.LoginDialog;
import com.mycompany.proyectosistemainmobiliario.vistas.MenuSwing;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

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
}

package com.mycompany.proyectosistemainmobiliario.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Clase utilitaria que centraliza la conexión a MySQL.
 * Cambia usuario/contraseña si tu instalación local es diferente.
 */
public class Conexion {

    private static final String URL      = "jdbc:mysql://localhost:3306/inmobiliaria?useSSL=false&serverTimezone=America/Bogota&allowPublicKeyRetrieval=true";
    private static final String USUARIO  = "root";
    private static final String CLAVE    = "";   // <-- cambia esto a tu contraseña de MySQL

    public static Connection obtener() throws SQLException {
        return DriverManager.getConnection(URL, USUARIO, CLAVE);
    }

    /** Prueba rápida de conexión; retorna true si todo está bien */
    public static boolean probar() {
        try (Connection c = obtener()) {
            return c != null && !c.isClosed();
        } catch (SQLException e) {
            System.err.println("Error de conexión: " + e.getMessage());
            return false;
        }
    }
}

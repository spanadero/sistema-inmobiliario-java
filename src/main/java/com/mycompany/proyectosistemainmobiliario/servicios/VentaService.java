package com.mycompany.proyectosistemainmobiliario.servicios;

import com.mycompany.proyectosistemainmobiliario.db.Conexion;
import com.mycompany.proyectosistemainmobiliario.modelos.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class VentaService {

    // Pila en memoria (igual que el original, para mostrarUltimaVenta)
    private Stack<Venta> pilaVentas = new Stack<>();

    public boolean realizarVenta(Venta v) {
        String sqlCheck = "SELECT estado_prop FROM propiedad_tb WHERE id_prop = ? FOR UPDATE";
        String sqlInsert = "INSERT INTO venta_tb (clienteId_venta, propiedadId_venta, agenteId_venta, precioFinal_venta, fecha_venta) VALUES (?,?,?,?,?)";
        String sqlEstado = "UPDATE propiedad_tb SET estado_prop='vendida' WHERE id_prop=?";

        Connection con = null;
        try {
            con = Conexion.obtener();
            con.setAutoCommit(false);

            // 1) Verificar estado con LOCK para evitar dobles ventas concurrentes
            try (PreparedStatement ps = con.prepareStatement(sqlCheck)) {
                ps.setInt(1, v.getPropiedad().getId());
                ResultSet rs = ps.executeQuery();
                if (!rs.next()) {
                    con.rollback();
                    System.err.println("La propiedad no existe.");
                    return false;
                }
                if (!"disponible".equalsIgnoreCase(rs.getString("estado_prop"))) {
                    con.rollback();
                    System.err.println("La propiedad ya no está disponible.");
                    return false;
                }
            }

            // 2) Insertar la venta
            try (PreparedStatement ps = con.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, v.getCliente().getId());
                ps.setInt(2, v.getPropiedad().getId());
                ps.setInt(3, v.getAgente().getId());
                ps.setDouble(4, v.getPrecioFinal());
                ps.setString(5, v.getFecha());
                ps.executeUpdate();
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    System.out.println("Venta guardada en BD. ID: " + rs.getInt(1));
                }
            }

            // 3) Actualizar estado de la propiedad
            try (PreparedStatement ps = con.prepareStatement(sqlEstado)) {
                ps.setInt(1, v.getPropiedad().getId());
                ps.executeUpdate();
            }

            con.commit();
            pilaVentas.push(v);
            return true;

        } catch (SQLException e) {
            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (SQLException ignored) {
            }
            System.err.println("Error al registrar venta: " + e.getMessage());
            return false;
        } finally {
            try {
                if (con != null) {
                    con.setAutoCommit(true);
                    con.close();
                }
            } catch (SQLException ignored) {
            }
        }
    }

    public List<Venta> obtenerTodas() {
        List<Venta> lista = new ArrayList<>();
        String sql = """
            SELECT v.id_venta, v.precioFinal_venta, v.fecha_venta,
                   c.id_cli, c.nombre_cli, c.correo_cli, c.telefono_cli, c.tipoDocumento_cli, c.numeroDocumento_cli,
                   p.id_prop, p.direccion_prop, p.area_prop, p.estado_prop,
                   a.id_agt, a.nombre_agt, a.correo_agt, a.telefono_agt, a.salario_agt, a.codigoEmpleado_agt,
                   apt.piso_apto, apt.administracion_apto, apt.numeroApartamento_apto,
                   cas.tienePatio_casa
            FROM venta_tb v
            JOIN cliente_tb c ON v.clienteId_venta = c.id_cli
            JOIN propiedad_tb p ON v.propiedadId_venta = p.id_prop
            JOIN agente_tb a ON v.agenteId_venta = a.id_agt
            LEFT JOIN apartamento_tb apt ON p.id_prop = apt.prop_id
            LEFT JOIN casa_tb cas ON p.id_prop = cas.prop_id
            ORDER BY v.id_venta
            """;
        try (Connection con = Conexion.obtener(); Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Cliente cli = new Cliente(rs.getInt("id_cli"), rs.getString("nombre_cli"), rs.getString("correo_cli"), rs.getString("telefono_cli"), rs.getString("tipoDocumento_cli"), rs.getString("numeroDocumento_cli"));
                Agente agt = new Agente(rs.getInt("id_agt"), rs.getString("nombre_agt"), rs.getString("correo_agt"), rs.getString("telefono_agt"), rs.getDouble("salario_agt"), rs.getString("codigoEmpleado_agt"));
                Propiedad prop;
                if (rs.getString("numeroApartamento_apto") != null) {
                    prop = new Apartamento(rs.getString("numeroApartamento_apto"), rs.getInt("piso_apto"), rs.getDouble("administracion_apto"), rs.getInt("id_prop"), rs.getString("direccion_prop"), rs.getDouble("area_prop"), rs.getString("estado_prop"));
                } else {
                    prop = new Casa(rs.getBoolean("tienePatio_casa"), rs.getInt("id_prop"), rs.getString("direccion_prop"), rs.getDouble("area_prop"), rs.getString("estado_prop"));
                }
                lista.add(new Venta(rs.getInt("id_venta"), cli, prop, agt,
                        rs.getDouble("precioFinal_venta"),
                        rs.getString("fecha_venta")));
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener ventas: " + e.getMessage());
        }
        return lista;
    }

    public void mostrarVentas() {
        for (Venta v : obtenerTodas()) {
            System.out.println("ID Venta: " + v.getId());
            System.out.println("Cliente: " + v.getCliente().getNombre());
            System.out.println("Propiedad: " + v.getPropiedad().getDireccion());
            System.out.println("Agente: " + v.getAgente().getNombre());
            System.out.println("Precio: " + v.getPrecioFinal());
            System.out.println("----------------------");
        }
    }

    public void mostrarUltimaVenta() {
        if (!pilaVentas.isEmpty()) {
            Venta v = pilaVentas.peek();
            System.out.println("Última venta: " + v.getPrecioFinal());
        }
    }

    public Venta getUltimaVentaPila() {
        return pilaVentas.isEmpty() ? null : pilaVentas.peek();
    }

    public int contar() {
        try (Connection con = Conexion.obtener(); Statement st = con.createStatement(); ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM venta_tb")) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            /* ignore */ }
        return 0;
    }
}

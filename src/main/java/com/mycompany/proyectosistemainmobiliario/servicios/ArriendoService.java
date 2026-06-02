package com.mycompany.proyectosistemainmobiliario.servicios;

import com.mycompany.proyectosistemainmobiliario.db.Conexion;
import com.mycompany.proyectosistemainmobiliario.modelos.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class ArriendoService {

    // Cola FIFO en memoria (igual que el original)
    private Queue<Cliente> colaClientes = new LinkedList<>();

    public boolean registrarArriendo(Arriendo a) {
        String sqlCheck = "SELECT estado_prop FROM propiedad_tb WHERE id_prop = ? FOR UPDATE";
        String sqlInsert = "INSERT INTO arriendo_tb (clienteId_arr, propiedadId_arr, agenteId_arr, valorMensual_arr, meses_arr, fechaInicio_arr) VALUES (?,?,?,?,?,?)";
        String sqlEstado = "UPDATE propiedad_tb SET estado_prop='arrendada' WHERE id_prop=?";

        Connection con = null;
        try {
            con = Conexion.obtener();
            con.setAutoCommit(false);

            try (PreparedStatement ps = con.prepareStatement(sqlCheck)) {
                ps.setInt(1, a.getPropiedad().getId());
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

            try (PreparedStatement ps = con.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, a.getCliente().getId());
                ps.setInt(2, a.getPropiedad().getId());
                ps.setInt(3, a.getAgente().getId());
                ps.setDouble(4, a.getValorMensual());
                ps.setInt(5, a.getMeses());
                ps.setString(6, a.getFechaInicio());
                ps.executeUpdate();
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    System.out.println("Arriendo guardado en BD. ID: " + rs.getInt(1));
                }
            }

            try (PreparedStatement ps = con.prepareStatement(sqlEstado)) {
                ps.setInt(1, a.getPropiedad().getId());
                ps.executeUpdate();
            }

            con.commit();
            return true;

        } catch (SQLException e) {
            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (SQLException ignored) {
            }
            System.err.println("Error al registrar arriendo: " + e.getMessage());
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

    public List<Arriendo> obtenerTodos() {
        List<Arriendo> lista = new ArrayList<>();
        String sql = """
            SELECT ar.id_arr, ar.valorMensual_arr, ar.meses_arr, ar.fechaInicio_arr,
                   c.id_cli, c.nombre_cli, c.correo_cli, c.telefono_cli, c.tipoDocumento_cli, c.numeroDocumento_cli,
                   p.id_prop, p.direccion_prop, p.area_prop, p.estado_prop,
                   a.id_agt, a.nombre_agt, a.correo_agt, a.telefono_agt, a.salario_agt, a.codigoEmpleado_agt,
                   apt.piso_apto, apt.administracion_apto, apt.numeroApartamento_apto,
                   cas.tienePatio_casa
            FROM arriendo_tb ar
            JOIN cliente_tb c ON ar.clienteId_arr = c.id_cli
            JOIN propiedad_tb p ON ar.propiedadId_arr = p.id_prop
            JOIN agente_tb a ON ar.agenteId_arr = a.id_agt
            LEFT JOIN apartamento_tb apt ON p.id_prop = apt.prop_id
            LEFT JOIN casa_tb cas ON p.id_prop = cas.prop_id
            ORDER BY ar.id_arr
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
                lista.add(new Arriendo(rs.getInt("id_arr"), cli, prop, agt,
                        rs.getDouble("valorMensual_arr"),
                        rs.getInt("meses_arr"), rs.getString("fechaInicio_arr")));
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener arriendos: " + e.getMessage());
        }
        return lista;
    }

    public void mostrarArriendos() {
        for (Arriendo a : obtenerTodos()) {
            System.out.println("ID: " + a.getId());
            System.out.println("Cliente: " + a.getCliente().getNombre());
            System.out.println("Propiedad: " + a.getPropiedad().getDireccion());
            System.out.println("Valor mensual: " + a.getValorMensual());
            System.out.println("Total: " + a.calcularTotalArriendo());
            System.out.println("----------------------");
        }
    }

    public void agregarCliente(Cliente c) {
        colaClientes.add(c);
    }

    public void atenderCliente() {
        Cliente c = colaClientes.poll();
        if (c != null) {
            System.out.println("Atendiendo a: " + c.getNombre());
        } else {
            System.out.println("No hay clientes en espera");
        }
    }

    public int contar() {
        try (Connection con = Conexion.obtener(); Statement st = con.createStatement(); ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM arriendo_tb")) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            /* ignore */ }
        return 0;
    }
}

package com.mycompany.proyectosistemainmobiliario.servicios;

import com.mycompany.proyectosistemainmobiliario.db.Conexion;
import com.mycompany.proyectosistemainmobiliario.modelos.Cliente;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClienteService {

    public void registrarCliente(Cliente c) {
        String sql = "INSERT INTO cliente_tb (nombre_cli, correo_cli, telefono_cli, tipoDocumento_cli, numeroDocumento_cli) VALUES (?,?,?,?,?)";
        try (Connection con = Conexion.obtener();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, c.getNombre());
            ps.setString(2, c.getCorreo());
            ps.setString(3, c.getTelefono());
            ps.setString(4, c.getTipoDocumento());
            ps.setString(5, c.getNumeroDocumento());
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) c.setId(rs.getInt(1));
            System.out.println("Cliente registrado en BD. ID: " + c.getId());
        } catch (SQLException e) {
            System.err.println("Error al registrar cliente: " + e.getMessage());
        }
    }

    public List<Cliente> obtenerTodos() {
        List<Cliente> lista = new ArrayList<>();
        String sql = "SELECT * FROM cliente_tb ORDER BY id_cli";
        try (Connection con = Conexion.obtener();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(new Cliente(
                    rs.getInt("id_cli"),
                    rs.getString("nombre_cli"),
                    rs.getString("correo_cli"),
                    rs.getString("telefono_cli"),
                    rs.getString("tipoDocumento_cli"),
                    rs.getString("numeroDocumento_cli")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener clientes: " + e.getMessage());
        }
        return lista;
    }

    public Cliente buscarPorId(int id) {
        String sql = "SELECT * FROM cliente_tb WHERE id_cli = ?";
        try (Connection con = Conexion.obtener();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Cliente(
                    rs.getInt("id_cli"), rs.getString("nombre_cli"),
                    rs.getString("correo_cli"), rs.getString("telefono_cli"),
                    rs.getString("tipoDocumento_cli"), rs.getString("numeroDocumento_cli")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar cliente: " + e.getMessage());
        }
        return null;
    }

    public int contar() {
        try (Connection con = Conexion.obtener();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM cliente_tb")) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { /* ignore */ }
        return 0;
    }
    
    
}

package com.mycompany.proyectosistemainmobiliario.servicios;

import com.mycompany.proyectosistemainmobiliario.db.Conexion;
import com.mycompany.proyectosistemainmobiliario.modelos.Agente;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AgenteService {

    public List<Agente> obtenerTodos() {
        List<Agente> lista = new ArrayList<>();
        String sql = "SELECT * FROM agente_tb ORDER BY id_agt";
        try (Connection con = Conexion.obtener();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(new Agente(
                    rs.getInt("id_agt"),
                    rs.getString("nombre_agt"),
                    rs.getString("correo_agt"),
                    rs.getString("telefono_agt"),
                    rs.getDouble("salario_agt"),
                    rs.getString("codigoEmpleado_agt")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener agentes: " + e.getMessage());
        }
        return lista;
    }

    public Agente buscarPorId(int id) {
        String sql = "SELECT * FROM agente_tb WHERE id_agt = ?";
        try (Connection con = Conexion.obtener();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Agente(
                    rs.getInt("id_agt"), rs.getString("nombre_agt"),
                    rs.getString("correo_agt"), rs.getString("telefono_agt"),
                    rs.getDouble("salario_agt"), rs.getString("codigoEmpleado_agt")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar agente: " + e.getMessage());
        }
        return null;
    }
}

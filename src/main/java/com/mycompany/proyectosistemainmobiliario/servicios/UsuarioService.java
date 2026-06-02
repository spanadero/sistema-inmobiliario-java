/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.proyectosistemainmobiliario.servicios;

import com.mycompany.proyectosistemainmobiliario.db.Conexion;
import com.mycompany.proyectosistemainmobiliario.modelos.Usuario;
import com.mycompany.proyectosistemainmobiliario.modelos.Rol;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.mindrot.jbcrypt.BCrypt;

/**
 *
 * @author Usuario
 */
public class UsuarioService {

    //Busca un usuario por username y verifica la contraseña con BCrypt
    public Optional<Usuario> autenticarUsuario(String u, String p) {
        String sql = "SELECT * FROM usuario_tb WHERE username_usu = ? AND activo_usu = TRUE";
        try (Connection con = Conexion.obtener(); PreparedStatement st = con.prepareStatement(sql)) {

            st.setString(1, u);
            ResultSet rs = st.executeQuery();

            if (rs.next()) {
                String hashGuardado = rs.getString("password_usu");
                if (BCrypt.checkpw(p, hashGuardado)) {
                    Usuario usu = new Usuario(
                            rs.getInt("id_usu"),
                            rs.getString("username_usu"),
                            rs.getString("password_usu"),
                            Rol.valueOf(rs.getString("rol_usu")),
                            rs.getBoolean("activo_usu"),
                            rs.getString("nombre_usu")
                    );
                    return Optional.of(usu);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al autenticar: " + e.getMessage());
        }
        return Optional.empty();
    }

    public void registrarUsuario(Usuario u, String passwordPlano) {
        String hash = BCrypt.hashpw(passwordPlano, BCrypt.gensalt());
        u.setPasswordHash(hash);
        String sql = "INSERT INTO usuario_tb (username_usu, password_usu, rol_usu, nombre_usu) VALUES (?,?,?,?)";

        try (Connection con = Conexion.obtener(); PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, u.getUsername());
            ps.setString(2, u.getPasswordHash());
            ps.setString(3, u.getRol().name());
            ps.setString(4, u.getNombre());
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();

            if (rs.next()) {
                u.setId(rs.getInt(1));
            }
            System.out.println("Usuario registrado en BD: " + u.getId());

        } catch (SQLException e) {
            System.out.println("Error al registrar al cliente: " + e.getMessage());
        }
    }

    public List<Usuario> listarTodos() {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT * FROM usuario_tb ORDER BY id_usu";
        try (Connection con = Conexion.obtener(); Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                lista.add(new Usuario(
                        rs.getInt("id_usu"),
                        rs.getString("username_usu"),
                        rs.getString("password_usu"),
                        Rol.valueOf(rs.getString("rol_usu")),
                        rs.getBoolean("activo_usu"),
                        rs.getString("nombre_usu")
                ));
            }

        } catch (SQLException e) {
            System.out.println("Error al obtener los usuarios: " + e.getMessage());
        }
        return lista;
    }

    public void cambiarContraseña(int idUsuario, String nuevaContraseña) {
        String hash = BCrypt.hashpw(nuevaContraseña, BCrypt.gensalt());
        String sql = "UPDATE usuario_tb SET password_usu = ? WHERE id_usu = ?";
        try (Connection con = Conexion.obtener(); PreparedStatement st = con.prepareStatement(sql)) {

            st.setString(1, hash);
            st.setInt(2, idUsuario);
            st.executeUpdate();
            System.out.println("Contraseña actualizada correctamente.");

        } catch  (SQLException e) {
            System.out.println("Error al actualizar contraseña: " + e.getMessage());
        }
    }

    public void desactivar(int idUsuario) {
        String sql = "UPDATE usuario_tb SET activo_usu = FALSE WHERE id_usu = ?";

        try (Connection con = Conexion.obtener(); PreparedStatement st = con.prepareStatement(sql)) {

            st.setInt(1, idUsuario);
            st.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Error al desactivar usuario: " + e.getMessage());
        }
    }

    public boolean cambiarContraseñaPorUsername(String username, String nuevaContraseña) {
        String sql = "UPDATE usuario_tb SET password_usu = ? WHERE username_usu = ? AND activo_usu = TRUE";
        try (Connection con = Conexion.obtener(); PreparedStatement st = con.prepareStatement(sql)) {

            st.setString(1, BCrypt.hashpw(nuevaContraseña, BCrypt.gensalt()));
            st.setString(2, username);
            int filas = st.executeUpdate();
            return filas > 0;  // true si encontró y actualizó el usuario

        } catch (SQLException e) {
            System.out.println("Error al cambiar contraseña: " + e.getMessage());
            return false;
        }
    }
}

package com.mycompany.proyectosistemainmobiliario.servicios;
import java.util.stream.Collectors;
import com.mycompany.proyectosistemainmobiliario.db.Conexion;
import com.mycompany.proyectosistemainmobiliario.modelos.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PropiedadService {

    // ── Registrar apartamento ──────────────────────────────────────
    public void registrarApartamento(Apartamento a) {
        String sqlProp = "INSERT INTO propiedad_tb (direccion_prop, area_prop, estado_prop) VALUES (?,?,?)";
        String sqlApto = "INSERT INTO apartamento_tb (numeroApartamento_apto, piso_apto, administracion_apto, prop_id) VALUES (?,?,?,?)";

        Connection con = null;
        try {
            con = Conexion.obtener();
            con.setAutoCommit(false);

            PreparedStatement ps1 = con.prepareStatement(sqlProp, Statement.RETURN_GENERATED_KEYS);
            ps1.setString(1, a.getDireccion());
            ps1.setDouble(2, a.getArea());
            ps1.setString(3, a.getEstado());
            ps1.executeUpdate();
            int idProp = 0;
            ResultSet rs = ps1.getGeneratedKeys();
            if (rs.next()) idProp = rs.getInt(1);

            PreparedStatement ps2 = con.prepareStatement(sqlApto);
            ps2.setString(1, a.getNumeroApa());
            ps2.setInt   (2, a.getPiso());
            ps2.setDouble(3, a.getAdministracion());
            ps2.setInt   (4, idProp);
            ps2.executeUpdate();

            con.commit();
            System.out.println("Apartamento registrado en BD. ID propiedad: " + idProp);

        } catch (SQLException e) {
            try { if (con != null) con.rollback(); } catch (SQLException ex) { /* ignorar */ }
            System.err.println("Error al registrar apartamento: " + e.getMessage());
        } finally {
            try { if (con != null) con.close(); } catch (SQLException ex) { /* ignorar */ }
        }
    }

    // ── Registrar casa ─────────────────────────────────────────────
    public void registrarCasa(Casa c) {
        String sqlProp = "INSERT INTO propiedad_tb (direccion_prop, area_prop, estado_prop) VALUES (?,?,?)";
        String sqlCasa = "INSERT INTO casa_tb (tienePatio_casa, prop_id) VALUES (?,?)";

        Connection con = null;
        try {
            con = Conexion.obtener();
            con.setAutoCommit(false);

            PreparedStatement ps1 = con.prepareStatement(sqlProp, Statement.RETURN_GENERATED_KEYS);
            ps1.setString(1, c.getDireccion());
            ps1.setDouble(2, c.getArea());
            ps1.setString(3, c.getEstado());
            ps1.executeUpdate();
            int idProp = 0;
            ResultSet rs = ps1.getGeneratedKeys();
            if (rs.next()) idProp = rs.getInt(1);

            PreparedStatement ps2 = con.prepareStatement(sqlCasa);
            ps2.setBoolean(1, c.isTienePatio());
            ps2.setInt    (2, idProp);
            ps2.executeUpdate();

            con.commit();
            System.out.println("Casa registrada en BD. ID propiedad: " + idProp);

        } catch (SQLException e) {
            try { if (con != null) con.rollback(); } catch (SQLException ex) { /* ignorar */ }
            System.err.println("Error al registrar casa: " + e.getMessage());
        } finally {
            try { if (con != null) con.close(); } catch (SQLException ex) { /* ignorar */ }
        }
    }

    // Mantiene compatibilidad con Menu.java original (detecta tipo)
    public void registrarPropiedad(Propiedad p) {
        if (p instanceof Apartamento) registrarApartamento((Apartamento) p);
        else if (p instanceof Casa)   registrarCasa((Casa) p);
    }

    // ── Listar todas las propiedades ───────────────────────────────
    public List<Propiedad> obtenerTodas() {
        List<Propiedad> lista = new ArrayList<>();
        // Une propiedad_tb con apartamento_tb y casa_tb
        String sql = """
            SELECT p.id_prop, p.direccion_prop, p.area_prop, p.estado_prop,
                   a.numeroApartamento_apto, a.piso_apto, a.administracion_apto,
                   c.tienePatio_casa,
                   CASE WHEN a.prop_id IS NOT NULL THEN 'Apartamento'
                        WHEN c.prop_id IS NOT NULL THEN 'Casa'
                        ELSE 'Desconocido' END AS tipo
            FROM propiedad_tb p
            LEFT JOIN apartamento_tb a ON p.id_prop = a.prop_id
            LEFT JOIN casa_tb        c ON p.id_prop = c.prop_id
            ORDER BY p.id_prop
            """;
        try (Connection con = Conexion.obtener();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                int    id     = rs.getInt   ("id_prop");
                String dir    = rs.getString("direccion_prop");
                double area   = rs.getDouble("area_prop");
                String estado = rs.getString("estado_prop");
                String tipo   = rs.getString("tipo");

                if ("Apartamento".equals(tipo)) {
                    lista.add(new Apartamento(
                        rs.getString("numeroApartamento_apto"),
                        rs.getInt("piso_apto"),
                        rs.getDouble("administracion_apto"),
                        id, dir, area, estado
                    ));
                } else if ("Casa".equals(tipo)) {
                    lista.add(new Casa(rs.getBoolean("tienePatio_casa"), id, dir, area, estado));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener propiedades: " + e.getMessage());
        }
        return lista;
    }

    // ── Buscar por ID ──────────────────────────────────────────────
    public Propiedad buscarPorId(int id) {
        String sql = """
            SELECT p.id_prop, p.direccion_prop, p.area_prop, p.estado_prop,
                   a.numeroApartamento_apto, a.piso_apto, a.administracion_apto,
                   c.tienePatio_casa,
                   CASE WHEN a.prop_id IS NOT NULL THEN 'Apartamento'
                        WHEN c.prop_id IS NOT NULL THEN 'Casa'
                        ELSE 'Desconocido' END AS tipo
            FROM propiedad_tb p
            LEFT JOIN apartamento_tb a ON p.id_prop = a.prop_id
            LEFT JOIN casa_tb        c ON p.id_prop = c.prop_id
            WHERE p.id_prop = ?
            """;
        try (Connection con = Conexion.obtener();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String dir    = rs.getString("direccion_prop");
                double area   = rs.getDouble("area_prop");
                String estado = rs.getString("estado_prop");
                String tipo   = rs.getString("tipo");
                if ("Apartamento".equals(tipo))
                    return new Apartamento(rs.getString("numeroApartamento_apto"), rs.getInt("piso_apto"), rs.getDouble("administracion_apto"), id, dir, area, estado);
                else if ("Casa".equals(tipo))
                    return new Casa(rs.getBoolean("tienePatio_casa"), id, dir, area, estado);
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar propiedad: " + e.getMessage());
        }
        return null;
    }

    // ── Mostrar en consola (compatible con Menu.java original) ─────
    public void mostrarPropiedades() {
        for (Propiedad p : obtenerTodas()) {
            System.out.println("ID: " + p.getId());
            System.out.println("Dirección: " + p.getDireccion());
            System.out.println("Área: " + p.getArea());
            System.out.println("Estado: " + p.getEstado());
            System.out.println("Precio calculado: " + p.calcularPrecio());
            System.out.println("----------------------");
        }
    }

    // Para compatibilidad con MenuSwing (estadísticas)
    public int contarPropiedades() {
        try (Connection con = Conexion.obtener();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM propiedad_tb")) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { /* ignore */ }
        return 0;
    }
    //NUEVO AGREGADO
    
    // ── BÚSQUEDA AVANZADA ─────────────────────────────────────────
public List<Propiedad> buscarDisponibles(FiltroPropiedad f) {
    return obtenerTodas().stream()
        .filter(p -> "disponible".equalsIgnoreCase(p.getEstado()))
        .filter(p -> f.getCiudad() == null || f.getCiudad().isBlank()
                  || p.getCiudad().equalsIgnoreCase(f.getCiudad()))
        .filter(p -> f.getBarrio() == null || f.getBarrio().isBlank()
                  || p.getBarrio().equalsIgnoreCase(f.getBarrio()))
        .filter(p -> f.getTipo() == null || f.getTipo().isBlank()
                  || p.getClass().getSimpleName().equalsIgnoreCase(f.getTipo()))
        .filter(p -> f.getPrecioMin() == null || p.calcularPrecio() >= f.getPrecioMin())
        .filter(p -> f.getPrecioMax() == null || p.calcularPrecio() <= f.getPrecioMax())
        .filter(p -> f.getAreaMin()   == null || p.getArea()       >= f.getAreaMin())
        .filter(p -> f.getAreaMax()   == null || p.getArea()       <= f.getAreaMax())
        .collect(Collectors.toList());
}

public List<String> ciudadesDisponibles() {
    return obtenerTodas().stream()
        .filter(p -> "disponible".equalsIgnoreCase(p.getEstado()))
        .map(Propiedad::getCiudad)
        .filter(s -> !s.isBlank())
        .distinct()
        .sorted()
        .collect(Collectors.toList());
}

public List<String> barriosDeCiudad(String ciudad) {
    return obtenerTodas().stream()
        .filter(p -> "disponible".equalsIgnoreCase(p.getEstado()))
        .filter(p -> ciudad == null || ciudad.isBlank()
                  || p.getCiudad().equalsIgnoreCase(ciudad))
        .map(Propiedad::getBarrio)
        .filter(s -> !s.isBlank())
        .distinct()
        .sorted()
        .collect(Collectors.toList());
}
// ── Verificar disponibilidad en tiempo real ────────────────────
public boolean estaDisponible(int idPropiedad) {
    String sql = "SELECT estado_prop FROM propiedad_tb WHERE id_prop = ?";
    try (Connection con = Conexion.obtener();
         PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setInt(1, idPropiedad);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return "disponible".equalsIgnoreCase(rs.getString("estado_prop"));
        }
    } catch (SQLException e) {
        System.err.println("Error al verificar disponibilidad: " + e.getMessage());
    }
    return false; // Si no se puede consultar, asumir NO disponible (más seguro)
}
}

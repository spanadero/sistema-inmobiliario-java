/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.proyectosistemainmobiliario.modelos;

/**
 *
 * @author SantiagoPanaderoChav
 */
public abstract class Propiedad {

    private int id;
    private String direccion;
    private double area;
    private String estado;
    
    //CONSTRUCTOR

    public Propiedad(int id, String direccion, double area, String estado) {
        this.id = id;
        this.direccion = direccion;
        this.area = area;
        this.estado = estado;
    }
    
    //GETTERS AND SETTERS

    public int getId() {
        return id;
    }

    public String getDireccion() {
        return direccion;
    }

    public double getArea() {
        return area;
    }

    public String getEstado() {
        return estado;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public void setArea(double area) {
        this.area = area;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
    
    // ── DERIVADOS DE LA DIRECCIÓN ────────────────────────────────
// Convención: dirección = "<calle>, <barrio>, <ciudad>"
//             o también  = "<calle>, <ciudad>" (sin barrio)

public String getCiudad() {
    if (direccion == null) return "";
    String[] partes = direccion.split(",");
    if (partes.length == 0) return "";
    return partes[partes.length - 1].trim();
}

public String getBarrio() {
    if (direccion == null) return "";
    String[] partes = direccion.split(",");
    if (partes.length < 3) return ""; // sin barrio explícito
    return partes[partes.length - 2].trim();
}

    //METODO ABSTRACTO

    public abstract double calcularPrecio();
}

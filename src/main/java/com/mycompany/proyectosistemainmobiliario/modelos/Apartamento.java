/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.proyectosistemainmobiliario.modelos;

/**
 *
 * @author SantiagoPanaderoChav
 */
public class Apartamento extends Propiedad {

    private String numeroApa;
    private int piso;
    private double administracion;
    
    //CONSTRUCTOR CON SUPER

    public Apartamento(String numeroApa, int piso, double administracion,
            int id, String direccion, double area, String estado) {
        super(id, direccion, area, estado);
        this.numeroApa = numeroApa;
        this.piso = piso;
        this.administracion = administracion;
    }
    
    //GETTERS Y SETTERS

    public String getNumeroApa() {
        return numeroApa;
    }

    public void setNumeroApa(String numeroApa) {
        this.numeroApa = numeroApa;
    }

    public int getPiso() {
        return piso;
    }

    public void setPiso(int piso) {
        this.piso = piso;
    }

    public double getAdministracion() {
        return administracion;
    }

    public void setAdministracion(double administracion) {
        this.administracion = administracion;
    }
    
    //METODOS

    @Override
    public double calcularPrecio() {
        double precioBase = getArea() * 3000000;

        if (piso > 10) {
            precioBase *= 1.05;
        }

        return precioBase + administracion;
    }
}

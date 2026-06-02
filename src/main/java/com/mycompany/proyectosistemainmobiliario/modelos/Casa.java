/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.proyectosistemainmobiliario.modelos;

/**
 *
 * @author SantiagoPanaderoChav
 */
public class Casa extends Propiedad {

    private boolean tienePatio;
    
    //CONSTRUCTOR CON SUPER

    public Casa(boolean tienePatio, int id, String direccion, double area, String estado) {
        super(id, direccion, area, estado);
        this.tienePatio = tienePatio;
    }
    
    //GET Y SET

    public boolean isTienePatio() {
        return tienePatio;
    }

    public void setTienePatio(boolean tienePatio) {
        this.tienePatio = tienePatio;
    }
    
    //METODOS

    @Override
    public double calcularPrecio() {
        double precioBase = getArea() * 3500000;

        if (tienePatio) {
            precioBase += 20000000;
        }

        if (getArea() > 100) {
            precioBase *= 1.1;
        }

        return precioBase;
    }
}

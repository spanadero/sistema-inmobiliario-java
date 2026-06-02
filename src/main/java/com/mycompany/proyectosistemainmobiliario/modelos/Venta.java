/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.proyectosistemainmobiliario.modelos;

/**
 *
 * @author SantiagoPanaderoChav
 */
public class Venta {

    private int id;
    private Cliente cliente;
    private Propiedad propiedad;
    private Agente agente;
    private double precioFinal;
    private String fecha;
    
    //CONSTRUCTOR (nueva venta: calcula el precioFinal automáticamente)

    public Venta(int id, Cliente cliente, Propiedad propiedad, Agente agente, String fecha) {
        this.id = id;
        this.cliente = cliente;
        this.propiedad = propiedad;
        this.agente = agente;
        this.fecha = fecha;
        this.precioFinal = propiedad.calcularPrecio();
    }

    //CONSTRUCTOR (cargar desde BD: recibe el precioFinal ya guardado)

    public Venta(int id, Cliente cliente, Propiedad propiedad, Agente agente,
                 double precioFinal, String fecha) {
        this.id = id;
        this.cliente = cliente;
        this.propiedad = propiedad;
        this.agente = agente;
        this.precioFinal = precioFinal;
        this.fecha = fecha;
    }
    
    //GETTERS

    public int getId() {
        return id;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public Propiedad getPropiedad() {
        return propiedad;
    }

    public Agente getAgente() {
        return agente;
    }

    public double getPrecioFinal() {
        return precioFinal;
    }

    public String getFecha() {
        return fecha;
    }
}

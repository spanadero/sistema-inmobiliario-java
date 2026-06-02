/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.proyectosistemainmobiliario.modelos;

/**
 *
 * @author SantiagoPanaderoChav
 */
public class Arriendo {

    private int id;
    private Cliente cliente;
    private Propiedad propiedad;
    private Agente agente;
    private double valorMensual;
    private int meses;
    private String fechaInicio;
    
    //CONSTRUCTOR (nuevo arriendo: calcula el valorMensual automáticamente)

    public Arriendo(int id, Cliente cliente, Propiedad propiedad, Agente agente, int meses, String fechaInicio) {
        this.id = id;
        this.cliente = cliente;
        this.propiedad = propiedad;
        this.agente = agente;
        this.meses = meses;
        this.fechaInicio = fechaInicio;
        this.valorMensual = propiedad.calcularPrecio() * 0.01;
    }

    //CONSTRUCTOR (cargar desde BD: recibe el valorMensual ya guardado)

    public Arriendo(int id, Cliente cliente, Propiedad propiedad, Agente agente,
                    double valorMensual, int meses, String fechaInicio) {
        this.id = id;
        this.cliente = cliente;
        this.propiedad = propiedad;
        this.agente = agente;
        this.valorMensual = valorMensual;
        this.meses = meses;
        this.fechaInicio = fechaInicio;
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

    public double getValorMensual() {
        return valorMensual;
    }

    public int getMeses() {
        return meses;
    }

    public String getFechaInicio() {
        return fechaInicio;
    }
    
    //METODOS

    public double calcularTotalArriendo() {
        return valorMensual * meses;
    }
    
    public void mostrarArriendo() {
    System.out.println("ID: " + id);
    System.out.println("Cliente: " + cliente.getNombre());
    System.out.println("Propiedad: " + propiedad.getDireccion());
    System.out.println("Agente: " + agente.getNombre());
    System.out.println("Valor mensual: " + valorMensual);
    System.out.println("Meses: " + meses);
    System.out.println("Total: " + calcularTotalArriendo());
}
}

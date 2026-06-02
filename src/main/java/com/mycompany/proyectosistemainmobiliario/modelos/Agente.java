/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.proyectosistemainmobiliario.modelos;

/**
 *
 * @author SantiagoPanaderoChav
 */
public class Agente {

    private int id;
    private String nombre;
    private String correo;
    private String telefono;
    private double salario;
    private String codigoEmpleado;
    
    //CONSTRUCTOR

    public Agente(int id, String nombre, String correo, String telefono,
            double salario, String codigoEmpleado) {
        this.id = id;
        this.nombre = nombre;
        this.correo = correo;
        this.telefono = telefono;
        this.salario = salario;
        this.codigoEmpleado = codigoEmpleado;
    }
    
    //GETTERS Y SETTERS

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public String getTelefono() {
        return telefono;
    }

    public double getSalario() {
        return salario;
    }

    public String getCodigoEmpleado() {
        return codigoEmpleado;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public void setSalario(double salario) {
        this.salario = salario;
    }

    public void setCodigoEmpleado(String codigoEmpleado) {
        this.codigoEmpleado = codigoEmpleado;
    }
}

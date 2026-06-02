/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.proyectosistemainmobiliario.modelos;

import java.util.ArrayList;

/**
 *
 * @author Usuario
 */
public class Repositorio<T> {

    private ArrayList<T> lista;

    public Repositorio() {
        lista = new ArrayList<>();
    }

    public void agregar(T elemento) {
        lista.add(elemento);
    }

    public ArrayList<T> obtenerTodos() {
        return lista;
    }

    public int tamanio() {
        return lista.size();
    }
}

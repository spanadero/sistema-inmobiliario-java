/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.proyectosistemainmobiliario.auth;

import com.mycompany.proyectosistemainmobiliario.modelos.Rol;
import com.mycompany.proyectosistemainmobiliario.modelos.Usuario;

/**
 *
 * @author Usuario
 */
public class Sesion {
    private static Sesion instancia;
    private Usuario usuarioActual;
    

    private Sesion() {}
    
    public static Sesion getInstance() {
        if (instancia == null) instancia = new Sesion();
        return instancia;
    }
    
    public void iniciar(Usuario u){
        this.usuarioActual = u;
    }
    
    public void cerrar(){
        this.usuarioActual = null;
    }
    
    public Usuario getUsuario(){
        return usuarioActual;
    }
    
    public boolean estaLogueado(){
        return usuarioActual != null;
    }
    
    public boolean esAdmin(){
        return estaLogueado() && usuarioActual.getRol() == Rol.ADMIN;
    }
}

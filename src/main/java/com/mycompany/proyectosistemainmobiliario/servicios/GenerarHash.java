/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.proyectosistemainmobiliario.servicios;

import org.mindrot.jbcrypt.BCrypt;

/**
 *
 * @author Usuario
 */
public class GenerarHash {
    public static void main(String[] args) {
        String hash = BCrypt.hashpw("admin123", BCrypt.gensalt());
        System.out.println(hash);
    }
}


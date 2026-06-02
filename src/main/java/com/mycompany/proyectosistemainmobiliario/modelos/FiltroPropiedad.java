/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.proyectosistemainmobiliario.modelos;

/**
 * POJO que agrupa los criterios de búsqueda avanzada de propiedades.
 * Todos los campos son nullable: si un campo es null o vacío, ese criterio
 * no se aplica en el filtrado.
 */
public class FiltroPropiedad {

    private Double precioMin;
    private Double precioMax;
    private Double areaMin;
    private Double areaMax;
    private String ciudad;
    private String barrio;
    private String tipo; // "Apartamento", "Casa", o null para ambos

    public FiltroPropiedad() {}

    public Double getPrecioMin()                  { return precioMin; }
    public void   setPrecioMin(Double v)          { this.precioMin = v; }
    public Double getPrecioMax()                  { return precioMax; }
    public void   setPrecioMax(Double v)          { this.precioMax = v; }
    public Double getAreaMin()                    { return areaMin; }
    public void   setAreaMin(Double v)            { this.areaMin = v; }
    public Double getAreaMax()                    { return areaMax; }
    public void   setAreaMax(Double v)            { this.areaMax = v; }
    public String getCiudad()                     { return ciudad; }
    public void   setCiudad(String v)             { this.ciudad = v; }
    public String getBarrio()                     { return barrio; }
    public void   setBarrio(String v)             { this.barrio = v; }
    public String getTipo()                       { return tipo; }
    public void   setTipo(String v)               { this.tipo = v; }
    
}



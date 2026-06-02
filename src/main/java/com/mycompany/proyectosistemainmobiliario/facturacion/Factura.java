package com.mycompany.proyectosistemainmobiliario.facturacion;

/**
 * CLASE Factura (Modelo de datos)
 * ─────────────────────────────────────────────────────────────────
 * Representa los datos de una factura generada.
 * Esta clase es un simple "contenedor" de información (POJO):
 * solo tiene atributos, constructor, getters y setters.
 *
 * Separamos los DATOS (Factura) de la LÓGICA (GeneradorFactura)
 * siguiendo el principio de Responsabilidad Única (SRP) de POO.
 *
 * Archivo: facturacion/Factura.java
 */
public class Factura {

    // ── Atributos ──────────────────────────────────────────────────
    private String numeroFactura;
    private String tipoTransaccion;   // "VENTA" o "ARRIENDO"
    private String fechaEmision;

    // Datos del cliente
    private String nombreCliente;
    private String documentoCliente;
    private String correoCliente;
    private String telefonoCliente;

    // Datos de la propiedad
    private String direccionPropiedad;
    private String tipoPropiedad;     // "Casa" o "Apartamento"
    private double areaPropiedad;

    // Datos del agente
    private String nombreAgente;

    // Datos económicos
    private double subtotal;
    private double iva;
    private double total;

    // Solo para arriendos
    private int meses;
    private double valorMensual;

    // ── Constructor ────────────────────────────────────────────────
    public Factura(String numeroFactura, String tipoTransaccion, String fechaEmision,
                   String nombreCliente, String documentoCliente,
                   String correoCliente, String telefonoCliente,
                   String direccionPropiedad, String tipoPropiedad, double areaPropiedad,
                   String nombreAgente,
                   double subtotal, double iva, double total) {
        this.numeroFactura       = numeroFactura;
        this.tipoTransaccion     = tipoTransaccion;
        this.fechaEmision        = fechaEmision;
        this.nombreCliente       = nombreCliente;
        this.documentoCliente    = documentoCliente;
        this.correoCliente       = correoCliente;
        this.telefonoCliente     = telefonoCliente;
        this.direccionPropiedad  = direccionPropiedad;
        this.tipoPropiedad       = tipoPropiedad;
        this.areaPropiedad       = areaPropiedad;
        this.nombreAgente        = nombreAgente;
        this.subtotal            = subtotal;
        this.iva                 = iva;
        this.total               = total;
    }

    // ── Getters y Setters ──────────────────────────────────────────
    public String getNumeroFactura()        { return numeroFactura; }
    public String getTipoTransaccion()      { return tipoTransaccion; }
    public String getFechaEmision()         { return fechaEmision; }
    public String getNombreCliente()        { return nombreCliente; }
    public String getDocumentoCliente()     { return documentoCliente; }
    public String getCorreoCliente()        { return correoCliente; }
    public String getTelefonoCliente()      { return telefonoCliente; }
    public String getDireccionPropiedad()   { return direccionPropiedad; }
    public String getTipoPropiedad()        { return tipoPropiedad; }
    public double getAreaPropiedad()        { return areaPropiedad; }
    public String getNombreAgente()         { return nombreAgente; }
    public double getSubtotal()             { return subtotal; }
    public double getIva()                  { return iva; }
    public double getTotal()                { return total; }
    public int    getMeses()                { return meses; }
    public double getValorMensual()         { return valorMensual; }

    public void setMeses(int meses)                  { this.meses = meses; }
    public void setValorMensual(double valorMensual) { this.valorMensual = valorMensual; }
}

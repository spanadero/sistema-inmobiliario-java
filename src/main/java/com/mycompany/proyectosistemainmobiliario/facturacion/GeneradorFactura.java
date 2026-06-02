package com.mycompany.proyectosistemainmobiliario.facturacion;

import com.mycompany.proyectosistemainmobiliario.modelos.Venta;
import com.mycompany.proyectosistemainmobiliario.modelos.Arriendo;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * CLASE GeneradorFactura
 * ─────────────────────────────────────────────────────────────────
 * Implementa la interface IFacturable con toda la lógica de negocio
 * para generar facturas de ventas y arriendos inmobiliarios.
 *
 * CONCEPTO POO — "implements":
 *   Al escribir "implements IFacturable", esta clase se COMPROMETE
 *   a definir TODOS los métodos declarados en la interface.
 *   Si falta uno, el compilador lo marcará como error.
 *
 * Esta clase maneja DOS tipos de transacción:
 *   1. VENTA    → usa el objeto Venta
 *   2. ARRIENDO → usa el objeto Arriendo
 *
 * Usa el patrón FACTORY METHOD: dos métodos estáticos (desde() que
 * crean instancias según el tipo de operación, evitando constructores
 * confusos con muchos parámetros.
 *
 * Archivo: facturacion/GeneradorFactura.java
 */
public class GeneradorFactura implements IFacturable {

    // ── Constantes ─────────────────────────────────────────────────
    private static final double PORCENTAJE_IVA   = 0.19;   // IVA Colombia 19%
    private static final String PREFIJO_VENTA    = "VTA";
    private static final String PREFIJO_ARRIENDO = "ARR";

    // Formateador de moneda colombiana
    private static final NumberFormat FORMATO_MONEDA =
        NumberFormat.getCurrencyInstance(new Locale("es", "CO"));

    // ── Atributos internos ─────────────────────────────────────────
    private final Venta   venta;     // null si es arriendo
    private final Arriendo arriendo; // null si es venta
    private final String  tipoTransaccion;
    private final String  numeroFactura;
    private final String  fechaEmision;

    // ── Constructores privados (usa los métodos estáticos de abajo) ─

    /** Constructor para facturas de VENTA */
    private GeneradorFactura(Venta venta) {
        this.venta          = venta;
        this.arriendo       = null;
        this.tipoTransaccion = "VENTA";
        this.fechaEmision   = obtenerFechaActual();
        this.numeroFactura  = generarNumeroFactura(PREFIJO_VENTA, venta.getId());
    }

    /** Constructor para facturas de ARRIENDO */
    private GeneradorFactura(Arriendo arriendo) {
        this.venta          = null;
        this.arriendo       = arriendo;
        this.tipoTransaccion = "ARRIENDO";
        this.fechaEmision   = obtenerFechaActual();
        this.numeroFactura  = generarNumeroFactura(PREFIJO_ARRIENDO, arriendo.getId());
    }

    // ── Métodos estáticos de creación (Factory Methods) ────────────

    /**
     * Crea un GeneradorFactura a partir de una Venta.
     *
     * Uso: GeneradorFactura gen = GeneradorFactura.desde(miVenta);
     */
    public static GeneradorFactura desde(Venta venta) {
        if (venta == null) throw new IllegalArgumentException("La venta no puede ser nula.");
        return new GeneradorFactura(venta);
    }

    /**
     * Crea un GeneradorFactura a partir de un Arriendo.
     *
     * Uso: GeneradorFactura gen = GeneradorFactura.desde(miArriendo);
     */
    public static GeneradorFactura desde(Arriendo arriendo) {
        if (arriendo == null) throw new IllegalArgumentException("El arriendo no puede ser nulo.");
        return new GeneradorFactura(arriendo);
    }

    // ── Implementación de IFacturable ──────────────────────────────

    /**
     * @override IFacturable
     * Calcula el subtotal según el tipo de transacción.
     */
    @Override
    public double calcularTotal() {
        if (tipoTransaccion.equals("VENTA")) {
            // En venta: precio final de la propiedad (ya calculado en Venta)
            return venta.getPrecioFinal();
        } else {
            // En arriendo: valor mensual × número de meses
            return arriendo.calcularTotalArriendo();
        }
    }

    /**
     * @override IFacturable
     * Calcula el IVA (19%) sobre el subtotal.
     */
    @Override
    public double calcularIVA() {
        return calcularTotal() * PORCENTAJE_IVA;
    }

    /**
     * @override IFacturable
     * Devuelve el número único de factura generado en el constructor.
     */
    @Override
    public String obtenerNumeroFactura() {
        return numeroFactura;
    }

    /**
     * @override IFacturable
     * Resumen en una línea para logs o listados.
     */
    @Override
    public String obtenerResumen() {
        String cliente = tipoTransaccion.equals("VENTA")
            ? venta.getCliente().getNombre()
            : arriendo.getCliente().getNombre();

        return String.format("[%s] %s | Cliente: %s | Total: %s",
            numeroFactura,
            tipoTransaccion,
            cliente,
            FORMATO_MONEDA.format(calcularTotal() + calcularIVA())
        );
    }

    /**
     * @override IFacturable
     * Genera el texto completo de la factura con todos sus bloques.
     */
    @Override
    public String generarFactura() {
        if (tipoTransaccion.equals("VENTA")) {
            return construirFacturaVenta();
        } else {
            return construirFacturaArriendo();
        }
    }

    // ── Método público para obtener el objeto Factura armado ────────

    /**
     * Construye y devuelve un objeto Factura con todos los datos.
     * Útil si quieres guardar la factura en BD o pasarla a otra capa.
     */
    public Factura obtenerFactura() {
        double subtotal = calcularTotal();
        double iva      = calcularIVA();
        double total    = subtotal + iva;

        String nombreCliente, docCliente, correoCliente, telefonoCliente;
        String direccion, tipoPropiedad;
        double area;
        String nombreAgente;

        if (tipoTransaccion.equals("VENTA")) {
            nombreCliente   = venta.getCliente().getNombre();
            docCliente      = venta.getCliente().getTipoDocumento() + " "
                              + venta.getCliente().getNumeroDocumento();
            correoCliente   = venta.getCliente().getCorreo();
            telefonoCliente = venta.getCliente().getTelefono();
            direccion       = venta.getPropiedad().getDireccion();
            tipoPropiedad   = venta.getPropiedad().getClass().getSimpleName();
            area            = venta.getPropiedad().getArea();
            nombreAgente    = venta.getAgente().getNombre();
        } else {
            nombreCliente   = arriendo.getCliente().getNombre();
            docCliente      = arriendo.getCliente().getTipoDocumento() + " "
                              + arriendo.getCliente().getNumeroDocumento();
            correoCliente   = arriendo.getCliente().getCorreo();
            telefonoCliente = arriendo.getCliente().getTelefono();
            direccion       = arriendo.getPropiedad().getDireccion();
            tipoPropiedad   = arriendo.getPropiedad().getClass().getSimpleName();
            area            = arriendo.getPropiedad().getArea();
            nombreAgente    = arriendo.getAgente().getNombre();
        }

        Factura f = new Factura(
            numeroFactura, tipoTransaccion, fechaEmision,
            nombreCliente, docCliente, correoCliente, telefonoCliente,
            direccion, tipoPropiedad, area,
            nombreAgente,
            subtotal, iva, total
        );

        if (tipoTransaccion.equals("ARRIENDO")) {
            f.setMeses(arriendo.getMeses());
            f.setValorMensual(arriendo.getValorMensual());
        }

        return f;
    }

    // ── Métodos privados de construcción de texto ──────────────────

    private String construirFacturaVenta() {
        double subtotal = calcularTotal();
        double iva      = calcularIVA();
        double total    = subtotal + iva;

        StringBuilder sb = new StringBuilder();
        sb.append(lineaSeparadora('=', 55)).append("\n");
        sb.append(centrar("SISTEMA INMOBILIARIO", 55)).append("\n");
        sb.append(centrar("FACTURA DE VENTA", 55)).append("\n");
        sb.append(lineaSeparadora('=', 55)).append("\n");

        sb.append(String.format("%-22s %s%n", "N° Factura:",   numeroFactura));
        sb.append(String.format("%-22s %s%n", "Fecha emisión:", fechaEmision));
        sb.append(String.format("%-22s %s%n", "Tipo:",         "COMPRAVENTA DE PROPIEDAD"));

        sb.append(lineaSeparadora('-', 55)).append("\n");
        sb.append("  DATOS DEL CLIENTE\n");
        sb.append(lineaSeparadora('-', 55)).append("\n");
        sb.append(String.format("%-22s %s%n", "Nombre:",     venta.getCliente().getNombre()));
        sb.append(String.format("%-22s %s %s%n", "Documento:",
            venta.getCliente().getTipoDocumento(),
            venta.getCliente().getNumeroDocumento()));
        sb.append(String.format("%-22s %s%n", "Correo:",     venta.getCliente().getCorreo()));
        sb.append(String.format("%-22s %s%n", "Teléfono:",   venta.getCliente().getTelefono()));

        sb.append(lineaSeparadora('-', 55)).append("\n");
        sb.append("  DATOS DE LA PROPIEDAD\n");
        sb.append(lineaSeparadora('-', 55)).append("\n");
        sb.append(String.format("%-22s %s%n", "Tipo:",       venta.getPropiedad().getClass().getSimpleName()));
        sb.append(String.format("%-22s %s%n", "Dirección:",  venta.getPropiedad().getDireccion()));
        sb.append(String.format("%-22s %.2f m²%n", "Área:",  venta.getPropiedad().getArea()));
        sb.append(String.format("%-22s %s%n", "Estado:",     venta.getPropiedad().getEstado()));
        sb.append(String.format("%-22s %s%n", "Agente:",     venta.getAgente().getNombre()));

        sb.append(lineaSeparadora('-', 55)).append("\n");
        sb.append("  DETALLE ECONÓMICO\n");
        sb.append(lineaSeparadora('-', 55)).append("\n");
        sb.append(String.format("%-30s %s%n", "Precio de la propiedad:", FORMATO_MONEDA.format(subtotal)));
        sb.append(String.format("%-30s %s%n", "IVA (19%):",             FORMATO_MONEDA.format(iva)));
        sb.append(lineaSeparadora('-', 55)).append("\n");
        sb.append(String.format("%-30s %s%n", "TOTAL A PAGAR:",          FORMATO_MONEDA.format(total)));
        sb.append(lineaSeparadora('=', 55)).append("\n");
        sb.append(centrar("¡Gracias por su confianza!", 55)).append("\n");
        sb.append(lineaSeparadora('=', 55)).append("\n");

        return sb.toString();
    }

    private String construirFacturaArriendo() {
        double subtotal = calcularTotal();
        double iva      = calcularIVA();
        double total    = subtotal + iva;

        StringBuilder sb = new StringBuilder();
        sb.append(lineaSeparadora('=', 55)).append("\n");
        sb.append(centrar("SISTEMA INMOBILIARIO", 55)).append("\n");
        sb.append(centrar("FACTURA DE ARRIENDO", 55)).append("\n");
        sb.append(lineaSeparadora('=', 55)).append("\n");

        sb.append(String.format("%-22s %s%n", "N° Factura:",    numeroFactura));
        sb.append(String.format("%-22s %s%n", "Fecha emisión:", fechaEmision));
        sb.append(String.format("%-22s %s%n", "Tipo:",          "CONTRATO DE ARRIENDO"));

        sb.append(lineaSeparadora('-', 55)).append("\n");
        sb.append("  DATOS DEL CLIENTE\n");
        sb.append(lineaSeparadora('-', 55)).append("\n");
        sb.append(String.format("%-22s %s%n", "Nombre:",    arriendo.getCliente().getNombre()));
        sb.append(String.format("%-22s %s %s%n", "Documento:",
            arriendo.getCliente().getTipoDocumento(),
            arriendo.getCliente().getNumeroDocumento()));
        sb.append(String.format("%-22s %s%n", "Correo:",    arriendo.getCliente().getCorreo()));
        sb.append(String.format("%-22s %s%n", "Teléfono:",  arriendo.getCliente().getTelefono()));

        sb.append(lineaSeparadora('-', 55)).append("\n");
        sb.append("  DATOS DE LA PROPIEDAD\n");
        sb.append(lineaSeparadora('-', 55)).append("\n");
        sb.append(String.format("%-22s %s%n", "Tipo:",      arriendo.getPropiedad().getClass().getSimpleName()));
        sb.append(String.format("%-22s %s%n", "Dirección:", arriendo.getPropiedad().getDireccion()));
        sb.append(String.format("%-22s %.2f m²%n", "Área:", arriendo.getPropiedad().getArea()));
        sb.append(String.format("%-22s %s%n", "Estado:",    arriendo.getPropiedad().getEstado()));
        sb.append(String.format("%-22s %s%n", "Agente:",    arriendo.getAgente().getNombre()));
        sb.append(String.format("%-22s %s%n", "Fecha inicio:", arriendo.getFechaInicio()));

        sb.append(lineaSeparadora('-', 55)).append("\n");
        sb.append("  DETALLE ECONÓMICO\n");
        sb.append(lineaSeparadora('-', 55)).append("\n");
        sb.append(String.format("%-30s %s%n", "Valor mensual:",         FORMATO_MONEDA.format(arriendo.getValorMensual())));
        sb.append(String.format("%-30s %d meses%n", "Duración:",        arriendo.getMeses()));
        sb.append(String.format("%-30s %s%n", "Subtotal arriendo:",     FORMATO_MONEDA.format(subtotal)));
        sb.append(String.format("%-30s %s%n", "IVA (19%):",             FORMATO_MONEDA.format(iva)));
        sb.append(lineaSeparadora('-', 55)).append("\n");
        sb.append(String.format("%-30s %s%n", "TOTAL A PAGAR:",         FORMATO_MONEDA.format(total)));
        sb.append(lineaSeparadora('=', 55)).append("\n");
        sb.append(centrar("¡Gracias por su confianza!", 55)).append("\n");
        sb.append(lineaSeparadora('=', 55)).append("\n");

        return sb.toString();
    }

    // ── Utilidades privadas ────────────────────────────────────────

    /** Genera un número de factura único con prefijo + timestamp + ID */
    private static String generarNumeroFactura(String prefijo, int id) {
        String timestamp = LocalDateTime.now()
            .format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmm"));
        return String.format("%s-%s-%04d", prefijo, timestamp, id);
    }

    /** Devuelve la fecha y hora actual formateada */
    private static String obtenerFechaActual() {
        return LocalDateTime.now()
            .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
    }

    /** Crea una línea de 'n' caracteres repetidos */
    private static String lineaSeparadora(char c, int n) {
        return String.valueOf(c).repeat(n);
    }

    /** Centra un texto en un ancho dado */
    private static String centrar(String texto, int ancho) {
        int padding = Math.max(0, (ancho - texto.length()) / 2);
        return " ".repeat(padding) + texto;
    }
}

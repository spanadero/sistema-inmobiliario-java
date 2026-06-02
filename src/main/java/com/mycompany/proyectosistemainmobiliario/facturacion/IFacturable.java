package com.mycompany.proyectosistemainmobiliario.facturacion;

/**
 * INTERFACE IFacturable
 * ─────────────────────────────────────────────────────────────────
 * Define el CONTRATO que debe cumplir cualquier clase que quiera
 * generar facturas en el sistema inmobiliario.
 *
 * Una interface en Java es una plantilla de comportamiento:
 *   - Solo declara QUÉ se puede hacer (métodos sin cuerpo).
 *   - No dice CÓMO hacerlo (eso lo define la clase que la implementa).
 *   - Garantiza que toda clase que la implemente tenga estos métodos.
 *
 * Ventaja de POO: si mañana necesitas otro tipo de factura
 * (p.ej. FacturaElectronica), solo creas una nueva clase que
 * implemente IFacturable, sin tocar el código existente.
 *
 * @author SistemaInmobiliario
 */
public interface IFacturable {

    /**
     * Genera y devuelve la factura completa como texto formateado.
     *
     * @return String con el contenido completo de la factura
     */
    String generarFactura();

    /**
     * Calcula el total a pagar según el tipo de transacción.
     * En ventas: precio final de la propiedad.
     * En arriendos: valorMensual × número de meses.
     *
     * @return double con el monto total
     */
    double calcularTotal();

    /**
     * Calcula el IVA sobre el total calculado.
     * El porcentaje de IVA es definido por cada implementación.
     *
     * @return double con el valor del IVA
     */
    double calcularIVA();

    /**
     * Devuelve un número de factura único.
     * Cada implementación puede tener su propio formato de numeración.
     *
     * @return String con el número o código de la factura
     */
    String obtenerNumeroFactura();

    /**
     * Devuelve el resumen corto de la factura (una línea).
     * Útil para listados o logs.
     *
     * @return String con el resumen
     */
    String obtenerResumen();
}

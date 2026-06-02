package com.mycompany.proyectosistemainmobiliario.vistas;

import java.time.LocalDate;
import java.util.Scanner;
import java.util.Queue;
import java.util.LinkedList;

import com.mycompany.proyectosistemainmobiliario.modelos.*;
import com.mycompany.proyectosistemainmobiliario.servicios.*;

/**
 * Interfaz de consola del sistema (Menu original conservado).
 * La lógica de cada opción es idéntica al proyecto original;
 * los servicios ahora persisten en MySQL vía JDBC.
 */
public class Menu {

    private Scanner sc = new Scanner(System.in);

    private PropiedadService propiedadService = new PropiedadService();
    private VentaService     ventaService     = new VentaService();
    private ArriendoService  arriendoService  = new ArriendoService();
    private ClienteService   clienteService   = new ClienteService();

    // Cola FIFO de clientes en memoria
    private Queue<Cliente> colaClientes = new LinkedList<>();

    // Arreglo polimórfico
    private Propiedad[] arregloPropiedades = new Propiedad[5];

    public void iniciar() {
        int opcion;
        do {
            System.out.println("\n===== SISTEMA INMOBILIARIO =====");
            System.out.println("1.  Registrar cliente");
            System.out.println("2.  Agregar cliente a cola");
            System.out.println("3.  Atender cliente");
            System.out.println("4.  Registrar apartamento");
            System.out.println("5.  Registrar casa");
            System.out.println("6.  Mostrar propiedades");
            System.out.println("7.  Realizar venta");
            System.out.println("8.  Mostrar ventas");
            System.out.println("9.  Registrar arriendo");
            System.out.println("10. Mostrar arriendos");
            System.out.println("11. Demostración de arreglo polimórfico");
            System.out.println("0.  Salir");
            System.out.print("Opcion: ");
            opcion = sc.nextInt();

            switch (opcion) {
                case 1:  registrarCliente();             break;
                case 2:  agregarClienteCola();           break;
                case 3:  atenderCliente();               break;
                case 4:  registrarApartamento();         break;
                case 5:  registrarCasa();                break;
                case 6:  propiedadService.mostrarPropiedades(); break;
                case 7:  realizarVenta();                break;
                case 8:  ventaService.mostrarVentas();   break;
                case 9:  registrarArriendo();            break;
                case 10: arriendoService.mostrarArriendos(); break;
                case 11: llenarArreglo(); mostrarArreglo(); break;
                case 0:  System.out.println("Saliendo..."); break;
                default: System.out.println("Opción inválida");
            }
        } while (opcion != 0);
    }

    // ── CLIENTES ──────────────────────────────────────────────────
    private Cliente registrarCliente() {
        sc.nextLine();
        System.out.print("Nombre: ");    String nombre   = sc.nextLine();
        System.out.print("Correo: ");    String correo   = sc.nextLine();
        System.out.print("Telefono: ");  String telefono = sc.nextLine();
        System.out.print("Tipo documento (CC/CE/NIT): "); String tipoDoc = sc.nextLine();
        System.out.print("Número documento: ");           String numDoc  = sc.nextLine();

        Cliente c = new Cliente(0, nombre, correo, telefono,
                tipoDoc.isEmpty() ? "CC" : tipoDoc,
                numDoc.isEmpty()  ? "000" : numDoc);

        clienteService.registrarCliente(c); // persiste en BD, asigna ID real
        System.out.println("Cliente registrado con ID: " + c.getId());
        return c;
    }

    private void agregarClienteCola() {
        Cliente c = registrarCliente();
        colaClientes.add(c);
        arriendoService.agregarCliente(c);
        System.out.println("Cliente agregado a la cola. Total en cola: " + colaClientes.size());
    }

    private void atenderCliente() {
        Cliente c = colaClientes.poll();
        if (c != null) System.out.println("Atendiendo a: " + c.getNombre() + " [ID: " + c.getId() + "]");
        else           System.out.println("No hay clientes en espera");
    }

    // ── PROPIEDADES ───────────────────────────────────────────────
    private void registrarApartamento() {
        sc.nextLine();
        System.out.print("Dirección: ");      String dir   = sc.nextLine();
        System.out.print("Área (m²): ");      double area  = sc.nextDouble();
        System.out.print("Piso: ");           int piso     = sc.nextInt();
        System.out.print("Administración $: "); double admin = sc.nextDouble();
        sc.nextLine();
        System.out.print("Número apartamento (ej. 101): "); String numApa = sc.nextLine();

        Apartamento a = new Apartamento(
                numApa.isEmpty() ? "101" : numApa,
                piso, admin, 0, dir, area, "disponible");

        propiedadService.registrarApartamento(a);
        System.out.printf("Apartamento registrado. Precio calculado: $%,.0f%n", a.calcularPrecio());
    }

    private void registrarCasa() {
        sc.nextLine();
        System.out.print("Dirección: ");           String dir  = sc.nextLine();
        System.out.print("Área (m²): ");           double area = sc.nextDouble();
        System.out.print("¿Tiene patio? (s/n): ");
        sc.nextLine(); boolean patio = sc.nextLine().trim().equalsIgnoreCase("s");

        Casa c = new Casa(patio, 0, dir, area, "disponible");
        propiedadService.registrarCasa(c);
        System.out.printf("Casa registrada. Precio calculado: $%,.0f%n", c.calcularPrecio());
    }

    // ── VENTAS ────────────────────────────────────────────────────
    private void realizarVenta() {
        Cliente cliente = colaClientes.poll();
        if (cliente == null) {
            System.out.println("No hay clientes en cola. Agrega uno con la opción 2.");
            return;
        }
        System.out.println("Cliente: " + cliente.getNombre());

        System.out.print("ID Propiedad: ");
        int idProp = sc.nextInt();
        Propiedad propiedad = propiedadService.buscarPorId(idProp);
        if (propiedad == null) {
            System.out.println("Propiedad no encontrada.");
            colaClientes.add(cliente); // devolver a la cola
            return;
        }

        // Agente: en la consola se selecciona por ID
        System.out.print("ID Agente (ver lista: 1-Diana, 2-Ricardo, 3-Patricia): ");
        int idAgente = sc.nextInt();
        AgenteService agenteService = new AgenteService();
        Agente agente = agenteService.buscarPorId(idAgente);
        if (agente == null) {
            System.out.println("Agente no encontrado. Usando agente por defecto.");
            agente = new Agente(1, "Diana Molina", "diana@inmobiliaria.co", "3123334455", 3500000, "AGT-001");
        }

        String fecha = LocalDate.now().toString(); // formato yyyy-MM-dd correcto para DATE
        Venta venta = new Venta(0, cliente, propiedad, agente, fecha);
        ventaService.realizarVenta(venta);
        System.out.printf("Venta realizada. Precio final: $%,.0f%n", venta.getPrecioFinal());
    }

    // ── ARRIENDOS ─────────────────────────────────────────────────
    private void registrarArriendo() {
        System.out.println("Datos del cliente para el arriendo:");
        Cliente cliente = registrarCliente();

        System.out.print("ID Propiedad: ");
        int idProp = sc.nextInt();
        Propiedad propiedad = propiedadService.buscarPorId(idProp);
        if (propiedad == null) {
            System.out.println("Propiedad no encontrada.");
            return;
        }

        System.out.print("ID Agente (1-Diana, 2-Ricardo, 3-Patricia): ");
        int idAgente = sc.nextInt();
        AgenteService agenteService = new AgenteService();
        Agente agente = agenteService.buscarPorId(idAgente);
        if (agente == null) {
            agente = new Agente(1, "Diana Molina", "diana@inmobiliaria.co", "3123334455", 3500000, "AGT-001");
        }

        System.out.print("Meses: ");
        int meses = sc.nextInt();
        sc.nextLine();

        String fecha = LocalDate.now().toString();
        Arriendo a = new Arriendo(0, cliente, propiedad, agente, meses, fecha);
        arriendoService.registrarArriendo(a);
        System.out.printf("Arriendo registrado. Valor mensual: $%,.0f | Total: $%,.0f%n",
                a.getValorMensual(), a.calcularTotalArriendo());
    }

    // ── ARREGLO POLIMÓRFICO ───────────────────────────────────────
    private void llenarArreglo() {
        arregloPropiedades[0] = new Casa(true, 1, "Calle 1", 120, "disponible");
        arregloPropiedades[1] = new Apartamento("A1", 5, 200000, 2, "Calle 2", 80, "disponible");
    }

    private void mostrarArreglo() {
        System.out.println("\n-- Arreglo polimórfico (calcularPrecio()) --");
        for (Propiedad p : arregloPropiedades) {
            if (p != null) {
                System.out.println("Dirección: " + p.getDireccion());
                System.out.printf("Precio   : $%,.0f%n", p.calcularPrecio());
                System.out.println("------------------");
            }
        }
    }
}

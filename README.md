# 🏠 Sistema de Gestión Inmobiliaria

Sistema de escritorio para la administración integral de propiedades, clientes, ventas y arriendos de una empresa inmobiliaria. Desarrollado en **Java** con interfaz gráfica **Swing**, persistencia en **MySQL** mediante **JDBC** y generación de facturas en **PDF**.

> Proyecto académico desarrollado en la **Fundación Universitaria Compensar** para la asignatura de Programación Orientada a Objetos.

---

## 📑 Tabla de contenido

- [Características](#-características)
- [Tecnologías](#️-tecnologías)
- [Arquitectura](#-arquitectura)
- [Requisitos previos](#-requisitos-previos)
- [Instalación paso a paso](#-instalación-paso-a-paso)
- [Configuración de la conexión](#-configuración-de-la-conexión)
- [Ejecución](#-ejecución)
- [Usuarios por defecto](#-usuarios-por-defecto)
- [Estructura del proyecto](#-estructura-del-proyecto)
- [Pruebas de rendimiento](#-pruebas-de-rendimiento)
- [Solución de problemas](#-solución-de-problemas)
- [Autores](#-autores)

---

## ✨ Características

- **Registro y consulta** de clientes, propiedades (casas y apartamentos), agentes, ventas y arriendos
- **Cálculo automático** de precios según las características de la propiedad (área, piso, administración, patio)
- **Cola FIFO** para atención de clientes en orden de llegada
- **Pila Stack** para acceso rápido a la última venta registrada
- **Búsqueda avanzada** con filtros combinados (precio, área, ciudad, barrio, tipo de propiedad)
- **Generación de facturas en PDF** descargables
- **Sistema de autenticación** con roles (Administrador y Agente)
- **Persistencia completa** en base de datos MySQL
- **Indicador en tiempo real** del estado de conexión a la base de datos
- **Módulo de pruebas de rendimiento** integrado (benchmarking de tiempo de ejecución y throughput)

---

## 🛠️ Tecnologías

| Tecnología | Versión | Uso |
|---|---|---|
| Java | 23 | Lenguaje principal |
| MySQL | 8.x | Motor de base de datos |
| MySQL Connector/J | 8.3.0 | Driver JDBC |
| OpenPDF | 1.3.43 | Generación de facturas PDF |
| jBCrypt | 0.4 | Cifrado de contraseñas |
| Maven | 3.x | Gestión de dependencias |
| Java Swing | — | Interfaz gráfica |
| JUnit 5 | 5.10.2 | Pruebas unitarias |
| Mockito | 5.8.0 | Mocking para pruebas |

---

## 🏗️ Arquitectura

El proyecto sigue una **arquitectura modular** con separación clara de responsabilidades:

```
com.mycompany.proyectosistemainmobiliario/
├── main/           → Punto de entrada (Main.java)
├── modelos/        → Clases del dominio (Cliente, Propiedad, Casa, Apartamento, Agente, Venta, Arriendo)
├── servicios/      → Lógica de negocio + acceso a datos (Services)
├── db/             → Gestión de conexión a MySQL (Conexion.java)
├── vistas/         → Interfaz gráfica (MenuSwing, LoginDialog, FacturaSwing)
├── facturacion/    → Generación de facturas PDF (Factura, GeneradorFactura, ExportadorPDF)
└── auth/           → Autenticación y manejo de sesiones (Sesion)
```

**Principios aplicados:**
- ✅ Encapsulamiento, herencia, polimorfismo y abstracción
- ✅ Separación de responsabilidades
- ✅ Reutilización mediante interfaces (`IFacturable`)
- ✅ Genéricos (`Repositorio<T>`)
- ✅ Transacciones atómicas en operaciones críticas

---

## 📋 Requisitos previos

Antes de comenzar, asegúrate de tener instalado:

- ☕ **Java JDK 23** o superior — [Descargar](https://www.oracle.com/java/technologies/downloads/)
- 🗄️ **MySQL Server 8.x** — [Descargar](https://dev.mysql.com/downloads/mysql/)
- 🖥️ **MySQL Workbench** (recomendado) — [Descargar](https://dev.mysql.com/downloads/workbench/)
- 💻 **NetBeans 19+** o **IntelliJ IDEA** o cualquier IDE compatible con Maven
- 📦 **Maven 3.x** (incluido en NetBeans/IntelliJ)
- 🌐 **Git** (para clonar el repositorio)

Verifica tus instalaciones con:
```bash
java -version    # debe mostrar 23 o superior
mvn -version     # debe mostrar 3.x
mysql --version  # debe mostrar 8.x
```

---

## 🚀 Instalación paso a paso

### 1. Clonar el repositorio

```bash
git clone https://github.com/[tu-usuario]/ProyectoSistemaInmobiliario.git
cd ProyectoSistemaInmobiliario
```

### 2. Crear la base de datos en MySQL

Abre **MySQL Workbench** (o consola MySQL) y ejecuta el script `inmobiliaria.sql`:

**Opción A — MySQL Workbench:**
1. Conéctate a tu servidor local
2. Abre `File → Open SQL Script...` y selecciona `inmobiliaria.sql`
3. Ejecuta con `Ctrl + Shift + Enter` (o el ícono de rayo ⚡)

**Opción B — Consola MySQL:**
```bash
mysql -u root -p < inmobiliaria.sql
```

Esto creará la base de datos `inmobiliaria` con 7 tablas:
- `cliente_tb`, `agente_tb`, `propiedad_tb`
- `apartamento_tb`, `casa_tb`
- `venta_tb`, `arriendo_tb`
- y datos de ejemplo precargados

### 3. Verificar que la base de datos se creó correctamente

```sql
USE inmobiliaria;
SHOW TABLES;
SELECT COUNT(*) FROM propiedad_tb;  -- debe retornar 7
```

---

## 🔧 Configuración de la conexión

Antes de ejecutar el programa, debes ajustar los datos de conexión a **tu** servidor MySQL local.

Abre el archivo:
```
src/main/java/com/mycompany/proyectosistemainmobiliario/db/Conexion.java
```

Modifica estas líneas con tus credenciales:

```java
private static final String URL      = "jdbc:mysql://localhost:3306/inmobiliaria?useSSL=false&serverTimezone=America/Bogota&allowPublicKeyRetrieval=true";
private static final String USUARIO  = "root";          // ← tu usuario MySQL
private static final String CLAVE    = "tu_contraseña"; // ← tu contraseña MySQL
```

> ⚠️ **Importante:** Por seguridad, considera usar variables de entorno en lugar de hardcodear la contraseña en un proyecto de producción.

---

## ▶️ Ejecución

### Desde NetBeans

1. Abrir el proyecto: `File → Open Project` y seleccionar la carpeta `ProyectoSistemaInmobiliario`
2. Esperar a que Maven descargue las dependencias automáticamente
3. Clic derecho sobre el proyecto → `Clean and Build`
4. Clic derecho → `Run` (o presionar `F6`)

### Desde IntelliJ IDEA

1. `File → Open` y seleccionar la carpeta del proyecto
2. Esperar a que IntelliJ indexe el proyecto
3. Abrir `Main.java` y presionar el ícono de play ▶️ o `Shift + F10`

### Desde línea de comandos (Maven)

```bash
mvn clean install
mvn exec:java -Dexec.mainClass="com.mycompany.proyectosistemainmobiliario.main.Main"
```

Al ejecutar, la primera pantalla será el **login**. Una vez autenticado, verás la ventana principal con el indicador verde **● BD conectada** en la esquina superior derecha si la conexión es exitosa.

---

## 👤 Usuarios por defecto

El script SQL incluye estos usuarios para pruebas (las contraseñas están cifradas con bcrypt):

| Usuario | Contraseña | Rol |
|---|---|---|
| `admin` | `admin123` | Administrador |
| `agente` | `agente123` | Agente |

> El **Administrador** tiene acceso completo al sistema. El **Agente** tiene acceso limitado a operaciones de gestión diaria.

---

## 📁 Estructura del proyecto

```
ProyectoSistemaInmobiliario/
├── pom.xml                    # Configuración Maven y dependencias
├── inmobiliaria.sql           # Script de creación de BD
├── README.md                  # Este archivo
└── src/
    └── main/
        └── java/
            └── com/mycompany/proyectosistemainmobiliario/
                ├── main/
                │   └── Main.java
                ├── modelos/
                │   ├── Propiedad.java       (abstracta)
                │   ├── Casa.java
                │   ├── Apartamento.java
                │   ├── Cliente.java
                │   ├── Agente.java
                │   ├── Venta.java
                │   ├── Arriendo.java
                │   ├── Usuario.java
                │   ├── Rol.java             (enum)
                │   ├── FiltroPropiedad.java
                │   └── Repositorio.java     (genérico)
                ├── servicios/
                │   ├── PropiedadService.java
                │   ├── ClienteService.java
                │   ├── AgenteService.java
                │   ├── VentaService.java
                │   ├── ArriendoService.java
                │   ├── UsuarioService.java
                │   └── GenerarHash.java
                ├── db/
                │   └── Conexion.java
                ├── vistas/
                │   ├── MenuSwing.java
                │   ├── LoginDialog.java
                │   ├── FacturaSwing.java
                │   └── Menu.java
                ├── facturacion/
                │   ├── IFacturable.java     (interfaz)
                │   ├── Factura.java
                │   ├── GeneradorFactura.java
                │   └── ExportadorPDF.java
                └── auth/
                    └── Sesion.java
```

---

## 📊 Pruebas de rendimiento

El proyecto incluye un módulo de benchmarking que mide:

- **Tiempo de ejecución**: cuánto tarda una operación individual (con `System.nanoTime()`)
- **Throughput**: cuántas operaciones por segundo soporta el sistema

**Resultados obtenidos en entorno local:**

| Operación | Throughput | Interpretación |
|---|---|---|
| INSERTs | **138 ops/s** | Rendimiento normal (rango esperado 50-200 ops/s) |
| SELECTs | **313 ops/s** | Alto rendimiento (> 200 ops/s) |

Equivalente a más de **8.000 operaciones por minuto**, suficiente para una inmobiliaria mediana en horario pico.

---

## 🔧 Solución de problemas

<details>
<summary><b>Error: <code>Access denied for user 'root'@'localhost'</code></b></summary>

La contraseña en `Conexion.java` no coincide con la de tu MySQL. Edita el archivo y pon la contraseña correcta.
</details>

<details>
<summary><b>Error: <code>Unknown database 'inmobiliaria'</code></b></summary>

No ejecutaste el script SQL. Vuelve al paso 2 de instalación.
</details>

<details>
<summary><b>Error: <code>Communications link failure</code></b></summary>

El servidor MySQL no está corriendo. Inícialo desde MySQL Workbench, o desde consola con:
```bash
# Windows: en Services.msc activar "MySQL80"
# Linux:   sudo systemctl start mysql
# macOS:   brew services start mysql
```
</details>

<details>
<summary><b>Error: <code>Could not create connection to database server</code></b></summary>

Falta el driver JDBC. Ejecuta `Clean and Build` en NetBeans para que Maven descargue las dependencias.
</details>

<details>
<summary><b>El indicador muestra <code>● Sin conexión</code> en rojo</b></summary>

Combinación de las soluciones anteriores. Revisa: contraseña correcta, BD creada, servidor MySQL corriendo, puerto 3306 disponible.
</details>

<details>
<summary><b>Error al generar PDFs</b></summary>

Asegúrate de tener la dependencia OpenPDF descargada (ejecuta `mvn clean install`) y permisos de escritura en la carpeta donde se guardarán las facturas.
</details>

---

## 📄 Licencia

Este proyecto fue desarrollado con fines académicos. Puede ser usado libremente para aprendizaje y referencia.

---

## 👥 Autores

**Equipo de desarrollo:**

- **Santiago Panadero Chavarro** — *Desarrollo principal y arquitectura*
- **Andrés Barrios** — *Reportes PDF y búsqueda avanzada*
- **Lis Eliana Sofía Cajicá** — *Pruebas de rendimiento*

**Docente:** Nathaly Díaz Morales
**Asignatura:** Programación Orientada a Objetos
**Institución:** Fundación Universitaria Compensar
**Año:** 2026

---

<div align="center">

⭐ **Si este proyecto te fue útil, no olvides darle una estrella en GitHub** ⭐

</div>

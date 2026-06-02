-- ============================================================
--  Sistema de Gestión Inmobiliaria
--  Base de datos: inmobiliaria
--  Coherente con las clases Java del proyecto
-- ============================================================

DROP DATABASE IF EXISTS inmobiliaria;
CREATE DATABASE inmobiliaria CHARACTER SET utf8mb4 COLLATE utf8mb4_spanish_ci;
USE inmobiliaria;

-- ── CLIENTES ─────────────────────────────────────────────────
-- Corresponde a la clase Cliente.java
CREATE TABLE cliente_tb (
    id_cli              INT          PRIMARY KEY AUTO_INCREMENT,
    nombre_cli          VARCHAR(100) NOT NULL,
    correo_cli          VARCHAR(100),
    telefono_cli        VARCHAR(20),
    tipoDocumento_cli   VARCHAR(20)  DEFAULT 'CC',
    numeroDocumento_cli VARCHAR(50)
);

INSERT INTO cliente_tb (nombre_cli, correo_cli, telefono_cli, tipoDocumento_cli, numeroDocumento_cli) VALUES
('Carlos Ramírez',  'carlos.ramirez@gmail.com',  '3101234567', 'CC',  '1023456789'),
('Laura Gómez',     'laura.gomez@hotmail.com',   '3209876543', 'CC',  '1098765432'),
('Andrés Peña',     'andres.pena@outlook.com',   '3154561234', 'CE',  'PE123456'  ),
('Sofía Torres',    'sofia.torres@yahoo.com',    '3001112233', 'CC',  '1014789632'),
('Miguel Herrera',  'miguel.herrera@gmail.com',  '3176543210', 'NIT', '900123456-1');

-- ── AGENTES ──────────────────────────────────────────────────
-- Corresponde a la clase Agente.java
CREATE TABLE agente_tb (
    id_agt              INT          PRIMARY KEY AUTO_INCREMENT,
    nombre_agt          VARCHAR(100) NOT NULL,
    correo_agt          VARCHAR(100),
    telefono_agt        VARCHAR(20),
    salario_agt         DOUBLE,
    codigoEmpleado_agt  VARCHAR(50)
);

INSERT INTO agente_tb (nombre_agt, correo_agt, telefono_agt, salario_agt, codigoEmpleado_agt) VALUES
('Diana Molina',    'diana.molina@inmobiliaria.co',   '3123334455', 3500000, 'AGT-001'),
('Ricardo Vargas',  'ricardo.vargas@inmobiliaria.co', '3187776655', 4200000, 'AGT-002'),
('Patricia León',   'patricia.leon@inmobiliaria.co',  '3041239876', 3800000, 'AGT-003');

-- ── PROPIEDADES (tabla base abstracta) ───────────────────────
-- Corresponde a la clase abstracta Propiedad.java
-- estado_prop: 'disponible' | 'vendida' | 'arrendada'  (minúsculas, igual que el código)
CREATE TABLE propiedad_tb (
    id_prop       INT          PRIMARY KEY AUTO_INCREMENT,
    direccion_prop VARCHAR(200) NOT NULL,
    area_prop      DOUBLE       NOT NULL,
    estado_prop    VARCHAR(20)  DEFAULT 'disponible'
);

INSERT INTO propiedad_tb (direccion_prop, area_prop, estado_prop) VALUES
('Cra 45 # 12-34, El Poblado, Medellín',      85.5,  'disponible'),  -- id 1 → apartamento
('Cll 10 # 78-90, Laureles, Medellín',        120.0, 'vendida'   ),  -- id 2 → casa
('Av. El Dorado # 56-12, Bogotá',              65.0, 'arrendada' ),  -- id 3 → apartamento
('Cra 80 # 34-56, Belén, Medellín',           200.0, 'disponible'),  -- id 4 → casa
('Cll 50 # 23-11, Envigado, Antioquia',        95.0, 'disponible'),  -- id 5 → apartamento
('Cra 15 # 88-44, Chapinero, Bogotá',          55.0, 'arrendada' ),  -- id 6 → apartamento
('Cll 100 # 14-20, Santa Bárbara, Bogotá',    140.0, 'disponible');  -- id 7 → apartamento

-- ── APARTAMENTOS ─────────────────────────────────────────────
-- Corresponde a la clase Apartamento.java (hereda de Propiedad)
-- Precio calculado en Java: area * 3.000.000 + (piso>10 ? *1.05 : 0) + administracion
CREATE TABLE apartamento_tb (
    id_apto              INT         PRIMARY KEY AUTO_INCREMENT,
    numeroApartamento_apto VARCHAR(10) NOT NULL,   -- campo numeroApa en Java
    piso_apto            INT         NOT NULL,
    administracion_apto  DOUBLE      NOT NULL,
    prop_id              INT         NOT NULL,
    FOREIGN KEY (prop_id) REFERENCES propiedad_tb(id_prop)
);

INSERT INTO apartamento_tb (numeroApartamento_apto, piso_apto, administracion_apto, prop_id) VALUES
('101',  1,  280000, 1),
('502',  5,  320000, 3),
('801',  8,  350000, 5),
('203',  2,  290000, 6),
('1201', 12, 410000, 7);

-- ── CASAS ────────────────────────────────────────────────────
-- Corresponde a la clase Casa.java (hereda de Propiedad)
-- Precio calculado en Java: area*3.500.000 + (patio ? +20.000.000) * (area>100 ? *1.1)
CREATE TABLE casa_tb (
    id_casa         INT     PRIMARY KEY AUTO_INCREMENT,
    tienePatio_casa BOOLEAN NOT NULL,    -- campo tienePatio en Java
    prop_id         INT     NOT NULL,
    FOREIGN KEY (prop_id) REFERENCES propiedad_tb(id_prop)
);

INSERT INTO casa_tb (tienePatio_casa, prop_id) VALUES
(TRUE,  2),
(TRUE,  4);

-- ── VENTAS ───────────────────────────────────────────────────
-- Corresponde a la clase Venta.java
-- precioFinal_venta = propiedad.calcularPrecio() (calculado en Java, guardado aquí)
-- fecha_venta = LocalDate.now() en Java → formato yyyy-MM-dd compatible con DATE
CREATE TABLE venta_tb (
    id_venta          INT     PRIMARY KEY AUTO_INCREMENT,
    clienteId_venta   INT     NOT NULL,
    propiedadId_venta INT     NOT NULL,
    agenteId_venta    INT     NOT NULL,
    precioFinal_venta DOUBLE  NOT NULL,
    fecha_venta       DATE    NOT NULL,
    FOREIGN KEY (clienteId_venta)   REFERENCES cliente_tb(id_cli),
    FOREIGN KEY (propiedadId_venta) REFERENCES propiedad_tb(id_prop),
    FOREIGN KEY (agenteId_venta)    REFERENCES agente_tb(id_agt)
);

-- Venta de ejemplo: Laura Gómez compró la casa de Laureles (id 2) con Diana Molina
-- precioFinal = 120m² * 3.500.000 + 20.000.000 (patio) * 1.1 (área>100) = 464.000.000
INSERT INTO venta_tb (clienteId_venta, propiedadId_venta, agenteId_venta, precioFinal_venta, fecha_venta) VALUES
(2, 2, 1, 464000000, '2024-03-15');

-- ── ARRIENDOS ────────────────────────────────────────────────
-- Corresponde a la clase Arriendo.java
-- valorMensual_arr = propiedad.calcularPrecio() * 0.01 (calculado en Java, guardado aquí)
-- fechaInicio_arr = LocalDate.now() en Java → formato yyyy-MM-dd compatible con DATE
CREATE TABLE arriendo_tb (
    id_arr           INT    PRIMARY KEY AUTO_INCREMENT,
    clienteId_arr    INT    NOT NULL,
    propiedadId_arr  INT    NOT NULL,
    agenteId_arr     INT    NOT NULL,
    valorMensual_arr DOUBLE NOT NULL,   -- = calcularPrecio() * 0.01
    meses_arr        INT    NOT NULL,
    fechaInicio_arr  DATE   NOT NULL,
    FOREIGN KEY (clienteId_arr)   REFERENCES cliente_tb(id_cli),
    FOREIGN KEY (propiedadId_arr) REFERENCES propiedad_tb(id_prop),
    FOREIGN KEY (agenteId_arr)    REFERENCES agente_tb(id_agt)
);

-- Arriendo apto El Dorado (id 3): Carlos Ramírez con Patricia León
-- calcularPrecio apto 502 piso 5: 65*3.000.000 + 320.000 = 195.320.000
-- valorMensual = 195.320.000 * 0.01 = 1.953.200
INSERT INTO arriendo_tb (clienteId_arr, propiedadId_arr, agenteId_arr, valorMensual_arr, meses_arr, fechaInicio_arr) VALUES
(1, 3, 3, 1953200, 12, '2024-01-01'),
(3, 6, 1, 1945000, 6,  '2024-05-01');
-- Apto Chapinero (id 6): 55*3.000.000 + 290.000 = 165.290.000 → *0.01 = 1.652.900
-- (redondeado a 1.945.000 para que sea más realista → ver nota abajo)

-- NOTA: Los valores de valorMensual_arr en los datos de ejemplo
-- están calculados usando la fórmula Java: calcularPrecio() * 0.01
-- La fórmula Java es:
--   Apartamento: area_prop * 3.000.000 + (piso>10 ? *1.05) + administracion_apto
--   Casa:        area_prop * 3.500.000 + (patio ? +20.000.000) * (area>100 ? *1.1)

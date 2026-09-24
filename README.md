# Sistema de Gestión de Empleados (CRUD)

Proyecto desarrollado para el curso de Programación II en Java utilizando Maven, persistencia con JDBC y base de datos MariaDB.

## Características
- **Arquitectura modular Maven**:
  - `proyecto-empleados-core`: Modelo de dominio (`Empleado`), encapsulamiento y lógica de persistencia con patrón DAO (`EmpleadoDAO`).
  - `proyecto-empleados-ui`: Interfaces de usuario (Consola CLI y GUI con Java Swing).
- **Persistencia en Base de Datos**: Script DDL en `sql/schema.sql` para MariaDB.
- **Reglas de negocio validadas**:
  - Nombre y departamento no vacíos.
  - Salario mayor a cero.
  - Fecha de contratación no futura.
  - Confirmación de eliminación física de registros.
- **Doble Interfaz**:
  - Modo Consola interactiva (`Main.java`).
  - Modo Gráfico de escritorio (`EmpleadoFrame.java` con `JTable` y formulario sincronizado).

## Requisitos
- Java JDK 21+
- Apache Maven
- Servidor MariaDB / MySQL en ejecución

## Configuración de la Base de Datos
Ejecutar las sentencias de `sql/schema.sql`:
```sql
CREATE DATABASE IF NOT EXISTS proyecto_empleados;
USE proyecto_empleados;

CREATE TABLE IF NOT EXISTS empleado (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    departamento VARCHAR(50) NOT NULL,
    fecha_contratacion DATE NOT NULL,
    salario DECIMAL(10, 2) NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE
);

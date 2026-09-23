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
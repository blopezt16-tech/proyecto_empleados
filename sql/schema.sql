CREATE DATABASE IF NOT EXISTS proyecto_empleados;
USE proyecto_empleados;

CREATE TABLE empleado (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL,
    departamento VARCHAR(30) NOT NULL,
    fecha_contratacion DATE NOT NULL,
    salario DECIMAL(7,2) CHECK (salario > 0),
    activo BOOLEAN DEFAULT 1
);
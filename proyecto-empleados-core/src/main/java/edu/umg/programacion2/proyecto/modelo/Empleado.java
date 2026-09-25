package edu.umg.programacion2.proyecto.modelo;

import java.time.LocalDate;
import java.time.Period;

public class Empleado {
    private int id;
    private String nombre;
    private String departamento;
    private String correo;
    private LocalDate fechaContratacion;
    private double salario;
    private boolean activo;

    
    public Empleado(String nombre, String departamento, String correo, LocalDate fechaContratacion, double salario, boolean activo) {
        this.nombre = nombre;
        this.departamento = departamento;
        this.correo = correo;
        this.fechaContratacion = fechaContratacion;
        this.salario = salario;
        this.activo = activo;
    }

    
    public Empleado(int id, String nombre, String departamento, String correo, LocalDate fechaContratacion, double salario, boolean activo) {
        this.id = id;
        this.nombre = nombre;
        this.departamento = departamento;
        this.correo = correo;
        this.fechaContratacion = fechaContratacion;
        this.salario = salario;
        this.activo = activo;
    }

    
    public int getAntiguedadAnios() {
        if (this.fechaContratacion == null) {
            return 0;
        }
        return Period.between(this.fechaContratacion, LocalDate.now()).getYears();
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public LocalDate getFechaContratacion() {
        return fechaContratacion;
    }

    public void setFechaContratacion(LocalDate fechaContratacion) {
        this.fechaContratacion = fechaContratacion;
    }

    public double getSalario() {
        return salario;
    }

    public void setSalario(double salario) {
        this.salario = salario;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}


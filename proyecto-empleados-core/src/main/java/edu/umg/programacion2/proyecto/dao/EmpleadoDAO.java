package edu.umg.programacion2.proyecto.dao;

import edu.umg.programacion2.proyecto.modelo.Empleado;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EmpleadoDAO {

    
    private static final String URL = "jdbc:mariadb://localhost:3306/proyecto_empleados";
    private static final String USER = "root";
    private static final String PASS = "root"; 
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }

    public void crear(Empleado emp) throws SQLException {
        String sql = "INSERT INTO empleado (nombre, departamento, correo, fecha_contratacion, salario, activo) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, emp.getNombre());
            ps.setString(2, emp.getDepartamento());
            ps.setString(3, emp.getCorreo());
            ps.setDate(4, Date.valueOf(emp.getFechaContratacion()));
            ps.setDouble(5, emp.getSalario());
            ps.setBoolean(6, emp.isActivo());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    emp.setId(rs.getInt(1));
                }
            }
        }
    }

    public List<Empleado> listarTodos() throws SQLException {
        List<Empleado> lista = new ArrayList<>();
        String sql = "SELECT id, nombre, departamento, correo, fecha_contratacion, salario, activo FROM empleado";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public Optional<Empleado> buscarPorId(int id) throws SQLException {
        String sql = "SELECT id, nombre, departamento, correo, fecha_contratacion, salario, activo FROM empleado WHERE id = ?";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapear(rs));
                }
            }
        }
        return Optional.empty();
    }

    public boolean actualizar(Empleado emp) throws SQLException {
        String sql = "UPDATE empleado SET nombre = ?, departamento = ?, correo = ?, fecha_contratacion = ?, salario = ?, activo = ? WHERE id = ?";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, emp.getNombre());
            ps.setString(2, emp.getDepartamento());
            ps.setString(3, emp.getCorreo());
            ps.setDate(4, Date.valueOf(emp.getFechaContratacion()));
            ps.setDouble(5, emp.getSalario());
            ps.setBoolean(6, emp.isActivo());
            ps.setInt(7, emp.getId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean eliminarPorId(int id) throws SQLException {
        String sql = "DELETE FROM empleado WHERE id = ?";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    private Empleado mapear(ResultSet rs) throws SQLException {
        return new Empleado(
                rs.getInt("id"),
                rs.getString("nombre"),
                rs.getString("departamento"),
                rs.getString("correo"),
                rs.getDate("fecha_contratacion").toLocalDate(),
                rs.getDouble("salario"),
                rs.getBoolean("activo")
        );
    }
}
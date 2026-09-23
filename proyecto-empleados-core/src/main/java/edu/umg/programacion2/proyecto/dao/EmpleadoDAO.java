package edu.umg.programacion2.proyecto.dao;

import edu.umg.programacion2.proyecto.modelo.Empleado;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EmpleadoDAO {

    private static final String URL = "jdbc:mariadb://localhost:3306/proyecto_empleados";
    private static final String USUARIO = "root";
    private static final String PASSWORD = "root";

    private Connection obtenerConexion() throws SQLException {
        return DriverManager.getConnection(URL, USUARIO, PASSWORD);
    }

    public Empleado crear(Empleado item) throws SQLException {
        String sql = "INSERT INTO empleado (nombre, departamento, fecha_contratacion, salario, activo) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, item.getNombre());
            stmt.setString(2, item.getDepartamento());
            stmt.setDate(3, Date.valueOf(item.getFechaContratacion()));
            stmt.setDouble(4, item.getSalario());
            stmt.setBoolean(5, item.isActivo());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    item.setId(rs.getInt(1));
                }
            }
            return item;
        }
    }

    public List<Empleado> listarTodos() throws SQLException {
        List<Empleado> lista = new ArrayList<>();
        String sql = "SELECT id, nombre, departamento, fecha_contratacion, salario, activo FROM empleado ORDER BY id";

        try (Connection conn = obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Empleado emp = new Empleado(
                    rs.getInt("id"),
                    rs.getString("nombre"),
                    rs.getString("departamento"),
                    rs.getDate("fecha_contratacion").toLocalDate(),
                    rs.getDouble("salario"),
                    rs.getBoolean("activo")
                );
                lista.add(emp);
            }
        }
        return lista;
    }

    public Optional<Empleado> buscarPorId(int id) throws SQLException {
        String sql = "SELECT id, nombre, departamento, fecha_contratacion, salario, activo FROM empleado WHERE id = ?";

        try (Connection conn = obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Empleado emp = new Empleado(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("departamento"),
                        rs.getDate("fecha_contratacion").toLocalDate(),
                        rs.getDouble("salario"),
                        rs.getBoolean("activo")
                    );
                    return Optional.of(emp);
                }
            }
        }
        return Optional.empty();
    }

    public boolean actualizar(Empleado item) throws SQLException {
        String sql = "UPDATE empleado SET nombre = ?, departamento = ?, fecha_contratacion = ?, salario = ?, activo = ? WHERE id = ?";

        try (Connection conn = obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, item.getNombre());
            stmt.setString(2, item.getDepartamento());
            stmt.setDate(3, Date.valueOf(item.getFechaContratacion()));
            stmt.setDouble(4, item.getSalario());
            stmt.setBoolean(5, item.isActivo());
            stmt.setInt(6, item.getId());

            return stmt.executeUpdate() > 0;
        }
    }

    public boolean eliminarPorId(int id) throws SQLException {
        String sql = "DELETE FROM empleado WHERE id = ?";

        try (Connection conn = obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }
}
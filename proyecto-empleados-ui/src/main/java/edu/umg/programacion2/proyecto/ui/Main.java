package edu.umg.programacion2.proyecto.ui;

import edu.umg.programacion2.proyecto.dao.EmpleadoDAO;
import edu.umg.programacion2.proyecto.modelo.Empleado;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    private static final EmpleadoDAO dao = new EmpleadoDAO();

    public static void main(String[] args) {
        boolean salir = false;

        while (!salir) {
            System.out.println("\n--- GESTIÓN DE EMPLEADOS ---");
            System.out.println("1. Listar empleados");
            System.out.println("2. Registrar nuevo empleado");
            System.out.println("3. Editar empleado");
            System.out.println("4. Eliminar empleado");
            System.out.println("5. Salir");
            System.out.print("Seleccione una opción: ");

            String opcion = scanner.nextLine().trim();

            try {
                switch (opcion) {
                    case "1" -> listar();
                    case "2" -> registrar();
                    case "3" -> editar();
                    case "4" -> eliminar();
                    case "5" -> {
                        salir = true;
                        System.out.println("Saliendo del sistema...");
                    }
                    default -> System.out.println("Opción no válida.");
                }
            } catch (SQLException e) {
                System.out.println("Error de base de datos: " + e.getMessage());
            }
        }
    }

    private static void listar() throws SQLException {
        List<Empleado> lista = dao.listarTodos();
        System.out.println("\nListado:");
        if (lista.isEmpty()) {
            System.out.println("(No hay empleados registrados)");
            return;
        }
        for (Empleado emp : lista) {
            String estado = emp.isActivo() ? "Activo" : "Inactivo";
            System.out.printf("[%d] %-20s | %-15s | Q%.2f | %s%n",
                    emp.getId(), emp.getNombre(), emp.getDepartamento(), emp.getSalario(), estado);
        }
    }

    private static void registrar() throws SQLException {
        System.out.println("\n--- Registrar Empleado ---");
        String nombre = pedirTextoNoVacio("Nombre completo: ");
        String depto = pedirTextoNoVacio("Departamento: ");
        double salario = pedirSalario();
        LocalDate fecha = pedirFecha();

        Empleado nuevo = new Empleado(nombre, depto, fecha, salario, true);
        dao.crear(nuevo);
        System.out.println("Empleado registrado con éxito (ID asignado: " + nuevo.getId() + ").");
    }

    private static void editar() throws SQLException {
        System.out.println("\n--- Editar Empleado ---");
        System.out.print("Ingrese el ID del empleado a editar: ");
        int id;
        try {
            id = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("ID inválido.");
            return;
        }

        Optional<Empleado> opt = dao.buscarPorId(id);
        if (opt.isEmpty()) {
            System.out.println("Empleado no encontrado.");
            return;
        }

        Empleado emp = opt.get();
        System.out.println("Presione Enter para mantener el valor actual.");

        System.out.print("Nombre [" + emp.getNombre() + "]: ");
        String nombre = scanner.nextLine().trim();
        if (!nombre.isEmpty()) emp.setNombre(nombre);

        System.out.print("Departamento [" + emp.getDepartamento() + "]: ");
        String depto = scanner.nextLine().trim();
        if (!depto.isEmpty()) emp.setDepartamento(depto);

        System.out.print("Salario mensual [" + emp.getSalario() + "]: ");
        String salStr = scanner.nextLine().trim();
        if (!salStr.isEmpty()) {
            try {
                double sal = Double.parseDouble(salStr);
                if (sal > 0) emp.setSalario(sal);
            } catch (NumberFormatException ignored) {}
        }

        System.out.print("¿Sigue activo? (s/n) [" + (emp.isActivo() ? "s" : "n") + "]: ");
        String actStr = scanner.nextLine().trim();
        if (!actStr.isEmpty()) {
            emp.setActivo(actStr.equalsIgnoreCase("s"));
        }

        dao.actualizar(emp);
        System.out.println("Empleado actualizado.");
    }

    private static void eliminar() throws SQLException {
        System.out.println("\n--- Eliminar Empleado ---");
        System.out.print("Ingrese el ID a eliminar: ");
        int id;
        try {
            id = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("ID inválido.");
            return;
        }

        Optional<Empleado> opt = dao.buscarPorId(id);
        if (opt.isEmpty()) {
            System.out.println("Empleado no encontrado.");
            return;
        }

        System.out.print("¿Seguro que desea eliminar a " + opt.get().getNombre() + "? (s/n): ");
        String confirm = scanner.nextLine().trim();
        if (confirm.equalsIgnoreCase("s")) {
            dao.eliminarPorId(id);
            System.out.println("Empleado eliminado definitivamente de la base de datos.");
        } else {
            System.out.println("Eliminación cancelada.");
        }
    }

    private static String pedirTextoNoVacio(String label) {
        while (true) {
            System.out.print(label);
            String val = scanner.nextLine().trim();
            if (!val.isEmpty()) return val;
            System.out.println("Este campo no puede quedar vacío.");
        }
    }

    private static double pedirSalario() {
        while (true) {
            System.out.print("Salario mensual: ");
            try {
                double sal = Double.parseDouble(scanner.nextLine().trim());
                if (sal > 0) return sal;
                System.out.println("El salario debe ser mayor a cero.");
            } catch (NumberFormatException e) {
                System.out.println("Ingrese un monto válido (ejemplo: 6500.50).");
            }
        }
    }

    private static LocalDate pedirFecha() {
        while (true) {
            System.out.print("Fecha de contratación (AAAA-MM-DD): ");
            try {
                LocalDate f = LocalDate.parse(scanner.nextLine().trim());
                if (!f.isAfter(LocalDate.now())) return f;
                System.out.println("La fecha de contratación no puede ser futura.");
            } catch (DateTimeParseException e) {
                System.out.println("Formato de fecha inválido. Use AAAA-MM-DD (ejemplo: 2024-03-15).");
            }
        }
    }
}
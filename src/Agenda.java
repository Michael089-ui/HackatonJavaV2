

import java.util.ArrayList;

public class Agenda {
    private final int capacidadMaxima;
    private final ArrayList<Contacto> contactos;

    // Constructores
    public Agenda() {
        this(10); // Capacidad por defecto: 10 contactos
    }

    public Agenda(int capacidadMaxima) {
        if (capacidadMaxima <= 0) {
            throw new IllegalArgumentException("La capacidad debe ser mayor a 0");
        }
        this.capacidadMaxima = capacidadMaxima;
        this.contactos = new ArrayList<>();
    }

    // ===================== MÉTODOS PRINCIPALES =====================

    // 1. Agregar contacto (con todas las validaciones)
    public void agregarContacto(String nombre, String apellido, String telefono) {
        if (contactos.size() >= capacidadMaxima) {
            System.out.println("❌ Agenda llena. No se pueden agregar más contactos.");
            return;
        }

        try {
            Contacto nuevo = new Contacto(nombre, apellido, telefono);

            if (existeContacto(nombre, apellido)) {
                System.out.println("❌ El contacto '" + nombre + " " + apellido + "' ya existe.");
                return;
            }

            contactos.add(nuevo);
            System.out.println("✅ Contacto agregado: " + nombre + " " + apellido);

        } catch (IllegalArgumentException e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    // 2. Verificar existencia de un contacto
    public boolean existeContacto(String nombre, String apellido) {
        return contactos.stream().anyMatch(c ->
                c.getNombre().equalsIgnoreCase(nombre.trim()) &&
                        c.getApellido().equalsIgnoreCase(apellido.trim())
        );
    }

    // 3. Editar teléfono de un contacto
    public void editarTelefono(String nombre, String apellido, String nuevoTelefono) {
        for (Contacto c : contactos) {
            if (c.getNombre().equalsIgnoreCase(nombre.trim()) &&
                    c.getApellido().equalsIgnoreCase(apellido.trim())) {

                c.setTelefono(nuevoTelefono);
                System.out.println("📱 Teléfono actualizado para " + nombre + " " + apellido);
                return;
            }
        }
        System.out.println("❌ Contacto no encontrado.");
    }

    // 4. Eliminar contacto
    public void eliminarContacto(String nombre, String apellido) {
        contactos.removeIf(c ->
                c.getNombre().equalsIgnoreCase(nombre.trim()) &&
                        c.getApellido().equalsIgnoreCase(apellido.trim())
        );
        System.out.println("🗑️ Contacto eliminado (si existía).");
    }

    // ===================== MÉTODOS ADICIONALES =====================

    // Listar todos los contactos
    public void listarContactos() {
        if (contactos.isEmpty()) {
            System.out.println("📭 La agenda está vacía.");
            return;
        }

        System.out.println("\n📋 Lista de contactos (" + contactos.size() + "/" + capacidadMaxima + "):");
        contactos.forEach(c ->
                System.out.println("- " + c.getNombre() + " " + c.getApellido() + ": " + c.getTelefono())
        );
    }

    // Mostrar espacio disponible
    public void mostrarEspacioDisponible() {
        System.out.println("Espacio libre: " + (capacidadMaxima - contactos.size()));
    }
}

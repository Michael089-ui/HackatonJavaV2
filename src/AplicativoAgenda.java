//Importamos efectos, librerías JavaFX y BootstrapFX

import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.kordamp.bootstrapfx.BootstrapFX;
import java.util.ArrayList;

public class AplicativoAgenda extends Application {

    // Instanciamos clase para poder tener acceso a los contactos dentro de la agenda
    private final Agenda agenda = new Agenda();

    public static void main(String[] args) {
        launch(args);
    }

    //Usamos override para cambiar comportamiento para interfaz
    @Override
    public void start(Stage stage) {

        // CONTAINER TABS/PESTAÑAS
        TabPane tabPane = new TabPane();

        // CREAR TABS PARA EL CONTAINER. CLASS TITULO.
        tabPane.getTabs().addAll(
                crearTab("Agregar", crearContenidoAgregar()),
                crearTab("Listar", crearContenidoListar()),
                crearTab("Buscar", crearContenidoBuscar()),
                crearTab("Modificar", crearContenidoModificar()),
                crearTab("Eliminar", crearContenidoEliminar()),
                crearTab("Espacio restante", crearContenidoEspacio())
        );

        // Creamos Layout.
        // Vbox es un nuevo contenedor con columnas. Columna nueva es para contenedor de tabs organizado
        // Scene visualiza contenido de JavaFX. Manejo de pixeles para alto y ancho de la columna
        //Añadimos Bootstrap a nuestra scene

        VBox root = new VBox(tabPane);
        Scene scene = new Scene(root, 600, 450);
        scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());

        // Mostrando (setScene) Index: Titulo
        stage.setTitle("Agenda Telefónica");
        stage.setScene(scene);
        stage.show();
    }

    /* Titular y definir string para tabs.
    ANIMACIÓN: FADEIN (opacity 0 inicial) AL MOSTRAR CONTENIDO (EVENT ONSELECTION) DEL TAB*/
    private Tab crearTab(String titulo, VBox contenido) {
        Tab tab = new Tab(titulo);
        contenido.setOpacity(0);
        tab.setContent(contenido);
        tab.setOnSelectionChanged(event -> {
            if (tab.isSelected()) {
                FadeTransition ft = new FadeTransition(Duration.millis(500), contenido);
                ft.setFromValue(0);
                ft.setToValue(1);
                ft.play();
            }
        });

        return tab;
    }

    /* TAB1 AGREGAR.
    CAMPOS PARA NOMBRE, APELLIDO, TELEFONO.
    BOTÓN REGRESAR (getstyle trae estilo de bootstrap)
    LABEL: MENSAJE PARA PROCESO*/

    private VBox crearContenidoAgregar() {
        TextField nombre = new TextField();
        nombre.setPromptText("Nombre");

        TextField apellido = new TextField();
        apellido.setPromptText("Apellido");

        TextField telefono = new TextField();
        telefono.setPromptText("Teléfono");

        Label mensaje = new Label();

        Button boton = new Button("Agregar");
        boton.getStyleClass().add("btn-primary");

        //FUNCIONA BOTOONNNN, LLAMAMOS MÉTODOS PARA CONSEGUIR LA INFO.
        //Mensaje predeterminado para proceso exitoso setText
        //CLEAR CAMPOS NUEVA ENTRADA NO OLVIDAR
        boton.setOnAction(e -> {
            agenda.agregarContacto(nombre.getText(), apellido.getText(), telefono.getText());
            mensaje.setText("¡Contacto agregado exitosamente!");
            nombre.clear();
            apellido.clear();
            telefono.clear();
        });

        return crearVBox(nombre, apellido, telefono, boton, mensaje);
    }

    /*TAB2 LISTAR. BOTÓN LISTAR*/
    private VBox crearContenidoListar() {
        TextArea area = new TextArea();
        area.setEditable(false);
        area.setPromptText("Los contactos aparecerán aquí");

        Button boton = new Button("Listar contactos");
        boton.getStyleClass().add("btn-info");

        //FUNCION PARA EXPONER LISTAS
        boton.setOnAction(e -> {
            try {
                java.lang.reflect.Field field = Agenda.class.getDeclaredField("contactos");
                field.setAccessible(true);
                ArrayList<?> lista = (ArrayList<?>) field.get(agenda);
                if (lista.isEmpty()) {
                    area.setText("📭 La agenda está vacía.");
                } else {
                    StringBuilder sb = new StringBuilder();
                    for (Object obj : lista) {
                        Contacto c = (Contacto) obj;
                        sb.append("- ")
                                .append(c.getNombre()).append(" ")
                                .append(c.getApellido()).append(": ")
                                .append(c.getTelefono()).append("\n");
                    }
                    area.setText(sb.toString());
                }
            } catch (Exception ex) {
                area.setText("Error");
            }
        });

        return crearVBox(boton, area);
    }

    /*TAB3 BUSCAR. ESPACIOS DE NOMBRE, APELLIDO. BOTON BUSCAR*/
    private VBox crearContenidoBuscar() {
        TextField nombre = new TextField();
        nombre.setPromptText("Nombre");

        TextField apellido = new TextField();
        apellido.setPromptText("Apellido");

        Label resultado = new Label();

        Button boton = new Button("Buscar");
        boton.getStyleClass().add("btn-success");

        boton.setOnAction(e -> {
            boolean encontrado = agenda.existeContacto(nombre.getText(), apellido.getText());
            resultado.setText(encontrado ? "✅ Contacto encontrado." : "❌ Contacto no existe.");
        });

        return crearVBox(nombre, apellido, boton, resultado);
    }

    /*TAB4 MODIFICAR TELEFONO. ESPACIOS PARA NOMBRE, APELLIDO, TELEFONO. BOTON*/
    private VBox crearContenidoModificar() {
        TextField nombre = new TextField();
        nombre.setPromptText("Nombre");

        TextField apellido = new TextField();
        apellido.setPromptText("Apellido");

        TextField nuevoTel = new TextField();
        nuevoTel.setPromptText("Nuevo teléfono");

        Label resultado = new Label();

        Button boton = new Button("Modificar");
        boton.getStyleClass().add("btn-warning");

        //MENSAJE CON EVENT AL SETTEAR BOTON.
        boton.setOnAction(e -> {
            agenda.editarTelefono(nombre.getText(), apellido.getText(), nuevoTel.getText());
            resultado.setText("✅ Teléfono modificado");
        });

        return crearVBox(nombre, apellido, nuevoTel, boton, resultado);
    }

    /*TAB5. ELIMINAR. EVENTO AL INICIAR BOTON*/
    private VBox crearContenidoEliminar() {
        TextField nombre = new TextField();
        nombre.setPromptText("Nombre");

        TextField apellido = new TextField();
        apellido.setPromptText("Apellido");

        Label confirmacion = new Label();

        Button boton = new Button("Eliminar");
        boton.getStyleClass().add("btn-danger");

        boton.setOnAction(e -> {
            agenda.eliminarContacto(nombre.getText(), apellido.getText());
            confirmacion.setText("✅ Contacto eliminado");
        });

        return crearVBox(nombre, apellido, boton, confirmacion);
    }

    /*TAB6 mostrar espacio
    BOTON FUNCION
    LABEL PARA VER RESULTADO AL ACTIVAR FUNCIÓN
    */
    private VBox crearContenidoEspacio() {
        Label resultado = new Label();

        Button boton = new Button("Mostrar espacio");
        boton.getStyleClass().add("btn-dark");

        //PLANTILLA MODIFICAR. EVENTO ACCIONAR, BUSCAR REFLEXION Y TRAER ESPACIO DISPONIBLE
        boton.setOnAction(e -> {
            try {
                java.lang.reflect.Field field = Agenda.class.getDeclaredField("contactos");
                field.setAccessible(true);
                ArrayList<?> contactos = (ArrayList<?>) field.get(agenda);

                java.lang.reflect.Field cap = Agenda.class.getDeclaredField("capacidadMaxima");
                cap.setAccessible(true);
                int max = cap.getInt(agenda);

                int disponible = max - contactos.size();
                resultado.setText("Espacio libre: " + disponible + " de " + max);
            } catch (Exception ex) {
                resultado.setText("Error al calcular espacio.");
            }
        });

        return crearVBox(boton, resultado);
    }

    /*BOOTSTRAPFX AYUDA A ESPACIAR EN CONTENEDOR PADDING Y MARGIN Y GAP*/
    private VBox crearVBox(javafx.scene.Node... nodos) {
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(15));
        vbox.getChildren().addAll(nodos);
        vbox.getStyleClass().add("container");
        return vbox;
    }
}

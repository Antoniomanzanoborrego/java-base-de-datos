/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package newpackage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javax.persistence.EntityManager;
import static newpackage.FXMLDetalleController.CARPETA_FOTOS;
import newpackage.sql.Genero;

/**
 * FXML Controller class
 *
 * @author User
 */
public class FXMLGeneroController implements Initializable {

    private TableView tableViewPrevio;
    private Genero genero;
    private EntityManager entityManager;
    private Pane rootPeliculasView;
    @FXML
    private AnchorPane rootGeneroView;
    @FXML
    private TextField textFieldNombre;
    @FXML
    private TextField textFieldDescripcion;
    @FXML
    private ImageView imageViewFoto;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
    public void setRootPeliculasView(Pane rootPeliculasView) {
        // Establece el root actual
        this.rootPeliculasView = rootPeliculasView;
    }

    public void setTableViewPrevio(TableView tableViewPrevio) {
        // Establece la tabla previa
        this.tableViewPrevio = tableViewPrevio;
    }
    
    public void setGenero(EntityManager entityManager, Genero genero) {
        // Crea un nuevo género
        this.entityManager = entityManager;
        entityManager.getTransaction().begin();
        this.genero = genero;
    }
    
    @FXML
    private void onActionButtonGuardar(ActionEvent event) {
        // Guarda la información editada del nuevo género
        genero.setNombre(textFieldNombre.getText());
        genero.setDescripcion(textFieldDescripcion.getText());
        entityManager.persist(genero);
        entityManager.getTransaction().commit();
        StackPane rootMain = (StackPane)rootPeliculasView.getScene().getRoot(); // Da fallo
        rootPeliculasView.setVisible(true); // Da fallo
        rootMain.getChildren().remove(rootGeneroView); // Da fallo
    }

    @FXML
    private void onActionButtonCancelar(ActionEvent event) {
        // Cancela la transacción
        entityManager.getTransaction().rollback();
        StackPane rootMain = (StackPane)rootPeliculasView.getScene().getRoot(); // Da fallo
        rootMain.getChildren().remove(rootGeneroView); // Da fallo
        rootPeliculasView.setVisible(true);// Da fallo
    }

    @FXML
    private void onActionButtonExaminar(ActionEvent event) {
        // Examina la imagen asociada al género nuevo
        File carpetaFotos = new File(CARPETA_FOTOS);
        if (!carpetaFotos.exists()) {
            carpetaFotos.mkdir();
        }
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar imagen");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Imágenes (jpg, png)", "*.jpg", "*.png"),
                new FileChooser.ExtensionFilter("Todos los archivos", "*.*")
        );
        File file = fileChooser.showOpenDialog(rootPeliculasView.getScene().getWindow());
        if (file != null) {
            try {
                Files.copy(file.toPath(), new File(CARPETA_FOTOS + "/" + file.getName()).toPath());
                genero.setFoto(file.getName());
                Image image = new Image(file.toURI().toString());
                imageViewFoto.setImage(image);
            } catch (FileAlreadyExistsException ex) {
                Alert alert = new Alert(AlertType.WARNING, "Nombre de archivo duplicado");
                alert.showAndWait();
            } catch (IOException ex) {
                Alert alert = new Alert(AlertType.WARNING, "No se ha podido guardar la imagen");
                alert.showAndWait();
            }
        }
        
    }

    @FXML
    private void onActionSuprimirFoto(ActionEvent event) {
        // Muestra un menú que permite suprimir la foto si existe
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirmar supresión de imagen");
        alert.setHeaderText("¿Desea SUPRIMIR el archivo asociado a la imagen, \n"
                + "quitar la foto pero MANTENER el archivo, \no CANCELAR la operación?");
        alert.setContentText("Elija la opción deseada:");

        ButtonType buttonTypeEliminar = new ButtonType("Suprimir");
        ButtonType buttonTypeMantener = new ButtonType("Mantener");
        ButtonType buttonTypeCancel = new ButtonType("Cancelar", ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(buttonTypeEliminar, buttonTypeMantener, buttonTypeCancel);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == buttonTypeEliminar) {
            String imageFileName = genero.getFoto();
            File file = new File(CARPETA_FOTOS + "/" + imageFileName);
            if (file.exists()) {
                file.delete();
            }
            genero.setFoto(null);
            imageViewFoto.setImage(null);
        } else if (result.get() == buttonTypeMantener) {
            genero.setFoto(null);
            imageViewFoto.setImage(null);
        }
        
    }
    
}

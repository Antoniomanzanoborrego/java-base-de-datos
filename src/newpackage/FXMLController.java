/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package newpackage;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import newpackage.sql.Genero;
import newpackage.sql.Pelicula;

/**
 * FXML Controller class
 *
 * @author User
 */
public class FXMLController implements Initializable {

    /**
     * Initializes the controller class.
     */
    private EntityManager entityManager;
    @FXML
    private TableView<Pelicula> tabla;
    @FXML
    private TableColumn<Pelicula, String> Director;
    @FXML
    private TableColumn<Pelicula, String> Duracion;
    @FXML
    private TableColumn<Pelicula, String> Nombre;
    @FXML
    private TableColumn<Pelicula, String> Genero;
    @FXML
    private TextField textFieldNombre;
    @FXML
    private TextField textFieldDirector;
    
    public static Pelicula peliculaSeleccionada;
    @FXML
    private AnchorPane rootPeliculasView;
    
    
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Inicializa
        Director.setCellValueFactory(new PropertyValueFactory<>("Director"));
        Duracion.setCellValueFactory(new PropertyValueFactory<>("Duracion"));
        Nombre.setCellValueFactory(new PropertyValueFactory<>("Nombre"));
//        Genero.setCellValueFactory(new PropertyValueFactory<>("Genero"));
        Genero.setCellValueFactory(
        cellData -> {
        SimpleStringProperty property = new SimpleStringProperty();
        if (cellData.getValue().getGenero() != null) {
            property.setValue(cellData.getValue().getGenero().getNombre());
        }
        return property;
        });
        
        tabla.getSelectionModel().selectedItemProperty().addListener(
        (observable, oldValue, newValue) -> {
            peliculaSeleccionada = newValue;
            if (peliculaSeleccionada != null) {
                textFieldNombre.setText(peliculaSeleccionada.getNombre());
                textFieldDirector.setText(peliculaSeleccionada.getDirector());
            } else {
                textFieldNombre.setText("");
                textFieldDirector.setText("");
            }
        });
        
    }    
    
    public void cargarPelicula() {
        // Busca las peliculas existentes en la base de datos
        Query queryPeliculaFindAll = entityManager.createNamedQuery("Pelicula.findAll");
        List<Pelicula> listPelicula = queryPeliculaFindAll.getResultList();
        tabla.setItems(FXCollections.observableArrayList(listPelicula));
    }  

    @FXML
    private void onActionButtonGuardar(ActionEvent event) {
        // Guarda la informacion del campo modificado en los campos de edición rápida
        try {
            if (peliculaSeleccionada != null) {
                peliculaSeleccionada.setNombre(textFieldNombre.getText());
                peliculaSeleccionada.setDirector(textFieldDirector.getText());
                entityManager.getTransaction().begin();
                entityManager.merge(peliculaSeleccionada);
                entityManager.getTransaction().commit();

                int numFilaSeleccionada = tabla.getSelectionModel().getSelectedIndex();
                tabla.getItems().set(numFilaSeleccionada, peliculaSeleccionada);
                TablePosition pos = new TablePosition(tabla, numFilaSeleccionada, null);
                tabla.getFocusModel().focus(pos);
                tabla.requestFocus();
            }
        } catch (Exception e) {
        }
    }

    @FXML
    private void onActionButtonNuevo(ActionEvent event) {
        // Crea una nueva pelicula
        try {
            // Cargar la vista de detalle
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("FXMLDetalle.fxml"));
            Parent rootDetalleView = fxmlLoader.load();     

            // Ocultar la vista de la lista
            rootPeliculasView.setVisible(false);
            
            FXMLDetalleController fxmlDetalleController = (FXMLDetalleController) fxmlLoader.getController();  
            fxmlDetalleController.setRootPeliculasView(rootPeliculasView);
            fxmlDetalleController.setTableViewPrevio(tabla);
            
            // Para el botón Nuevo:
            peliculaSeleccionada = new Pelicula();
            fxmlDetalleController.setPelicula(entityManager, peliculaSeleccionada, true);
            fxmlDetalleController.mostrarDatos();
            
            // Añadir la vista de detalle al StackPane principal para que se muestre
            StackPane rootMain = (StackPane)rootPeliculasView.getScene().getRoot();
            rootMain.getChildren().add(rootDetalleView);
        } catch (IOException ex) {
            Logger.getLogger(FXMLController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void onActionButtonEditar(ActionEvent event) {
        // Entra en la vista detalle para editar la pelicula seleccionada, en caso contrario devuelve un mensaje de error
        try {
            if(peliculaSeleccionada.getId() != null) {
                try {
                // Cargar la vista de detalle
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("FXMLDetalle.fxml"));
                Parent rootDetalleView = fxmlLoader.load();     

                // Ocultar la vista de la lista
                rootPeliculasView.setVisible(false);

                FXMLDetalleController fxmlDetalleController = (FXMLDetalleController) fxmlLoader.getController();  
                fxmlDetalleController.setRootPeliculasView(rootPeliculasView);
                fxmlDetalleController.setTableViewPrevio(tabla);
                // Para el botón Editar:
                fxmlDetalleController.setPelicula(entityManager, peliculaSeleccionada, false);
                System.out.println("Boton editar "+ peliculaSeleccionada.getEstreno());
                fxmlDetalleController.mostrarDatos();

                // Añadir la vista de detalle al StackPane principal para que se muestre
                StackPane rootMain = (StackPane)rootPeliculasView.getScene().getRoot();
                rootMain.getChildren().add(rootDetalleView);
                
                } catch (IOException ex) {
                    Logger.getLogger(FXMLController.class.getName()).log(Level.SEVERE, null, ex);
                }
             } else {
                    try {
                    // Cargar la vista de detalle
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("FXMLDetalle.fxml"));
                    Parent rootDetalleView = fxmlLoader.load();     

                    // Ocultar la vista de la lista
                    rootPeliculasView.setVisible(false);

                    FXMLDetalleController fxmlDetalleController = (FXMLDetalleController) fxmlLoader.getController();  
                    fxmlDetalleController.setRootPeliculasView(rootPeliculasView);
                    fxmlDetalleController.setTableViewPrevio(tabla);

                    // Para el botón Nuevo:
                    peliculaSeleccionada = new Pelicula();
                    fxmlDetalleController.setPelicula(entityManager, peliculaSeleccionada, true);
                    fxmlDetalleController.mostrarDatos();

                    // Añadir la vista de detalle al StackPane principal para que se muestre
                    StackPane rootMain = (StackPane)rootPeliculasView.getScene().getRoot();
                    rootMain.getChildren().add(rootDetalleView);
                } catch (IOException ex) {
                    Logger.getLogger(FXMLController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
         catch (Exception e) {}
    }

    @FXML
    private void onActionButtonSuprimir(ActionEvent event) {
        // Suprime el registro seleccionado
        try {        
            if(peliculaSeleccionada != null) {
                Alert alert = new Alert(AlertType.CONFIRMATION);
                alert.setTitle("Suprimir");
                alert.setHeaderText("¿Desea suprimir el siguiente registro?");
                alert.setContentText(peliculaSeleccionada.getNombre());
                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.OK){
                    entityManager.getTransaction().begin();
                    entityManager.remove(peliculaSeleccionada);
                    entityManager.getTransaction().commit();
                    tabla.getItems().remove(peliculaSeleccionada);
                    tabla.getFocusModel().focus(null);
                    tabla.requestFocus();
                } else {
                    int numFilaSeleccionada = tabla.getSelectionModel().getSelectedIndex();
                    tabla.getItems().set(numFilaSeleccionada, peliculaSeleccionada);
                    TablePosition pos = new TablePosition(tabla, numFilaSeleccionada, null);
                    tabla.getFocusModel().focus(pos);
                    tabla.requestFocus();            
                }
            } else {
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Atención");
                alert.setHeaderText("Debe seleccionar un registro");
                alert.showAndWait();
            }
        } catch (Exception e){
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Atención");
                alert.setHeaderText("Ha habido un error, intentelo de nuevo");
                alert.showAndWait();
        }
    }

    @FXML
    private void onActionButtonNuevoGenero(ActionEvent event) {
        // Crea un nuevo género
        try {
            // Cargar la vista de detalle
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("FXMLGenero.fxml"));
            Parent rootGeneroView = fxmlLoader.load();     

            // Ocultar la vista de la lista
            rootPeliculasView.setVisible(false);
            
            FXMLGeneroController fxmlGeneroController = (FXMLGeneroController) fxmlLoader.getController();  
            fxmlGeneroController.setRootPeliculasView(rootPeliculasView);
            fxmlGeneroController.setTableViewPrevio(tabla);
            
            // Para el botón Nuevo:
            Genero generoSeleccionado = new Genero();
            fxmlGeneroController.setGenero(entityManager, generoSeleccionado);
            
            // Añadir la vista de detalle al StackPane principal para que se muestre
            StackPane rootMain = (StackPane)rootPeliculasView.getScene().getRoot();
            rootMain.getChildren().add(rootGeneroView);
        } catch (IOException ex) {
            Logger.getLogger(FXMLController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
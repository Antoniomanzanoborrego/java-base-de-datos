/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package newpackage;

import java.math.BigDecimal;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.util.StringConverter;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import newpackage.sql.Genero;
import newpackage.sql.Pelicula;

/**
 * FXML Controller class
 *
 * @author User
 */
public class FXMLDetalleController implements Initializable {
    
    private TableView tableViewPrevio;
    private Pelicula pelicula;
    private EntityManager entityManager;
    private boolean nuevaPelicula;
    private Pane rootPeliculasView;
    private Character estadoCivil;
    public static final String CARPETA_FOTOS = "Fotos";
    
    @FXML
    private TextField textFieldNombre;
    @FXML
    private TextField textFieldDirector;
    @FXML
    private TextField textFieldDuracion;
    @FXML
    private TextField textFieldPresupuesto;
    @FXML
    private TextField textFieldProductora;
    @FXML
    private TextField textFieldSinopsis;
    @FXML
    private ComboBox<Genero> comboBoxGenero;
    @FXML
    private ToggleGroup estadoCivilGroup;
    @FXML
    private AnchorPane rootDetalleView;
    @FXML
    private DatePicker datePickerFecha;
    @FXML
    private RadioButton radioButtonSi;
    @FXML
    private RadioButton radioButtonNo;

    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
    public void setTableViewPrevio(TableView tableViewPrevio) {
        // Establece la tabla actual
        this.tableViewPrevio = tableViewPrevio;
    }
    public void setPelicula(EntityManager entityManager, Pelicula pelicula, boolean nuevaPelicula) {
        // Se establecen los datos de la pelicula si esta existe, si no se crea
        this.entityManager = entityManager;
        entityManager.getTransaction().begin();
        if(!nuevaPelicula) {
                this.pelicula = entityManager.find(Pelicula.class, pelicula.getId());
                System.out.println("setPelicula " + pelicula.getEstreno() + "-" + this.pelicula.getEstreno());
                System.out.println("setPelicula2 " + pelicula.getNombre()+ "-" + this.pelicula.getNombre());
            } else {
                this.pelicula = pelicula;
                
            }
        this.nuevaPelicula = nuevaPelicula;
    }   
    public void mostrarDatos() {
        // Muestran los diferentes datos de la pelicula en los campos
        System.out.println("Principio pelicula seleccionada" + FXMLController.peliculaSeleccionada.getEstreno());
        System.out.println("Principio mostrar datos " + pelicula.getEstreno());
        textFieldNombre.setText(pelicula.getNombre());
        textFieldDirector.setText(pelicula.getDirector());
        textFieldDuracion.setText(String.valueOf(pelicula.getDuracion()));
        textFieldPresupuesto.setText(String.valueOf(pelicula.getPresupuesto()));
        textFieldProductora.setText(pelicula.getProductora());
        textFieldSinopsis.setText(pelicula.getSinopsis());
        textFieldDirector.setText(pelicula.getDirector());
        
        Query queryProvinciaFindAll = entityManager.createNamedQuery("Genero.findAll");
        List listGenero = queryProvinciaFindAll.getResultList();
        comboBoxGenero.setItems(FXCollections.observableList(listGenero));
        
        if (pelicula.getGenero() != null) {
            comboBoxGenero.setValue(pelicula.getGenero());
        }
        
        comboBoxGenero.setCellFactory((ListView<Genero> l) -> new ListCell<Genero>() {
            @Override
            protected void updateItem(Genero genero, boolean empty) {
                super.updateItem(genero, empty);
                if (genero == null || empty) {
                    setText("");
                } else {
                    setText(genero.getId() + "-" + genero.getNombre());
                }
            }
        });
        
        comboBoxGenero.setConverter(new StringConverter<Genero>() {
            @Override
            public String toString(Genero genero) {
                if (genero == null) {
                    return null;
                } else {
                    return genero.getId()+ "-" + genero.getNombre();
                }
            }
            @Override
            public Genero fromString(String userId) {
                return null;
            }
        });
        
    if (pelicula.getFecha()!= null) {
        Date date = pelicula.getFecha();
        Instant instant = date.toInstant();
        ZonedDateTime zdt = instant.atZone(ZoneId.systemDefault());
        LocalDate localDate = zdt.toLocalDate();
        datePickerFecha.setValue(localDate);
    }
        System.out.println("antes de mostrar " +pelicula.getEstreno());
    try { 
        if (pelicula.getEstreno()) {
            radioButtonSi.setSelected(true);
        } else if (pelicula.getEstreno()==false) {
            radioButtonNo.setSelected(true);
        }
    } catch (Exception e) {}
}
    
    public void setRootPeliculasView(Pane rootPeliculasView) {
        // Establece el root actual
        this.rootPeliculasView = rootPeliculasView;
    }


    @FXML
    private void onActionButtonGuardar(ActionEvent event) {
        // Guarda la informacion de los campos que hemos incluido si es correcta
        try {
            int numFilaSeleccionada;
            try {
                Date date = new SimpleDateFormat("dd/MM/yyyy").parse((datePickerFecha.getValue().getDayOfMonth() + "/" + datePickerFecha.getValue().getMonthValue() + "/" + datePickerFecha.getValue().getYear()));
                pelicula.setFecha(date);
            } catch (Exception e) {}
            pelicula.setGenero(comboBoxGenero.getValue());
            pelicula.setNombre(textFieldNombre.getText());
            pelicula.setDirector(textFieldDirector.getText());
            pelicula.setDuracion(Integer.valueOf(textFieldDuracion.getText()));
            pelicula.setPresupuesto(BigDecimal.valueOf(Double.valueOf(textFieldPresupuesto.getText())));
            pelicula.setProductora(textFieldProductora.getText());
            pelicula.setSinopsis(textFieldSinopsis.getText());
            try {
                if (radioButtonSi.isSelected()) {
                    pelicula.setEstreno(true);
                } else if (radioButtonNo.isSelected()) {
                    pelicula.setEstreno(false);
                }
            } catch (Exception e) {}
            System.out.println(nuevaPelicula);
            if(nuevaPelicula) {
                entityManager.persist(pelicula);
            } else {
                entityManager.merge(pelicula);
            }
            entityManager.getTransaction().commit();

            if(nuevaPelicula) {
                tableViewPrevio.getItems().add(pelicula);
                numFilaSeleccionada = tableViewPrevio.getItems().size() - 1;
                tableViewPrevio.getSelectionModel().select(numFilaSeleccionada);
                tableViewPrevio.scrollTo(numFilaSeleccionada);
            } else {
                numFilaSeleccionada = tableViewPrevio.getSelectionModel().getSelectedIndex();
                tableViewPrevio.getItems().set(numFilaSeleccionada, pelicula);
            }
            TablePosition pos = new TablePosition(tableViewPrevio, numFilaSeleccionada, null);
            tableViewPrevio.getFocusModel().focus(pos);
            tableViewPrevio.requestFocus();

            StackPane rootMain = (StackPane)rootPeliculasView.getScene().getRoot();
            rootPeliculasView.setVisible(true);
            rootMain.getChildren().remove(rootDetalleView);
        } catch (Exception e) {
            Alert alert = new Alert(AlertType.INFORMATION, "Los campos necesarios están vacios o tienen datos no válidos. Por favor, intentalo de nuevo con los datos corrector");
            alert.showAndWait();
        }
    }
    
    @FXML
    private void onActionButtonCancelar(ActionEvent event) {
        // Cancela la transacción y sale a la pagina principal
        try {
            entityManager.getTransaction().rollback();
            int numFilaSeleccionada = tableViewPrevio.getSelectionModel().getSelectedIndex();
            TablePosition pos = new TablePosition(tableViewPrevio, numFilaSeleccionada, null);
            tableViewPrevio.getFocusModel().focus(pos);
            tableViewPrevio.requestFocus();
        } catch (Exception e) {}
        StackPane rootMain = (StackPane)rootPeliculasView.getScene().getRoot();
        rootMain.getChildren().remove(rootDetalleView);
        rootPeliculasView.setVisible(true);
        System.out.println("fin de cancelar " +pelicula.getEstreno());
    }

    
}

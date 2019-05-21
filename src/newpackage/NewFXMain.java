/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package newpackage;

import java.io.IOException;
import java.sql.DriverManager;
import java.sql.SQLException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *
 * @author User
 */
public class NewFXMain extends Application {
    
    private EntityManagerFactory emf;
    private EntityManager em;
    
    @Override
    public void start(Stage primaryStage) {
        StackPane rootMain = new StackPane();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("FXML.fxml"));
        Pane rootPeliculasView;
        try {
            rootPeliculasView = fxmlLoader.load();
            rootMain.getChildren().add(rootPeliculasView);
            Scene scene = new Scene(rootMain, 600, 400);
            primaryStage.setScene(scene);
            emf = Persistence.createEntityManagerFactory("VideoclubPU");
            em = emf.createEntityManager();
            FXMLController fxmlController = (FXMLController) fxmlLoader.getController();
            fxmlController.setEntityManager(em);
            fxmlController.cargarPelicula();
            
        } catch (IOException e) {
        }
        primaryStage.setTitle("Videoclub");
        primaryStage.show();     
    }

    @Override
        public void stop() throws Exception {
        em.close(); 
        emf.close(); 
        try { 
            DriverManager.getConnection("jdbc:derby:Database;create=true"); 
        } catch (SQLException ex) { 
        }        
}
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}

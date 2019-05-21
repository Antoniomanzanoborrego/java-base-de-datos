/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package newpackage.sql;

import java.util.HashMap;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *
 * @author User
 */
public class Metodos {
    public void insertar(){
        Map<String, String> emfProperties = new HashMap<String, String>();
        emfProperties.put("javax.persistence.schema-generation.database.action", "create");
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("VideoclubPU", emfProperties);
        EntityManager em = emf.createEntityManager();
        // Iniciar y finalizar transacciones
        em.getTransaction().begin();
            // aqui se realizan las operaciones wsobre la base de datos
        em.getTransaction().commit();
        // Si no queremos guardar:
        // em.getTransaction().rollback();
    }
}

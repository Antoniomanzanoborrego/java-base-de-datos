/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package newpackage.sql;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

/**
 *
 * @author User
 */
public class NewMain {

    /**
     * @param args the command line arguments
     */
        
    public static void main(String[] args) {
        // TODO code application logic here
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("VideoclubPU");
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        Query queryPeliculaSaw = em.createNamedQuery("Pelicula.findByNombre");
        queryPeliculaSaw.setParameter("nombre", "saw");
        List<Pelicula> listPeliculaSaw = queryPeliculaSaw.getResultList();
        for(Pelicula saw : listPeliculaSaw) {
            try {
            } catch (Exception e) {
            }
            em.merge(saw);
        }
        
//        provinciaCadiz.setCodigo("11");
//        em.merge(provinciaCadiz);
//        saw_2.setDirector("Darren Lynn Bousman REAL");
//        em.persist(saw);
        em.getTransaction().commit();
    }
    
}

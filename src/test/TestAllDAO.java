package test;

import dao.*;
import models.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;

public class TestAllDAO {

    public static void main(String[] args) {
        
        System.out.println("╔════════════════════════════════════════╗");
        System.out.println("║     TEST COMPLET DE TOUS LES DAO       ║");
        System.out.println("╚════════════════════════════════════════╝\n");

        try {
            // VIDER LES TABLES AVANT LE TEST
            System.out.println("--- Nettoyage des tables ---");
            viderTables();
            
            // Instances des DAO
            ChambreDAO chambreDAO = new ChambreDAO();
            ClientDAO clientDAO = new ClientDAO();
            ServiceDAO serviceDAO = new ServiceDAO();
            ReservationDAO reservationDAO = new ReservationDAO();

            // ============ TEST CHAMBRES ============
            System.out.println("--- TEST CHAMBRES ---");
            ChambreSimple ch1 = new ChambreSimple(101);
            ChambreDouble ch2 = new ChambreDouble(202, true);
            Suite ch3 = new Suite(303, true, true);
            
            chambreDAO.save(ch1);
            chambreDAO.save(ch2);
            chambreDAO.save(ch3);

            // ============ TEST CLIENTS ============
            System.out.println("\n--- TEST CLIENTS ---");
            Client client1 = new Client("Diop", "Fatou", "fatou.diop@email.com", "0601020304");
            Client client2 = new Client("Fall", "Moussa", "moussa.fall@email.com", "0605060708");
            
            clientDAO.save(client1);
            clientDAO.save(client2);

            // ============ TEST SERVICES ============
            System.out.println("\n--- TEST SERVICES ---");
            Service s1 = new Service("Petit-déjeuner", 15.0, "Buffet continental");
            Service s2 = new Service("Spa (1h)", 50.0, "Massage relaxant");
            Service s3 = new Service("Parking", 10.0, "Par jour");
            
            serviceDAO.save(s1);
            serviceDAO.save(s2);
            serviceDAO.save(s3);

            // ============ TEST RÉSERVATIONS ============
            System.out.println("\n--- TEST RÉSERVATIONS ---");
            LocalDate debut = LocalDate.now();
            LocalDate fin = debut.plusDays(3);
            
            Reservation resa1 = new Reservation(client1, ch1, debut, fin);
            resa1.ajouterService(s1);
            resa1.ajouterService(s2);
            
            reservationDAO.save(resa1);

            // ============ AFFICHAGE FINAL ============
            System.out.println("\n╔════════════════════════════════════════╗");
            System.out.println("║           RÉCAPITULATIF                ║");
            System.out.println("╚════════════════════════════════════════╝");
            
            System.out.println("\n📋 CHAMBRES:");
            List<Chambre> chambres = chambreDAO.findAll();
            chambres.forEach(System.out::println);

            System.out.println("\n👤 CLIENTS:");
            List<Client> clients = clientDAO.findAll();
            clients.forEach(System.out::println);

            System.out.println("\n🛎️ SERVICES:");
            List<Service> services = serviceDAO.findAll();
            services.forEach(System.out::println);

            System.out.println("\n📅 RÉSERVATIONS:");
            List<Reservation> reservations = reservationDAO.findAll();
            reservations.forEach(System.out::println);

            System.out.println("\n✅ TOUS LES TESTS SONT RÉUSSIS !");

        } catch (SQLException e) {
            System.err.println("❌ Erreur SQL : " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // MÉTHODE POUR VIDER TOUTES LES TABLES AVANT LE TEST
    private static void viderTables() throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            
            stmt.executeUpdate("DELETE FROM reservations_services");
            stmt.executeUpdate("DELETE FROM reservations");
            stmt.executeUpdate("DELETE FROM clients");
            stmt.executeUpdate("DELETE FROM services");
            stmt.executeUpdate("DELETE FROM chambres");
            
            System.out.println("✅ Tables vidées avec succès\n");
        }
    }
}
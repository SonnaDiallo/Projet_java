package dao;

import models.Reservation;
import models.Client;
import models.Chambre;
import models.Service;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReservationDAO {

    private ChambreDAO chambreDAO = new ChambreDAO();
    private ClientDAO clientDAO = new ClientDAO();
    private ServiceDAO serviceDAO = new ServiceDAO();

    // CREATE - Ajouter une réservation
    public void save(Reservation reservation) throws SQLException {
        String sql = "INSERT INTO reservations (numero_reservation, client_id, chambre_id, date_debut, date_fin, statut) " +
                     "SELECT ?, c.id, ch.id, ?, ?, ? " +
                     "FROM clients c, chambres ch " +
                     "WHERE c.numero_client = ? AND ch.numero = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, reservation.getNumeroReservation());
            stmt.setDate(2, Date.valueOf(reservation.getDateDebut()));
            stmt.setDate(3, Date.valueOf(reservation.getDateFin()));
            stmt.setString(4, reservation.getStatut());
            stmt.setInt(5, reservation.getClient().getNumeroClient());
            stmt.setInt(6, reservation.getChambre().getNumero());

            stmt.executeUpdate();
            
            // Sauvegarder les services associés
            saveServices(reservation);
            
            System.out.println("✅ Réservation n°" + reservation.getNumeroReservation() + " créée avec succès !");
        }
    }

    // Sauvegarder les services d'une réservation
    private void saveServices(Reservation reservation) throws SQLException {
        if (reservation.getServices().isEmpty()) {
            return; // Pas de services à sauvegarder
        }

        String sql = "INSERT INTO reservations_services (reservation_id, service_id) " +
                     "SELECT r.id, s.id FROM reservations r, services s " +
                     "WHERE r.numero_reservation = ? AND s.nom = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            for (Service service : reservation.getServices()) {
                stmt.setInt(1, reservation.getNumeroReservation());
                stmt.setString(2, service.getNom());
                stmt.executeUpdate();
            }
        }
    }

    // READ - Récupérer toutes les réservations
    public List<Reservation> findAll() throws SQLException {
        List<Reservation> reservations = new ArrayList<>();
        String sql = "SELECT r.*, " +
                     "c.numero_client, c.nom, c.prenom, c.email, c.telephone, " +
                     "ch.numero, ch.type, ch.prix_par_nuit, ch.capacite, ch.occupee, ch.lits_jumeaux, ch.jacuzzi, ch.balcon " +
                     "FROM reservations r " +
                     "JOIN clients c ON r.client_id = c.id " +
                     "JOIN chambres ch ON r.chambre_id = ch.id";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Reservation reservation = creerReservationDepuisResultSet(rs);
                // Charger les services
                loadServices(reservation);
                reservations.add(reservation);
            }
        }
        return reservations;
    }

    // Charger les services d'une réservation
    private void loadServices(Reservation reservation) throws SQLException {
        String sql = "SELECT s.* FROM services s " +
                     "JOIN reservations_services rs ON s.id = rs.service_id " +
                     "JOIN reservations r ON rs.reservation_id = r.id " +
                     "WHERE r.numero_reservation = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, reservation.getNumeroReservation());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Service service = new Service(
                        rs.getString("nom"),
                        rs.getDouble("prix"),
                        rs.getString("description")
                    );
                    reservation.ajouterService(service);
                }
            }
        }
    }

    // READ - Récupérer une réservation par numéro
    public Reservation findByNumero(int numeroReservation) throws SQLException {
        String sql = "SELECT r.*, " +
                     "c.numero_client, c.nom, c.prenom, c.email, c.telephone, " +
                     "ch.numero, ch.type, ch.prix_par_nuit, ch.capacite, ch.occupee, ch.lits_jumeaux, ch.jacuzzi, ch.balcon " +
                     "FROM reservations r " +
                     "JOIN clients c ON r.client_id = c.id " +
                     "JOIN chambres ch ON r.chambre_id = ch.id " +
                     "WHERE r.numero_reservation = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, numeroReservation);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Reservation reservation = creerReservationDepuisResultSet(rs);
                    loadServices(reservation);
                    return reservation;
                }
            }
        }
        return null;
    }

    // READ - Récupérer les réservations d'un client
    public List<Reservation> findByClient(int numeroClient) throws SQLException {
        List<Reservation> reservations = new ArrayList<>();
        String sql = "SELECT r.*, " +
                     "c.numero_client, c.nom, c.prenom, c.email, c.telephone, " +
                     "ch.numero, ch.type, ch.prix_par_nuit, ch.capacite, ch.occupee, ch.lits_jumeaux, ch.jacuzzi, ch.balcon " +
                     "FROM reservations r " +
                     "JOIN clients c ON r.client_id = c.id " +
                     "JOIN chambres ch ON r.chambre_id = ch.id " +
                     "WHERE c.numero_client = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, numeroClient);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Reservation reservation = creerReservationDepuisResultSet(rs);
                    loadServices(reservation);
                    reservations.add(reservation);
                }
            }
        }
        return reservations;
    }

    // UPDATE - Mettre à jour une réservation
    public void update(Reservation reservation) throws SQLException {
        String sql = "UPDATE reservations SET date_debut = ?, date_fin = ?, statut = ? " +
                     "WHERE numero_reservation = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(reservation.getDateDebut()));
            stmt.setDate(2, Date.valueOf(reservation.getDateFin()));
            stmt.setString(3, reservation.getStatut());
            stmt.setInt(4, reservation.getNumeroReservation());

            stmt.executeUpdate();
            System.out.println("✅ Réservation n°" + reservation.getNumeroReservation() + " mise à jour !");
        }
    }

    // DELETE - Supprimer une réservation
    public void delete(int numeroReservation) throws SQLException {
        String sql = "DELETE FROM reservations WHERE numero_reservation = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, numeroReservation);
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("✅ Réservation n°" + numeroReservation + " supprimée avec succès !");
            } else {
                System.out.println("⚠️ Aucune réservation trouvée");
            }
        }
    }

    // MÉTHODE UTILITAIRE - Créer une réservation à partir d'un ResultSet
    private Reservation creerReservationDepuisResultSet(ResultSet rs) throws SQLException {
        // Créer le client
        Client client = new Client(
            rs.getString("nom"),
            rs.getString("prenom"),
            rs.getString("email"),
            rs.getString("telephone")
        );

        // Créer la chambre (utilise ChambreDAO pour recréer correctement selon le type)
        int numeroChambre = rs.getInt("numero");
        Chambre chambre = chambreDAO.findById(numeroChambre);

        // Créer la réservation
        LocalDate dateDebut = rs.getDate("date_debut").toLocalDate();
        LocalDate dateFin = rs.getDate("date_fin").toLocalDate();
        
        Reservation reservation = new Reservation(client, chambre, dateDebut, dateFin);
        reservation.setStatut(rs.getString("statut"));

        return reservation;
    }
}
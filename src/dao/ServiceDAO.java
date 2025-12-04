package dao;

import models.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ServiceDAO {

    // CREATE - Ajouter un service
    public void save(Service service) throws SQLException {
        String sql = "INSERT INTO services (nom, prix, description) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, service.getNom());
            stmt.setDouble(2, service.getPrix());
            stmt.setString(3, service.getDescription());

            stmt.executeUpdate();
            System.out.println("✅ Service '" + service.getNom() + "' ajouté avec succès !");
        }
    }

    // READ - Récupérer tous les services
    public List<Service> findAll() throws SQLException {
        List<Service> services = new ArrayList<>();
        String sql = "SELECT * FROM services";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Service service = creerServiceDepuisResultSet(rs);
                services.add(service);
            }
        }
        return services;
    }

    // READ - Récupérer un service par ID
    public Service findById(int id) throws SQLException {
        String sql = "SELECT * FROM services WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return creerServiceDepuisResultSet(rs);
                }
            }
        }
        return null;
    }

    // READ - Récupérer un service par nom
    public Service findByNom(String nom) throws SQLException {
        String sql = "SELECT * FROM services WHERE nom = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nom);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return creerServiceDepuisResultSet(rs);
                }
            }
        }
        return null;
    }

    // UPDATE - Mettre à jour un service
    public void update(Service service, int id) throws SQLException {
        String sql = "UPDATE services SET nom = ?, prix = ?, description = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, service.getNom());
            stmt.setDouble(2, service.getPrix());
            stmt.setString(3, service.getDescription());
            stmt.setInt(4, id);

            stmt.executeUpdate();
            System.out.println("✅ Service mis à jour !");
        }
    }

    // DELETE - Supprimer un service
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM services WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("✅ Service supprimé avec succès !");
            } else {
                System.out.println("⚠️ Aucun service trouvé avec cet ID");
            }
        }
    }

    // MÉTHODE UTILITAIRE - Créer un service à partir d'un ResultSet
    private Service creerServiceDepuisResultSet(ResultSet rs) throws SQLException {
        String nom = rs.getString("nom");
        double prix = rs.getDouble("prix");
        String description = rs.getString("description");

        return new Service(nom, prix, description);
    }
}
package dao;

import models.Client;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ClientDAO {

    // CREATE - Ajouter un client
    public void save(Client client) throws SQLException {
        String sql = "INSERT INTO clients (nom, prenom, email, telephone) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, client.getNom());
            stmt.setString(2, client.getPrenom());
            stmt.setString(3, client.getEmail());
            stmt.setString(4, client.getTelephone());

            stmt.executeUpdate();
            
            // Récupérer l'ID généré
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    client.setId(generatedKeys.getInt(1));
                }
            }
            System.out.println("✅ Client " + client.getNomComplet() + " ajouté avec succès !");
        }
    }

    // READ - Récupérer tous les clients
    public List<Client> findAll() throws SQLException {
        List<Client> clients = new ArrayList<>();
        String sql = "SELECT * FROM clients";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Client client = creerClientDepuisResultSet(rs);
                clients.add(client);
            }
        }
        return clients;
    }

    // READ - Récupérer un client par son ID
    public Client findById(int id) throws SQLException {
        String sql = "SELECT * FROM clients WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return creerClientDepuisResultSet(rs);
                }
            }
        }
        return null;
    }

    // READ - Récupérer un client par numéro client
    public Client findByNumeroClient(int numeroClient) throws SQLException {
        String sql = "SELECT * FROM clients WHERE numero_client = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, numeroClient);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return creerClientDepuisResultSet(rs);
                }
            }
        }
        return null;
    }

    // READ - Récupérer un client par email
    public Client findByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM clients WHERE email = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return creerClientDepuisResultSet(rs);
                }
            }
        }
        return null;
    }

    // UPDATE - Mettre à jour un client
    public void update(Client client, int id) throws SQLException {
        String sql = "UPDATE clients SET nom = ?, prenom = ?, email = ?, telephone = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, client.getNom());
            stmt.setString(2, client.getPrenom());
            stmt.setString(3, client.getEmail());
            stmt.setString(4, client.getTelephone());
            stmt.setInt(5, id);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("✅ Client " + client.getNomComplet() + " mis à jour !");
            } else {
                System.out.println("⚠️ Aucun client trouvé avec l'ID " + id);
            }
        }
    }

    // DELETE - Supprimer un client
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM clients WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("✅ Client supprimé avec succès !");
            } else {
                System.out.println("⚠️ Aucun client trouvé avec cet ID");
            }
        }
    }

    // Rechercher des clients par nom
    public List<Client> findByNom(String nom) throws SQLException {
        List<Client> clients = new ArrayList<>();
        String sql = "SELECT * FROM clients WHERE nom LIKE ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + nom + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    clients.add(creerClientDepuisResultSet(rs));
                }
            }
        }
        return clients;
    }

    // MÉTHODE UTILITAIRE - Créer un client à partir d'un ResultSet
    private Client creerClientDepuisResultSet(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String nom = rs.getString("nom");
        String prenom = rs.getString("prenom");
        String email = rs.getString("email");
        String telephone = rs.getString("telephone");

        return new Client(id, nom, prenom, email, telephone);
    }
}
package dao;

// IMPORTS NÉCESSAIRES
import models.Chambre;
import models.ChambreSimple;
import models.ChambreDouble;
import models.Suite;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class ChambreDAO {

    // CREATE - Ajouter une chambre
    public void save(Chambre chambre) throws SQLException {
        String sql = "INSERT INTO chambres (numero, type, prix_par_nuit, capacite, occupee, lits_jumeaux, jacuzzi, balcon) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, chambre.getNumero());
            stmt.setString(2, chambre.getType());
            stmt.setDouble(3, chambre.getPrixParNuit());  // CORRIGÉ : getPrixParNuit() au lieu de getPrix()
            stmt.setInt(4, chambre.getCapacite());
            stmt.setBoolean(5, chambre.isOccupee());

            // Gestion des attributs spécifiques selon le type
            if (chambre instanceof ChambreDouble) {
                stmt.setBoolean(6, ((ChambreDouble) chambre).isLitsJumeaux());
                stmt.setNull(7, Types.BOOLEAN);
                stmt.setNull(8, Types.BOOLEAN);
            } else if (chambre instanceof Suite) {
                stmt.setNull(6, Types.BOOLEAN);
                stmt.setBoolean(7, ((Suite) chambre).isJacuzzi());
                stmt.setBoolean(8, ((Suite) chambre).isBalcon());
            } else { // ChambreSimple
                stmt.setNull(6, Types.BOOLEAN);
                stmt.setNull(7, Types.BOOLEAN);
                stmt.setNull(8, Types.BOOLEAN);
            }

            stmt.executeUpdate();
            System.out.println("✅ Chambre " + chambre.getNumero() + " ajoutée avec succès !");
        }
    }

    // READ - Récupérer toutes les chambres
    public List<Chambre> findAll() throws SQLException {
        List<Chambre> chambres = new ArrayList<>();
        String sql = "SELECT * FROM chambres";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Chambre chambre = creerChambreDepuisResultSet(rs);
                chambres.add(chambre);
            }
        }
        return chambres;
    }

    // READ - Récupérer une chambre par son numéro
    public Chambre findById(int numero) throws SQLException {
        String sql = "SELECT * FROM chambres WHERE numero = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, numero);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return creerChambreDepuisResultSet(rs);
                }
            }
        }
        return null;
    }

    // UPDATE - Mettre à jour une chambre
    public void update(Chambre chambre) throws SQLException {
        String sql = "UPDATE chambres SET type = ?, prix_par_nuit = ?, capacite = ?, occupee = ?, lits_jumeaux = ?, jacuzzi = ?, balcon = ? WHERE numero = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, chambre.getType());
            stmt.setDouble(2, chambre.getPrixParNuit());
            stmt.setInt(3, chambre.getCapacite());
            stmt.setBoolean(4, chambre.isOccupee());

            // Gestion des attributs spécifiques
            if (chambre instanceof ChambreDouble) {
                stmt.setBoolean(5, ((ChambreDouble) chambre).isLitsJumeaux());
                stmt.setNull(6, Types.BOOLEAN);
                stmt.setNull(7, Types.BOOLEAN);
            } else if (chambre instanceof Suite) {
                stmt.setNull(5, Types.BOOLEAN);
                stmt.setBoolean(6, ((Suite) chambre).isJacuzzi());
                stmt.setBoolean(7, ((Suite) chambre).isBalcon());
            } else {
                stmt.setNull(5, Types.BOOLEAN);
                stmt.setNull(6, Types.BOOLEAN);
                stmt.setNull(7, Types.BOOLEAN);
            }

            stmt.setInt(8, chambre.getNumero());

            stmt.executeUpdate();
            System.out.println("✅ Chambre " + chambre.getNumero() + " mise à jour !");
        }
    }

    // DELETE - Supprimer une chambre
    public void delete(int numero) throws SQLException {
        String sql = "DELETE FROM chambres WHERE numero = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, numero);
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("✅ Chambre " + numero + " supprimée avec succès !");
            } else {
                System.out.println("⚠️ Aucune chambre trouvée avec le numéro " + numero);
            }
        }
    }

    // Récupérer les chambres disponibles
    public List<Chambre> findChambresDisponibles() throws SQLException {
        List<Chambre> chambres = new ArrayList<>();
        String sql = "SELECT * FROM chambres WHERE occupee = false";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Chambre chambre = creerChambreDepuisResultSet(rs);
                chambres.add(chambre);
            }
        }
        return chambres;
    }

    // Récupérer les chambres par type
    public List<Chambre> findByType(String type) throws SQLException {
        List<Chambre> chambres = new ArrayList<>();
        String sql = "SELECT * FROM chambres WHERE type = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, type);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Chambre chambre = creerChambreDepuisResultSet(rs);
                    chambres.add(chambre);
                }
            }
        }
        return chambres;
    }

    // MÉTHODE UTILITAIRE - Créer une chambre à partir d'un ResultSet
    private Chambre creerChambreDepuisResultSet(ResultSet rs) throws SQLException {
        int numero = rs.getInt("numero");
        String type = rs.getString("type");
        double prixParNuit = rs.getDouble("prix_par_nuit");
        int capacite = rs.getInt("capacite");
        boolean occupee = rs.getBoolean("occupee");

        Chambre chambre = null;

        switch (type) {
            case "Simple":
                chambre = new ChambreSimple(numero, prixParNuit);
                break;

            case "Double":
                boolean litsJumeaux = rs.getBoolean("lits_jumeaux");
                chambre = new ChambreDouble(numero, prixParNuit, litsJumeaux);
                break;

            case "Suite":
                boolean jacuzzi = rs.getBoolean("jacuzzi");
                boolean balcon = rs.getBoolean("balcon");
                chambre = new Suite(numero, prixParNuit, jacuzzi, balcon);
                break;
        }

        if (chambre != null) {
            chambre.setOccupee(occupee);
        }

        return chambre;
    }
}
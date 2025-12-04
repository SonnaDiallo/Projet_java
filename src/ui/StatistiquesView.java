package ui;

import dao.ChambreDAO;
import dao.ClientDAO;
import dao.ReservationDAO;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import models.Chambre;
import models.Reservation;

import java.sql.SQLException;
import java.util.List;

public class StatistiquesView extends ScrollPane {

    private final ChambreDAO chambreDAO = new ChambreDAO();
    private final ClientDAO clientDAO = new ClientDAO();
    private final ReservationDAO reservationDAO = new ReservationDAO();

    public StatistiquesView() {
        setFitToWidth(true);
        setStyle("-fx-background: transparent; -fx-background-color: #f8f9fa;");
        setPadding(new Insets(20));

        VBox mainContainer = new VBox(20);
        mainContainer.setPadding(new Insets(20));
        mainContainer.setAlignment(Pos.TOP_CENTER);

        // Titre principal
        Label title = new Label("📊 DASHBOARD STATISTIQUES");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 28));
        title.setStyle("-fx-text-fill: #4e54c8;");

        try {
            System.out.println("\n============================================");
            System.out.println("🔄 CHARGEMENT DES STATISTIQUES DYNAMIQUES");
            System.out.println("============================================");
            
            // CHARGEMENT DES DONNÉES RÉELLES DEPUIS LA BDD
            List<Chambre> chambres = chambreDAO.findAll();
            System.out.println("✅ Chambres chargées : " + chambres.size());
            
            List<models.Client> clients = clientDAO.findAll();
            System.out.println("✅ Clients chargés : " + clients.size());
            
            List<Reservation> reservations = reservationDAO.findAll();
            System.out.println("✅ Réservations chargées : " + reservations.size());

            // ========================================
            // CALCUL 1 : STATISTIQUES DES CHAMBRES
            // ========================================
            int totalChambres = chambres.size();
            long chambresOccupees = chambres.stream()
                    .filter(Chambre::isOccupee)
                    .count();
            long chambresDisponibles = totalChambres - chambresOccupees;
            
            double tauxOccupation = totalChambres > 0 
                    ? ((double) chambresOccupees / totalChambres) * 100 
                    : 0;
            
            // ========================================
            // CALCUL 2 : STATISTIQUES DES RÉSERVATIONS
            // ========================================
            int totalReservations = reservations.size();
            
            long reservationsActives = reservations.stream()
                    .filter(r -> "Confirmée".equals(r.getStatut()) || "En cours".equals(r.getStatut()))
                    .count();
            
            long reservationsTerminees = reservations.stream()
                    .filter(r -> "Terminée".equals(r.getStatut()))
                    .count();
            
            long reservationsAnnulees = reservations.stream()
                    .filter(r -> "Annulée".equals(r.getStatut()))
                    .count();
            
            // ========================================
            // CALCUL 3 : REVENUS
            // ========================================
            double revenuTotal = reservations.stream()
                    .filter(r -> !"Annulée".equals(r.getStatut()))
                    .mapToDouble(Reservation::calculerPrixTotal)
                    .sum();
            
            double revenuMoyen = totalReservations > 0
                    ? reservations.stream()
                        .filter(r -> !"Annulée".equals(r.getStatut()))
                        .mapToDouble(Reservation::calculerPrixTotal)
                        .average()
                        .orElse(0)
                    : 0;
            
            // ========================================
            // CALCUL 4 : TAUX DE REMPLISSAGE
            // ========================================
            double tauxRemplissage = totalReservations > 0
                    ? ((double) (totalReservations - reservationsAnnulees) / totalReservations) * 100
                    : 0;
            
            // ========================================
            // CALCUL 5 : CHAMBRE LA PLUS POPULAIRE
            // ========================================
            String chambrePopulaire = findMostPopularRoom(reservations, chambres);
            
            // ========================================
            // CALCUL 6 : DURÉE MOYENNE DE SÉJOUR
            // ========================================
            double dureeMoyenne = reservations.stream()
                    .filter(r -> !"Annulée".equals(r.getStatut()))
                    .mapToInt(Reservation::calculerNombreNuits)
                    .average()
                    .orElse(0);

            // AFFICHAGE DES RÉSULTATS DANS LA CONSOLE
            System.out.println("\n📊 RÉSULTATS DES CALCULS :");
            System.out.println("────────────────────────────────────────");
            System.out.println("💰 Revenu total         : " + String.format("%.2f €", revenuTotal));
            System.out.println("💵 Revenu moyen/résa    : " + String.format("%.2f €", revenuMoyen));
            System.out.println("📈 Taux d'occupation    : " + String.format("%.1f%%", tauxOccupation));
            System.out.println("🛏️  Chambres occupées    : " + chambresOccupees + "/" + totalChambres);
            System.out.println("📅 Réservations actives : " + reservationsActives);
            System.out.println("✅ Résa terminées       : " + reservationsTerminees);
            System.out.println("❌ Résa annulées        : " + reservationsAnnulees);
            System.out.println("📊 Taux de remplissage  : " + String.format("%.1f%%", tauxRemplissage));
            System.out.println("🏆 Chambre populaire    : " + chambrePopulaire);
            System.out.println("⏱️  Durée moyenne        : " + String.format("%.1f nuits", dureeMoyenne));
            System.out.println("────────────────────────────────────────\n");

            // ========================================
            // INTERFACE : CARTES DE STATISTIQUES
            // ========================================
            GridPane statsGrid = new GridPane();
            statsGrid.setHgap(20);
            statsGrid.setVgap(20);
            statsGrid.setPadding(new Insets(20));
            statsGrid.setAlignment(Pos.CENTER);

            // Carte 1 : Revenus (DONNÉES RÉELLES)
            VBox card1 = createStatCard(
                "💰 REVENUS", 
                String.format("%,.2f €", revenuTotal), 
                "#27ae60",
                "Chiffre d'affaires total",
                "arrow_upward",
                String.format("Moyenne: %.2f €/résa", revenuMoyen)
            );

            // Carte 2 : Occupation (DONNÉES RÉELLES)
            VBox card2 = createStatCard(
                "📈 OCCUPATION", 
                String.format("%.1f%%", tauxOccupation), 
                "#3498db",
                "Taux d'occupation",
                "trending_up",
                chambresOccupees + " / " + totalChambres + " chambres"
            );

            // Carte 3 : Chambres (DONNÉES RÉELLES)
            VBox card3 = createStatCard(
                "🛏 CHAMBRES", 
                String.valueOf(totalChambres), 
                "#9b59b6",
                "Total chambres",
                "hotel",
                chambresDisponibles + " disponibles"
            );

            // Carte 4 : Réservations (DONNÉES RÉELLES)
            VBox card4 = createStatCard(
                "📅 RÉSERVATIONS", 
                String.valueOf(totalReservations), 
                "#e74c3c",
                "Total réservations",
                "calendar_today",
                reservationsActives + " en cours"
            );

            statsGrid.add(card1, 0, 0);
            statsGrid.add(card2, 1, 0);
            statsGrid.add(card3, 0, 1);
            statsGrid.add(card4, 1, 1);

            // ========================================
            // INTERFACE : ANALYSE DÉTAILLÉE
            // ========================================
            VBox detailsBox = new VBox(15);
            detailsBox.setPadding(new Insets(20));
            detailsBox.setStyle("-fx-background-color: white; " +
                              "-fx-background-radius: 15; " +
                              "-fx-border-radius: 15; " +
                              "-fx-border-color: #e0e0e0; " +
                              "-fx-border-width: 1; " +
                              "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.08), 8, 0, 0, 3);");

            Label detailsTitle = new Label("📋 ANALYSE DÉTAILLÉE");
            detailsTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
            detailsTitle.setStyle("-fx-text-fill: #2c3e50; -fx-padding: 0 0 10 0;");

            GridPane detailsGrid = new GridPane();
            detailsGrid.setHgap(30);
            detailsGrid.setVgap(15);
            detailsGrid.setPadding(new Insets(15));

            // TOUTES LES DONNÉES PROVIENNENT DE LA BDD
            detailsGrid.add(createDetailItem("Chambre la plus populaire", chambrePopulaire, "#3498db"), 0, 0);
            detailsGrid.add(createDetailItem("Réservations terminées", String.valueOf(reservationsTerminees), "#27ae60"), 1, 0);
            detailsGrid.add(createDetailItem("Réservations annulées", String.valueOf(reservationsAnnulees), "#e74c3c"), 2, 0);
            detailsGrid.add(createDetailItem("Nombre total de clients", String.valueOf(clients.size()), "#9b59b6"), 0, 1);
            detailsGrid.add(createDetailItem("Taux de remplissage", String.format("%.1f%%", tauxRemplissage), "#f39c12"), 1, 1);
            detailsGrid.add(createDetailItem("Durée moyenne séjour", String.format("%.1f nuits", dureeMoyenne), "#1abc9c"), 2, 1);

            detailsBox.getChildren().addAll(detailsTitle, detailsGrid);

            // ========================================
            // INTERFACE : INFORMATIONS
            // ========================================
            VBox infoBox = new VBox(10);
            infoBox.setPadding(new Insets(20));
            infoBox.setStyle("-fx-background-color: white; " +
                           "-fx-background-radius: 10; " +
                           "-fx-border-radius: 10; " +
                           "-fx-border-color: #e0e0e0; " +
                           "-fx-border-width: 1;");

            Label infoTitle = new Label("ℹ INFORMATIONS GÉNÉRALES");
            infoTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
            infoTitle.setStyle("-fx-text-fill: #2c3e50;");

            Label infoText = new Label(
                "✅ Toutes les données proviennent de votre base de données\n" +
                "✅ Statistiques calculées en temps réel\n" +
                "✅ Mise à jour automatique à chaque ouverture de l'onglet\n" +
                "✅ " + totalReservations + " réservations analysées | " + 
                    clients.size() + " clients enregistrés | " + 
                    totalChambres + " chambres gérées"
            );
            infoText.setStyle("-fx-text-fill: #6c757d; -fx-font-size: 14px;");
            infoText.setLineSpacing(5);

            infoBox.getChildren().addAll(infoTitle, infoText);

            mainContainer.getChildren().addAll(title, statsGrid, detailsBox, infoBox);
            
            System.out.println("✅ Interface statistiques créée avec succès !\n");
            System.out.println("============================================\n");

        } catch (SQLException e) {
            System.err.println("\n❌ ERREUR SQL : " + e.getMessage());
            e.printStackTrace();
            
            VBox errorBox = new VBox(15);
            errorBox.setAlignment(Pos.CENTER);
            errorBox.setPadding(new Insets(50));
            errorBox.setStyle("-fx-background-color: white; " +
                            "-fx-background-radius: 15; " +
                            "-fx-border-color: #e74c3c; " +
                            "-fx-border-width: 2; " +
                            "-fx-border-radius: 15;");
            
            Label errorIcon = new Label("❌");
            errorIcon.setStyle("-fx-font-size: 64px;");
            
            Label errorTitle = new Label("Erreur de chargement");
            errorTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 24));
            errorTitle.setStyle("-fx-text-fill: #e74c3c;");
            
            Label errorMessage = new Label("Impossible de charger les données statistiques.\nVeuillez vérifier votre connexion à la base de données.");
            errorMessage.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 14px; -fx-text-alignment: center;");
            errorMessage.setWrapText(true);
            errorMessage.setMaxWidth(400);
            
            Label errorDetails = new Label("Détails : " + e.getMessage());
            errorDetails.setStyle("-fx-text-fill: #95a5a6; -fx-font-size: 12px; -fx-font-style: italic;");
            errorDetails.setWrapText(true);
            errorDetails.setMaxWidth(400);
            
            errorBox.getChildren().addAll(errorIcon, errorTitle, errorMessage, errorDetails);
            mainContainer.getChildren().addAll(title, errorBox);
        }

        setContent(mainContainer);
    }

    private VBox createStatCard(String title, String value, String color, String subtitle, String icon, String info) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(20));
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPrefSize(250, 150);
        
        card.setStyle("-fx-background-color: white; " +
                     "-fx-background-radius: 15; " +
                     "-fx-border-radius: 15; " +
                     "-fx-border-color: #e0e0e0; " +
                     "-fx-border-width: 1; " +
                     "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 5);");
        
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);
        
        Label iconLabel = new Label(getIconEmoji(icon));
        iconLabel.setStyle("-fx-font-size: 24px;");
        
        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        titleLabel.setStyle("-fx-text-fill: #6c757d;");
        
        header.getChildren().addAll(iconLabel, titleLabel);
        
        Label valueLabel = new Label(value);
        valueLabel.setFont(Font.font("Segoe UI", FontWeight.EXTRA_BOLD, 32));
        valueLabel.setStyle("-fx-text-fill: " + color + ";");
        
        Label subtitleLabel = new Label(subtitle);
        subtitleLabel.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 12));
        subtitleLabel.setStyle("-fx-text-fill: #95a5a6;");
        
        Label infoLabel = new Label(info);
        infoLabel.setFont(Font.font("Segoe UI", FontWeight.MEDIUM, 11));
        infoLabel.setStyle("-fx-text-fill: #7f8c8d; -fx-padding: 5 0 0 0;");
        
        Rectangle colorBar = new Rectangle(0, 3);
        colorBar.setFill(Color.web(color));
        colorBar.widthProperty().bind(card.widthProperty().subtract(40));
        
        card.getChildren().addAll(header, valueLabel, subtitleLabel, infoLabel, colorBar);
        
        return card;
    }

    private HBox createDetailItem(String label, String value, String color) {
        HBox item = new HBox(10);
        item.setAlignment(Pos.CENTER_LEFT);
        
        Rectangle dot = new Rectangle(8, 8);
        dot.setArcWidth(8);
        dot.setArcHeight(8);
        dot.setFill(Color.web(color));
        
        VBox textBox = new VBox(2);
        
        Label itemLabel = new Label(label);
        itemLabel.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 12));
        itemLabel.setStyle("-fx-text-fill: #6c757d;");
        
        Label itemValue = new Label(value);
        itemValue.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        itemValue.setStyle("-fx-text-fill: " + color + ";");
        
        textBox.getChildren().addAll(itemLabel, itemValue);
        item.getChildren().addAll(dot, textBox);
        
        return item;
    }

    private String getIconEmoji(String iconName) {
        switch (iconName) {
            case "arrow_upward": return "📈";
            case "trending_up": return "📊";
            case "hotel": return "🏨";
            case "calendar_today": return "📅";
            default: return "📊";
        }
    }

    /**
     * Trouve la chambre la plus réservée (100% DONNÉES RÉELLES)
     */
    private String findMostPopularRoom(List<Reservation> reservations, List<Chambre> chambres) {
        if (reservations.isEmpty()) {
            return "Aucune réservation";
        }
        
        // Compter le nombre de réservations par chambre
        java.util.Map<Integer, Integer> roomCount = new java.util.HashMap<>();
        for (Reservation r : reservations) {
            int roomNum = r.getChambre().getNumero();
            roomCount.put(roomNum, roomCount.getOrDefault(roomNum, 0) + 1);
        }
        
        // Trouver la chambre avec le plus de réservations
        int maxCount = 0;
        int popularRoom = -1;
        for (java.util.Map.Entry<Integer, Integer> entry : roomCount.entrySet()) {
            if (entry.getValue() > maxCount) {
                maxCount = entry.getValue();
                popularRoom = entry.getKey();
            }
        }
        
        if (popularRoom == -1) {
            return "Aucune";
        }
        
        // Retrouver les infos de la chambre
        for (Chambre c : chambres) {
            if (c.getNumero() == popularRoom) {
                return c.getType() + " n°" + popularRoom + " (" + maxCount + " rés.)";
            }
        }
        
        return "Chambre n°" + popularRoom + " (" + maxCount + " rés.)";
    }
}
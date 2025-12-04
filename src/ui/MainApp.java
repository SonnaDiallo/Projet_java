package ui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.Node;

import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.sql.SQLException;
import java.util.List;

import dao.ChambreDAO;
import dao.ClientDAO;
import dao.ReservationDAO;
import models.Chambre;
import models.Client;
import models.Reservation;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) {
        stage.setTitle("Système de Gestion d'Hôtel");

        // ====== EN-TÊTE ======
        Label titleLabel = new Label("Système de Gestion d'Hôtel");
        titleLabel.setStyle("-fx-text-fill: white; -fx-font-size: 24px; -fx-font-weight: bold;");

        Label subtitleLabel = new Label("Grand Hotel - 123 Rue de Paris");
        subtitleLabel.setStyle("-fx-text-fill: #ecf0f1; -fx-font-size: 14px;");

        VBox headerBox = new VBox(5, titleLabel, subtitleLabel);
        headerBox.setAlignment(Pos.CENTER_LEFT);

        HBox header = new HBox(headerBox);
        header.setPadding(new Insets(20));
        header.setStyle(
                "-fx-background-color: linear-gradient(to right, #695ebeff, #8f94fb);");

        // ====== TABS PRINCIPAUX ======
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        tabPane.setStyle(
                "-fx-background-color: #484380ff;"
        );
        // Cacher la barre d'onglets
        tabPane.setTabMaxHeight(0);
        tabPane.lookup(".tab-header-area");

        // ====== PAGE D'ACCUEIL (DASHBOARD) ======
        VBox homeBackground = new VBox();
        homeBackground.setPadding(new Insets(20));
        homeBackground.setAlignment(Pos.TOP_CENTER);
        homeBackground.setStyle("-fx-background-color: #484380ff;");

        VBox homeCard = new VBox(25);
        homeCard.setPadding(new Insets(30));
        homeCard.setMaxWidth(900);
        homeCard.setStyle("-fx-background-color: transparent;");

        // En-tête de la carte
        VBox homeHeader = new VBox(8);
        homeHeader.setAlignment(Pos.CENTER);

        Label homeIcon = new Label("🏨");
        homeIcon.setStyle("-fx-font-size: 40px;");

        Label homeTitle = new Label("Système de Gestion d'Hôtel");
        homeTitle.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: white;");

        Label homeSubtitle = new Label("Grand Hotel - 123 Rue de Paris");
        homeSubtitle.setStyle("-fx-font-size: 16px; -fx-text-fill: #ecf0f1;");

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE d MMMM yyyy à HH:mm", java.util.Locale.FRENCH);
        Label homeTime = new Label(now.format(formatter));
        homeTime.setStyle(
                "-fx-background-color: rgba(102,126,234,0.1);" +
                "-fx-text-fill: white;" +
                "-fx-padding: 6 14;" +
                "-fx-background-radius: 20;" +
                "-fx-font-size: 13px;"
        );

        homeHeader.getChildren().addAll(homeIcon, homeTitle, homeSubtitle, homeTime);

        // Statistiques rapides
        HBox statsRow = new HBox(15);
        statsRow.setAlignment(Pos.CENTER);
        statsRow.setPadding(new Insets(10, 0, 0, 0));

        // Récupération des données depuis la BDD
        int totalChambres = 0;
        int chambresDisponibles = 0;
        int totalClients = 0;
        int totalReservations = 0;

        try {
            ChambreDAO chambreDAO = new ChambreDAO();
            ClientDAO clientDAO = new ClientDAO();
            ReservationDAO reservationDAO = new ReservationDAO();

            List<Chambre> chambres = chambreDAO.findAll();
            totalChambres = chambres.size();
            chambresDisponibles = (int) chambres.stream().filter(c -> !c.isOccupee()).count();

            List<Client> clients = clientDAO.findAll();
            totalClients = clients.size();

            List<Reservation> reservations = reservationDAO.findAll();
            totalReservations = reservations.size();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        VBox statChambres = createStatCard("🛏️", String.valueOf(totalChambres), "CHAMBRES TOTAL");
        VBox statDisponibles = createStatCard("✅", String.valueOf(chambresDisponibles), "DISPONIBLES");
        VBox statClients = createStatCard("👥", String.valueOf(totalClients), "CLIENTS");
        VBox statReservations = createStatCard("📅", String.valueOf(totalReservations), "RÉSERVATIONS");

        statsRow.getChildren().addAll(statChambres, statDisponibles, statClients, statReservations);

        // Actions principales
        VBox actionsSection = new VBox(15);
        actionsSection.setPadding(new Insets(10, 0, 0, 0));

        Label actionsTitle = new Label("Accès Rapide");
        actionsTitle.setStyle("-fx-font-size: 22px; -fx-font-weight: 600; -fx-text-fill: white;");

        HBox actionsRow = new HBox(15);
        actionsRow.setAlignment(Pos.CENTER);

        VBox actionChambres = createActionCard("🛏️", "Gestion Chambres", "Ajouter, modifier et consulter les chambres", "-fx-background-color: linear-gradient(to right, #4CAF50, #45a049);");
        VBox actionClients = createActionCard("👤", "Gestion Clients", "Gérer la base de données des clients", "-fx-background-color: linear-gradient(to right, #2196F3, #1976D2);");
        VBox actionReservations = createActionCard("📅", "Réservations", "Créer et suivre les réservations", "-fx-background-color: linear-gradient(to right, #FF9800, #F57C00);");
        VBox actionStats = createActionCard("📊", "Statistiques", "Analyser les performances", "-fx-background-color: linear-gradient(to right, #9C27B0, #7B1FA2);");

        actionsRow.getChildren().addAll(actionChambres, actionClients, actionReservations, actionStats);
        actionsSection.getChildren().addAll(actionsTitle, actionsRow);

        // Infos rapides
        VBox quickInfo = new VBox(10);
        quickInfo.setPadding(new Insets(20));
        quickInfo.setAlignment(Pos.CENTER);
        quickInfo.setStyle("-fx-background-color: rgba(255,255,255,0.1); -fx-background-radius: 16;");

        Label quickTitle = new Label("📌 Statut en temps réel");
        quickTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: 600; -fx-text-fill: white;");

        HBox quickRow = new HBox(25);
        quickRow.setAlignment(Pos.CENTER);

        int chambresOccupees = totalChambres - chambresDisponibles;
        long reservationsEnCours = 0;
        try {
            ReservationDAO reservationDAO = new ReservationDAO();
            reservationsEnCours = reservationDAO.findAll().stream()
                    .filter(r -> "En cours".equals(r.getStatut())).count();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Label infoDisponible = new Label("● " + chambresDisponibles + " Chambres disponibles");
        infoDisponible.setStyle("-fx-text-fill: #2ecc71;");

        Label infoOccupee = new Label("● " + chambresOccupees + " Chambres occupées");
        infoOccupee.setStyle("-fx-text-fill: #e74c3c;");

        Label infoReservations = new Label("● " + reservationsEnCours + " Réservations en cours");
        infoReservations.setStyle("-fx-text-fill: #f39c12;");

        quickRow.getChildren().addAll(infoDisponible, infoOccupee, infoReservations);
        quickInfo.getChildren().addAll(quickTitle, quickRow);

        // Footer
        Label footer = new Label("© Grand Hotel – Système de Gestion | Développé par l'équipe B3");
        footer.setStyle("-fx-text-fill: #ecf0f1; -fx-font-size: 12px;");
        VBox footerBox = new VBox(footer);
        footerBox.setAlignment(Pos.CENTER);
        footerBox.setPadding(new Insets(20, 0, 10, 0));
        footerBox.setStyle("-fx-background-color: transparent;");

        homeCard.getChildren().addAll(homeHeader, statsRow, actionsSection, quickInfo, footerBox);

        homeBackground.getChildren().add(homeCard);

        // Scroll pour atteindre le bas de la page d'accueil
        ScrollPane homeScroll = new ScrollPane(homeBackground);
        homeScroll.setFitToWidth(true);
        homeScroll.setStyle("-fx-background-color: transparent; -fx-background: transparent;");

        Tab homeTab = new Tab("", homeScroll);

        // Créer les vues avec bouton retour
        VBox chambresContent = createViewWithBackButton(new ChambresView(), "Gestion des Chambres", tabPane);
        VBox clientsContent = createViewWithBackButton(new ClientsView(), "Gestion des Clients", tabPane);
        VBox reservationsContent = createViewWithBackButton(new ReservationsView(), "Gestion des Réservations", tabPane);
        
        // CORRECTION ICI : Utiliser la vraie classe StatistiquesView au lieu d'un Label
        VBox statsContent = createViewWithBackButton(new StatistiquesView(), "Statistiques", tabPane);

        Tab chambresTab = new Tab("", chambresContent);
        Tab clientsTab  = new Tab("", clientsContent);
        Tab reservationsTab = new Tab("", reservationsContent);
        Tab statsTab = new Tab("", statsContent);

        // Clic sur les cartes d'action -> changer d'onglet
        actionChambres.setOnMouseClicked(e ->
                tabPane.getSelectionModel().select(chambresTab));
        actionClients.setOnMouseClicked(e ->
                tabPane.getSelectionModel().select(clientsTab));
        actionReservations.setOnMouseClicked(e ->
                tabPane.getSelectionModel().select(reservationsTab));
        actionStats.setOnMouseClicked(e ->
                tabPane.getSelectionModel().select(statsTab));

        tabPane.getTabs().addAll(homeTab, chambresTab, clientsTab, reservationsTab, statsTab);

        BorderPane root = new BorderPane();
        root.setTop(header);
        root.setCenter(tabPane);
        root.setStyle("-fx-background-color: #484380ff;");

        Scene scene = new Scene(root, 1000, 650);
        stage.setScene(scene);
        stage.show();
    }

    // === Helpers pour la page d'accueil ===
    private VBox createStatCard(String icon, String number, String label) {
        VBox card = new VBox(8);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(18));
        card.setStyle(
                "-fx-background-color: rgba(255,255,255,0.15);" +
                "-fx-background-radius: 15;" +
                "-fx-border-color: rgba(255,255,255,0.3);" +
                "-fx-border-radius: 15;" +
                "-fx-border-width: 1;"
        );

        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-size: 28px;");

        Label numberLabel = new Label(number);
        numberLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white;");

        Label textLabel = new Label(label);
        textLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #ecf0f1; -fx-text-alignment: center;");

        card.getChildren().addAll(iconLabel, numberLabel, textLabel);
        return card;
    }

    private VBox createActionCard(String icon, String title, String description, String backgroundStyle) {
        VBox card = new VBox(10);
        card.setAlignment(Pos.TOP_CENTER);
        card.setPadding(new Insets(20));
        card.setStyle(
                backgroundStyle +
                "-fx-background-radius: 15;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 15,0,0,6);" +
                "-fx-cursor: hand;"
        );

        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-size: 32px;");

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white;");

        Label descLabel = new Label(description);
        descLabel.setWrapText(true);
        descLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: rgba(255,255,255,0.95);");

        card.getChildren().addAll(iconLabel, titleLabel, descLabel);
        return card;
    }

    private VBox createViewWithBackButton(Node content, String title, TabPane tabPane) {
        VBox container = new VBox(10);
        container.setPadding(new Insets(15));
        container.setStyle("-fx-background-color: #484380ff;");

        // Barre de navigation avec bouton retour et titre
        HBox navBar = new HBox(15);
        navBar.setAlignment(Pos.CENTER_LEFT);
        navBar.setPadding(new Insets(5, 10, 15, 10));

        Button backBtn = new Button("← Accueil");
        backBtn.setStyle(
                "-fx-background-color: rgba(255,255,255,0.2);" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 14px;" +
                "-fx-font-weight: bold;" +
                "-fx-padding: 8 16;" +
                "-fx-background-radius: 8;" +
                "-fx-cursor: hand;"
        );
        backBtn.setOnAction(e -> tabPane.getSelectionModel().select(0));

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: white;");

        navBar.getChildren().addAll(backBtn, titleLabel);

        container.getChildren().addAll(navBar, content);
        return container;
    }

    public static void main(String[] args) {
        launch();
    }
}
package ui;

import dao.ChambreDAO;
import dao.ClientDAO;
import dao.ReservationDAO;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import models.Chambre;
import models.Client;
import models.Reservation;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ReservationsView extends VBox {

    private final ReservationDAO reservationDAO = new ReservationDAO();
    private final ClientDAO clientDAO = new ClientDAO();
    private final ChambreDAO chambreDAO = new ChambreDAO();

    private final TableView<Reservation> reservationsTable = new TableView<>();
    private final ObservableList<Reservation> reservationsData = FXCollections.observableArrayList();

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public ReservationsView() {
        setSpacing(20);
        setPadding(new Insets(20));
        setAlignment(Pos.TOP_LEFT);
        setStyle("-fx-background-color: transparent;");

        Label title = new Label("Gestion des Réservations");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        Button newButton = new Button("➕  Nouvelle Réservation");
        newButton.setStyle(
                "-fx-background-color: #27ae60;" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-font-size: 13px;" +
                "-fx-background-radius: 6;" +
                "-fx-padding: 8 18;"
        );

        Button cancelButton = new Button("❌  Annuler");
        cancelButton.setStyle(
                "-fx-background-color: #e74c3c;" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-font-size: 13px;" +
                "-fx-background-radius: 6;" +
                "-fx-padding: 8 16;"
        );

        Button checkoutButton = new Button("✅  Check-out");
        checkoutButton.setStyle(
                "-fx-background-color: #3498db;" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-font-size: 13px;" +
                "-fx-background-radius: 6;" +
                "-fx-padding: 8 16;"
        );

        HBox actionsBar = new HBox(10, newButton, cancelButton, checkoutButton);
        actionsBar.setPadding(new Insets(5, 0, 0, 0));
        actionsBar.setAlignment(Pos.CENTER_LEFT);

        createReservationsTable();
        loadReservations();

        // carte violette pour le tableau
        VBox tableCard = new VBox(reservationsTable);
        tableCard.setPadding(new Insets(10));
        tableCard.setStyle(
                "-fx-background-color: #6c5ce7;" +
                "-fx-background-radius: 12;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.12), 10,0,0,3);"
        );
        VBox.setMargin(tableCard, new Insets(10, 0, 0, 0));

        reservationsTable.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 0 0 12 12;" +
                "-fx-padding: 0;" +
                "-fx-table-header-border-color: transparent;" +
                "-fx-border-color: transparent;"
        );

        newButton.setOnAction(e -> openNewReservationDialog());
        cancelButton.setOnAction(e -> cancelSelectedReservation());
        checkoutButton.setOnAction(e -> checkoutSelectedReservation());

        getChildren().addAll(title, actionsBar, tableCard);
    }
//Crée les 7 colonnes du tableau
    private void createReservationsTable() {
        TableColumn<Reservation, String> numeroCol = new TableColumn<>("N° Réservation");
        numeroCol.setCellValueFactory(cell ->
                new SimpleStringProperty("#" + cell.getValue().getNumeroReservation()));
        numeroCol.setPrefWidth(120);

        TableColumn<Reservation, String> clientCol = new TableColumn<>("Client");
        clientCol.setCellValueFactory(cell -> {
            Client c = cell.getValue().getClient();
            return new SimpleStringProperty(c != null ? c.getNomComplet() : "");
        });
        clientCol.setPrefWidth(150);

        TableColumn<Reservation, String> chambreCol = new TableColumn<>("Chambre");
        chambreCol.setCellValueFactory(cell -> {
            Chambre ch = cell.getValue().getChambre();
            if (ch == null) return new SimpleStringProperty("");
            return new SimpleStringProperty(ch.getNumero() + " - " + ch.getType());
        });
        chambreCol.setPrefWidth(150);

        TableColumn<Reservation, String> datesCol = new TableColumn<>("Du - Au");
        datesCol.setCellValueFactory(cell -> {
            Reservation r = cell.getValue();
            if (r.getDateDebut() == null || r.getDateFin() == null) {
                return new SimpleStringProperty("");
            }
            return new SimpleStringProperty(
                    r.getDateDebut().format(dateFormatter) + " - " +
                            r.getDateFin().format(dateFormatter)
            );
        });
        datesCol.setPrefWidth(180);

        TableColumn<Reservation, Integer> nuitsCol = new TableColumn<>("Nuits");
        nuitsCol.setCellValueFactory(cell ->
                new SimpleIntegerProperty(cell.getValue().calculerNombreNuits()).asObject());
        nuitsCol.setPrefWidth(70);

        TableColumn<Reservation, Double> prixCol = new TableColumn<>("Prix Total");
        prixCol.setCellValueFactory(cell ->
                new SimpleDoubleProperty(cell.getValue().calculerPrixTotal()).asObject());
        prixCol.setPrefWidth(110);
        prixCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                if (empty || price == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(String.format("%.2f €", price));
                    setStyle("-fx-font-weight: bold; -fx-text-fill: #27ae60;");
                }
            }
        });

        TableColumn<Reservation, String> statutCol = new TableColumn<>("Statut");
        statutCol.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getStatut()));
        statutCol.setPrefWidth(100);

        reservationsTable.getColumns().setAll(
                numeroCol, clientCol, chambreCol, datesCol, nuitsCol, prixCol, statutCol
        );
        reservationsTable.setItems(reservationsData);
        reservationsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        reservationsTable.setPrefHeight(400);
    }
    //Charge depuis la BDD

    private void loadReservations() {
        reservationsData.clear();
        try {
            reservationsData.addAll(reservationDAO.findAll());
        } catch (SQLException e) {
            System.err.println("Erreur chargement réservations : " + e.getMessage());
        }
    }

    // ============================
    //  NOUVELLE RÉSERVATION
    // ============================
    private void openNewReservationDialog() {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Nouvelle Réservation");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(12);
        grid.setPadding(new Insets(20));

        Label clientLabel = new Label("Client existant :");
        ComboBox<Client> clientCombo = new ComboBox<>();

        Label chambreLabel = new Label("Chambre :");
        ComboBox<Chambre> chambreCombo = new ComboBox<>();

        Label debutLabel = new Label("Date début :");
        DatePicker debutPicker = new DatePicker(LocalDate.now());

        Label finLabel = new Label("Date fin :");
        DatePicker finPicker = new DatePicker(LocalDate.now().plusDays(1));

        // Champs pour nouveau client
        Label newClientLabel = new Label("Ou nouveau client :");
        newClientLabel.setStyle("-fx-font-weight: bold;");

        Label nomLabel = new Label("Nom :");
        TextField nomField = new TextField();

        Label prenomLabel = new Label("Prénom :");
        TextField prenomField = new TextField();

        Label emailLabel = new Label("Email :");
        TextField emailField = new TextField();

        Label telLabel = new Label("Téléphone :");
        TextField telField = new TextField();

        // Charger les données
        try {
            clientCombo.getItems().addAll(clientDAO.findAll());
            chambreCombo.getItems().addAll(chambreDAO.findChambresDisponibles());
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Erreur chargement clients/chambres : " + e.getMessage());
        }

        clientCombo.setPromptText("Sélectionner un client");
        chambreCombo.setPromptText("Sélectionner une chambre");

        // Affichage plus propre pour les clients
        clientCombo.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(Client c, boolean empty) {
                super.updateItem(c, empty);
                if (empty || c == null) {
                    setText(null);
                } else {
                    setText("Client n°" + c.getNumeroClient() + " - " + c.getNomComplet()
                            + " (" + c.getEmail() + ")");
                }
            }
        });
        clientCombo.setButtonCell(clientCombo.getCellFactory().call(null));

        // Affichage plus propre pour les chambres
        chambreCombo.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(Chambre ch, boolean empty) {
                super.updateItem(ch, empty);
                if (empty || ch == null) {
                    setText(null);
                } else {
                    setText("Chambre " + ch.getNumero() + " - " + ch.getType()
                            + " (" + ch.getCapacite() + " pers.)");
                }
            }
        });
        chambreCombo.setButtonCell(chambreCombo.getCellFactory().call(null));

        // Placement dans la grille
        grid.addRow(0, clientLabel, clientCombo);
        grid.addRow(1, chambreLabel, chambreCombo);
        grid.addRow(2, debutLabel, debutPicker);
        grid.addRow(3, finLabel, finPicker);

        grid.add(newClientLabel, 0, 4, 2, 1);
        grid.addRow(5, nomLabel, nomField);
        grid.addRow(6, prenomLabel, prenomField);
        grid.addRow(7, emailLabel, emailField);
        grid.addRow(8, telLabel, telField);

        Button saveBtn = new Button("Enregistrer");
        saveBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold;");
        Button cancelBtn = new Button("Annuler");

        HBox buttons = new HBox(10, saveBtn, cancelBtn);
        buttons.setAlignment(Pos.CENTER_RIGHT);
        buttons.setPadding(new Insets(15, 0, 0, 0));

        VBox root = new VBox(10, grid, buttons);
        root.setPadding(new Insets(10));

        Scene scene = new Scene(root, 500, 420);
        dialog.setScene(scene);

        cancelBtn.setOnAction(e -> dialog.close());

        saveBtn.setOnAction(e -> {
            Client client = clientCombo.getValue();
            Chambre chambre = chambreCombo.getValue();
            LocalDate debut = debutPicker.getValue();
            LocalDate fin = finPicker.getValue();

            if (chambre == null || debut == null || fin == null) {
                showError("Chambre et dates sont obligatoires.");
                return;
            }
            if (!fin.isAfter(debut)) {
                showError("La date de fin doit être après la date de début.");
                return;
            }

            try {
                // Aucun client sélectionné → création d'un nouveau
                if (client == null) {
                    String nom = nomField.getText().trim();
                    String prenom = prenomField.getText().trim();
                    String email = emailField.getText().trim();
                    String tel = telField.getText().trim();

                    if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty() || tel.isEmpty()) {
                        showError("Choisissez un client OU remplissez tous les champs du nouveau client.");
                        return;
                    }

                    client = new Client(nom, prenom, email, tel);
                    if (!client.validerEmail()) {
                        showError("L'email du client est invalide.");
                        return;
                    }
                    clientDAO.save(client);
                }

                // Création de la réservation
                Reservation reservation = new Reservation(client, chambre, debut, fin);
                reservation.setStatut("Confirmée");
                reservationDAO.save(reservation);

                chambreDAO.update(chambre); // chambre occupée

                loadReservations();
                dialog.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
                showError("Erreur lors de la création de la réservation : " + ex.getMessage());
            }
        });

        dialog.showAndWait();
    }

    // ============================
    //  ANNULATION
    // ============================
    private void cancelSelectedReservation() {
        Reservation selected = reservationsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Veuillez sélectionner une réservation à annuler.");
            return;
        }

        if ("Annulée".equalsIgnoreCase(selected.getStatut())) {
            showError("Cette réservation est déjà annulée.");
            return;
        }
        if ("Terminée".equalsIgnoreCase(selected.getStatut())) {
            showError("Impossible d'annuler une réservation terminée.");
            return;
        }

        Alert confirm = new Alert(AlertType.CONFIRMATION,
                "Annuler la réservation n°" + selected.getNumeroReservation() + " ?",
                ButtonType.OK, ButtonType.CANCEL);
        confirm.setHeaderText(null);
        confirm.showAndWait().ifPresent(button -> {
            if (button == ButtonType.OK) {
                try {
                    selected.annuler();        // met le statut + libère la chambre
                    reservationDAO.update(selected);
                    chambreDAO.update(selected.getChambre());
                    loadReservations();
                } catch (SQLException e) {
                    e.printStackTrace();
                    showError("Erreur lors de l'annulation : " + e.getMessage());
                }
            }
        });
    }

    // ============================
    //  CHECK-OUT
    // ============================
    private void checkoutSelectedReservation() {
    Reservation selected = reservationsTable.getSelectionModel().getSelectedItem();
    if (selected == null) {
        showError("Veuillez sélectionner une réservation pour le check-out.");
        return;
    }

    if ("Terminée".equalsIgnoreCase(selected.getStatut())) {
        showError("Cette réservation est déjà terminée.");
        return;
    }
    if ("Annulée".equalsIgnoreCase(selected.getStatut())) {
        showError("Impossible de faire le check-out d'une réservation annulée.");
        return;
    }

    Alert confirm = new Alert(AlertType.CONFIRMATION,
            "Effectuer le check-out de la réservation n°" + selected.getNumeroReservation() + " ?",
            ButtonType.OK, ButtonType.CANCEL);
    confirm.setHeaderText(null);
    confirm.showAndWait().ifPresent(button -> {
        if (button == ButtonType.OK) {
            try {
                selected.checkout();        // termine + libère la chambre
                reservationDAO.update(selected);
                chambreDAO.update(selected.getChambre());
                loadReservations();
            } catch (SQLException e) {
                e.printStackTrace();
                showError("Erreur lors du check-out : " + e.getMessage());
            }
        }
    });
}
    // ============================
    //  UTILITAIRE
    // ============================
    private void showError(String message) {
        Alert alert = new Alert(AlertType.ERROR, message, ButtonType.OK);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}
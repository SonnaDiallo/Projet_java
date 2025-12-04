package ui;

import dao.ClientDAO;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import models.Client;

public class ClientsView extends VBox {

    private final ClientDAO clientDAO = new ClientDAO();
    private final TableView<Client> clientsTable = new TableView<>();
    private final ObservableList<Client> clientsData = FXCollections.observableArrayList();

    public ClientsView() {
        setSpacing(20);
        setPadding(new Insets(20));
        setAlignment(Pos.TOP_LEFT);
        setStyle("-fx-background-color: transparent;");

        Label title = new Label("Gestion des Clients");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        // ==== BARRE DE BOUTONS ====
        Button addClientBtn = new Button("➕  Ajouter un Client");
        addClientBtn.setStyle(
                "-fx-background-color: #27ae60;" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-font-size: 13px;" +
                "-fx-background-radius: 6;" +
                "-fx-padding: 8 18;"
        );

        Button editClientBtn = new Button("✏️  Modifier");
        editClientBtn.setStyle(
                "-fx-background-color: #f1c40f;" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-font-size: 13px;" +
                "-fx-background-radius: 6;" +
                "-fx-padding: 8 16;"
        );

        Button deleteClientBtn = new Button("🗑️  Supprimer");
        deleteClientBtn.setStyle(
                "-fx-background-color: #e74c3c;" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-font-size: 13px;" +
                "-fx-background-radius: 6;" +
                "-fx-padding: 8 16;"
        );

        TextField searchField = new TextField();
        searchField.setPromptText("Nom ou email...");
        searchField.setPrefWidth(220);
        searchField.setStyle(
                "-fx-background-radius: 6;" +
                "-fx-border-radius: 6;" +
                "-fx-border-color: #dcdde1;" +
                "-fx-padding: 6 8;"
        );

        Button searchBtn = new Button("🔍  Rechercher");
        searchBtn.setStyle(
                "-fx-background-color: #3498db;" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-font-size: 13px;" +
                "-fx-background-radius: 6;" +
                "-fx-padding: 8 18;"
        );

        HBox actionsBar = new HBox(10, addClientBtn, editClientBtn, deleteClientBtn, searchField, searchBtn);
        actionsBar.setPadding(new Insets(5, 0, 0, 0));
        actionsBar.setAlignment(Pos.CENTER_LEFT);

        // ==== TABLE DANS UNE CARTE BLEUE ====
        createClientsTable();
        loadClients();

        VBox tableCard = new VBox(clientsTable);
        tableCard.setPadding(new Insets(10));
        tableCard.setStyle(
                "-fx-background-color: #5f7cff;" +
                "-fx-background-radius: 12;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.12), 10,0,0,3);"
        );
        VBox.setMargin(tableCard, new Insets(10, 0, 0, 0));

        clientsTable.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 0 0 12 12;" +
                "-fx-padding: 0;" +
                "-fx-table-header-border-color: transparent;" +
                "-fx-border-color: transparent;"
        );

        // ==== ACTIONS ====
        searchBtn.setOnAction(e -> filterClients(searchField.getText()));
        searchField.textProperty().addListener((obs, o, n) -> filterClients(n));

        addClientBtn.setOnAction(e -> openAddClientDialog());
        editClientBtn.setOnAction(e -> openEditClientDialog());
        deleteClientBtn.setOnAction(e -> deleteSelectedClient());

        getChildren().addAll(title, actionsBar, tableCard);
    }
//Crée les colonnes du tableau
    private void createClientsTable() {
        TableColumn<Client, Integer> numeroCol = new TableColumn<>("N° Client");
        numeroCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        numeroCol.setPrefWidth(100);

        TableColumn<Client, String> nomCompletCol = new TableColumn<>("Nom Complet");
        nomCompletCol.setCellValueFactory(cd ->
                Bindings.createStringBinding(cd.getValue()::getNomComplet));
        nomCompletCol.setPrefWidth(180);

        TableColumn<Client, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        emailCol.setPrefWidth(220);

        TableColumn<Client, String> telCol = new TableColumn<>("Téléphone");
        telCol.setCellValueFactory(new PropertyValueFactory<>("telephone"));
        telCol.setPrefWidth(140);

        clientsTable.getColumns().setAll(numeroCol, nomCompletCol, emailCol, telCol);
        clientsTable.setItems(clientsData);
        clientsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        clientsTable.setPrefHeight(400);
    }
//Charge les clients depuis la BDD
    private void loadClients() {
        clientsData.clear();
        try {
            clientsData.addAll(clientDAO.findAll());
        } catch (java.sql.SQLException e) {
            System.err.println("Erreur chargement clients : " + e.getMessage());
        }
    }
//Filtre par nom ou email
    private void filterClients(String text) {
        if (text == null || text.isBlank()) {
            loadClients();
            return;
        }
        String q = text.toLowerCase();
        clientsData.clear();
        try {
            for (Client c : clientDAO.findAll()) {
                if (c.getNomComplet().toLowerCase().contains(q)
                        || c.getEmail().toLowerCase().contains(q)) {
                    clientsData.add(c);
                }
            }
        } catch (java.sql.SQLException e) {
            System.err.println("Erreur filtre clients : " + e.getMessage());
        }
    }

    // ==== AJOUT ====
    private void openAddClientDialog() {
        openClientDialog(null);
    }

    // ==== MODIFICATION ====
    private void openEditClientDialog() {
        Client selected = clientsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Veuillez sélectionner un client à modifier.");
            return;
        }
        openClientDialog(selected);
    }

    // Fenêtre commune d'édition/ajout Ouvre le formulaire ajout/modif
    private void openClientDialog(Client existingClient) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle(existingClient == null ? "Ajouter un Client" : "Modifier un Client");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        Label nomLabel = new Label("Nom :");
        TextField nomField = new TextField();

        Label prenomLabel = new Label("Prénom :");
        TextField prenomField = new TextField();

        Label emailLabel = new Label("Email :");
        TextField emailField = new TextField();

        Label telLabel = new Label("Téléphone :");
        TextField telField = new TextField();

        if (existingClient != null) {
            nomField.setText(existingClient.getNom());
            prenomField.setText(existingClient.getPrenom());
            emailField.setText(existingClient.getEmail());
            telField.setText(existingClient.getTelephone());
        }

        grid.addRow(0, nomLabel, nomField);
        grid.addRow(1, prenomLabel, prenomField);
        grid.addRow(2, emailLabel, emailField);
        grid.addRow(3, telLabel, telField);

        Button saveBtn = new Button("Enregistrer");
        saveBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold;");
        Button cancelBtn = new Button("Annuler");

        HBox buttons = new HBox(10, saveBtn, cancelBtn);
        buttons.setAlignment(Pos.CENTER_RIGHT);
        buttons.setPadding(new Insets(10, 0, 0, 0));

        VBox root = new VBox(10, grid, buttons);
        root.setPadding(new Insets(10));

        Scene scene = new Scene(root, 420, 260);
        dialog.setScene(scene);

        cancelBtn.setOnAction(e -> dialog.close());

        saveBtn.setOnAction(e -> {
            String nom = nomField.getText().trim();
            String prenom = prenomField.getText().trim();
            String email = emailField.getText().trim();
            String tel = telField.getText().trim();

            if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty() || tel.isEmpty()) {
                showError("Tous les champs sont obligatoires.");
                return;
            }

            Client client;
            if (existingClient == null) {
                client = new Client(nom, prenom, email, tel);
            } else {
                existingClient.setNom(nom);
                existingClient.setPrenom(prenom);
                existingClient.setEmail(email);
                existingClient.setTelephone(tel);
                client = existingClient;
            }

            if (!client.validerEmail()) {
                showError("L'email est invalide.");
                return;
            }

            try {
                if (existingClient == null) {
                    clientDAO.save(client);
                } else {
                    clientDAO.update(client, client.getId());
                }
                loadClients();
                dialog.close();
            } catch (java.sql.SQLException ex) {
                ex.printStackTrace();
                showError("Erreur lors de l'enregistrement du client : " + ex.getMessage());
            }
        });

        dialog.showAndWait();
    }

    // ==== SUPPRESSION avec confirmation ====
    private void deleteSelectedClient() {
        Client selected = clientsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Veuillez sélectionner un client à supprimer.");
            return;
        }

        Alert confirm = new Alert(AlertType.CONFIRMATION,
                "Supprimer le client " + selected.getNomComplet() + " ?",
                ButtonType.OK, ButtonType.CANCEL);
        confirm.setHeaderText(null);
        confirm.showAndWait().ifPresent(button -> {
            if (button == ButtonType.OK) {
                try {
                    clientDAO.delete(selected.getId());
                    loadClients();
                } catch (java.sql.SQLException e) {
                    e.printStackTrace();
                    showError("Erreur lors de la suppression du client : " + e.getMessage());
                }
            }
        });
    }

    private void showError(String message) {
        Alert alert = new Alert(AlertType.ERROR, message, ButtonType.OK);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}

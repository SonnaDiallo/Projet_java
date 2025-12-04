package ui;

import dao.ChambreDAO;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import models.Chambre;
import models.ChambreSimple;
import models.ChambreDouble;
import models.Suite;

public class ChambresView extends VBox {

    private final ChambreDAO chambreDAO = new ChambreDAO();
    private final TableView<Chambre> chambresTable = new TableView<>();
    private final ObservableList<Chambre> chambresData = FXCollections.observableArrayList();
    
    // Variables pour le filtrage
    private RadioButton toutesChambresRadio;
    private RadioButton chambresDisponiblesRadio;
    private ToggleGroup filterToggleGroup;

    public ChambresView() {
        setSpacing(20);
        setPadding(new Insets(20));
        setAlignment(Pos.TOP_LEFT);
        setStyle("-fx-background-color: transparent;");

        // ====== TITRE ======
        Label title = new Label("Gestion des Chambres");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        // ====== BOUTONS D'ACTION ======
        Button addButton = new Button("➕  Ajouter une Chambre");
        addButton.setStyle(
                "-fx-background-color: #27ae60;" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-font-size: 13px;" +
                "-fx-background-radius: 6;" +
                "-fx-padding: 8 18;"
        );

        Button deleteButton = new Button("🗑️  Supprimer");
        deleteButton.setStyle(
                "-fx-background-color: #e74c3c;" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-font-size: 13px;" +
                "-fx-background-radius: 6;" +
                "-fx-padding: 8 16;"
        );

        Button detailsButton = new Button("ℹ️  Voir Détails");
        detailsButton.setStyle(
                "-fx-background-color: #3498db;" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-font-size: 13px;" +
                "-fx-background-radius: 6;" +
                "-fx-padding: 8 16;"
        );

        // ====== BARRE D'ACTIONS ======
        HBox actionsBar = new HBox(10, addButton, deleteButton, detailsButton);
        actionsBar.setPadding(new Insets(5, 0, 0, 0));
        actionsBar.setAlignment(Pos.CENTER_LEFT);

        // ====== BARRE DE FILTRAGE ======
        HBox filterBox = createFilterBar();

        // ====== INITIALISATION DU TABLEAU ET DONNÉES ======
        createChambresTable();
        loadChambres();

        // Carte violette autour du tableau
        VBox tableCard = new VBox(chambresTable);
        tableCard.setPadding(new Insets(10));
        tableCard.setStyle(
                "-fx-background-color: #6c5ce7;" +
                "-fx-background-radius: 12;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.12), 10,0,0,3);"
        );
        VBox.setMargin(tableCard, new Insets(10, 0, 0, 0));

        chambresTable.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 0 0 12 12;" +
                "-fx-padding: 0;" +
                "-fx-table-header-border-color: transparent;" +
                "-fx-border-color: transparent;"
        );

        // ====== AJOUT DES GESTIONNAIRES D'ÉVÉNEMENTS ======
        addButton.setOnAction(e -> openAddChambreDialog());
        deleteButton.setOnAction(e -> deleteSelectedChambre());
        detailsButton.setOnAction(e -> showChambreDetails());

        // ====== AJOUT DES ÉLÉMENTS À L'INTERFACE ======
        getChildren().addAll(title, actionsBar, filterBox, tableCard);
    }

    /**
     * Crée la barre de filtrage avec les boutons radio
     */
    private HBox createFilterBar() {
        Label filterLabel = new Label("Filtrer par :");
        filterLabel.setStyle("-fx-font-weight: bold;");
        
        // Création des boutons radio
        toutesChambresRadio = new RadioButton("Toutes les Chambres");
        chambresDisponiblesRadio = new RadioButton("Chambres Disponibles");
        
        // Groupe pour les boutons radio (un seul sélectionné à la fois)
        filterToggleGroup = new ToggleGroup();
        toutesChambresRadio.setToggleGroup(filterToggleGroup);
        chambresDisponiblesRadio.setToggleGroup(filterToggleGroup);
        
        // Sélection par défaut : toutes les chambres
        toutesChambresRadio.setSelected(true);
        
        // Écouteurs pour les changements de filtre
        toutesChambresRadio.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                applyFilter("toutes");
            }
        });
        
        chambresDisponiblesRadio.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                applyFilter("disponible");
            }
        });
        
        // Disposition horizontale
        HBox filterBox = new HBox(15, filterLabel, toutesChambresRadio, chambresDisponiblesRadio);
        filterBox.setPadding(new Insets(10, 0, 10, 0));
        
        return filterBox;
    }

    /**
     * Applique le filtre sélectionné sur les chambres
     */
    private void applyFilter(String filterType) {
        ObservableList<Chambre> filteredList = FXCollections.observableArrayList();
        
        try {
            // Récupérer toutes les chambres depuis la BDD
            java.util.List<Chambre> allChambres = chambreDAO.findAll();
            
            if ("toutes".equals(filterType)) {
                // Afficher toutes les chambres
                filteredList.addAll(allChambres);
            } else if ("disponible".equals(filterType)) {
                // Filtrer uniquement les chambres disponibles
                for (Chambre chambre : allChambres) {
                    if (!chambre.isOccupee()) {
                        filteredList.add(chambre);
                    }
                }
            }
            
            // Mettre à jour le tableau avec les données filtrées
            chambresData.clear();
            chambresData.addAll(filteredList);
            
        } catch (java.sql.SQLException e) {
            System.err.println("Erreur lors du filtrage : " + e.getMessage());
        }
    }

    /**
     * Crée et configure les colonnes du tableau des chambres
     */
    private void createChambresTable() {
        // Colonne Numéro de chambre
        TableColumn<Chambre, Integer> numeroCol = new TableColumn<>("N° Chambre");
        numeroCol.setCellValueFactory(new PropertyValueFactory<>("numero"));
        numeroCol.setPrefWidth(120);

        // Colonne Type de chambre
        TableColumn<Chambre, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(cd ->
                Bindings.createStringBinding(cd.getValue()::getType));
        typeCol.setPrefWidth(120);

        // Colonne Capacité
        TableColumn<Chambre, Integer> capaciteCol = new TableColumn<>("Capacité");
        capaciteCol.setCellValueFactory(new PropertyValueFactory<>("capacite"));
        capaciteCol.setPrefWidth(120);

        // Colonne Prix par nuit
        TableColumn<Chambre, Double> prixCol = new TableColumn<>("Prix/Nuit");
        prixCol.setCellValueFactory(new PropertyValueFactory<>("prixParNuit"));
        prixCol.setPrefWidth(120);

        // Colonne Statut (Occupée/Disponible)
        TableColumn<Chambre, String> statutCol = new TableColumn<>("Statut");
        statutCol.setCellValueFactory(cd ->
                Bindings.createStringBinding(
                        () -> cd.getValue().isOccupee() ? "Occupée" : "Disponible"
                ));
        statutCol.setPrefWidth(120);

        // Ajout des colonnes au tableau
        chambresTable.getColumns().setAll(numeroCol, typeCol, capaciteCol, prixCol, statutCol);
        chambresTable.setItems(chambresData);
        chambresTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        chambresTable.setPrefHeight(400);
    }

    /**
     * Charge les chambres depuis la base de données
     */
    private void loadChambres() {
        chambresData.clear();
        try {
            chambresData.addAll(chambreDAO.findAll());
        } catch (java.sql.SQLException e) {
            System.err.println("Erreur chargement chambres : " + e.getMessage());
        }
    }

    /**
     * Ouvre une boîte de dialogue pour ajouter une nouvelle chambre
     */
    private void openAddChambreDialog() {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Ajouter une nouvelle chambre");

        // ====== FORMULAIRE ======
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setPadding(new Insets(20));

        // Champ Numéro de chambre
        Label numeroLabel = new Label("Numéro de chambre :");
        TextField numeroField = new TextField();
        numeroField.setPromptText("Ex: 101");

        // Champ Type de chambre
        Label typeLabel = new Label("Type de chambre :");
        ComboBox<String> typeComboBox = new ComboBox<>();
        typeComboBox.getItems().addAll("Simple", "Double", "Suite");
        typeComboBox.setValue("Simple");

        // Champ Prix par nuit
        Label prixLabel = new Label("Prix par nuit (€) :");
        TextField prixField = new TextField();
        prixField.setPromptText("Ex: 50.0");

        // Champs spécifiques pour Chambre Double
        Label litsLabel = new Label("Lits jumeaux :");
        ComboBox<String> litsComboBox = new ComboBox<>();
        litsComboBox.getItems().addAll("Oui", "Non");
        litsComboBox.setValue("Non");

        // Champs spécifiques pour Suite
        Label jacuzziLabel = new Label("Jacuzzi :");
        ComboBox<String> jacuzziComboBox = new ComboBox<>();
        jacuzziComboBox.getItems().addAll("Oui", "Non");
        jacuzziComboBox.setValue("Non");

        Label balconLabel = new Label("Balcon :");
        ComboBox<String> balconComboBox = new ComboBox<>();
        balconComboBox.getItems().addAll("Oui", "Non");
        balconComboBox.setValue("Non");

        // Masquer initialement les champs spécifiques
        litsLabel.setVisible(false);
        litsComboBox.setVisible(false);
        jacuzziLabel.setVisible(false);
        jacuzziComboBox.setVisible(false);
        balconLabel.setVisible(false);
        balconComboBox.setVisible(false);

        // Gestion du changement de type
        typeComboBox.setOnAction(e -> {
            String typeSelectionne = typeComboBox.getValue();
            
            // Réinitialiser la visibilité
            litsLabel.setVisible(false);
            litsComboBox.setVisible(false);
            jacuzziLabel.setVisible(false);
            jacuzziComboBox.setVisible(false);
            balconLabel.setVisible(false);
            balconComboBox.setVisible(false);
            
            // Afficher les champs spécifiques selon le type
            if ("Double".equals(typeSelectionne)) {
                litsLabel.setVisible(true);
                litsComboBox.setVisible(true);
            } else if ("Suite".equals(typeSelectionne)) {
                jacuzziLabel.setVisible(true);
                jacuzziComboBox.setVisible(true);
                balconLabel.setVisible(true);
                balconComboBox.setVisible(true);
            }
        });

        // Ajout des éléments au grid
        grid.addRow(0, numeroLabel, numeroField);
        grid.addRow(1, typeLabel, typeComboBox);
        grid.addRow(2, prixLabel, prixField);
        grid.addRow(3, litsLabel, litsComboBox);
        grid.addRow(4, jacuzziLabel, jacuzziComboBox);
        grid.addRow(5, balconLabel, balconComboBox);

        // ====== BOUTONS ======
        Button saveBtn = new Button("Enregistrer");
        saveBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold;");
        Button cancelBtn = new Button("Annuler");
        cancelBtn.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white;");

        HBox buttons = new HBox(15, saveBtn, cancelBtn);
        buttons.setAlignment(Pos.CENTER_RIGHT);
        buttons.setPadding(new Insets(20, 0, 0, 0));

        VBox root = new VBox(10, grid, buttons);
        root.setPadding(new Insets(15));
        root.setStyle("-fx-background-color: #f8f9fa;");

        Scene scene = new Scene(root, 450, 350);
        dialog.setScene(scene);

        // ====== ACTIONS DES BOUTONS ======
        cancelBtn.setOnAction(e -> dialog.close());

        saveBtn.setOnAction(e -> {
            // Validation des champs
            if (!validateFields(numeroField, prixField)) {
                return;
            }

            try {
                // Récupération des valeurs
                int numero = Integer.parseInt(numeroField.getText().trim());
                double prix = Double.parseDouble(prixField.getText().trim());
                String type = typeComboBox.getValue();

                // Création de la chambre selon le type
                Chambre nouvelleChambre = null;

                switch (type) {
                    case "Simple":
                        nouvelleChambre = new ChambreSimple(numero, prix);
                        break;
                    case "Double":
                        boolean litsJumeaux = "Oui".equals(litsComboBox.getValue());
                        nouvelleChambre = new ChambreDouble(numero, prix, litsJumeaux);
                        break;
                    case "Suite":
                        boolean jacuzzi = "Oui".equals(jacuzziComboBox.getValue());
                        boolean balcon = "Oui".equals(balconComboBox.getValue());
                        nouvelleChambre = new Suite(numero, prix, jacuzzi, balcon);
                        break;
                }

                // Sauvegarde en base de données
                if (nouvelleChambre != null) {
                    chambreDAO.save(nouvelleChambre);
                    loadChambres(); // Rechargement de la liste
                    showAlert("Succès", "Chambre n°" + numero + " ajoutée avec succès !");
                    dialog.close();
                }

            } catch (NumberFormatException ex) {
                showAlert("Erreur de format", "Veuillez vérifier le format du numéro et du prix.");
            } catch (java.sql.SQLException ex) {
                showAlert("Erreur base de données", "Erreur lors de l'ajout : " + ex.getMessage());
            }
        });

        dialog.showAndWait();
    }

    /**
     * Valide les champs du formulaire
     */
    private boolean validateFields(TextField numeroField, TextField prixField) {
        // Validation du numéro
        String numeroText = numeroField.getText().trim();
        if (numeroText.isEmpty()) {
            showAlert("Champ manquant", "Le numéro de chambre est obligatoire.");
            return false;
        }

        try {
            int numero = Integer.parseInt(numeroText);
            if (numero <= 0) {
                showAlert("Numéro invalide", "Le numéro de chambre doit être positif.");
                return false;
            }

            // Vérifier si le numéro existe déjà
            for (Chambre chambre : chambresData) {
                if (chambre.getNumero() == numero) {
                    showAlert("Numéro existant", "Une chambre avec le numéro " + numero + " existe déjà.");
                    return false;
                }
            }
        } catch (NumberFormatException e) {
            showAlert("Format invalide", "Le numéro de chambre doit être un nombre entier.");
            return false;
        }

        // Validation du prix
        String prixText = prixField.getText().trim();
        if (prixText.isEmpty()) {
            showAlert("Champ manquant", "Le prix par nuit est obligatoire.");
            return false;
        }

        try {
            double prix = Double.parseDouble(prixText);
            if (prix <= 0) {
                showAlert("Prix invalide", "Le prix par nuit doit être positif.");
                return false;
            }
        } catch (NumberFormatException e) {
            showAlert("Format invalide", "Le prix doit être un nombre valide (ex: 50.0).");
            return false;
        }

        return true;
    }

    /**
     * Supprime la chambre sélectionnée dans le tableau
     */
    private void deleteSelectedChambre() {
        // Récupère la chambre sélectionnée
        Chambre selected = chambresTable.getSelectionModel().getSelectedItem();
        
        // Vérifie si une chambre est sélectionnée
        if (selected == null) {
            showAlert("Aucune sélection", "Veuillez sélectionner une chambre à supprimer.");
            return;
        }
        
        // Confirmation de suppression
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirmation de suppression");
        confirmAlert.setHeaderText("Supprimer la chambre " + selected.getNumero() + " ?");
        confirmAlert.setContentText("Êtes-vous sûr de vouloir supprimer cette chambre ? Cette action est irréversible.");
        
        // Si l'utilisateur confirme la suppression
        if (confirmAlert.showAndWait().get() == javafx.scene.control.ButtonType.OK) {
            try {
                // Suppression de la base de données
                chambreDAO.delete(selected.getNumero());
                // Rechargement de la liste
                loadChambres();
                showAlert("Succès", "Chambre n°" + selected.getNumero() + " supprimée avec succès.");
            } catch (java.sql.SQLException e) {
                showAlert("Erreur", "Erreur lors de la suppression : " + e.getMessage());
            }
        }
    }

    /**
     * Affiche les détails de la chambre sélectionnée
     */
    private void showChambreDetails() {
        // Récupère la chambre sélectionnée
        Chambre selected = chambresTable.getSelectionModel().getSelectedItem();
        
        // Vérifie si une chambre est sélectionnée
        if (selected == null) {
            showAlert("Aucune sélection", "Veuillez sélectionner une chambre pour voir les détails.");
            return;
        }
        
        // Affichage des détails dans une boîte de dialogue
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Détails de la chambre");
        alert.setHeaderText("Chambre n°" + selected.getNumero() + " - " + selected.getType());
        alert.setContentText(selected.toString());
        alert.getDialogPane().setPrefSize(400, 300);
        alert.showAndWait();
    }

    /**
     * Utilitaire pour afficher des messages d'alerte
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
package models;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class Reservation {
    
    private static int compteurReservation = 1;
    
    private int numeroReservation;
    private Client client;
    private Chambre chambre;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private List<Service> services = new ArrayList<>();
    private String statut; // "En cours", "Confirmée", "Annulée", "Terminée"

    public Reservation(Client client, Chambre chambre, LocalDate dateDebut, LocalDate dateFin) {
        this.numeroReservation = compteurReservation++;
        this.client = client;
        this.chambre = chambre;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.statut = "En cours";
        chambre.occuper();
    }

    // Getters et Setters
    public int getNumeroReservation() {
        return numeroReservation;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Chambre getChambre() {
        return chambre;
    }

    public void setChambre(Chambre chambre) {
        this.chambre = chambre;
    }

    public LocalDate getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(LocalDate dateDebut) {
        this.dateDebut = dateDebut;
    }

    public LocalDate getDateFin() {
        return dateFin;
    }

    public void setDateFin(LocalDate dateFin) {
        this.dateFin = dateFin;
    }

    public List<Service> getServices() {
        return services;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    // Méthodes métier
    public void ajouterService(Service s) {
        services.add(s);
    }

    public int calculerNombreNuits() {
        return (int) ChronoUnit.DAYS.between(dateDebut, dateFin);
    }

    public double calculerPrixChambre() {
        int nbNuits = calculerNombreNuits();
        return chambre.calculerPrix(nbNuits);
    }

    public double calculerPrixServices() {
        double total = 0;
        for (Service s : services) {
            total += s.getPrix();
        }
        return total;
    }

    public double calculerPrixTotal() {
        return calculerPrixChambre() + calculerPrixServices();
    }

    // Alias pour compatibilité
    public double totalReservation() {
        return calculerPrixTotal();
    }

    public void annuler() {
        this.statut = "Annulée";
        chambre.liberer();
    }

    public void terminer() {
        this.statut = "Terminée";
        chambre.liberer();
    }

    public void checkout() {
        terminer();
    }

    @Override
    public String toString() {
        return "═══════════════════════════════════════\n" +
               "  RÉSERVATION N°" + numeroReservation + "\n" +
               "═══════════════════════════════════════\n" +
               "Client: " + client.getNomComplet() + "\n" +
               "Chambre: " + chambre.getType() + " n°" + chambre.getNumero() + "\n" +
               "Période: du " + dateDebut + " au " + dateFin + "\n" +
               "Nombre de nuits: " + calculerNombreNuits() + "\n" +
               "Prix chambre: " + calculerPrixChambre() + "€\n" +
               "Prix services: " + calculerPrixServices() + "€\n" +
               "TOTAL: " + calculerPrixTotal() + "€\n" +
               "Statut: " + statut + "\n" +
               "═══════════════════════════════════════";
    }
}
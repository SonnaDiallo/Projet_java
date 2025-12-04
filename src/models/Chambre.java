package models;

public abstract class Chambre {
    protected int numero;
    protected double prixParNuit;
    protected boolean occupee;
    protected int capacite;

    public Chambre(int numero, double prixParNuit, int capacite) {
        this.numero = numero;
        this.prixParNuit = prixParNuit;
        this.capacite = capacite;
        this.occupee = false;
    }

    // Getters et Setters
    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public double getPrixParNuit() {
        return prixParNuit;
    }

    public void setPrixParNuit(double prixParNuit) {
        this.prixParNuit = prixParNuit;
    }

    public boolean isOccupee() {
        return occupee;
    }

    public void setOccupee(boolean occupee) {
        this.occupee = occupee;
    }

    public int getCapacite() {
        return capacite;
    }

    public void setCapacite(int capacite) {
        this.capacite = capacite;
    }

    // Méthodes métier
    public void occuper() {
        this.occupee = true;
    }

    public void liberer() {
        this.occupee = false;
    }

    // Méthode abstraite
    public abstract String getType();

    // Méthode de calcul du prix (peut être redéfinie dans les classes filles)
    public double calculerPrix(int nbNuits) {
        return prixParNuit * nbNuits;
    }

    @Override
    public String toString() {
        return "Chambre " + getType() + " n°" + numero +
               "\n  Prix/nuit: " + prixParNuit + "€" +
               "\n  Capacité: " + capacite + " personne(s)" +
               "\n  Statut: " + (occupee ? "Occupée" : "Disponible");
    }
}
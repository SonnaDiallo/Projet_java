package models;

public class Suite extends Chambre {
    
    private boolean jacuzzi;
    private boolean balcon;
    
    public Suite(int numero, double prixParNuit, boolean jacuzzi, boolean balcon) {
        super(numero, prixParNuit, 4); // capacité de 4 personnes
        this.jacuzzi = jacuzzi;
        this.balcon = balcon;
    }
    
    // Constructeur avec prix de base par défaut - CORRIGÉ
    public Suite(int numero, boolean jacuzzi, boolean balcon) {
        super(numero, 150.0, 4); // capacité de 4 personnes
        this.jacuzzi = jacuzzi;
        this.balcon = balcon;
    }

    // NOUVEAU CONSTRUCTEUR POUR TON CODE
    public Suite(int numero, double prix) {
        super(numero, prix, 4); // capacité de 4 personnes
        this.jacuzzi = false; // par défaut
        this.balcon = false; // par défaut
    }
    
    @Override
    public String getType() {
        return "Suite";
    }
    
    @Override
    public double calculerPrix(int nbNuits) {
        double prixTotal = getPrixParNuit();
        
        // Ajouter les suppléments
        if (jacuzzi) {
            prixTotal += 30;
        }
        if (balcon) {
            prixTotal += 20;
        }
        
        return prixTotal * nbNuits;
    }
    
    // Getters et Setters
    public boolean isJacuzzi() { return jacuzzi; }
    public void setJacuzzi(boolean jacuzzi) { this.jacuzzi = jacuzzi; }
    public boolean isBalcon() { return balcon; }
    public void setBalcon(boolean balcon) { this.balcon = balcon; }
    
    @Override
    public String toString() {
        return super.toString() + 
               "\n  Jacuzzi: " + (jacuzzi ? "Oui (+30€/nuit)" : "Non") +
               "\n  Balcon: " + (balcon ? "Oui (+20€/nuit)" : "Non") +
               "\n  Prix total/nuit: " + calculerPrix(1) + "€";
    }
}
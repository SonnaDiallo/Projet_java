package models;

public class ChambreDouble extends Chambre {
    
    private boolean litsJumeaux;
    
    public ChambreDouble(int numero, double prixParNuit, boolean litsJumeaux) {
        super(numero, prixParNuit, 2); // capacité de 2 personnes
        this.litsJumeaux = litsJumeaux;
    }
    
    // Constructeur avec prix par défaut - CORRIGÉ
    public ChambreDouble(int numero, boolean litsJumeaux) {
        super(numero, 80.0, 2); // capacité de 2 personnes
        this.litsJumeaux = litsJumeaux;
    }

    // NOUVEAU CONSTRUCTEUR POUR TON CODE
    public ChambreDouble(int numero, double prix) {
        super(numero, prix, 2); // capacité de 2 personnes
        this.litsJumeaux = false; // par défaut
    }

    @Override
    public String getType() {
        return "Double";
    }
    
    // Getters et Setters
    public boolean isLitsJumeaux() { return litsJumeaux; }
    public void setLitsJumeaux(boolean litsJumeaux) { this.litsJumeaux = litsJumeaux; }
    
    @Override
    public String toString() {
        return super.toString() + 
               "\n  Configuration: " + (litsJumeaux ? "2 lits simples" : "1 lit double");
    }
}
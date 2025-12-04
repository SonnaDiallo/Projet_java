package models;

public class ChambreSimple extends Chambre {
    
    public ChambreSimple(int numero, double prixParNuit) {
        super(numero, prixParNuit, 1); // capacité de 1 personne
    }
    
    // Constructeur avec prix par défaut - CORRIGÉ
    public ChambreSimple(int numero) {
        super(numero, 50.0, 1); // capacité de 1 personne
    }

    @Override
    public String getType() {
        return "Simple";
    }
}
package models;

public class Client {
    
    private int id;  // ID de la BDD
    private String nom;
    private String prenom;
    private String email;
    private String telephone;

    // Constructeur pour nouveau client (sans ID)
    public Client(String nom, String prenom, String email, String telephone) {
        this.id = 0;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.telephone = telephone;
    }

    // Constructeur avec ID (depuis BDD)
    public Client(int id, String nom, String prenom, String email, String telephone) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.telephone = telephone;
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNumeroClient() {
        return id;  // Alias pour compatibilité avec l'UI
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    // Méthode getNomComplet() - AJOUTÉE
    public String getNomComplet() {
        return prenom + " " + nom.toUpperCase();
    }

    // Validation email
    public boolean validerEmail() {
        if (email == null || email.isEmpty()) {
            return false;
        }
        // Regex simple pour valider le format email
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(emailRegex);
    }

    @Override
    public String toString() {
        return "Client n°" + id + " - " + getNomComplet() + 
               "\nEmail: " + email + 
               "\nTéléphone: " + telephone;
    }
}
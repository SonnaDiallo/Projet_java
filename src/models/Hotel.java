package models;

import java.util.ArrayList;
import java.util.List;

public class Hotel {

    private String nom;
    private String adresse;
    private List<Chambre> chambres = new ArrayList<>();
    private List<Client> clients = new ArrayList<>();
    private List<Reservation> reservations = new ArrayList<>();
    private List<Service> servicesDisponibles = new ArrayList<>();

    public Hotel() {
        // Exemples de chambres - CORRIGÉ
        chambres.add(new ChambreSimple(101)); // ✓ Constructeur à 1 paramètre
        chambres.add(new ChambreDouble(202, true)); // ✓ Constructeur à 2 paramètres  
        chambres.add(new Suite(303, true, true)); // ✓ Constructeur à 3 paramètres
        
        // Exemples de services
        servicesDisponibles.add(new Service("Petit-déjeuner", 15.0, "Buffet continental"));
        servicesDisponibles.add(new Service("Dîner au restaurant", 35.0, "Menu gastronomique"));
        servicesDisponibles.add(new Service("Spa (1h)", 50.0, "Massage relaxant"));
        servicesDisponibles.add(new Service("Parking", 10.0, "Par jour"));
        servicesDisponibles.add(new Service("Wifi Premium", 5.0, "Haut débit"));
    }

    // Getters et Setters
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }
    public List<Chambre> getChambres() { return chambres; }
    public List<Client> getClients() { return clients; }
    public List<Reservation> getReservations() { return reservations; }
    public List<Service> getServicesDisponibles() { return servicesDisponibles; }

    // Méthodes de gestion
    public void ajouterChambre(Chambre c) { chambres.add(c); }
    public void ajouterClient(Client c) { clients.add(c); }
    public void ajouterReservation(Reservation r) { reservations.add(r); }
    public void ajouterService(Service s) { servicesDisponibles.add(s); }

    // ------- STATISTIQUES --------
    public double calculerChiffreAffaires() {
        double total = 0;
        for (Reservation r : reservations) {
            if (!"Annulée".equals(r.getStatut())) {
                total += r.totalReservation();
            }
        }
        return total;
    }

    public double calculerTauxOccupation() {
        if (chambres.isEmpty()) return 0;
        long occ = chambres.stream().filter(Chambre::isOccupee).count();
        return ((double) occ / chambres.size()) * 100;
    }

    public String chambreLaPlusReservee() {
        if (reservations.isEmpty()) return "Aucune";
        Chambre top = null;
        int max = 0;

        for (Chambre c : chambres) {
            int count = (int) reservations.stream()
                    .filter(r -> r.getChambre().getNumero() == c.getNumero())
                    .count();
            if (count > max) {
                max = count;
                top = c;
            }
        }
        return top != null ? top.getType() + " n°" + top.getNumero() : "Aucune";
    }

    public void afficherStatistiques() {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║        STATISTIQUES DE L'HÔTEL         ║");
        System.out.println("╚════════════════════════════════════════╝");
        System.out.println("Chiffre d'affaires: " + calculerChiffreAffaires() + "€");
        System.out.println("Taux d'occupation: " + String.format("%.2f", calculerTauxOccupation()) + "%");
        System.out.println("Chambre la plus réservée: " + chambreLaPlusReservee());
        System.out.println("Nombre de chambres: " + chambres.size());
        System.out.println("Nombre de clients: " + clients.size());
        System.out.println("Nombre de réservations: " + reservations.size());
    }
}
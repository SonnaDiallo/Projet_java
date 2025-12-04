package test;

import dao.ChambreDAO;
import models.ChambreSimple;
import models.ChambreDouble;
import models.Suite;
import models.Chambre;
import java.sql.SQLException;
import java.util.List;

public class TestChambreDAO {

    public static void main(String[] args) {
        
        ChambreDAO dao = new ChambreDAO();

        try {
            System.out.println("╔════════════════════════════════════════╗");
            System.out.println("║   TEST DE LA CLASSE ChambreDAO         ║");
            System.out.println("╚════════════════════════════════════════╝\n");

            // 1. TEST INSERT
            System.out.println("--- TEST 1 : Insertion de chambres ---");
            ChambreSimple cSimple = new ChambreSimple(101, 50.0);
            ChambreDouble cDouble = new ChambreDouble(202, 80.0, true);
            Suite suite = new Suite(303, 150.0, true, true);

            dao.save(cSimple);
            dao.save(cDouble);
            dao.save(suite);

            // 2. TEST SELECT ALL
            System.out.println("\n--- TEST 2 : Récupération de toutes les chambres ---");
            List<Chambre> toutes = dao.findAll();
            for (Chambre c : toutes) {
                System.out.println(c);
                System.out.println("---");
            }

            // 3. TEST SELECT BY ID
            System.out.println("\n--- TEST 3 : Recherche chambre n°101 ---");
            Chambre c101 = dao.findById(101);
            if (c101 != null) {
                System.out.println(c101);
            }

            // 4. TEST CHAMBRES DISPONIBLES
            System.out.println("\n--- TEST 4 : Chambres disponibles ---");
            List<Chambre> dispo = dao.findChambresDisponibles();
            System.out.println("Nombre de chambres disponibles : " + dispo.size());

            // 5. TEST UPDATE
            System.out.println("\n--- TEST 5 : Mise à jour (occupation chambre 101) ---");
            if (c101 != null) {
                c101.occuper();
                dao.update(c101);
            }

            // 6. TEST DELETE
            System.out.println("\n--- TEST 6 : Suppression chambre 303 ---");
            dao.delete(303);

            System.out.println("\n✅ TOUS LES TESTS SONT TERMINÉS !");

        } catch (SQLException e) {
            System.err.println("❌ Erreur SQL : " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("❌ Erreur inattendue : " + e.getMessage());
            e.printStackTrace();
        }
    }
}
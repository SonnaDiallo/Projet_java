package test;

public class TestDB {
    public static void main(String[] args) {
        System.out.println("Test connexion");
        
        java.sql.Connection conn = null;
        
        try {
            conn = dao.DatabaseConnection.getConnection();
            System.out.println("CONNEXION OK");
        } catch (java.sql.SQLException e) {
            System.out.println("ERREUR : " + e.getMessage());
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (java.sql.SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
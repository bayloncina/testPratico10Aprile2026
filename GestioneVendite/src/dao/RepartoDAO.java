package dao;

import db.DataBaseManager;
import entity.Reparto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RepartoDAO {

    private Connection connection;

    public RepartoDAO() {
        this.connection = DataBaseManager.getIstanzaDb().getConnection();
    }

    // -------------------------------------------------------
    // CREATE: inserisce un nuovo reparto
    // -------------------------------------------------------
    public boolean inserisci(Reparto reparto) {
        try {
            String query = "INSERT INTO reparti (nome, descrizione) VALUES (?, ?)";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, reparto.getNome());
            stmt.setString(2, reparto.getDescrizione());
            stmt.executeUpdate();
            System.out.println("Reparto '" + reparto.getNome() + "' inserito con successo.");
            return true;
        } catch (Exception e) {
            System.out.println("Errore inserimento reparto: " + e.getMessage());
            return false;
        }
    }

    // -------------------------------------------------------
    // READ: cerca reparto per id
    // -------------------------------------------------------
    public Reparto cercaPerId(int id) {
        try {
            String query = "SELECT * FROM reparti WHERE id = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return estraiReparto(rs);
            }
        } catch (Exception e) {
            System.out.println("Errore ricerca reparto per id: " + e.getMessage());
        }
        return null;
    }

    // -------------------------------------------------------
    // READ: cerca reparto per nome
    // -------------------------------------------------------
    public Reparto cercaPerNome(String nome) {
        try {
            String query = "SELECT * FROM reparti WHERE nome = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, nome);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return estraiReparto(rs);
            }
        } catch (Exception e) {
            System.out.println("Errore ricerca reparto per nome: " + e.getMessage());
        }
        return null;
    }

    // -------------------------------------------------------
    // READ: restituisce tutti i reparti
    // -------------------------------------------------------
    public List<Reparto> tuttiIReparti() {
        List<Reparto> lista = new ArrayList<>();
        try {
            String query = "SELECT * FROM reparti ORDER BY id";
            PreparedStatement stmt = connection.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                lista.add(estraiReparto(rs));
            }
        } catch (Exception e) {
            System.out.println("Errore recupero reparti: " + e.getMessage());
        }
        return lista;
    }

    // -------------------------------------------------------
    // READ: restituisce i reparti associati a un utente
    // -------------------------------------------------------
    public List<Reparto> repartiDiUtente(int utenteId) {
        List<Reparto> lista = new ArrayList<>();
        try {
            String query = "SELECT r.* FROM reparti r " +
                    "JOIN utente_reparto ur ON r.id = ur.reparto_id " +
                    "WHERE ur.utente_id = ? " +
                    "ORDER BY r.id";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, utenteId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                lista.add(estraiReparto(rs));
            }
        } catch (Exception e) {
            System.out.println("Errore recupero reparti utente: " + e.getMessage());
        }
        return lista;
    }

    // -------------------------------------------------------
    // UPDATE: aggiorna nome e descrizione di un reparto
    // -------------------------------------------------------
    public boolean aggiorna(Reparto reparto) {
        try {
            String query = "UPDATE reparti SET nome = ?, descrizione = ? WHERE id = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, reparto.getNome());
            stmt.setString(2, reparto.getDescrizione());
            stmt.setInt(3, reparto.getId());
            stmt.executeUpdate();
            System.out.println("Reparto aggiornato.");
            return true;
        } catch (Exception e) {
            System.out.println("Errore aggiornamento reparto: " + e.getMessage());
            return false;
        }
    }

    // -------------------------------------------------------
    // DELETE: elimina reparto per id
    // -------------------------------------------------------
    public boolean elimina(int id) {
        try {
            String query = "DELETE FROM reparti WHERE id = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, id);
            stmt.executeUpdate();
            System.out.println("Reparto eliminato.");
            return true;
        } catch (Exception e) {
            System.out.println("Errore eliminazione reparto: " + e.getMessage());
            return false;
        }
    }

    // -------------------------------------------------------
    // UTILITY PRIVATA: costruisce un Reparto dal ResultSet
    // -------------------------------------------------------
    private Reparto estraiReparto(ResultSet rs) throws SQLException {
        return new Reparto(
                rs.getInt("id"),
                rs.getString("nome"),
                rs.getString("descrizione"));
    }
}
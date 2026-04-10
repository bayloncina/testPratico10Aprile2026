package dao;

import db.DataBaseManager;
import entity.UtenteReparto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UtenteRepartoDAO {

    private Connection connection;

    public UtenteRepartoDAO() {
        this.connection = DataBaseManager.getIstanzaDb().getConnection();
    }

    // -------------------------------------------------------
    // CREATE: associa un utente a un reparto con un ruolo
    // -------------------------------------------------------
    public boolean inserisci(UtenteReparto ur) {
        try {
            String query = "INSERT INTO utente_reparto (utente_id, reparto_id, ruolo) VALUES (?, ?, ?)";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, ur.getUtenteId());
            stmt.setInt(2, ur.getRepartoId());
            stmt.setString(3, ur.getRuolo());
            stmt.executeUpdate();
            System.out.println("Associazione utente-reparto creata con ruolo " + ur.getRuolo() + ".");
            return true;
        } catch (Exception e) {
            System.out.println("Errore inserimento associazione: " + e.getMessage());
            return false;
        }
    }

    // -------------------------------------------------------
    // READ: restituisce tutte le associazioni di un utente
    // -------------------------------------------------------
    public List<UtenteReparto> associazioniDiUtente(int utenteId) {
        List<UtenteReparto> lista = new ArrayList<>();
        try {
            String query = "SELECT * FROM utente_reparto WHERE utente_id = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, utenteId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                lista.add(estraiAssociazione(rs));
            }
        } catch (Exception e) {
            System.out.println("Errore recupero associazioni utente: " + e.getMessage());
        }
        return lista;
    }

    // -------------------------------------------------------
    // READ: restituisce tutti gli utenti di un reparto
    // -------------------------------------------------------
    public List<UtenteReparto> utentiDelReparto(int repartoId) {
        List<UtenteReparto> lista = new ArrayList<>();
        try {
            String query = "SELECT * FROM utente_reparto WHERE reparto_id = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, repartoId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                lista.add(estraiAssociazione(rs));
            }
        } catch (Exception e) {
            System.out.println("Errore recupero utenti del reparto: " + e.getMessage());
        }
        return lista;
    }

    // -------------------------------------------------------
    // READ: cerca associazione specifica utente + reparto
    // -------------------------------------------------------
    public UtenteReparto cerca(int utenteId, int repartoId) {
        try {
            String query = "SELECT * FROM utente_reparto WHERE utente_id = ? AND reparto_id = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, utenteId);
            stmt.setInt(2, repartoId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return estraiAssociazione(rs);
            }
        } catch (Exception e) {
            System.out.println("Errore ricerca associazione: " + e.getMessage());
        }
        return null;
    }

    // -------------------------------------------------------
    // READ: controlla se un utente è admin di un reparto
    // -------------------------------------------------------
    public boolean isAdminDelReparto(int utenteId, int repartoId) {
        try {
            String query = "SELECT ruolo FROM utente_reparto WHERE utente_id = ? AND reparto_id = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, utenteId);
            stmt.setInt(2, repartoId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return "ADMIN".equals(rs.getString("ruolo"));
            }
        } catch (Exception e) {
            System.out.println("Errore verifica ruolo: " + e.getMessage());
        }
        return false;
    }

    // -------------------------------------------------------
    // UPDATE: cambia il ruolo di un utente in un reparto
    // -------------------------------------------------------
    public boolean aggiornaRuolo(int utenteId, int repartoId, String nuovoRuolo) {
        try {
            String query = "UPDATE utente_reparto SET ruolo = ? WHERE utente_id = ? AND reparto_id = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, nuovoRuolo);
            stmt.setInt(2, utenteId);
            stmt.setInt(3, repartoId);
            stmt.executeUpdate();
            System.out.println("Ruolo aggiornato a " + nuovoRuolo + ".");
            return true;
        } catch (Exception e) {
            System.out.println("Errore aggiornamento ruolo: " + e.getMessage());
            return false;
        }
    }

    // -------------------------------------------------------
    // DELETE: rimuove un utente da un reparto
    // -------------------------------------------------------
    public boolean elimina(int utenteId, int repartoId) {
        try {
            String query = "DELETE FROM utente_reparto WHERE utente_id = ? AND reparto_id = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, utenteId);
            stmt.setInt(2, repartoId);
            stmt.executeUpdate();
            System.out.println("Associazione utente-reparto rimossa.");
            return true;
        } catch (Exception e) {
            System.out.println("Errore eliminazione associazione: " + e.getMessage());
            return false;
        }
    }

    // -------------------------------------------------------
    // UTILITY PRIVATA: costruisce UtenteReparto dal ResultSet
    // -------------------------------------------------------
    private UtenteReparto estraiAssociazione(ResultSet rs) throws SQLException {
        return new UtenteReparto(
            rs.getInt("id"),
            rs.getInt("utente_id"),
            rs.getInt("reparto_id"),
            rs.getString("ruolo")
        );
    }
}
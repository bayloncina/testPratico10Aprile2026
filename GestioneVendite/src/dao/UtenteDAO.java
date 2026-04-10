package dao;

import db.DataBaseManager;
import entity.Utente;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UtenteDAO {

    private Connection connection;

    public UtenteDAO() {
        this.connection = DataBaseManager.getIstanzaDb().getConnection();
    }

    // -------------------------------------------------------
    // CREATE: inserisce un nuovo utente
    // -------------------------------------------------------
    public boolean inserisci(Utente utente) {
        try {
            String query = "INSERT INTO utenti (username, password, tipo_account, is_superadmin, attivo) " +
                    "VALUES (?, ?, ?, ?, ?)";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, utente.getUsername());
            stmt.setString(2, utente.getPassword());
            stmt.setString(3, utente.getTipoAccount());
            stmt.setBoolean(4, utente.isSuperAdmin());
            stmt.setBoolean(5, utente.isAttivo());
            stmt.executeUpdate();
            System.out.println("Utente '" + utente.getUsername() + "' inserito con successo.");
            return true;
        } catch (Exception e) {
            System.out.println("Errore inserimento utente: " + e.getMessage());
            return false;
        }
    }

    // -------------------------------------------------------
    // READ: cerca utente per id
    // -------------------------------------------------------
    public Utente cercaPerId(int id) {
        try {
            String query = "SELECT * FROM utenti WHERE id = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return estraiUtente(rs);
            }
        } catch (Exception e) {
            System.out.println("Errore ricerca utente per id: " + e.getMessage());
        }
        return null;
    }

    // -------------------------------------------------------
    // READ: cerca utente per username
    // -------------------------------------------------------
    public Utente cercaPerUsername(String username) {
        try {
            String query = "SELECT * FROM utenti WHERE username = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return estraiUtente(rs);
            }
        } catch (Exception e) {
            System.out.println("Errore ricerca utente per username: " + e.getMessage());
        }
        return null;
    }

    // -------------------------------------------------------
    // READ: restituisce tutti gli utenti
    // -------------------------------------------------------
    public List<Utente> tuttiGliUtenti() {
        List<Utente> lista = new ArrayList<>();
        try {
            String query = "SELECT * FROM utenti ORDER BY username";
            PreparedStatement stmt = connection.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                lista.add(estraiUtente(rs));
            }
        } catch (Exception e) {
            System.out.println("Errore recupero utenti: " + e.getMessage());
        }
        return lista;
    }

    // -------------------------------------------------------
    // READ: restituisce solo gli utenti attivi
    // -------------------------------------------------------
    public List<Utente> utentiAttivi() {
        List<Utente> lista = new ArrayList<>();
        try {
            String query = "SELECT * FROM utenti WHERE attivo = TRUE ORDER BY username";
            PreparedStatement stmt = connection.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                lista.add(estraiUtente(rs));
            }
        } catch (Exception e) {
            System.out.println("Errore recupero utenti attivi: " + e.getMessage());
        }
        return lista;
    }

    // -------------------------------------------------------
    // LOGIN: verifica credenziali, restituisce l'utente o null
    // -------------------------------------------------------
    public Utente login(String username, String password) {
        try {
            String query = "SELECT * FROM utenti WHERE username = ? AND password = ? AND attivo = TRUE";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Utente u = estraiUtente(rs);
                System.out.println("Login effettuato. Benvenuto, " + u.getUsername() + "!");
                return u;
            } else {
                System.out.println("Credenziali errate o utente non attivo.");
                return null;
            }
        } catch (Exception e) {
            System.out.println("Errore durante il login: " + e.getMessage());
            return null;
        }
    }

    // -------------------------------------------------------
    // UPDATE: modifica tipo account (NORMAL <-> PRO)
    // -------------------------------------------------------
    public boolean aggiornaTipoAccount(int id, String nuovoTipo) {
        try {
            String query = "UPDATE utenti SET tipo_account = ? WHERE id = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, nuovoTipo);
            stmt.setInt(2, id);
            stmt.executeUpdate();
            System.out.println("Tipo account aggiornato a " + nuovoTipo + ".");
            return true;
        } catch (Exception e) {
            System.out.println("Errore aggiornamento tipo account: " + e.getMessage());
            return false;
        }
    }

    // -------------------------------------------------------
    // UPDATE: aggiorna password
    // -------------------------------------------------------
    public boolean aggiornaPassword(int id, String nuovaPassword) {
        try {
            String query = "UPDATE utenti SET password = ? WHERE id = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, nuovaPassword);
            stmt.setInt(2, id);
            stmt.executeUpdate();
            System.out.println("Password aggiornata.");
            return true;
        } catch (Exception e) {
            System.out.println("Errore aggiornamento password: " + e.getMessage());
            return false;
        }
    }

    // -------------------------------------------------------
    // UPDATE: attiva o disattiva un utente
    // -------------------------------------------------------
    public boolean impostaAttivo(int id, boolean attivo) {
        try {
            String query = "UPDATE utenti SET attivo = ? WHERE id = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setBoolean(1, attivo);
            stmt.setInt(2, id);
            stmt.executeUpdate();
            System.out.println("Utente " + (attivo ? "attivato" : "disattivato") + ".");
            return true;
        } catch (Exception e) {
            System.out.println("Errore aggiornamento stato utente: " + e.getMessage());
            return false;
        }
    }

    // -------------------------------------------------------
    // DELETE: elimina utente per id
    // -------------------------------------------------------
    public boolean elimina(int id) {
        try {
            String query = "DELETE FROM utenti WHERE id = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, id);
            stmt.executeUpdate();
            System.out.println("Utente eliminato.");
            return true;
        } catch (Exception e) {
            System.out.println("Errore eliminazione utente: " + e.getMessage());
            return false;
        }
    }

    // -------------------------------------------------------
    // UTILITY PRIVATA: costruisce un Utente dal ResultSet
    // -------------------------------------------------------
    private Utente estraiUtente(ResultSet rs) throws SQLException {
        return new Utente(
                rs.getInt("id"),
                rs.getString("username"),
                rs.getString("password"),
                rs.getString("tipo_account"),
                rs.getBoolean("is_superadmin"),
                rs.getBoolean("attivo"));
    }
}
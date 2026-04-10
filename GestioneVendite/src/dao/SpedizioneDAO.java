package dao;

import db.DataBaseManager;
import entity.Spedizione;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SpedizioneDAO {

    private Connection connection;

    public SpedizioneDAO() {
        this.connection = DataBaseManager.getIstanzaDb().getConnection();
    }

    // -------------------------------------------------------
    // CREATE: inserisce una nuova spedizione
    // -------------------------------------------------------
    public boolean inserisci(Spedizione spedizione) {
        try {
            String query = "INSERT INTO spedizioni (vendita_id, tipo_id, indirizzo, stato, data_prevista) " +
                    "VALUES (?, ?, ?, ?, ?)";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, spedizione.getVenditaId());
            stmt.setInt(2, spedizione.getTipoId());
            stmt.setString(3, spedizione.getIndirizzo());
            stmt.setString(4, spedizione.getStato());
            stmt.setDate(5, spedizione.getDataPrevista() != null
                    ? Date.valueOf(spedizione.getDataPrevista())
                    : null);
            stmt.executeUpdate();
            System.out.println("Spedizione creata per vendita id: " + spedizione.getVenditaId());
            return true;
        } catch (Exception e) {
            System.out.println("Errore inserimento spedizione: " + e.getMessage());
            return false;
        }
    }

    // -------------------------------------------------------
    // READ: cerca spedizione per id vendita
    // -------------------------------------------------------
    public Spedizione cercaPerVendita(int venditaId) {
        try {
            String query = "SELECT * FROM spedizioni WHERE vendita_id = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, venditaId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return estraiSpedizione(rs);
            }
        } catch (Exception e) {
            System.out.println("Errore ricerca spedizione: " + e.getMessage());
        }
        return null;
    }

    // -------------------------------------------------------
    // READ: tutte le spedizioni per stato
    // -------------------------------------------------------
    public List<Spedizione> spedizioniPerStato(String stato) {
        List<Spedizione> lista = new ArrayList<>();
        try {
            String query = "SELECT * FROM spedizioni WHERE stato = ? ORDER BY data_prevista";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, stato);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                lista.add(estraiSpedizione(rs));
            }
        } catch (Exception e) {
            System.out.println("Errore recupero spedizioni per stato: " + e.getMessage());
        }
        return lista;
    }

    // -------------------------------------------------------
    // READ: tutte le spedizioni
    // -------------------------------------------------------
    public List<Spedizione> tutteLeSpedizioni() {
        List<Spedizione> lista = new ArrayList<>();
        try {
            String query = "SELECT * FROM spedizioni ORDER BY data_prevista";
            PreparedStatement stmt = connection.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                lista.add(estraiSpedizione(rs));
            }
        } catch (Exception e) {
            System.out.println("Errore recupero spedizioni: " + e.getMessage());
        }
        return lista;
    }

    // -------------------------------------------------------
    // UPDATE: aggiorna stato spedizione
    // -------------------------------------------------------
    public boolean aggiornaStato(int venditaId, String nuovoStato) {
        try {
            String query = "UPDATE spedizioni SET stato = ? WHERE vendita_id = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, nuovoStato);
            stmt.setInt(2, venditaId);
            stmt.executeUpdate();
            System.out.println("Stato spedizione aggiornato a " + nuovoStato + ".");
            return true;
        } catch (Exception e) {
            System.out.println("Errore aggiornamento stato spedizione: " + e.getMessage());
            return false;
        }
    }

    // -------------------------------------------------------
    // DELETE: elimina spedizione per id vendita
    // -------------------------------------------------------
    public boolean elimina(int venditaId) {
        try {
            String query = "DELETE FROM spedizioni WHERE vendita_id = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, venditaId);
            stmt.executeUpdate();
            System.out.println("Spedizione eliminata.");
            return true;
        } catch (Exception e) {
            System.out.println("Errore eliminazione spedizione: " + e.getMessage());
            return false;
        }
    }

    // -------------------------------------------------------
    // UTILITY PRIVATA: costruisce una Spedizione dal ResultSet
    // -------------------------------------------------------
    private Spedizione estraiSpedizione(ResultSet rs) throws SQLException {
        Date dataPrevista = rs.getDate("data_prevista");
        return new Spedizione(
                rs.getInt("id"),
                rs.getInt("vendita_id"),
                rs.getInt("tipo_id"),
                rs.getString("indirizzo"),
                rs.getString("stato"),
                dataPrevista != null ? dataPrevista.toLocalDate() : null);
    }
}
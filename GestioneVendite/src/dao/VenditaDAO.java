package dao;

import db.DataBaseManager;
import entity.DettaglioVendita;
import entity.Vendita;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VenditaDAO {

    private Connection connection;

    public VenditaDAO() {
        this.connection = DataBaseManager.getIstanzaDb().getConnection();
    }

    // -------------------------------------------------------
    // CREATE: inserisce una nuova vendita, restituisce l'id generato
    // -------------------------------------------------------
    public int inserisci(Vendita vendita) {
        try {
            String query = "INSERT INTO vendite (utente_id, reparto_id, data, totale, stato) " +
                    "VALUES (?, ?, NOW(), ?, ?)";
            PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, vendita.getUtenteId());
            stmt.setInt(2, vendita.getRepartoId());
            stmt.setDouble(3, vendita.getTotale());
            stmt.setString(4, vendita.getStato());
            stmt.executeUpdate();
            ResultSet chiavi = stmt.getGeneratedKeys();
            if (chiavi.next()) {
                int idGenerato = chiavi.getInt(1);
                System.out.println("Vendita creata con id: " + idGenerato);
                return idGenerato;
            }
        } catch (Exception e) {
            System.out.println("Errore inserimento vendita: " + e.getMessage());
        }
        return -1;
    }

    // -------------------------------------------------------
    // CREATE: inserisce una riga di dettaglio
    // -------------------------------------------------------
    public boolean inserisciDettaglio(DettaglioVendita dettaglio) {
        try {
            String query = "INSERT INTO dettaglio_vendita (vendita_id, prodotto_id, quantita, prezzo_unitario) " +
                    "VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, dettaglio.getVenditaId());
            stmt.setInt(2, dettaglio.getProdottoId());
            stmt.setInt(3, dettaglio.getQuantita());
            stmt.setDouble(4, dettaglio.getPrezzoUnitario());
            stmt.executeUpdate();
            return true;
        } catch (Exception e) {
            System.out.println("Errore inserimento dettaglio: " + e.getMessage());
            return false;
        }
    }

    // -------------------------------------------------------
    // READ: cerca vendita per id
    // -------------------------------------------------------
    public Vendita cercaPerId(int id) {
        try {
            String query = "SELECT * FROM vendite WHERE id = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return estraiVendita(rs);
            }
        } catch (Exception e) {
            System.out.println("Errore ricerca vendita: " + e.getMessage());
        }
        return null;
    }

    // -------------------------------------------------------
    // READ: vendite di un utente specifico
    // -------------------------------------------------------
    public List<Vendita> venditePerUtente(int utenteId) {
        List<Vendita> lista = new ArrayList<>();
        try {
            String query = "SELECT * FROM vendite WHERE utente_id = ? ORDER BY data DESC";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, utenteId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                lista.add(estraiVendita(rs));
            }
        } catch (Exception e) {
            System.out.println("Errore recupero vendite utente: " + e.getMessage());
        }
        return lista;
    }

    // -------------------------------------------------------
    // READ: vendite di un reparto specifico
    // -------------------------------------------------------
    public List<Vendita> venditePerReparto(int repartoId) {
        List<Vendita> lista = new ArrayList<>();
        try {
            String query = "SELECT * FROM vendite WHERE reparto_id = ? ORDER BY data DESC";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, repartoId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                lista.add(estraiVendita(rs));
            }
        } catch (Exception e) {
            System.out.println("Errore recupero vendite reparto: " + e.getMessage());
        }
        return lista;
    }

    // -------------------------------------------------------
    // READ: dettagli di una vendita
    // -------------------------------------------------------
    public List<DettaglioVendita> dettagliVendita(int venditaId) {
        List<DettaglioVendita> lista = new ArrayList<>();
        try {
            String query = "SELECT * FROM dettaglio_vendita WHERE vendita_id = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, venditaId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                lista.add(new DettaglioVendita(
                        rs.getInt("id"),
                        rs.getInt("vendita_id"),
                        rs.getInt("prodotto_id"),
                        rs.getInt("quantita"),
                        rs.getDouble("prezzo_unitario")));
            }
        } catch (Exception e) {
            System.out.println("Errore recupero dettagli vendita: " + e.getMessage());
        }
        return lista;
    }

    // -------------------------------------------------------
    // UPDATE: aggiorna stato vendita
    // -------------------------------------------------------
    public boolean aggiornaStato(int id, String nuovoStato) {
        try {
            String query = "UPDATE vendite SET stato = ? WHERE id = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, nuovoStato);
            stmt.setInt(2, id);
            stmt.executeUpdate();
            System.out.println("Stato vendita aggiornato a " + nuovoStato + ".");
            return true;
        } catch (Exception e) {
            System.out.println("Errore aggiornamento stato vendita: " + e.getMessage());
            return false;
        }
    }

    // -------------------------------------------------------
    // UPDATE: aggiorna il totale di una vendita
    // -------------------------------------------------------
    public boolean aggiornaTotale(int id, double nuovoTotale) {
        try {
            String query = "UPDATE vendite SET totale = ? WHERE id = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setDouble(1, nuovoTotale);
            stmt.setInt(2, id);
            stmt.executeUpdate();
            return true;
        } catch (Exception e) {
            System.out.println("Errore aggiornamento totale: " + e.getMessage());
            return false;
        }
    }

    // -------------------------------------------------------
    // DELETE: elimina vendita per id (cascade sui dettagli)
    // -------------------------------------------------------
    public boolean elimina(int id) {
        try {
            String query = "DELETE FROM vendite WHERE id = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, id);
            stmt.executeUpdate();
            System.out.println("Vendita eliminata.");
            return true;
        } catch (Exception e) {
            System.out.println("Errore eliminazione vendita: " + e.getMessage());
            return false;
        }
    }

    // -------------------------------------------------------
    // UTILITY PRIVATA: costruisce una Vendita dal ResultSet
    // -------------------------------------------------------
    private Vendita estraiVendita(ResultSet rs) throws SQLException {
        return new Vendita(
                rs.getInt("id"),
                rs.getInt("utente_id"),
                rs.getInt("reparto_id"),
                rs.getTimestamp("data").toLocalDateTime(),
                rs.getDouble("totale"),
                rs.getString("stato"));
    }
}
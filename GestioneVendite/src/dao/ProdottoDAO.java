package dao;

import db.DataBaseManager;
import entity.Prodotto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProdottoDAO {

    private Connection connection;

    public ProdottoDAO() {
        this.connection = DataBaseManager.getIstanzaDb().getConnection();
    }

    // -------------------------------------------------------
    // CREATE: inserisce un nuovo prodotto
    // -------------------------------------------------------
    public boolean inserisci(Prodotto prodotto) {
        try {
            String query = "INSERT INTO prodotti (nome, prezzo, categoria_id, reparto_id, disponibilita) " +
                    "VALUES (?, ?, ?, ?, ?)";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, prodotto.getNome());
            stmt.setDouble(2, prodotto.getPrezzo());
            stmt.setInt(3, prodotto.getCategoriaId());
            stmt.setInt(4, prodotto.getRepartoId());
            stmt.setInt(5, prodotto.getDisponibilita());
            stmt.executeUpdate();
            System.out.println("Prodotto '" + prodotto.getNome() + "' inserito con successo.");
            return true;
        } catch (Exception e) {
            System.out.println("Errore inserimento prodotto: " + e.getMessage());
            return false;
        }
    }

    // -------------------------------------------------------
    // READ: cerca prodotto per id
    // -------------------------------------------------------
    public Prodotto cercaPerId(int id) {
        try {
            String query = "SELECT * FROM prodotti WHERE id = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return estraiProdotto(rs);
            }
        } catch (Exception e) {
            System.out.println("Errore ricerca prodotto: " + e.getMessage());
        }
        return null;
    }

    // -------------------------------------------------------
    // READ: restituisce tutti i prodotti
    // -------------------------------------------------------
    public List<Prodotto> tuttiIProdotti() {
        List<Prodotto> lista = new ArrayList<>();
        try {
            String query = "SELECT * FROM prodotti ORDER BY nome";
            PreparedStatement stmt = connection.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                lista.add(estraiProdotto(rs));
            }
        } catch (Exception e) {
            System.out.println("Errore recupero prodotti: " + e.getMessage());
        }
        return lista;
    }

    // -------------------------------------------------------
    // READ: prodotti di un reparto specifico
    // -------------------------------------------------------
    public List<Prodotto> prodottiPerReparto(int repartoId) {
        List<Prodotto> lista = new ArrayList<>();
        try {
            String query = "SELECT * FROM prodotti WHERE reparto_id = ? ORDER BY nome";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, repartoId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                lista.add(estraiProdotto(rs));
            }
        } catch (Exception e) {
            System.out.println("Errore recupero prodotti per reparto: " + e.getMessage());
        }
        return lista;
    }

    // -------------------------------------------------------
    // READ: prodotti di una categoria specifica
    // -------------------------------------------------------
    public List<Prodotto> prodottiPerCategoria(int categoriaId) {
        List<Prodotto> lista = new ArrayList<>();
        try {
            String query = "SELECT * FROM prodotti WHERE categoria_id = ? ORDER BY nome";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, categoriaId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                lista.add(estraiProdotto(rs));
            }
        } catch (Exception e) {
            System.out.println("Errore recupero prodotti per categoria: " + e.getMessage());
        }
        return lista;
    }

    // -------------------------------------------------------
    // UPDATE: aggiorna i dati di un prodotto
    // -------------------------------------------------------
    public boolean aggiorna(Prodotto prodotto) {
        try {
            String query = "UPDATE prodotti SET nome = ?, prezzo = ?, categoria_id = ?, " +
                    "reparto_id = ?, disponibilita = ? WHERE id = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, prodotto.getNome());
            stmt.setDouble(2, prodotto.getPrezzo());
            stmt.setInt(3, prodotto.getCategoriaId());
            stmt.setInt(4, prodotto.getRepartoId());
            stmt.setInt(5, prodotto.getDisponibilita());
            stmt.setInt(6, prodotto.getId());
            stmt.executeUpdate();
            System.out.println("Prodotto aggiornato.");
            return true;
        } catch (Exception e) {
            System.out.println("Errore aggiornamento prodotto: " + e.getMessage());
            return false;
        }
    }

    // -------------------------------------------------------
    // UPDATE: aggiorna solo la disponibilità (usato dall'Observer)
    // -------------------------------------------------------
    public boolean aggiornaDisponibilita(int id, int nuovaDisponibilita) {
        try {
            String query = "UPDATE prodotti SET disponibilita = ? WHERE id = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, nuovaDisponibilita);
            stmt.setInt(2, id);
            stmt.executeUpdate();
            System.out.println("Disponibilità aggiornata a " + nuovaDisponibilita + ".");
            return true;
        } catch (Exception e) {
            System.out.println("Errore aggiornamento disponibilità: " + e.getMessage());
            return false;
        }
    }

    // -------------------------------------------------------
    // DELETE: elimina prodotto per id
    // -------------------------------------------------------
    public boolean elimina(int id) {
        try {
            String query = "DELETE FROM prodotti WHERE id = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, id);
            stmt.executeUpdate();
            System.out.println("Prodotto eliminato.");
            return true;
        } catch (Exception e) {
            System.out.println("Errore eliminazione prodotto: " + e.getMessage());
            return false;
        }
    }

    // -------------------------------------------------------
    // UTILITY PRIVATA: costruisce un Prodotto dal ResultSet
    // -------------------------------------------------------
    private Prodotto estraiProdotto(ResultSet rs) throws SQLException {
        return new Prodotto(
                rs.getInt("id"),
                rs.getString("nome"),
                rs.getDouble("prezzo"),
                rs.getInt("categoria_id"),
                rs.getInt("reparto_id"),
                rs.getInt("disponibilita"));
    }
}
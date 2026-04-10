package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class DataBaseManager {

        private static DataBaseManager istanzaDb;
        private int connectionCount = 0;
        private Connection connection;

        // prima ci connettiamo senza specificare il db, così lo possiamo creare se non
        // esiste
        private static final String URL_BASE = "jdbc:mysql://localhost:3306/";
        private static final String URL = "jdbc:mysql://localhost:3306/gestionale_vendite";
        private static final String USER = "root";
        private static final String PASSWORD = "";

        private DataBaseManager() {
        }

        // restituisce sempre la stessa istanza, la crea solo la prima volta
        public static DataBaseManager getIstanzaDb() {
                if (istanzaDb == null) {
                        istanzaDb = new DataBaseManager();
                }
                return istanzaDb;
        }

        // apre la connessione, crea il db e le tabelle se non esistono
        public void connect() {
                try {
                        // prima connessione senza db per poterlo creare
                        Connection connBase = DriverManager.getConnection(URL_BASE, USER, PASSWORD);
                        Statement stmtBase = connBase.createStatement();
                        stmtBase.executeUpdate("CREATE DATABASE IF NOT EXISTS gestionale_vendite " +
                                        "CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci");
                        connBase.close();
                        System.out.println("Database verificato.");

                        // connessione reale al db
                        connection = DriverManager.getConnection(URL, USER, PASSWORD);
                        connectionCount++;
                        System.out.println("Connessione stabilita. Connessioni totali: " + connectionCount);

                        // crea tabelle e dati iniziali
                        inizializzaSchema();

                } catch (Exception e) {
                        System.out.println("Errore di connessione: " + e.getMessage());
                }
        }

        // -------------------------------------------------------
        // CREA TABELLE E DATI INIZIALI
        // -------------------------------------------------------
        private void inizializzaSchema() {
                try {
                        Statement stmt = connection.createStatement();

                        // REPARTI
                        stmt.executeUpdate(
                                        "CREATE TABLE IF NOT EXISTS reparti (" +
                                                        "  id          INT PRIMARY KEY AUTO_INCREMENT," +
                                                        "  nome        VARCHAR(50)  NOT NULL UNIQUE," +
                                                        "  descrizione VARCHAR(200)" +
                                                        ")");

                        // UTENTI
                        stmt.executeUpdate(
                                        "CREATE TABLE IF NOT EXISTS utenti (" +
                                                        "  id            INT PRIMARY KEY AUTO_INCREMENT," +
                                                        "  username      VARCHAR(50)  NOT NULL UNIQUE," +
                                                        "  password      VARCHAR(255) NOT NULL," +
                                                        "  tipo_account  ENUM('NORMAL','PRO') NOT NULL DEFAULT 'NORMAL',"
                                                        +
                                                        "  is_superadmin BOOLEAN NOT NULL DEFAULT FALSE," +
                                                        "  attivo        BOOLEAN NOT NULL DEFAULT TRUE" +
                                                        ")");

                        // UTENTE_REPARTO
                        stmt.executeUpdate(
                                        "CREATE TABLE IF NOT EXISTS utente_reparto (" +
                                                        "  id         INT PRIMARY KEY AUTO_INCREMENT," +
                                                        "  utente_id  INT NOT NULL," +
                                                        "  reparto_id INT NOT NULL," +
                                                        "  ruolo      ENUM('USER','ADMIN') NOT NULL DEFAULT 'USER'," +
                                                        "  FOREIGN KEY (utente_id)  REFERENCES utenti(id)  ON DELETE CASCADE,"
                                                        +
                                                        "  FOREIGN KEY (reparto_id) REFERENCES reparti(id) ON DELETE CASCADE,"
                                                        +
                                                        "  UNIQUE KEY uq_utente_reparto (utente_id, reparto_id)" +
                                                        ")");

                        // CATEGORIE PRODOTTO
                        stmt.executeUpdate(
                                        "CREATE TABLE IF NOT EXISTS categorie_prodotto (" +
                                                        "  id   INT PRIMARY KEY AUTO_INCREMENT," +
                                                        "  nome VARCHAR(50) NOT NULL UNIQUE" +
                                                        ")");

                        // PRODOTTI
                        stmt.executeUpdate(
                                        "CREATE TABLE IF NOT EXISTS prodotti (" +
                                                        "  id            INT PRIMARY KEY AUTO_INCREMENT," +
                                                        "  nome          VARCHAR(100)  NOT NULL," +
                                                        "  prezzo        DECIMAL(10,2) NOT NULL," +
                                                        "  categoria_id  INT NOT NULL," +
                                                        "  reparto_id    INT NOT NULL," +
                                                        "  disponibilita INT NOT NULL DEFAULT 0," +
                                                        "  FOREIGN KEY (categoria_id) REFERENCES categorie_prodotto(id),"
                                                        +
                                                        "  FOREIGN KEY (reparto_id)   REFERENCES reparti(id)" +
                                                        ")");

                        // TIPI SPEDIZIONE
                        stmt.executeUpdate(
                                        "CREATE TABLE IF NOT EXISTS tipi_spedizione (" +
                                                        "  id         INT PRIMARY KEY AUTO_INCREMENT," +
                                                        "  nome       VARCHAR(50)   NOT NULL UNIQUE," +
                                                        "  costo_base DECIMAL(10,2) NOT NULL DEFAULT 0.00" +
                                                        ")");

                        // VENDITE
                        stmt.executeUpdate(
                                        "CREATE TABLE IF NOT EXISTS vendite (" +
                                                        "  id         INT PRIMARY KEY AUTO_INCREMENT," +
                                                        "  utente_id  INT NOT NULL," +
                                                        "  reparto_id INT NOT NULL," +
                                                        "  data       DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,"
                                                        +
                                                        "  totale     DECIMAL(10,2) NOT NULL DEFAULT 0.00," +
                                                        "  stato      ENUM('APERTA','CHIUSA','ANNULLATA') NOT NULL DEFAULT 'APERTA',"
                                                        +
                                                        "  FOREIGN KEY (utente_id)  REFERENCES utenti(id)," +
                                                        "  FOREIGN KEY (reparto_id) REFERENCES reparti(id)" +
                                                        ")");

                        // DETTAGLIO VENDITA
                        stmt.executeUpdate(
                                        "CREATE TABLE IF NOT EXISTS dettaglio_vendita (" +
                                                        "  id              INT PRIMARY KEY AUTO_INCREMENT," +
                                                        "  vendita_id      INT NOT NULL," +
                                                        "  prodotto_id     INT NOT NULL," +
                                                        "  quantita        INT           NOT NULL DEFAULT 1," +
                                                        "  prezzo_unitario DECIMAL(10,2) NOT NULL," +
                                                        "  FOREIGN KEY (vendita_id)  REFERENCES vendite(id)  ON DELETE CASCADE,"
                                                        +
                                                        "  FOREIGN KEY (prodotto_id) REFERENCES prodotti(id)" +
                                                        ")");

                        // SPEDIZIONI
                        stmt.executeUpdate(
                                        "CREATE TABLE IF NOT EXISTS spedizioni (" +
                                                        "  id            INT PRIMARY KEY AUTO_INCREMENT," +
                                                        "  vendita_id    INT NOT NULL UNIQUE," +
                                                        "  tipo_id       INT NOT NULL," +
                                                        "  indirizzo     VARCHAR(200) NOT NULL," +
                                                        "  stato         ENUM('IN_ATTESA','SPEDITO','CONSEGNATO') NOT NULL DEFAULT 'IN_ATTESA',"
                                                        +
                                                        "  data_prevista DATE," +
                                                        "  FOREIGN KEY (vendita_id) REFERENCES vendite(id)  ON DELETE CASCADE,"
                                                        +
                                                        "  FOREIGN KEY (tipo_id)    REFERENCES tipi_spedizione(id)" +
                                                        ")");

                        System.out.println("Schema verificato.");

                        // DATI INIZIALI (solo se le tabelle sono vuote)
                        inserisciDatiIniziali();

                } catch (Exception e) {
                        System.out.println("Errore inizializzazione schema: " + e.getMessage());
                }
        }

        // -------------------------------------------------------
        // DATI INIZIALI
        // inserisce reparti, categorie, tipi spedizione e superadmin
        // solo se non esistono già
        // -------------------------------------------------------
        private void inserisciDatiIniziali() {
                try {
                        Statement stmt = connection.createStatement();

                        // reparti
                        stmt.executeUpdate(
                                        "INSERT IGNORE INTO reparti (nome, descrizione) VALUES " +
                                                        "('Vendite',    'Gestione delle vendite e degli ordini')," +
                                                        "('Prodotti',   'Gestione del catalogo prodotti')," +
                                                        "('Spedizioni', 'Gestione delle spedizioni e logistica')");

                        // categorie prodotto
                        stmt.executeUpdate(
                                        "INSERT IGNORE INTO categorie_prodotto (nome) VALUES " +
                                                        "('Elettronica')," +
                                                        "('Abbigliamento')," +
                                                        "('Alimentari')");

                        // tipi spedizione
                        stmt.executeUpdate(
                                        "INSERT IGNORE INTO tipi_spedizione (nome, costo_base) VALUES " +
                                                        "('Standard',  4.99)," +
                                                        "('Express',   9.99)," +
                                                        "('Same-day', 19.99)");

                        // utenti
                        // superadmin
                        stmt.executeUpdate(
                                        "INSERT IGNORE INTO utenti (username, password, tipo_account, is_superadmin, attivo) "
                                                        +
                                                        "VALUES ('superadmin', 'superadmin123', 'PRO', TRUE, TRUE)");
                        // admin reparto vendite
                        stmt.executeUpdate(
                                        "INSERT IGNORE INTO utenti (username, password, tipo_account, is_superadmin, attivo) "
                                                        +
                                                        "VALUES ('admin_vendite', 'admin123', 'PRO', FALSE, TRUE)");
                        // utente PRO reparto prodotti
                        stmt.executeUpdate(
                                        "INSERT IGNORE INTO utenti (username, password, tipo_account, is_superadmin, attivo) "
                                                        +
                                                        "VALUES ('mario_pro', 'mario123', 'PRO', FALSE, TRUE)");
                        // utente NORMAL reparto vendite
                        stmt.executeUpdate(
                                        "INSERT IGNORE INTO utenti (username, password, tipo_account, is_superadmin, attivo) "
                                                        +
                                                        "VALUES ('luigi_normal', 'luigi123', 'NORMAL', FALSE, TRUE)");

                        // associazioni utente-reparto
                        // admin_vendite → reparto Vendite (id=1) come ADMIN
                        stmt.executeUpdate(
                                        "INSERT IGNORE INTO utente_reparto (utente_id, reparto_id, ruolo) " +
                                                        "SELECT u.id, r.id, 'ADMIN' FROM utenti u, reparti r " +
                                                        "WHERE u.username = 'admin_vendite' AND r.nome = 'Vendite'");
                        // mario_pro → reparto Prodotti (id=2) come USER
                        stmt.executeUpdate(
                                        "INSERT IGNORE INTO utente_reparto (utente_id, reparto_id, ruolo) " +
                                                        "SELECT u.id, r.id, 'USER' FROM utenti u, reparti r " +
                                                        "WHERE u.username = 'mario_pro' AND r.nome = 'Prodotti'");
                        // luigi_normal → reparto Vendite (id=1) come USER
                        stmt.executeUpdate(
                                        "INSERT IGNORE INTO utente_reparto (utente_id, reparto_id, ruolo) " +
                                                        "SELECT u.id, r.id, 'USER' FROM utenti u, reparti r " +
                                                        "WHERE u.username = 'luigi_normal' AND r.nome = 'Vendite'");

                        // prodotti
                        stmt.executeUpdate(
                                        "INSERT IGNORE INTO prodotti (nome, prezzo, categoria_id, reparto_id, disponibilita) "
                                                        +
                                                        "SELECT 'Smartphone XR', 499.99, c.id, r.id, 20 " +
                                                        "FROM categorie_prodotto c, reparti r " +
                                                        "WHERE c.nome = 'Elettronica' AND r.nome = 'Prodotti'");
                        stmt.executeUpdate(
                                        "INSERT IGNORE INTO prodotti (nome, prezzo, categoria_id, reparto_id, disponibilita) "
                                                        +
                                                        "SELECT 'Laptop Pro 15', 1299.99, c.id, r.id, 10 " +
                                                        "FROM categorie_prodotto c, reparti r " +
                                                        "WHERE c.nome = 'Elettronica' AND r.nome = 'Prodotti'");
                        stmt.executeUpdate(
                                        "INSERT IGNORE INTO prodotti (nome, prezzo, categoria_id, reparto_id, disponibilita) "
                                                        +
                                                        "SELECT 'Giacca Invernale', 89.99, c.id, r.id, 35 " +
                                                        "FROM categorie_prodotto c, reparti r " +
                                                        "WHERE c.nome = 'Abbigliamento' AND r.nome = 'Prodotti'");
                        stmt.executeUpdate(
                                        "INSERT IGNORE INTO prodotti (nome, prezzo, categoria_id, reparto_id, disponibilita) "
                                                        +
                                                        "SELECT 'Pasta 500g', 1.49, c.id, r.id, 200 " +
                                                        "FROM categorie_prodotto c, reparti r " +
                                                        "WHERE c.nome = 'Alimentari' AND r.nome = 'Prodotti'");

                        // vendite di esempio (solo se non esistono già)
                        ResultSet rsVendite = stmt.executeQuery("SELECT COUNT(*) AS tot FROM vendite");
                        if (rsVendite.next() && rsVendite.getInt("tot") == 0) {

                                // vendita 1 — chiusa, reparto Vendite, utente admin_vendite
                                stmt.executeUpdate(
                                                "INSERT INTO vendite (utente_id, reparto_id, data, totale, stato) " +
                                                                "SELECT u.id, r.id, '2024-01-10 10:30:00', 1799.98, 'CHIUSA' "
                                                                +
                                                                "FROM utenti u, reparti r " +
                                                                "WHERE u.username = 'admin_vendite' AND r.nome = 'Vendite'");
                                // dettaglio vendita 1: Smartphone + Laptop
                                stmt.executeUpdate(
                                                "INSERT INTO dettaglio_vendita (vendita_id, prodotto_id, quantita, prezzo_unitario) "
                                                                +
                                                                "SELECT v.id, p.id, 1, p.prezzo FROM vendite v, prodotti p "
                                                                +
                                                                "WHERE v.totale = 1799.98 AND p.nome = 'Smartphone XR'");
                                stmt.executeUpdate(
                                                "INSERT INTO dettaglio_vendita (vendita_id, prodotto_id, quantita, prezzo_unitario) "
                                                                +
                                                                "SELECT v.id, p.id, 1, p.prezzo FROM vendite v, prodotti p "
                                                                +
                                                                "WHERE v.totale = 1799.98 AND p.nome = 'Laptop Pro 15'");
                                // spedizione vendita 1
                                stmt.executeUpdate(
                                                "INSERT INTO spedizioni (vendita_id, tipo_id, indirizzo, stato, data_prevista) "
                                                                +
                                                                "SELECT v.id, t.id, 'Via Roma 10, Milano', 'CONSEGNATO', '2024-01-13' "
                                                                +
                                                                "FROM vendite v, tipi_spedizione t " +
                                                                "WHERE v.totale = 1799.98 AND t.nome = 'Express'");

                                // vendita 2 — aperta, reparto Vendite, utente luigi_normal
                                stmt.executeUpdate(
                                                "INSERT INTO vendite (utente_id, reparto_id, data, totale, stato) " +
                                                                "SELECT u.id, r.id, '2024-02-05 14:00:00', 179.98, 'APERTA' "
                                                                +
                                                                "FROM utenti u, reparti r " +
                                                                "WHERE u.username = 'luigi_normal' AND r.nome = 'Vendite'");
                                // dettaglio vendita 2: 2 Giacche
                                stmt.executeUpdate(
                                                "INSERT INTO dettaglio_vendita (vendita_id, prodotto_id, quantita, prezzo_unitario) "
                                                                +
                                                                "SELECT v.id, p.id, 2, p.prezzo FROM vendite v, prodotti p "
                                                                +
                                                                "WHERE v.totale = 179.98 AND p.nome = 'Giacca Invernale'");
                                // spedizione vendita 2
                                stmt.executeUpdate(
                                                "INSERT INTO spedizioni (vendita_id, tipo_id, indirizzo, stato, data_prevista) "
                                                                +
                                                                "SELECT v.id, t.id, 'Via Napoli 5, Roma', 'IN_ATTESA', '2024-02-10' "
                                                                +
                                                                "FROM vendite v, tipi_spedizione t " +
                                                                "WHERE v.totale = 179.98 AND t.nome = 'Standard'");

                                // vendita 3 — aperta, reparto Vendite, utente admin_vendite
                                stmt.executeUpdate(
                                                "INSERT INTO vendite (utente_id, reparto_id, data, totale, stato) " +
                                                                "SELECT u.id, r.id, '2024-03-01 09:15:00', 5.96, 'APERTA' "
                                                                +
                                                                "FROM utenti u, reparti r " +
                                                                "WHERE u.username = 'admin_vendite' AND r.nome = 'Vendite'");
                                // dettaglio vendita 3: 4 Pasta
                                stmt.executeUpdate(
                                                "INSERT INTO dettaglio_vendita (vendita_id, prodotto_id, quantita, prezzo_unitario) "
                                                                +
                                                                "SELECT v.id, p.id, 4, p.prezzo FROM vendite v, prodotti p "
                                                                +
                                                                "WHERE v.totale = 5.96 AND p.nome = 'Pasta 500g'");
                                // spedizione vendita 3
                                stmt.executeUpdate(
                                                "INSERT INTO spedizioni (vendita_id, tipo_id, indirizzo, stato, data_prevista) "
                                                                +
                                                                "SELECT v.id, t.id, 'Corso Torino 22, Torino', 'SPEDITO', '2024-03-03' "
                                                                +
                                                                "FROM vendite v, tipi_spedizione t " +
                                                                "WHERE v.totale = 5.96 AND t.nome = 'Standard'");
                        }

                        System.out.println("Dati iniziali verificati.");

                } catch (Exception e) {
                        System.out.println("Errore inserimento dati iniziali: " + e.getMessage());
                }
        }

        // restituisce la connessione ai DAO che ne hanno bisogno
        public Connection getConnection() {
                return connection;
        }

        // restituisce il numero totale di connessioni effettuate
        public int getConnectionCount() {
                return connectionCount;
        }

        // chiude la connessione quando il programma termina
        public void disconnect() {
                try {
                        if (connection != null && !connection.isClosed()) {
                                connection.close();
                                System.out.println("Connessione chiusa.");
                        }
                } catch (Exception e) {
                        System.out.println("Errore durante la chiusura: " + e.getMessage());
                }
        }

        // -------------------------------------------------------
        // LOGIN: cerca utente per username e password
        // -------------------------------------------------------
        public boolean login(String username, String password) {
                try {
                        String query = "SELECT * FROM utenti WHERE username = ? AND password = ? AND attivo = TRUE";
                        PreparedStatement stmt = connection.prepareStatement(query);
                        stmt.setString(1, username);
                        stmt.setString(2, password);
                        ResultSet rs = stmt.executeQuery();
                        if (rs.next()) {
                                System.out.println("Login effettuato. Benvenuto, " + rs.getString("username") + "!");
                                return true;
                        } else {
                                System.out.println("Credenziali errate o utente non attivo.");
                                return false;
                        }
                } catch (Exception e) {
                        System.out.println("Errore durante il login: " + e.getMessage());
                        return false;
                }
        }

        // -------------------------------------------------------
        // TEST CONNESSIONE: verifica che il db risponda
        // -------------------------------------------------------
        public void testConnessione() {
                try {
                        String query = "SELECT COUNT(*) AS totale FROM utenti";
                        PreparedStatement stmt = connection.prepareStatement(query);
                        ResultSet rs = stmt.executeQuery();
                        if (rs.next()) {
                                System.out.println("DB raggiungibile. Utenti presenti: " + rs.getInt("totale"));
                        }
                } catch (Exception e) {
                        System.out.println("Errore nel test: " + e.getMessage());
                }
        }
}
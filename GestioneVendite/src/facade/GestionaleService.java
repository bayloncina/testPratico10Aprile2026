package facade;

import java.util.ArrayList;
import java.util.List;

import dao.*;
import db.DataBaseManager;
import decorator.*;
import entity.*;
import observer.*;
import strategy.*;

public class GestionaleService {

    // -------------------------------------------------------
    // SINGLETON
    // -------------------------------------------------------
    private static GestionaleService istanza;

    public static GestionaleService getIstanza() {
        if (istanza == null) {
            istanza = new GestionaleService();
        }
        return istanza;
    }

    // -------------------------------------------------------
    // STATO SESSIONE
    // utente loggato e il suo decorator (NORMAL o PRO)
    // -------------------------------------------------------
    private Utente utenteCorrente;
    private UtenteComponent decoratorCorrente;

    // -------------------------------------------------------
    // DAO
    // -------------------------------------------------------
    private UtenteDAO utenteDAO;
    private RepartoDAO repartoDAO;
    private UtenteRepartoDAO utenteRepartoDAO;
    private ProdottoDAO prodottoDAO;
    private VenditaDAO venditaDAO;
    private SpedizioneDAO spedizioneDAO;

    // -------------------------------------------------------
    // STRATEGY
    // -------------------------------------------------------
    private PermessoStrategy permessoVendite;
    private PermessoStrategy permessoProdotti;
    private PermessoStrategy permessoSpedizioni;
    private PermessoStrategy permessoAdmin;

    // -------------------------------------------------------
    // OBSERVER: lista degli ascoltatori sugli eventi vendita
    // -------------------------------------------------------
    private List<VenditaObserver> observers;

    // -------------------------------------------------------
    // COSTRUTTORE PRIVATO
    // si occupa anche di aprire la connessione al DB
    // -------------------------------------------------------
    private GestionaleService() {
        // apre la connessione e inizializza schema + dati
        DataBaseManager.getIstanzaDb().connect();

        // inizializza DAO
        utenteDAO = new UtenteDAO();
        repartoDAO = new RepartoDAO();
        utenteRepartoDAO = new UtenteRepartoDAO();
        prodottoDAO = new ProdottoDAO();
        venditaDAO = new VenditaDAO();
        spedizioneDAO = new SpedizioneDAO();

        // inizializza Strategy
        permessoVendite = new PermessoVendite();
        permessoProdotti = new PermessoProdotti();
        permessoSpedizioni = new PermessoSpedizioni();
        permessoAdmin = new PermessoAdmin();

        // inizializza Observer
        observers = new ArrayList<>();
        observers.add(new LogObserver());
        observers.add(new StockObserver());
        observers.add(new AdminNotifier());
    }

    // chiude la connessione al DB — da chiamare alla fine del main
    public void chiudi() {
        DataBaseManager.getIstanzaDb().disconnect();
    }

    // -------------------------------------------------------
    // NOTIFICA OBSERVER
    // -------------------------------------------------------
    private void notifica(String evento, Vendita vendita) {
        for (VenditaObserver o : observers) {
            o.onVenditaAggiornata(evento, vendita);
        }
    }

    // -------------------------------------------------------
    // LOGIN / LOGOUT
    // -------------------------------------------------------
    public boolean login(String username, String password) {
        Utente u = utenteDAO.login(username, password);
        if (u != null) {
            utenteCorrente = u;
            decoratorCorrente = UtenteDecoratorFactory.crea(u);
            System.out.println("Tipo account: " + decoratorCorrente.getTipoAccount());
            System.out.println(decoratorCorrente.descrizionePermessi());
            return true;
        }
        return false;
    }

    public void logout() {
        System.out.println("Arrivederci, " + utenteCorrente.getUsername() + "!");
        utenteCorrente = null;
        decoratorCorrente = null;
    }

    public Utente getUtenteCorrente() {
        return utenteCorrente;
    }

    public UtenteComponent getDecoratorCorrente() {
        return decoratorCorrente;
    }

    // -------------------------------------------------------
    // GESTIONE UTENTI
    // solo admin del reparto o superadmin
    // -------------------------------------------------------
    public boolean creaUtente(Utente nuovoUtente, int repartoId) {
        if (!permessoAdmin.haPermesso(utenteCorrente, repartoId)) {
            System.out.println("Permesso negato: non puoi creare utenti in questo reparto.");
            return false;
        }
        return utenteDAO.inserisci(nuovoUtente);
    }

    public List<Utente> getUtentiAttivi() {
        return utenteDAO.utentiAttivi();
    }

    // restituisce utenti attivi con reparto e ruolo — usato dalla visualizzazione
    public List<InfoUtenteReparto> getUtentiAttiviConReparto() {
        List<Utente> utenti = utenteDAO.utentiAttivi();
        List<InfoUtenteReparto> lista = new ArrayList<>();
        for (Utente u : utenti) {
            if (u.isSuperAdmin()) {
                lista.add(new InfoUtenteReparto(
                        u.getId(), u.getUsername(), u.getTipoAccount(), true, null, null));
            } else {
                List<UtenteReparto> assoc = utenteRepartoDAO.associazioniDiUtente(u.getId());
                for (UtenteReparto ur : assoc) {
                    Reparto r = repartoDAO.cercaPerId(ur.getRepartoId());
                    if (r != null) {
                        lista.add(new InfoUtenteReparto(
                                u.getId(), u.getUsername(), u.getTipoAccount(),
                                false, r.getNome(), ur.getRuolo()));
                    }
                }
            }
        }
        return lista;
    }

    // restituisce utenti di un reparto con il loro ruolo attuale — usato da
    // cambiaRuolo
    public List<InfoUtenteReparto> getUtentiRepartoConRuolo(int repartoId) {
        List<UtenteReparto> associazioni = utenteRepartoDAO.utentiDelReparto(repartoId);
        List<InfoUtenteReparto> lista = new ArrayList<>();
        Reparto reparto = repartoDAO.cercaPerId(repartoId);
        String nomeReparto = reparto != null ? reparto.getNome() : "N/D";
        for (UtenteReparto ur : associazioni) {
            Utente u = utenteDAO.cercaPerId(ur.getUtenteId());
            if (u != null && u.isAttivo()) {
                lista.add(new InfoUtenteReparto(
                        u.getId(), u.getUsername(), u.getTipoAccount(),
                        false, nomeReparto, ur.getRuolo()));
            }
        }
        return lista;
    }

    public boolean isAdminInAlmenoUnReparto() {
        List<Reparto> reparti = getReparti();
        for (Reparto r : reparti) {
            if (utenteRepartoDAO.isAdminDelReparto(utenteCorrente.getId(), r.getId())) {
                return true;
            }
        }
        return false;
    }

    public boolean disattivaUtente(int utenteId, int repartoId) {
        if (!permessoAdmin.haPermesso(utenteCorrente, repartoId)) {
            System.out.println("Permesso negato: non puoi disattivare utenti in questo reparto.");
            return false;
        }
        return utenteDAO.impostaAttivo(utenteId, false);
    }

    public boolean cambiaRuoloUtente(int utenteId, int repartoId, String nuovoRuolo) {
        if (!permessoAdmin.haPermesso(utenteCorrente, repartoId)) {
            System.out.println("Permesso negato: non puoi cambiare ruoli in questo reparto.");
            return false;
        }
        return utenteRepartoDAO.aggiornaRuolo(utenteId, repartoId, nuovoRuolo);
    }

    // -------------------------------------------------------
    // GESTIONE REPARTI
    // solo superadmin
    // -------------------------------------------------------
    public boolean creaReparto(Reparto reparto) {
        if (!utenteCorrente.isSuperAdmin()) {
            System.out.println("Permesso negato: solo il superadmin può creare reparti.");
            return false;
        }
        return repartoDAO.inserisci(reparto);
    }

    public Reparto getRepartoPerNome(String nome) {
        return repartoDAO.cercaPerNome(nome);
    }

    public List<Reparto> getReparti() {
        if (utenteCorrente.isSuperAdmin()) {
            return repartoDAO.tuttiIReparti();
        }
        return repartoDAO.repartiDiUtente(utenteCorrente.getId());
    }

    // -------------------------------------------------------
    // GESTIONE PRODOTTI
    // -------------------------------------------------------
    public List<Prodotto> getProdottiReparto(int repartoId) {
        if (!permessoProdotti.haPermesso(utenteCorrente, repartoId)) {
            System.out.println("Permesso negato: non puoi vedere i prodotti di questo reparto.");
            return new ArrayList<>();
        }
        return prodottoDAO.prodottiPerReparto(repartoId);
    }

    public boolean creaProdotto(Prodotto prodotto) {
        if (!permessoProdotti.haPermesso(utenteCorrente, prodotto.getRepartoId())) {
            System.out.println("Permesso negato: non puoi aggiungere prodotti in questo reparto.");
            return false;
        }
        return prodottoDAO.inserisci(prodotto);
    }

    public boolean aggiornaProdotto(Prodotto prodotto) {
        if (!permessoProdotti.haPermesso(utenteCorrente, prodotto.getRepartoId())) {
            System.out.println("Permesso negato: non puoi modificare prodotti in questo reparto.");
            return false;
        }
        return prodottoDAO.aggiorna(prodotto);
    }

    public boolean eliminaProdotto(int prodottoId, int repartoId) {
        if (!permessoProdotti.haPermesso(utenteCorrente, repartoId)) {
            System.out.println("Permesso negato: non puoi eliminare prodotti in questo reparto.");
            return false;
        }
        return prodottoDAO.elimina(prodottoId);
    }

    // -------------------------------------------------------
    // GESTIONE VENDITE
    // -------------------------------------------------------
    public List<Vendita> getVenditeReparto(int repartoId) {
        if (!permessoVendite.haPermesso(utenteCorrente, repartoId)) {
            System.out.println("Permesso negato: non puoi vedere le vendite di questo reparto.");
            return new ArrayList<>();
        }
        return venditaDAO.venditePerReparto(repartoId);
    }

    public List<Vendita> getMieVendite() {
        return venditaDAO.venditePerUtente(utenteCorrente.getId());
    }

    public int creaVendita(Vendita vendita, List<DettaglioVendita> dettagli) {
        if (!permessoVendite.haPermesso(utenteCorrente, vendita.getRepartoId())) {
            System.out.println("Permesso negato: non puoi creare vendite in questo reparto.");
            return -1;
        }

        // calcola il totale dai dettagli
        double totale = 0;
        for (DettaglioVendita d : dettagli) {
            totale += d.getSubtotale();
        }
        vendita.setTotale(totale);

        // inserisce la vendita e ottiene l'id generato
        int idVendita = venditaDAO.inserisci(vendita);
        if (idVendita == -1)
            return -1;

        // inserisce i dettagli con l'id della vendita
        for (DettaglioVendita d : dettagli) {
            d.setVenditaId(idVendita);
            venditaDAO.inserisciDettaglio(d);
        }

        // notifica gli observer
        vendita.setId(idVendita);
        notifica("CREATA", vendita);
        return idVendita;
    }

    public boolean chiudiVendita(int venditaId, int repartoId) {
        if (!decoratorCorrente.puoModificareVendite()) {
            System.out.println("Permesso negato: solo gli utenti PRO possono chiudere vendite.");
            return false;
        }
        if (!permessoVendite.haPermesso(utenteCorrente, repartoId)) {
            System.out.println("Permesso negato: non puoi modificare vendite di questo reparto.");
            return false;
        }
        boolean ok = venditaDAO.aggiornaStato(venditaId, "CHIUSA");
        if (ok) {
            Vendita v = venditaDAO.cercaPerId(venditaId);
            if (v != null)
                notifica("CHIUSA", v);
            else
                System.out.println("Attenzione: vendita con id " + venditaId + " non trovata.");
        }
        return ok;
    }

    public boolean annullaVendita(int venditaId, int repartoId) {
        if (!permessoAdmin.haPermesso(utenteCorrente, repartoId)) {
            System.out.println("Permesso negato: solo gli admin possono annullare vendite.");
            return false;
        }
        boolean ok = venditaDAO.aggiornaStato(venditaId, "ANNULLATA");
        if (ok) {
            Vendita v = venditaDAO.cercaPerId(venditaId);
            if (v != null)
                notifica("ANNULLATA", v);
            else
                System.out.println("Attenzione: vendita con id " + venditaId + " non trovata.");
        }
        return ok;
    }

    // -------------------------------------------------------
    // GESTIONE SPEDIZIONI
    // -------------------------------------------------------
    public Spedizione getSpedizioneVendita(int venditaId, int repartoId) {
        if (!permessoSpedizioni.haPermesso(utenteCorrente, repartoId)) {
            System.out.println("Permesso negato: non puoi vedere le spedizioni di questo reparto.");
            return null;
        }
        return spedizioneDAO.cercaPerVendita(venditaId);
    }

    public boolean creaSpedizione(Spedizione spedizione, int repartoId) {
        if (!permessoSpedizioni.haPermesso(utenteCorrente, repartoId)) {
            System.out.println("Permesso negato: non puoi creare spedizioni in questo reparto.");
            return false;
        }
        return spedizioneDAO.inserisci(spedizione);
    }

    public boolean aggiornaStatoSpedizione(int venditaId, String nuovoStato, int repartoId) {
        if (!decoratorCorrente.puoModificareStatiSpedizione()) {
            System.out.println("Permesso negato: solo gli utenti PRO possono aggiornare lo stato spedizione.");
            return false;
        }
        if (!permessoSpedizioni.haPermesso(utenteCorrente, repartoId)) {
            System.out.println("Permesso negato: non puoi modificare spedizioni di questo reparto.");
            return false;
        }
        return spedizioneDAO.aggiornaStato(venditaId, nuovoStato);
    }

    public List<Spedizione> getSpedizioniPerStato(String stato, int repartoId) {
        if (!permessoSpedizioni.haPermesso(utenteCorrente, repartoId)) {
            System.out.println("Permesso negato: non puoi vedere le spedizioni di questo reparto.");
            return new ArrayList<>();
        }
        return spedizioneDAO.spedizioniPerStato(stato);
    }
}
import facade.GestionaleService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import entity.*;

public class App {

    static Scanner scanner = new Scanner(System.in);
    static GestionaleService service;

    public static void main(String[] args) {
        inizializzaApp();
    }

    private static void inizializzaApp() {
        service = GestionaleService.getIstanza();

        System.out.println("===========================================");
        System.out.println("     GESTIONALE VENDITE - Benvenuto        ");
        System.out.println("===========================================");

        boolean eseguiProgramma = true;
        while (eseguiProgramma) {
            if (service.getUtenteCorrente() == null) {
                eseguiProgramma = menuLogin();
            } else {
                menuPrincipale();
            }
        }

        service.chiudi();
        System.out.println("Programma terminato.");
    }

    // -------------------------------------------------------
    // MENU LOGIN
    // -------------------------------------------------------
    private static boolean menuLogin() {
        System.out.println("\n--- LOGIN ---");
        System.out.println("1. Accedi");
        System.out.println("0. Esci");
        System.out.print("Scelta: ");
        int scelta = leggiIntero();

        switch (scelta) {
            case 1:
                System.out.print("Username: ");
                String username = scanner.nextLine();
                System.out.print("Password: ");
                String password = scanner.nextLine();
                service.login(username, password);
                break;
            case 0:
                return false;
            default:
                System.out.println("Scelta non valida.");
        }
        return true;
    }

    // -------------------------------------------------------
    // MENU PRINCIPALE (dopo il login)
    // le voci variano in base al ruolo dell'utente
    // -------------------------------------------------------
    private static void menuPrincipale() {
        Utente u = service.getUtenteCorrente();

        System.out.println("\n===========================================");
        System.out.println("  Utente: " + u.getUsername() +
                " | Tipo: " + u.getTipoAccount() +
                (u.isSuperAdmin() ? " | SUPERADMIN" : ""));
        System.out.println("===========================================");
        System.out.println("1. Gestione Vendite");
        System.out.println("2. Gestione Prodotti");
        System.out.println("3. Gestione Spedizioni");

        // voce visibile solo ad admin e superadmin
        if (u.isSuperAdmin() || haNRuoloAdmin()) {
            System.out.println("4. Gestione Utenti");
        }

        // voce visibile solo al superadmin
        if (u.isSuperAdmin()) {
            System.out.println("5. Gestione Reparti");
        }

        System.out.println("0. Logout");
        System.out.print("Scelta: ");
        int scelta = leggiIntero();

        switch (scelta) {
            case 1:
                menuVendite();
                break;
            case 2:
                menuProdotti();
                break;
            case 3:
                menuSpedizioni();
                break;
            case 4:
                if (u.isSuperAdmin() || haNRuoloAdmin())
                    menuUtenti();
                else
                    System.out.println("Scelta non valida.");
                break;
            case 5:
                if (u.isSuperAdmin())
                    menuReparti();
                else
                    System.out.println("Scelta non valida.");
                break;
            case 0:
                service.logout();
                break;
            default:
                System.out.println("Scelta non valida.");
        }
    }

    // -------------------------------------------------------
    // MENU VENDITE
    // -------------------------------------------------------
    private static void menuVendite() {
        boolean continua = true;
        while (continua) {
            System.out.println("\n--- VENDITE ---");
            System.out.println("1. Visualizza mie vendite");
            System.out.println("2. Visualizza vendite per reparto");
            System.out.println("3. Crea nuova vendita");
            if (service.getDecoratorCorrente().puoModificareVendite()) {
                System.out.println("4. Chiudi vendita");
            }
            if (service.getUtenteCorrente().isSuperAdmin() || haNRuoloAdmin()) {
                System.out.println("5. Annulla vendita");
            }
            System.out.println("0. Torna al menu principale");
            System.out.print("Scelta: ");
            int scelta = leggiIntero();

            switch (scelta) {
                case 1:
                    visualizzaMieVendite();
                    break;
                case 2:
                    visualizzaVenditeReparto();
                    break;
                case 3:
                    creaVendita();
                    break;
                case 4:
                    if (service.getDecoratorCorrente().puoModificareVendite())
                        chiudiVendita();
                    else
                        System.out.println("Scelta non valida.");
                    break;
                case 5:
                    if (service.getUtenteCorrente().isSuperAdmin() || haNRuoloAdmin())
                        annullaVendita();
                    else
                        System.out.println("Scelta non valida.");
                    break;
                case 0:
                    continua = false;
                    break;
                default:
                    System.out.println("Scelta non valida.");
            }
        }
    }

    private static void visualizzaMieVendite() {
        List<Vendita> vendite = service.getMieVendite();
        if (vendite.isEmpty()) {
            System.out.println("Nessuna vendita trovata.");
            return;
        }
        System.out.println("\n--- LE MIE VENDITE ---");
        for (Vendita v : vendite) {
            System.out.println(v);
        }
    }

    private static void visualizzaVenditeReparto() {
        int repartoId = selezionaReparto();
        if (repartoId == -1)
            return;
        List<Vendita> vendite = service.getVenditeReparto(repartoId);
        if (vendite.isEmpty()) {
            System.out.println("Nessuna vendita trovata per questo reparto.");
            return;
        }
        System.out.println("\n--- VENDITE REPARTO ---");
        for (Vendita v : vendite) {
            System.out.println(v);
        }
    }

    private static void creaVendita() {
        int repartoId = selezionaReparto();
        if (repartoId == -1)
            return;

        List<DettaglioVendita> dettagli = new ArrayList<>();
        boolean aggiungiProdotti = true;

        System.out.println("Aggiungi prodotti alla vendita (0 per terminare):");
        while (aggiungiProdotti) {
            List<Prodotto> prodotti = service.getProdottiReparto(repartoId);
            if (prodotti.isEmpty()) {
                System.out.println("Nessun prodotto disponibile in questo reparto.");
                return;
            }
            System.out.println("\n--- PRODOTTI DISPONIBILI ---");
            for (Prodotto p : prodotti) {
                System.out.println(p.getId() + ". " + p.getNome() +
                        " | Prezzo: " + p.getPrezzo() +
                        " | Disponibilità: " + p.getDisponibilita());
            }
            System.out.print("Id prodotto (0 per terminare): ");
            int prodottoId = leggiIntero();
            if (prodottoId == 0) {
                aggiungiProdotti = false;
                continue;
            }
            System.out.print("Quantità: ");
            int quantita = leggiIntero();

            // cerca il prodotto selezionato per il prezzo
            Prodotto scelto = null;
            for (Prodotto p : prodotti) {
                if (p.getId() == prodottoId) {
                    scelto = p;
                    break;
                }
            }
            if (scelto == null) {
                System.out.println("Prodotto non trovato.");
                continue;
            }
            dettagli.add(new DettaglioVendita(0, prodottoId, quantita, scelto.getPrezzo()));
            System.out.println("Prodotto aggiunto.");
        }

        if (dettagli.isEmpty()) {
            System.out.println("Nessun prodotto aggiunto, vendita annullata.");
            return;
        }

        Vendita nuovaVendita = new Vendita(service.getUtenteCorrente().getId(), repartoId);
        int idCreato = service.creaVendita(nuovaVendita, dettagli);
        if (idCreato != -1) {
            System.out.println("Vendita creata con id: " + idCreato);
        }
    }

    private static void chiudiVendita() {
        System.out.print("Id vendita da chiudere: ");
        int id = leggiIntero();
        int repartoId = selezionaReparto();
        if (repartoId == -1)
            return;
        service.chiudiVendita(id, repartoId);
    }

    private static void annullaVendita() {
        System.out.print("Id vendita da annullare: ");
        int id = leggiIntero();
        int repartoId = selezionaReparto();
        if (repartoId == -1)
            return;
        service.annullaVendita(id, repartoId);
    }

    // -------------------------------------------------------
    // MENU PRODOTTI
    // -------------------------------------------------------
    private static void menuProdotti() {
        // i prodotti appartengono sempre al reparto "Prodotti"
        Reparto repartoProdotti = service.getRepartoPerNome("Prodotti");
        if (repartoProdotti == null) {
            System.out.println("Reparto Prodotti non trovato.");
            return;
        }
        int repartoId = repartoProdotti.getId();

        boolean continua = true;
        while (continua) {
            System.out.println("\n--- PRODOTTI ---");
            System.out.println("1. Visualizza prodotti");
            System.out.println("2. Aggiungi prodotto");
            System.out.println("3. Modifica prodotto");
            System.out.println("4. Elimina prodotto");
            System.out.println("0. Torna al menu principale");
            System.out.print("Scelta: ");
            int scelta = leggiIntero();

            switch (scelta) {
                case 1:
                    visualizzaProdotti(repartoId);
                    break;
                case 2:
                    aggiungiProdotto(repartoId);
                    break;
                case 3:
                    modificaProdotto(repartoId);
                    break;
                case 4:
                    eliminaProdotto(repartoId);
                    break;
                case 0:
                    continua = false;
                    break;
                default:
                    System.out.println("Scelta non valida.");
            }
        }
    }

    private static void visualizzaProdotti(int repartoId) {
        List<Prodotto> prodotti = service.getProdottiReparto(repartoId);
        if (prodotti.isEmpty()) {
            System.out.println("Nessun prodotto trovato.");
            return;
        }
        System.out.println("\n--- PRODOTTI ---");
        for (Prodotto p : prodotti) {
            System.out.println(p);
        }
    }

    private static void aggiungiProdotto(int repartoId) {
        System.out.print("Nome prodotto: ");
        String nome = scanner.nextLine();
        System.out.print("Prezzo: ");
        double prezzo = leggiDouble();
        System.out.print("Id categoria: ");
        int categoriaId = leggiIntero();
        System.out.print("Disponibilità iniziale: ");
        int disp = leggiIntero();
        Prodotto p = new Prodotto(nome, prezzo, categoriaId, repartoId, disp);
        service.creaProdotto(p);
    }

    private static void modificaProdotto(int repartoId) {
        visualizzaProdotti(repartoId);
        System.out.print("Id prodotto da modificare: ");
        int id = leggiIntero();
        System.out.print("Nuovo nome: ");
        String nome = scanner.nextLine();
        System.out.print("Nuovo prezzo: ");
        double prezzo = leggiDouble();
        System.out.print("Id categoria: ");
        int categoriaId = leggiIntero();
        System.out.print("Nuova disponibilità: ");
        int disp = leggiIntero();
        Prodotto p = new Prodotto(id, nome, prezzo, categoriaId, repartoId, disp);
        service.aggiornaProdotto(p);
    }

    private static void eliminaProdotto(int repartoId) {
        visualizzaProdotti(repartoId);
        System.out.print("Id prodotto da eliminare: ");
        int id = leggiIntero();
        service.eliminaProdotto(id, repartoId);
    }

    // -------------------------------------------------------
    // MENU SPEDIZIONI
    // -------------------------------------------------------
    private static void menuSpedizioni() {
        Reparto repartoSpedizioni = service.getRepartoPerNome("Spedizioni");
        if (repartoSpedizioni == null) {
            System.out.println("Reparto Spedizioni non trovato.");
            return;
        }
        int repartoId = repartoSpedizioni.getId();

        boolean continua = true;
        while (continua) {
            System.out.println("\n--- SPEDIZIONI ---");
            System.out.println("1. Visualizza spedizione per vendita");
            System.out.println("2. Crea spedizione");
            if (service.getDecoratorCorrente().puoModificareStatiSpedizione()) {
                System.out.println("3. Aggiorna stato spedizione");
            }
            System.out.println("0. Torna al menu principale");
            System.out.print("Scelta: ");
            int scelta = leggiIntero();

            switch (scelta) {
                case 1:
                    visualizzaSpedizione(repartoId);
                    break;
                case 2:
                    creaSpedizione(repartoId);
                    break;
                case 3:
                    if (service.getDecoratorCorrente().puoModificareStatiSpedizione())
                        aggiornaStatoSpedizione(repartoId);
                    else
                        System.out.println("Scelta non valida.");
                    break;
                case 0:
                    continua = false;
                    break;
                default:
                    System.out.println("Scelta non valida.");
            }
        }
    }

    private static void visualizzaSpedizione(int repartoId) {
        System.out.print("Id vendita: ");
        int venditaId = leggiIntero();
        Spedizione s = service.getSpedizioneVendita(venditaId, repartoId);
        if (s == null)
            System.out.println("Spedizione non trovata.");
        else
            System.out.println(s);
    }

    private static void creaSpedizione(int repartoId) {
        System.out.print("Id vendita: ");
        int venditaId = leggiIntero();
        System.out.println("Tipo spedizione:");
        System.out.println("1. Standard");
        System.out.println("2. Express");
        System.out.println("3. Same-day");
        System.out.print("Scelta: ");
        int tipoId = leggiIntero();
        System.out.print("Indirizzo di consegna: ");
        String indirizzo = scanner.nextLine();
        System.out.print("Data prevista (gg-mm-aaaa): ");
        String dataStr = scanner.nextLine();
        LocalDate data = LocalDate.parse(dataStr,
                java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        Spedizione s = new Spedizione(venditaId, tipoId, indirizzo, data);
        service.creaSpedizione(s, repartoId);
    }

    private static void aggiornaStatoSpedizione(int repartoId) {
        System.out.print("Id vendita: ");
        int venditaId = leggiIntero();
        System.out.println("Nuovo stato:");
        System.out.println("1. IN_ATTESA");
        System.out.println("2. SPEDITO");
        System.out.println("3. CONSEGNATO");
        System.out.print("Scelta: ");
        int scelta = leggiIntero();
        String[] stati = { "IN_ATTESA", "SPEDITO", "CONSEGNATO" };
        if (scelta < 1 || scelta > 3) {
            System.out.println("Scelta non valida.");
            return;
        }
        service.aggiornaStatoSpedizione(venditaId, stati[scelta - 1], repartoId);
    }

    // -------------------------------------------------------
    // MENU UTENTI (solo admin / superadmin)
    // -------------------------------------------------------
    private static void menuUtenti() {
        boolean continua = true;
        while (continua) {
            System.out.println("\n--- GESTIONE UTENTI ---");
            System.out.println("1. Visualizza utenti attivi");
            System.out.println("2. Crea nuovo utente");
            System.out.println("3. Disattiva utente");
            System.out.println("4. Cambia ruolo utente nel reparto");
            System.out.println("0. Torna al menu principale");
            System.out.print("Scelta: ");
            int scelta = leggiIntero();

            switch (scelta) {
                case 1:
                    visualizzaUtenti();
                    break;
                case 2:
                    creaUtente();
                    break;
                case 3:
                    disattivaUtente();
                    break;
                case 4:
                    cambiaRuolo();
                    break;
                case 0:
                    continua = false;
                    break;
                default:
                    System.out.println("Scelta non valida.");
            }
        }
    }

    private static void visualizzaUtenti() {
        List<InfoUtenteReparto> utenti = service.getUtentiAttiviConReparto();
        if (utenti.isEmpty()) {
            System.out.println("Nessun utente trovato.");
            return;
        }
        System.out.println("\n--- UTENTI ATTIVI ---");
        for (InfoUtenteReparto u : utenti) {
            System.out.println(u);
        }
    }

    private static void creaUtente() {
        int repartoId = selezionaReparto();
        if (repartoId == -1)
            return;
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();
        System.out.println("Tipo account:");
        System.out.println("1. NORMAL");
        System.out.println("2. PRO");
        System.out.print("Scelta: ");
        int scelta = leggiIntero();
        String tipo = (scelta == 2) ? "PRO" : "NORMAL";
        Utente nuovo = new Utente(username, password, tipo);
        service.creaUtente(nuovo, repartoId);
    }

    private static void disattivaUtente() {
        int repartoId = selezionaReparto();
        if (repartoId == -1)
            return;
        System.out.print("Id utente da disattivare: ");
        int id = leggiIntero();
        service.disattivaUtente(id, repartoId);
    }

    private static void cambiaRuolo() {
        int repartoId = selezionaReparto();
        if (repartoId == -1)
            return;

        List<InfoUtenteReparto> utentiReparto = service.getUtentiRepartoConRuolo(repartoId);
        if (utentiReparto.isEmpty()) {
            System.out.println("Nessun utente in questo reparto.");
            return;
        }
        System.out.println("\n--- UTENTI DEL REPARTO ---");
        for (InfoUtenteReparto u : utentiReparto) {
            System.out.println(u);
        }

        System.out.print("Id utente: ");
        int id = leggiIntero();
        System.out.println("Nuovo ruolo:");
        System.out.println("1. USER");
        System.out.println("2. ADMIN");
        System.out.print("Scelta: ");
        int scelta = leggiIntero();
        String ruolo = (scelta == 2) ? "ADMIN" : "USER";
        service.cambiaRuoloUtente(id, repartoId, ruolo);
    }

    // -------------------------------------------------------
    // MENU REPARTI (solo superadmin)
    // -------------------------------------------------------
    private static void menuReparti() {
        boolean continua = true;
        while (continua) {
            System.out.println("\n--- GESTIONE REPARTI ---");
            System.out.println("1. Visualizza tutti i reparti");
            System.out.println("2. Crea nuovo reparto");
            System.out.println("0. Torna al menu principale");
            System.out.print("Scelta: ");
            int scelta = leggiIntero();

            switch (scelta) {
                case 1:
                    List<Reparto> reparti = service.getReparti();
                    if (reparti.isEmpty())
                        System.out.println("Nessun reparto trovato.");
                    else
                        reparti.forEach(System.out::println);
                    break;
                case 2:
                    System.out.print("Nome reparto: ");
                    String nome = scanner.nextLine();
                    System.out.print("Descrizione: ");
                    String desc = scanner.nextLine();
                    service.creaReparto(new Reparto(nome, desc));
                    break;
                case 0:
                    continua = false;
                    break;
                default:
                    System.out.println("Scelta non valida.");
            }
        }
    }

    // -------------------------------------------------------
    // UTILITY: selezione reparto da lista (tutti i reparti)
    // -------------------------------------------------------
    private static int selezionaReparto() {
        List<Reparto> reparti = service.getReparti();
        if (reparti.isEmpty()) {
            System.out.println("Nessun reparto disponibile.");
            return -1;
        }
        System.out.println("\n--- SELEZIONA REPARTO ---");
        for (Reparto r : reparti) {
            System.out.println(r.getId() + ". " + r.getNome());
        }
        System.out.print("Id reparto: ");
        return leggiIntero();
    }

    // -------------------------------------------------------
    // UTILITY: controlla se l'utente corrente è admin
    // in almeno un reparto
    // -------------------------------------------------------
    private static boolean haNRuoloAdmin() {
        return service.isAdminInAlmenoUnReparto();
    }

    // -------------------------------------------------------
    // UTILITY: lettura intera sicura
    // -------------------------------------------------------
    private static int leggiIntero() {
        try {
            int valore = Integer.parseInt(scanner.nextLine().trim());
            return valore;
        } catch (NumberFormatException e) {
            System.out.println("Inserisci un numero valido.");
            return -1;
        }
    }

    // -------------------------------------------------------
    // UTILITY: lettura double sicura
    // -------------------------------------------------------
    private static double leggiDouble() {
        try {
            return Double.parseDouble(scanner.nextLine().trim().replace(",", "."));
        } catch (NumberFormatException e) {
            System.out.println("Inserisci un valore numerico valido.");
            return 0.0;
        }
    }
}
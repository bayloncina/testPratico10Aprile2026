package observer;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import entity.Vendita;

public class LogObserver implements VenditaObserver {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    @Override
    public void onVenditaAggiornata(String evento, Vendita vendita) {
        String timestamp = LocalDateTime.now().format(FORMATTER);
        System.out.println("[LOG] " + timestamp +
                " | Evento: " + evento +
                " | VenditaId: " + vendita.getId() +
                " | UtenteId: " + vendita.getUtenteId() +
                " | Reparto: " + vendita.getRepartoId() +
                " | Totale: " + vendita.getTotale() +
                " | Stato: " + vendita.getStato());
    }
}
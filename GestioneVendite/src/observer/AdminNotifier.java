package observer;

import entity.Vendita;

public class AdminNotifier implements VenditaObserver {

    // soglia oltre la quale si notifica (utile per vendite di importo elevato)
    private static final double SOGLIA_NOTIFICA = 500.0;

    @Override
    public void onVenditaAggiornata(String evento, Vendita vendita) {
        // notifica sempre in caso di annullamento
        if ("ANNULLATA".equals(evento)) {
            System.out.println("[ADMIN NOTIFICA] Vendita id " + vendita.getId() +
                    " ANNULLATA dal reparto " + vendita.getRepartoId() +
                    " | Totale: " + vendita.getTotale() + " €");
            return;
        }

        // notifica se il totale supera la soglia
        if (vendita.getTotale() >= SOGLIA_NOTIFICA) {
            System.out.println("[ADMIN NOTIFICA] Vendita id " + vendita.getId() +
                    " | Evento: " + evento +
                    " | Totale elevato: " + vendita.getTotale() + " €" +
                    " | Reparto: " + vendita.getRepartoId());
        }
    }
}
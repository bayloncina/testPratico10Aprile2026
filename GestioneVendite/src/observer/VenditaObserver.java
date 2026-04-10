package observer;

import entity.Vendita;

public interface VenditaObserver {

    // chiamato ogni volta che una vendita viene creata, modificata o eliminata
    void onVenditaAggiornata(String evento, Vendita vendita);
}
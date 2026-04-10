package strategy;

import entity.Utente;

public interface PermessoStrategy {
    // restituisce true se l'utente ha il permesso di eseguire l'operazione
    boolean haPermesso(Utente utente, int repartoId);

    // restituisce una descrizione dell'operazione protetta
    String getDescrizione();
}

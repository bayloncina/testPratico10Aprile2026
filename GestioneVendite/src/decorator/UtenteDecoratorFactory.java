package decorator;

import entity.Utente;

public class UtenteDecoratorFactory {

    // restituisce il componente giusto in base al tipo account dell'utente
    public static UtenteComponent crea(Utente utente) {
        UtenteBase base = new UtenteBase();
        if ("PRO".equals(utente.getTipoAccount()) || utente.isSuperAdmin()) {
            return new UtenteProDecorator(base);
        }
        return base;
    }
}
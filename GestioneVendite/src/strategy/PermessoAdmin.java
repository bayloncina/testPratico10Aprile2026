package strategy;

import dao.UtenteRepartoDAO;
import entity.Utente;

public class PermessoAdmin implements PermessoStrategy {

    private UtenteRepartoDAO utenteRepartoDAO;

    public PermessoAdmin() {
        this.utenteRepartoDAO = new UtenteRepartoDAO();
    }

    // può gestire gli utenti se:
    // - è superadmin (gestisce tutti i reparti)
    // - oppure è ADMIN del reparto specifico
    @Override
    public boolean haPermesso(Utente utente, int repartoId) {
        if (utente.isSuperAdmin()) {
            return true;
        }
        return utenteRepartoDAO.isAdminDelReparto(utente.getId(), repartoId);
    }

    @Override
    public String getDescrizione() {
        return "Gestione utenti del reparto";
    }
}
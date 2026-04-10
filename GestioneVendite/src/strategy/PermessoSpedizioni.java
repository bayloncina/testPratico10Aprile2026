package strategy;

import dao.UtenteRepartoDAO;
import entity.Utente;

public class PermessoSpedizioni implements PermessoStrategy {

    private UtenteRepartoDAO utenteRepartoDAO;

    public PermessoSpedizioni() {
        this.utenteRepartoDAO = new UtenteRepartoDAO();
    }

    // può gestire le spedizioni se:
    // - è superadmin
    // - oppure è associato al reparto spedizioni (qualsiasi ruolo)
    // la modifica dello stato è riservata ad ADMIN o PRO (verificata nella Facade)
    @Override
    public boolean haPermesso(Utente utente, int repartoId) {
        if (utente.isSuperAdmin()) {
            return true;
        }
        return utenteRepartoDAO.cerca(utente.getId(), repartoId) != null;
    }

    @Override
    public String getDescrizione() {
        return "Accesso alle spedizioni del reparto";
    }
}
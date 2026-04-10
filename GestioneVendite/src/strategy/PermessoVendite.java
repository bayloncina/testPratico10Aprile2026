package strategy;

import dao.UtenteRepartoDAO;
import entity.Utente;

public class PermessoVendite implements PermessoStrategy {

    private UtenteRepartoDAO utenteRepartoDAO;

    public PermessoVendite() {
        this.utenteRepartoDAO = new UtenteRepartoDAO();
    }

    // può accedere alle vendite se:
    // - è superadmin
    // - oppure è associato al reparto vendite (con qualsiasi ruolo)
    @Override
    public boolean haPermesso(Utente utente, int repartoId) {
        if (utente.isSuperAdmin()) {
            return true;
        }
        return utenteRepartoDAO.cerca(utente.getId(), repartoId) != null;
    }

    @Override
    public String getDescrizione() {
        return "Accesso alle vendite del reparto";
    }
}

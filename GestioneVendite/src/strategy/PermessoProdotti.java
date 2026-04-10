package strategy;

import dao.UtenteRepartoDAO;
import entity.Utente;
import entity.UtenteReparto;

public class PermessoProdotti implements PermessoStrategy {

    private UtenteRepartoDAO utenteRepartoDAO;

    public PermessoProdotti() {
        this.utenteRepartoDAO = new UtenteRepartoDAO();
    }

    // può gestire i prodotti se:
    // - è superadmin
    // - oppure è ADMIN del reparto prodotti
    // - oppure è utente PRO del reparto prodotti
    @Override
    public boolean haPermesso(Utente utente, int repartoId) {
        if (utente.isSuperAdmin()) {
            return true;
        }
        UtenteReparto ur = utenteRepartoDAO.cerca(utente.getId(), repartoId);
        if (ur == null) {
            return false;
        }
        return ur.isAdmin() || "PRO".equals(utente.getTipoAccount());
    }

    @Override
    public String getDescrizione() {
        return "Gestione prodotti del reparto";
    }
}
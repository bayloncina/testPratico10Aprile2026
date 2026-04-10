package entity;

public class UtenteReparto {

    private int id;
    private int utenteId;
    private int repartoId;
    private String ruolo; // "USER" o "ADMIN"

    // costruttore completo (lettura dal DB)
    public UtenteReparto(int id, int utenteId, int repartoId, String ruolo) {
        this.id = id;
        this.utenteId = utenteId;
        this.repartoId = repartoId;
        this.ruolo = ruolo;
    }

    // costruttore senza id (nuova associazione da inserire)
    public UtenteReparto(int utenteId, int repartoId, String ruolo) {
        this.utenteId = utenteId;
        this.repartoId = repartoId;
        this.ruolo = ruolo;
    }

    // getter
    public int getId() {
        return id;
    }

    public int getUtenteId() {
        return utenteId;
    }

    public int getRepartoId() {
        return repartoId;
    }

    public String getRuolo() {
        return ruolo;
    }

    // setter
    public void setId(int id) {
        this.id = id;
    }

    public void setUtenteId(int id) {
        this.utenteId = id;
    }

    public void setRepartoId(int id) {
        this.repartoId = id;
    }

    public void setRuolo(String ruolo) {
        this.ruolo = ruolo;
    }

    // utility: controlla se il ruolo è ADMIN
    public boolean isAdmin() {
        return "ADMIN".equalsIgnoreCase(this.ruolo);
    }

    @Override
    public String toString() {
        return "UtenteReparto{utenteId=" + utenteId +
                ", repartoId=" + repartoId +
                ", ruolo='" + ruolo + "'}";
    }
}
package entity;

public class InfoUtenteReparto {

    private int id;
    private String username;
    private String tipoAccount;
    private boolean superAdmin;
    private String nomeReparto;
    private String ruolo;

    public InfoUtenteReparto(int id, String username, String tipoAccount,
            boolean superAdmin, String nomeReparto, String ruolo) {
        this.id = id;
        this.username = username;
        this.tipoAccount = tipoAccount;
        this.superAdmin = superAdmin;
        this.nomeReparto = nomeReparto;
        this.ruolo = ruolo;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getTipoAccount() {
        return tipoAccount;
    }

    public boolean isSuperAdmin() {
        return superAdmin;
    }

    public String getNomeReparto() {
        return nomeReparto;
    }

    public String getRuolo() {
        return ruolo;
    }

    @Override
    public String toString() {
        if (superAdmin) {
            return "id=" + id + " | " + username + " | " + tipoAccount + " | SUPERADMIN";
        }
        return "id=" + id + " | " + username + " | " + tipoAccount +
                " | " + nomeReparto + ": " + ruolo;
    }
}
package entity;

public class Utente {

    private int id;
    private String username;
    private String password;
    private String tipoAccount; // "NORMAL" o "PRO"
    private boolean isSuperAdmin;
    private boolean attivo;

    // costruttore completo (usato quando si legge dal DB)
    public Utente(int id, String username, String password, String tipoAccount, boolean isSuperAdmin, boolean attivo) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.tipoAccount = tipoAccount;
        this.isSuperAdmin = isSuperAdmin;
        this.attivo = attivo;
    }

    // costruttore senza id (usato quando si crea un nuovo utente da inserire)
    public Utente(String username, String password, String tipoAccount) {
        this.username = username;
        this.password = password;
        this.tipoAccount = tipoAccount;
        this.isSuperAdmin = false;
        this.attivo = true;
    }

    // getter
    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getTipoAccount() {
        return tipoAccount;
    }

    public boolean isSuperAdmin() {
        return isSuperAdmin;
    }

    public boolean isAttivo() {
        return attivo;
    }

    // setter
    public void setId(int id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setTipoAccount(String tipo) {
        this.tipoAccount = tipo;
    }

    public void setSuperAdmin(boolean superAdmin) {
        this.isSuperAdmin = superAdmin;
    }

    public void setAttivo(boolean attivo) {
        this.attivo = attivo;
    }

    @Override
    public String toString() {
        return "Utente{id=" + id +
                ", username='" + username + "'" +
                ", tipo=" + tipoAccount +
                ", superAdmin=" + isSuperAdmin +
                ", attivo=" + attivo + "}";
    }
}
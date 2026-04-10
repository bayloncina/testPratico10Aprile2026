package entity;

public class DettaglioVendita {

    private int id;
    private int venditaId;
    private int prodottoId;
    private int quantita;
    private double prezzoUnitario;

    // costruttore completo (lettura dal DB)
    public DettaglioVendita(int id, int venditaId, int prodottoId, int quantita, double prezzoUnitario) {
        this.id = id;
        this.venditaId = venditaId;
        this.prodottoId = prodottoId;
        this.quantita = quantita;
        this.prezzoUnitario = prezzoUnitario;
    }

    // costruttore senza id (nuova riga da inserire)
    public DettaglioVendita(int venditaId, int prodottoId, int quantita, double prezzoUnitario) {
        this.venditaId = venditaId;
        this.prodottoId = prodottoId;
        this.quantita = quantita;
        this.prezzoUnitario = prezzoUnitario;
    }

    // getter
    public int getId() {
        return id;
    }

    public int getVenditaId() {
        return venditaId;
    }

    public int getProdottoId() {
        return prodottoId;
    }

    public int getQuantita() {
        return quantita;
    }

    public double getPrezzoUnitario() {
        return prezzoUnitario;
    }

    // calcola il subtotale di questa riga
    public double getSubtotale() {
        return quantita * prezzoUnitario;
    }

    // setter
    public void setId(int id) {
        this.id = id;
    }

    public void setVenditaId(int venditaId) {
        this.venditaId = venditaId;
    }

    public void setProdottoId(int prodottoId) {
        this.prodottoId = prodottoId;
    }

    public void setQuantita(int quantita) {
        this.quantita = quantita;
    }

    public void setPrezzoUnitario(double prezzo) {
        this.prezzoUnitario = prezzo;
    }

    @Override
    public String toString() {
        return "DettaglioVendita{venditaId=" + venditaId +
                ", prodottoId=" + prodottoId +
                ", quantita=" + quantita +
                ", prezzoUnitario=" + prezzoUnitario +
                ", subtotale=" + getSubtotale() + "}";
    }
}
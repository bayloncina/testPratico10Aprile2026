package entity;

public class Prodotto {

    private int id;
    private String nome;
    private double prezzo;
    private int categoriaId;
    private int repartoId;
    private int disponibilita;

    // costruttore completo (lettura dal DB)
    public Prodotto(int id, String nome, double prezzo, int categoriaId, int repartoId, int disponibilita) {
        this.id = id;
        this.nome = nome;
        this.prezzo = prezzo;
        this.categoriaId = categoriaId;
        this.repartoId = repartoId;
        this.disponibilita = disponibilita;
    }

    // costruttore senza id (nuovo prodotto da inserire)
    public Prodotto(String nome, double prezzo, int categoriaId, int repartoId, int disponibilita) {
        this.nome = nome;
        this.prezzo = prezzo;
        this.categoriaId = categoriaId;
        this.repartoId = repartoId;
        this.disponibilita = disponibilita;
    }

    // getter
    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public double getPrezzo() {
        return prezzo;
    }

    public int getCategoriaId() {
        return categoriaId;
    }

    public int getRepartoId() {
        return repartoId;
    }

    public int getDisponibilita() {
        return disponibilita;
    }

    // setter
    public void setId(int id) {
        this.id = id;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setPrezzo(double prezzo) {
        this.prezzo = prezzo;
    }

    public void setCategoriaId(int categoriaId) {
        this.categoriaId = categoriaId;
    }

    public void setRepartoId(int repartoId) {
        this.repartoId = repartoId;
    }

    public void setDisponibilita(int disp) {
        this.disponibilita = disp;
    }

    @Override
    public String toString() {
        return "Prodotto{id=" + id +
                ", nome='" + nome + "'" +
                ", prezzo=" + prezzo +
                ", disponibilita=" + disponibilita + "}";
    }
}
package entity;

public class TipoSpedizione {

    private int id;
    private String nome;
    private double costoBase;

    public TipoSpedizione(int id, String nome, double costoBase) {
        this.id = id;
        this.nome = nome;
        this.costoBase = costoBase;
    }

    public TipoSpedizione(String nome, double costoBase) {
        this.nome = nome;
        this.costoBase = costoBase;
    }

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public double getCostoBase() {
        return costoBase;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setCostoBase(double costo) {
        this.costoBase = costo;
    }

    @Override
    public String toString() {
        return "TipoSpedizione{id=" + id + ", nome='" + nome + "', costo=" + costoBase + "}";
    }
}
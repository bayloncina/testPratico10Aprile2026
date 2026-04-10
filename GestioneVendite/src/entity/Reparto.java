package entity;

public class Reparto {

    private int id;
    private String nome;
    private String descrizione;

    // costruttore completo (lettura dal DB)
    public Reparto(int id, String nome, String descrizione) {
        this.id = id;
        this.nome = nome;
        this.descrizione = descrizione;
    }

    // costruttore senza id (nuovo reparto da inserire)
    public Reparto(String nome, String descrizione) {
        this.nome = nome;
        this.descrizione = descrizione;
    }

    // getter
    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getDescrizione() {
        return descrizione;
    }

    // setter
    public void setId(int id) {
        this.id = id;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setDescrizione(String desc) {
        this.descrizione = desc;
    }

    @Override
    public String toString() {
        return "Reparto{id=" + id + ", nome='" + nome + "'}";
    }
}
package entity;

import java.time.LocalDate;

public class Spedizione {

    private int id;
    private int venditaId;
    private int tipoId;
    private String indirizzo;
    private String stato; // "IN_ATTESA", "SPEDITO", "CONSEGNATO"
    private LocalDate dataPrevista;

    // costruttore completo (lettura dal DB)
    public Spedizione(int id, int venditaId, int tipoId, String indirizzo, String stato, LocalDate dataPrevista) {
        this.id = id;
        this.venditaId = venditaId;
        this.tipoId = tipoId;
        this.indirizzo = indirizzo;
        this.stato = stato;
        this.dataPrevista = dataPrevista;
    }

    // costruttore senza id (nuova spedizione da inserire)
    public Spedizione(int venditaId, int tipoId, String indirizzo, LocalDate dataPrevista) {
        this.venditaId = venditaId;
        this.tipoId = tipoId;
        this.indirizzo = indirizzo;
        this.stato = "IN_ATTESA";
        this.dataPrevista = dataPrevista;
    }

    // getter
    public int getId() {
        return id;
    }

    public int getVenditaId() {
        return venditaId;
    }

    public int getTipoId() {
        return tipoId;
    }

    public String getIndirizzo() {
        return indirizzo;
    }

    public String getStato() {
        return stato;
    }

    public LocalDate getDataPrevista() {
        return dataPrevista;
    }

    // setter
    public void setId(int id) {
        this.id = id;
    }

    public void setVenditaId(int venditaId) {
        this.venditaId = venditaId;
    }

    public void setTipoId(int tipoId) {
        this.tipoId = tipoId;
    }

    public void setIndirizzo(String indirizzo) {
        this.indirizzo = indirizzo;
    }

    public void setStato(String stato) {
        this.stato = stato;
    }

    public void setDataPrevista(LocalDate data) {
        this.dataPrevista = data;
    }

    @Override
    public String toString() {
        return "Spedizione{id=" + id +
                ", venditaId=" + venditaId +
                ", stato='" + stato + "'" +
                ", dataPrevista=" + dataPrevista + "}";
    }
}
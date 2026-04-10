package entity;

import java.time.LocalDateTime;

public class Vendita {

    private int id;
    private int utenteId;
    private int repartoId;
    private LocalDateTime data;
    private double totale;
    private String stato; // "APERTA", "CHIUSA", "ANNULLATA"

    // costruttore completo (lettura dal DB)
    public Vendita(int id, int utenteId, int repartoId, LocalDateTime data, double totale, String stato) {
        this.id = id;
        this.utenteId = utenteId;
        this.repartoId = repartoId;
        this.data = data;
        this.totale = totale;
        this.stato = stato;
    }

    // costruttore senza id (nuova vendita da inserire)
    public Vendita(int utenteId, int repartoId) {
        this.utenteId = utenteId;
        this.repartoId = repartoId;
        this.data = LocalDateTime.now();
        this.totale = 0.0;
        this.stato = "APERTA";
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

    public LocalDateTime getData() {
        return data;
    }

    public double getTotale() {
        return totale;
    }

    public String getStato() {
        return stato;
    }

    // setter
    public void setId(int id) {
        this.id = id;
    }

    public void setUtenteId(int utenteId) {
        this.utenteId = utenteId;
    }

    public void setRepartoId(int repartoId) {
        this.repartoId = repartoId;
    }

    public void setData(LocalDateTime data) {
        this.data = data;
    }

    public void setTotale(double totale) {
        this.totale = totale;
    }

    public void setStato(String stato) {
        this.stato = stato;
    }

    @Override
    public String toString() {
        return "Vendita{id=" + id +
                ", utenteId=" + utenteId +
                ", repartoId=" + repartoId +
                ", totale=" + totale +
                ", stato='" + stato + "'}";
    }
}
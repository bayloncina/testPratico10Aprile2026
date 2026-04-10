package observer;

import dao.ProdottoDAO;
import dao.VenditaDAO;
import entity.DettaglioVendita;
import entity.Prodotto;
import entity.Vendita;

import java.util.List;

public class StockObserver implements VenditaObserver {

    private ProdottoDAO prodottoDAO;
    private VenditaDAO venditaDAO;

    public StockObserver() {
        this.prodottoDAO = new ProdottoDAO();
        this.venditaDAO  = new VenditaDAO();
    }

    @Override
    public void onVenditaAggiornata(String evento, Vendita vendita) {
        // aggiorna lo stock solo quando una vendita viene creata o chiusa
        if (!"CREATA".equals(evento) && !"CHIUSA".equals(evento)) {
            return;
        }

        List<DettaglioVendita> dettagli = venditaDAO.dettagliVendita(vendita.getId());
        for (DettaglioVendita d : dettagli) {
            Prodotto prodotto = prodottoDAO.cercaPerId(d.getProdottoId());
            if (prodotto != null) {
                int nuovaDisponibilita = prodotto.getDisponibilita() - d.getQuantita();
                if (nuovaDisponibilita < 0) nuovaDisponibilita = 0;
                prodottoDAO.aggiornaDisponibilita(prodotto.getId(), nuovaDisponibilita);
                System.out.println("[STOCK] Prodotto '" + prodotto.getNome() +
                        "' disponibilità aggiornata: " + nuovaDisponibilita);
            }
        }
    }
}
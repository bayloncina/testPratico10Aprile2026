package decorator;

public class UtenteBase implements UtenteComponent {

    @Override
    public String getTipoAccount() {
        return "NORMAL";
    }

    // un utente NORMAL può solo inserire vendite, non modificarle
    @Override
    public boolean puoModificareVendite() {
        return false;
    }

    // un utente NORMAL non vede i report
    @Override
    public boolean puoVedereReport() {
        return false;
    }

    // un utente NORMAL non può applicare sconti
    @Override
    public boolean puoApplicareSconti() {
        return false;
    }

    // un utente NORMAL non può cambiare lo stato delle spedizioni
    @Override
    public boolean puoModificareStatiSpedizione() {
        return false;
    }

    @Override
    public String descrizionePermessi() {
        return "Account NORMAL: inserimento vendite, visualizzazione prodotti e spedizioni proprie.";
    }
}
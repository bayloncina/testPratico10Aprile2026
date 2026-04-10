package decorator;

public class UtenteProDecorator implements UtenteComponent {

    // wrappa il componente base (UtenteBase o un altro decorator)
    private UtenteComponent base;

    public UtenteProDecorator(UtenteComponent base) {
        this.base = base;
    }

    @Override
    public String getTipoAccount() {
        return "PRO";
    }

    // PRO può modificare le vendite del proprio reparto
    @Override
    public boolean puoModificareVendite() {
        return true;
    }

    // PRO può vedere i report e le statistiche
    @Override
    public boolean puoVedereReport() {
        return true;
    }

    // PRO può applicare sconti sulle vendite
    @Override
    public boolean puoApplicareSconti() {
        return true;
    }

    // PRO può cambiare lo stato delle spedizioni
    @Override
    public boolean puoModificareStatiSpedizione() {
        return true;
    }

    @Override
    public String descrizionePermessi() {
        return base.descrizionePermessi() +
                "\nAccount PRO: modifica vendite, report, sconti, gestione stati spedizione.";
    }
}
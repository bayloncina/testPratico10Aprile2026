package decorator;

public interface UtenteComponent {

    String getTipoAccount();

    boolean puoModificareVendite();

    boolean puoVedereReport();

    boolean puoApplicareSconti();

    boolean puoModificareStatiSpedizione();

    String descrizionePermessi();
}
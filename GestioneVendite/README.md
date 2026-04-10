# GestionaleVendite

Gestionale a console per la gestione di vendite, prodotti, spedizioni e utenti,
con sistema di permessi basato su reparti e tipo di account.

---

## Avvio rapido

Aggiungere il driver MySQL al progetto

Inserire credenziali del tuo db

Avviare App.java. Il database, le tabelle e i dati di esempio vengono creati
automaticamente al primo avvio. Non serve eseguire nessuno script SQL.

---

## Credenziali di accesso

superadmin / superadmin123 — PRO, SuperAdmin, accesso a tutto
admin_vendite / admin123 — PRO, Admin del reparto Vendite
mario_pro / mario123 — PRO, User del reparto Prodotti
luigi_normal / luigi123 — NORMAL, User del reparto Vendite

---

## Come funziona

Ogni utente ha un tipo di account e un ruolo nel reparto. Questi due elementi
insieme determinano cosa può fare.

Il tipo di account può essere NORMAL o PRO. Un utente NORMAL può inserire vendite
e consultare i dati del proprio reparto. Un utente PRO può in più modificare e
chiudere vendite, aggiornare lo stato delle spedizioni e applicare sconti.

Il ruolo nel reparto può essere USER o ADMIN. Un USER accede ai dati del reparto.
Un ADMIN può in più gestire gli utenti del reparto e annullare vendite.

Il SuperAdmin bypassa tutto e ha accesso completo. È l'unico che può creare
nuovi reparti.

I reparti di default sono tre: Vendite, Prodotti e Spedizioni. I prodotti
appartengono al reparto Prodotti, le spedizioni al reparto Spedizioni.
Le vendite appartengono al reparto Vendite.


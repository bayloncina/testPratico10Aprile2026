# testPratico10Aprile2026

GestionaleVendite
La traccia chiedeva di costruire un gestionale per gestire un'utenza che potesse
modificare un database di vendite in maniera variabile di responsabilità e tipologia
di accounting, con la possibilità di creare account pro o normali, admin già presenti
e aggiungibili su vari livelli, e supporto a diversi tipi di prodotti e spedizioni.

Avviare App.java e accedere con:
Username : superadmin
Password : superadmin123

gestire un'utenza che può modificare un db di vendite in maniera variabile
di responsabilità"
La variabilità di responsabilità è stata implementata attraverso i reparti.
Ogni utente appartiene a uno o più reparti con un ruolo specifico — USER o ADMIN.
Un utente non può vedere né modificare dati di un reparto a cui non è associato.
Un admin del reparto Vendite, per esempio, può annullare vendite e gestire gli
utenti di quel reparto, ma non ha alcuna visibilità sul reparto Prodotti.
I reparti non sono fissi nel codice: sono una tabella separata del database,
configurabile dal superadmin senza toccare il programma.

Il tipo di account — NORMAL o PRO — determina le capacità operative dell'utente
indipendentemente dal reparto in cui lavora. Un utente NORMAL può inserire vendite
e consultare i dati del proprio reparto. Un utente PRO può in più modificare e
chiudere vendite, aggiornare lo stato delle spedizioni e applicare sconti. Questa
distinzione è stata implementata con il pattern Decorator: UtenteBase rappresenta
le capacità base, UtenteProDecorator le estende senza modificarle. Se in futuro
servisse un terzo livello di account, basterebbe aggiungere un decorator.

Dal menu Gestione Utenti, un admin di reparto o il superadmin possono creare nuovi
utenti scegliendo il tipo di account (NORMAL o PRO) e assegnarli al reparto con
il ruolo appropriato. La creazione è protetta: solo chi ha il permesso sul reparto
può aggiungere utenti in quel reparto.

Il sistema parte con un superadmin già inserito nel database al primo avvio e un
admin di reparto (admin_vendite) pronto per i test. Il superadmin può promuovere
qualsiasi utente al ruolo di ADMIN in qualsiasi reparto tramite la funzione
"Cambia ruolo utente". I livelli sono due: USER e ADMIN per ogni reparto, più il
SuperAdmin che opera trasversalmente su tutto il sistema.

I prodotti sono organizzati per categoria (Elettronica, Abbigliamento, Alimentari)
e per reparto. Ogni reparto può avere il proprio catalogo. La disponibilità viene
aggiornata automaticamente ogni volta che una vendita viene creata o chiusa,
grazie all'Observer StockObserver che scala le quantità senza che nessuno
debba farlo a mano.

Ogni vendita può avere una spedizione associata con tipo a scelta tra Standard,
Express e Same-day, ciascuno con il proprio costo base. Lo stato della spedizione
(IN_ATTESA, SPEDITO, CONSEGNATO) può essere aggiornato dagli utenti PRO del
reparto Spedizioni.

I pattern e perché sono stati scelti
Singleton
La connessione al database e il coordinatore centrale del sistema (GestionaleService)
sono entrambi Singleton. Per la connessione il motivo è tecnico: aprire e chiudere
una connessione JDBC ad ogni operazione è lento e inutile per un'applicazione
monoutente. Per GestionaleService il motivo è architetturale: una sola istanza
garantisce che gli observer siano registrati una volta sola e che lo stato della
sessione utente sia sempre coerente.
Strategy
Ogni operazione del sistema ha regole di accesso diverse. Inserire una vendita
richiede solo di appartenere al reparto; modificarla richiede il tipo PRO; annullarla
richiede il ruolo ADMIN; creare un reparto richiede di essere superadmin. Invece di
mettere tutto in una catena di condizioni, ogni regola vive in una classe separata
che implementa la stessa interfaccia haPermesso(utente, repartoId). Aggiungere
una nuova regola significa aggiungere una classe, non riscrivere quelle esistenti.
Observer
Quando una vendita cambia stato devono succedere cose diverse e indipendenti tra
loro: il log viene aggiornato, la disponibilità dei prodotti viene scalata, gli
admin vengono avvisati se l'importo è rilevante o la vendita viene annullata.
Tenere questa logica tutta nello stesso posto sarebbe sbagliato. Con l'Observer
il sistema notifica che qualcosa è successo e ciascun observer reagisce per conto
suo. Il risultato è che aggiungere un nuovo comportamento — per esempio inviare
una mail — non richiede di toccare nulla di quello che già funziona.
Decorator
La distinzione tra account NORMAL e PRO non è una differenza di tipo ma una
differenza di capacità. Il Decorator modella esattamente questo: le capacità PRO
si aggiungono sopra quelle base senza sostituirle. UtenteDecoratorFactory crea
il decorator giusto subito dopo il login, e da quel momento il menu si costruisce
dinamicamente mostrando solo le voci che l'utente può effettivamente usare.
Facade
Il menu a console non conosce DAO, Strategy o Observer. Chiama solo metodi
di GestionaleService, che coordina internamente tutto il resto. Questo
disaccoppiamento ha un vantaggio concreto: l'intera interfaccia utente potrebbe
essere sostituita con una GUI Swing riutilizzando la stessa logica senza modificare
una riga del livello sottostante.

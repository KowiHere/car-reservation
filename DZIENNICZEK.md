# Dzienniczek kodu — projekt "Warsztat" (car-reservation)

> **Zasada trwała:** ten dzienniczek jest uzupełniany po każdym zakończonym etapie pracy nad projektem (każda naprawa, refaktoryzacja, nowa funkcjonalność). Nowe wpisy dodawane są jako kolejne sekcje na końcu pliku, ze wskazaniem daty/etapu i zmienionych plików wraz z zakresami wierszy — bez usuwania opisu stanu wcześniejszego, żeby zachować historię "przed/po".

Opis każdego pliku w projekcie: co robi dany zakres wierszy, jakie parametry/definicje zawiera i za co jest odpowiedzialny. Stan opisany dla commita `47071d3` ("panel stonks") — czyli stan przed pracami Claude Code.

---

## 1. `src/main/resources/static/index.html` — panel klienta (formularz rezerwacji + kalendarz)

| Wiersze | Co zawiera |
|---|---|
| 1–9 | `<head>`: deklaracja dokumentu (`<!DOCTYPE html>`, `lang="pl"`), kodowanie znaków (UTF-8), tytuł karty przeglądarki, podłączenie własnego arkusza stylów `styles.css` oraz biblioteki **FullCalendar** (CSS i JS) z zewnętrznego CDN — to zewnętrzna biblioteka do rysowania kalendarza. |
| 10–11 | Nagłówek strony `<h1>`. |
| 13–18 | `<nav>` — pasek nawigacji z dwoma linkami: "Panel klienta" (ta strona) i "Panel admina" (`admin.html`). |
| 20–58 | `<div class="flexbox">` — układ dwukolumnowy (flexbox z CSS): po lewej formularz, po prawej kalendarz. |
| 22–52 | `<form id="rezerwacjaForm">` — formularz rezerwacji. Pola: imię, nazwisko, email, telefon (wszystkie `required`), marka/model pojazdu (opcjonalne), typ usługi (`<select>` z 5 zaszytymi na sztywno opcjami: PRZEGLAD, NAPRAWA, MODYFIKACJA, DIAGNOSTYKA, MONTAZ_AKCESORIOW), data i godzina wizyty (`required`), notatka (opcjonalna, textarea), miejsce na komunikat (`#msg`) i przycisk wysyłki. |
| 54–57 | Kontener kalendarza (`#calendar-container` → `#calendar`), do którego FullCalendar wstrzykuje swój widok. |
| 60–120 | `<script>` — cała logika JS strony. |
| 62–89 | Obsługa wysyłki formularza: przy `submit` blokuje domyślne przeładowanie strony (`e.preventDefault()`), wysyła `POST /api/reservations` z danymi formularza jako JSON, i na podstawie odpowiedzi (`res.ok`) wypisuje komunikat sukcesu/błędu w `#msg` oraz resetuje formularz. |
| 92–119 | Inicjalizacja FullCalendar po załadowaniu strony (`DOMContentLoaded`): widok tygodniowy z godzinami (`timeGridWeek`), zakres godzin 08:00–16:00, brak sloty "cały dzień", polski język, tydzień zaczyna się w poniedziałek. `select` (linie 102–105) — kliknięcie/zaznaczenie terminu w kalendarzu automatycznie wypełnia pola daty/godziny w formularzu. `events` (linia 106) — kalendarz dociąga zajęte terminy z `/api/reservations/calendar/fullcalendar`. Linie 112–118 — funkcja dopasowująca wysokość kalendarza do wysokości formularza (czysto kosmetyczne, responsywność). |

---

## 2. `src/main/resources/static/admin.html` — panel administratora

| Wiersze | Co zawiera |
|---|---|
| 1–41 | `<head>` — jak w `index.html` (tytuł, `styles.css`), plus **dodatkowy, lokalny styl w `<style>`** (linie 7–40) tylko na potrzeby okna modalnego edycji rezerwacji (tło przyciemniające `.modal-bg`, wygląd okienka `.modal-content`, przycisk zamknięcia `.modal-close`, odstępy między przyciskami akcji `.reservation-buttons`). |
| 42–50 | Nagłówek strony i ten sam pasek nawigacji co w `index.html`. |
| 52–62 | Filtr statusu rezerwacji: `<select id="statusFilter">` z opcjami Wszystkie/PENDING/ACCEPTED/REJECTED/CANCELLED, plus przycisk "Odśwież" wywołujący `fetchReservations()`. |
| 63–64 | Miejsce na komunikaty (`#msg`) i lista rezerwacji (`<ul id="reservationList">`), wypełniana dynamicznie przez JS. |
| 66–95 | Okno modalne edycji rezerwacji (`#modal-bg` → `#editForm`) — pola identyczne jak w formularzu klienta plus **notatka admina** i **status** (select z 4 opcjami). Domyślnie ukryte (`display:none` w CSS), pojawia się po kliknięciu "Edytuj". |
| 98 | Zmienna `reservationsCache` — lokalna kopia ostatnio pobranej listy rezerwacji z backendu (żeby filtrować bez ponownego zapytania do API). |
| 101–122 | Funkcje obsługi modala: `openModal(reservation)` wypełnia pola formularza edycji danymi konkretnej rezerwacji; `closeModal()` chowa okno; kliknięcie w tło modala (poza treścią) też je zamyka. |
| 125–154 | Obsługa zapisu formularza edycji: zbiera wartości z pól, wysyła `PUT /api/reservations/{id}` z całym obiektem rezerwacji, po sukcesie zamyka modal i odświeża listę. |
| 157–167 | `fetchReservations()` — pobiera `GET /api/reservations`, zapisuje wynik do `reservationsCache`, wywołuje renderowanie listy. |
| 169–202 | `renderReservations()` — filtruje `reservationsCache` po statusie (jeśli wybrany w filtrze) i buduje HTML listy (`innerHTML`) z danymi każdej rezerwacji oraz przyciskami akcji: Usuń, Edytuj, a warunkowo (linie 190–198) Akceptuj/Odrzuć (gdy status PENDING) albo Anuluj (gdy status ACCEPTED). **Uwaga:** dane wstawiane są bezpośrednio do `innerHTML` bez zabezpieczenia przed wstrzyknięciem kodu (patrz sekcja "Problemy" niżej). |
| 204–214 | `deleteReservation(id)` — pyta o potwierdzenie (`confirm()`), wysyła `DELETE /api/reservations/{id}`. |
| 216–229 | `changeStatus(id, status)` — wysyła `PATCH /api/reservations/{id}/status` z nowym statusem w body. |
| 231 | Podłączenie filtra statusu do `renderReservations()` przy zmianie wartości. |
| 233 | Wywołanie `fetchReservations()` przy starcie strony — ładuje listę od razu. |

---

## 3. `src/main/resources/static/styles.css` — wspólny arkusz stylów

| Wiersze | Co zawiera |
|---|---|
| 1–7 | Globalne tło (ciemny motyw, `#171717`) i kolor tekstu strony. |
| 9–11 | Kolory nagłówków (`h1`,`h2` — pomarańczowy) i etykiet formularzy (`label` — orange). |
| 13–19 | `.flexbox` — układ dwukolumnowy (formularz + kalendarz) w `index.html`. |
| 21–38 | Wygląd "kart" formularza i kontenera kalendarza (tło, padding, zaokrąglone rogi, cień). |
| 40–48 | Wygląd pól formularza (input/select/textarea) — ciemne tło, pomarańczowa ramka. |
| 50–64 | Wygląd przycisków (pomarańczowe tło, zmiana koloru po `:hover`). |
| 66–67 | Styl komunikatów (`.msg`) — żółty, wytłuszczony tekst. |
| 69–90 | Style specyficzne dla biblioteki FullCalendar (nadpisanie domyślnego wyglądu na ciemny motyw: tło komórek, kolory linii siatki, nagłówki dni). |
| 92–97 | Kolor "wydarzeń" w kalendarzu, czyli zajętych terminów (pomarańczowy blok). |
| 99–103 | Media query — poniżej 1100px szerokości układ dwukolumnowy zmienia się w jednokolumnowy (responsywność). |
| 105–120 | Dodatkowe style dla `.admin-panel` (nieużywana klasa — w obecnym `admin.html` nie występuje żaden element z klasą `admin-panel`, patrz "Problemy"). |
| 122–142 | Styl paska nawigacji (`.navbar`) używanego w obu plikach HTML. |
| 144–159 | Druga, częściowo redundantna definicja stylu przycisków (`button, .btn-orange`) — pokrywa się z regułą z linii 51–64. |
| 161–181 | Style dla listy rezerwacji w panelu admina (`.rezerwacje`, `.filterbar`, `ul#reservationList`) — część z tych klas (`.rezerwacje`, `.filterbar`) nie jest używana w obecnym `admin.html`. |

---

## 4. `src/main/resources/application.properties` — konfiguracja Springa

| Wiersze | Co zawiera |
|---|---|
| 1 | Nazwa aplikacji Springa (`warsztat`). |
| 2 | Adres bazy danych PostgreSQL (host `nozomi.proxy.rlwy.net`, port `29871`, baza `railway` — hostowana na Railway.app). |
| 3–4 | **Login i hasło do bazy danych, zapisane w czystym tekście, commitowane do repozytorium.** To krytyczny problem bezpieczeństwa (opisany w poprzednim przeglądzie). |
| 6 | `ddl-auto=update` — Hibernate automatycznie modyfikuje schemat bazy na podstawie encji Java (ryzykowne dla produkcji, brak kontrolowanych migracji). |
| 7 | `show-sql=true` — logowanie zapytań SQL do konsoli (przydatne w developmencie, warto wyłączyć na produkcji). |
| 8–9 | Dialekt Hibernate (PostgreSQL) i sterownik JDBC. |

---

## 5. Backend Java — pakiet `com.crp.warsztat`

### 5.1 `WarsztatApplication.java` (cały plik, 14 wierszy)
Punkt startowy aplikacji Spring Boot — adnotacja `@SpringBootApplication` (włącza autokonfigurację, skanowanie komponentów), metoda `main()` uruchamia serwer wbudowany (Tomcat).

### 5.2 `model/Reservation.java` — encja rezerwacji (główna tabela)
| Wiersze | Co zawiera |
|---|---|
| 5–10 | `@Entity` — mapowanie na tabelę bazy; `id` — klucz główny, generowany automatycznie przez bazę (`IDENTITY`). |
| 12–16 | Dane klienta zapisane wprost w rezerwacji (nie jako relacja do `Client`!): imię, nazwisko, email, telefon. |
| 18–20 | Dane pojazdu: marka, model. |
| 22–23 | Typ usługi jako zwykły `String` (nie relacja do encji `ServiceType`, nie enum). |
| 25–27 | Data i godzina wizyty **jako `String`**, nie `LocalDate`/`LocalTime` — komentarz w kodzie (wiersz 26) sam podaje przykładowy format `"2025-06-08"`, ale nic go nie wymusza programowo. |
| 29–31 | Notatka klienta i notatka admina (oddzielne pola tekstowe). |
| 33–35 | Status rezerwacji jako enum `ReservationStatus`, zapisywany w bazie jako tekst (`EnumType.STRING`), domyślnie `PENDING`. |
| 37–80 | Konstruktor bezparametrowy (wymagany przez JPA) + standardowe gettery/settery dla każdego pola. |

### 5.3 `model/Client.java` — encja klienta
| Wiersze | Co zawiera |
|---|---|
| 13–18 | `@Entity`, `id` jak wyżej. |
| 20–25 | Pola: `name` (imię/nazwa firmy), `email`, `phone`, `wantsNotifications` (czy chce powiadomienia), `internalNotes` (notatka wewnętrzna dla pracowników). |
| 29–31 | Konstruktor bezparametrowy. |
| 35–77 | Gettery/settery. **Uwaga:** brak settera/gettera dla `id` (`id` jest tylko do odczytu — to normalne dla klucza generowanego automatycznie, ale w innych encjach, np. `Reservation`, setter `id` istnieje — niekonsekwencja). |
| 79–80 | `setFirstName(String s) {}` — **metoda zupełnie pusta i nieużywana w całym projekcie.** Encja `Client` nie ma pola `firstName` (ma `name`), więc ta metoda nie robi nic i wygląda na pozostawiony fragment po zmianie koncepcji (klient miał kiedyś `firstName`/`lastName`, potem zmieniono na jedno pole `name`). Do usunięcia albo do wyjaśnienia, jeśli miała inny cel. |

### 5.4 `model/ServiceType.java` — encja typu usługi
| Wiersze | Co zawiera |
|---|---|
| 13–23 | `@Entity`, pola: `name`, `description`, `durationMinutes` (czas trwania w minutach), `category`. |
| 27–29 | **Konstruktor `ServiceType(String naprawaBlacharska, double v)` — całkowicie pusty (nic nie przypisuje) i nieużywany w projekcie.** Nazwa parametru (`naprawaBlacharska` = "naprawa karoserii" po polsku) sugeruje, że był to zaczątek jakiegoś konkretnego przypadku (np. tworzenie usługi "naprawa blacharska" z jakąś ceną `v`), który nie został dokończony. Zalecane usunięcie lub dokończenie. |
| 31–36 | Konstruktor właściwy, przypisujący wszystkie 4 pola. |
| 38–40 | Drugi konstruktor bezparametrowy (wymagany przez JPA). |
| 42–79 | Gettery/settery. |

### 5.5 `model/ReservationComment.java` — komentarze do rezerwacji
| Wiersze | Co zawiera |
|---|---|
| 9–14 | `@Entity`, `id`. |
| 16–17 | `@ManyToOne` relacja do `Reservation` — komentarz zawsze należy do jednej rezerwacji. |
| 19–20 | `authorType` — enum `ReservationCommentAuthorType` (CLIENT/ADMIN), zapisany jako tekst. |
| 22–24 | `authorName` (opcjonalne imię autora), `content` (treść), `createdAt` (data/godzina utworzenia — typ `LocalDateTime`, poprawnie typowany, w przeciwieństwie do dat w `Reservation`). |
| 26–34 | Konstruktor bezparametrowy + konstruktor z wszystkimi polami. |
| 38–81 | Gettery/settery. |

### 5.6 `model/ReservationStatus.java` (cały plik, 8 wierszy)
Enum z czterema możliwymi statusami rezerwacji: `PENDING`, `ACCEPTED`, `REJECTED`, `CANCELLED`. Używany w `Reservation.status` i w logice zmiany statusu w kontrolerze.

### 5.7 `model/ReservationCommentAuthorType.java` (cały plik, 6 wierszy)
Enum: `CLIENT` albo `ADMIN` — określa, kto napisał dany komentarz.

### 5.8 `dto/ReservationCalendarDTO.java` (cały plik, 11 wierszy)
Prosty obiekt transportowy (DTO) z dwoma publicznymi polami `date` i `time` — używany przez endpoint `/api/reservations/calendar` (starszy, uproszczony format kalendarza, niezależny od formatu wymaganego przez bibliotekę FullCalendar).

### 5.9 Repozytoria — `repository/*.java`
Wszystkie cztery repozytoria (`ReservationRepository`, `ClientRepository`, `ServiceTypeRepository`, `ReservationCommentRepository`) to interfejsy rozszerzające `JpaRepository<Encja, Long>` ze Spring Data JPA — dają "za darmo" podstawowe operacje CRUD (`findAll`, `findById`, `save`, `deleteById`) bez pisania jakiegokolwiek SQL. Jedyna metoda dodana ręcznie to `findByReservationId(Long)` w `ReservationCommentRepository.java:13` — Spring Data generuje jej implementację automatycznie na podstawie nazwy metody (konwencja nazewnicza, nie ma tu żadnego kodu SQL napisanego przez człowieka).

### 5.10 Kontrolery — `controller/*.java`

**`ReservationApiController.java`** (`/api/reservations`) — najważniejszy i najbardziej rozbudowany kontroler:
| Wiersze | Co zawiera |
|---|---|
| 18–22 | Wstrzyknięcie zależności `ReservationRepository` przez konstruktor. |
| 25–28 | `GET /api/reservations` — zwraca wszystkie rezerwacje. |
| 30–34 | `GET /api/reservations/{id}` — zwraca jedną rezerwację (albo pusty `Optional`, jeśli nie istnieje — **uwaga: przy braku danych zwraca puste ciało z kodem 200, a nie 404**, co jest niezgodne z dobrą praktyką REST). |
| 37–42 | `POST /api/reservations` — tworzy nową rezerwację; wymusza status `PENDING` i puste `adminNotes` niezależnie od tego, co przyśle klient (dobre zabezpieczenie przed nadpisaniem statusu przez formularz publiczny). |
| 46–49 | `DELETE /api/reservations/{id}` — usuwa rezerwację. **Brak sprawdzenia, czy dane ID istnieje** — `deleteById` na nieistniejącym ID rzuci wyjątek widoczny jako 500. |
| 52–70 | `PUT /api/reservations/{id}` — pełna aktualizacja wszystkich pól rezerwacji na podstawie przesłanego obiektu; `orElseThrow()` (wiersz 54) bez obsługi wyjątku → nieistniejące ID = błąd 500 zamiast 404. |
| 74–79 | `PATCH /api/reservations/{id}/status` — zmiana tylko statusu; `ReservationStatus.valueOf(...)` (wiersz 77) **rzuci `IllegalArgumentException` (→ 500), jeśli przesłana wartość statusu nie jest jedną z 4 dozwolonych** (np. literówka z frontendu). |
| 83–91 | `GET /api/reservations/calendar` — zwraca uproszczoną listę dat/godzin wszystkich rezerwacji (niezależnie od statusu) jako `ReservationCalendarDTO`. |
| 93–104 | `GET /api/reservations/calendar/fullcalendar` — zwraca zdarzenia w formacie zrozumiałym dla biblioteki FullCalendar, **tylko dla rezerwacji o statusie `ACCEPTED`** (wiersz 96) — to jest źródło danych o "zajętych terminach" widocznych w kalendarzu na `index.html`. |
| 106–109 | Prywatna metoda pomocnicza `endTime()` — parsuje godzinę startu (`String` → `LocalTime`) i dodaje 1 godzinę, żeby wyznaczyć koniec "zajętego" bloku w kalendarzu. Zakłada na sztywno, że każda wizyta trwa dokładnie godzinę (nie korzysta z pola `durationMinutes` z `ServiceType`, mimo że takie pole istnieje). |

**`ClientController.java`** (`/clients`) — podstawowy CRUD dla klientów: `GET` wszystkich (24–27), `GET` po ID (29–32), `POST` nowego klienta (34–37), `DELETE` po ID (39–42). **Brak endpointu do edycji (`PUT`/`PATCH`)** — dane klienta raz zapisane nie można zmienić przez API.

**`ServiceTypeController.java`** (`/service-types`) — analogicznie: `GET` wszystkich (25–28), `GET` po ID (31–34), `POST` (37–40), `DELETE` (43–46). Też brak edycji.

**`ReservationCommentController.java`** (`/reservation-comments`) — `GET` wszystkich (25–28), `GET` po ID (31–34), **`GET` po ID rezerwacji** (37–40, endpoint specyficzny: `/by-reservation/{reservationId}`), `POST` nowego komentarza (43–46), `DELETE` (49–52). Brak edycji treści komentarza po utworzeniu.

---

## Wzorce widoczne w całym kodzie (dotyczą wielu plików naraz)

1. **Brak edycji (`PUT`/`PATCH`) dla `Client`, `ServiceType`, `ReservationComment`** — tylko `Reservation` ma pełną edycję.
2. **Każdy kontroler jest publiczny** — żaden endpoint nie wymaga logowania (brak Spring Security w projekcie).
3. **Wyjątki nieobsłużone** — wzorzec `.orElseThrow()` / `deleteById()` / `Enum.valueOf()` bez `try/catch` powtarza się w kontrolerach i przy błędnych danych zwraca kod 500 zamiast sensownego 400/404.
4. **Dane wpisywane bezpośrednio do `innerHTML`** w `admin.html` (linie 179–201) — potencjalne ryzyko XSS, jeśli klient wpisze w formularzu np. znaki `<script>` w polu notatki.
5. **Dwa martwe/nieużywane fragmenty kodu** — `Client.setFirstName()` (`Client.java:79-80`) i konstruktor `ServiceType(String, double)` (`ServiceType.java:27-29`) — wygląda na pozostawione resztki po zmianie koncepcji, obie metody nic nie robią.

---

## Etap: naprawa najważniejszych problemów bezpieczeństwa i błędów (po przeglądzie)

Zmiany wprowadzone w tym etapie, bez logowania się do żadnej usługi trzeciej (Railway/GitHub) — same poprawki w kodzie.

### 1. Usunięcie hasła do bazy z repozytorium
- **`src/main/resources/application.properties:1-11`** — `spring.datasource.url`, `spring.datasource.username`, `spring.datasource.password` nie są już wpisane na sztywno, tylko odczytywane ze zmiennych środowiskowych `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` (bez wartości domyślnych — aplikacja nie wystartuje bez ich ustawienia, co jest zamierzone). Dodano też `admin.username`/`admin.password` (zmienne `ADMIN_USERNAME`/`ADMIN_PASSWORD`, z domyślną wartością dev `admin`/`admin` do szybkiego startu lokalnego).
- **`.gitignore`** — dodano wpisy `.env` i `application-local.properties`, żeby ewentualne lokalne pliki z sekretami nigdy nie trafiły do repo.
- **`SETUP.md`** (nowy plik) — instrukcja, jakie zmienne środowiskowe trzeba ustawić, żeby uruchomić aplikację, i przypomnienie, że **stare hasło do bazy Railway trzeba zrotować ręcznie w panelu Railway** (samo usunięcie go z pliku nie unieważnia hasła, które nadal widać w historii commitów — to jedyny krok wymagający zalogowania się do serwisu trzeciego, którego nie mogłem wykonać automatycznie).

### 2. Autoryzacja panelu admina (Spring Security)
- **`pom.xml`** — dodano zależności `spring-boot-starter-security` i `spring-boot-starter-validation`.
- **`src/main/java/com/crp/warsztat/config/SecurityConfig.java`** (nowy plik) — konfiguracja bezpieczeństwa: tylko strona klienta (`/`, `/index.html`, `/styles.css`), składanie rezerwacji (`POST /api/reservations`) i publiczny kalendarz zajętych terminów (`GET /api/reservations/calendar/**`) są dostępne bez logowania. Wszystko inne (panel admina, edycja/usuwanie rezerwacji, klienci, typy usług, komentarze) wymaga zalogowania metodą HTTP Basic jako użytkownik z rolą ADMIN, którego dane pochodzą ze zmiennych środowiskowych.

### 3. Obsługa błędów i walidacja danych
- **`src/main/java/com/crp/warsztat/controller/GlobalExceptionHandler.java`** (nowy plik) — globalny handler wyjątków: brak zasobu (`NoSuchElementException`) → HTTP 404, niepoprawna wartość (np. błędny status, `IllegalArgumentException`) → HTTP 400, błąd walidacji formularza (`MethodArgumentNotValidException`) → HTTP 400 z listą błędnych pól. Wcześniej wszystkie te przypadki kończyły się kodem 500.
- **`model/Reservation.java`** — dodano adnotacje walidacyjne (`@NotBlank`, `@Email`) na polach `firstName`, `lastName`, `email`, `phoneNumber`, `visitDate`, `visitTime`.
- **`controller/ReservationApiController.java`** — `createReservation`/`updateReservation` używają teraz `@Valid`; `getReservationById` i `deleteReservation` rzucają `NoSuchElementException` przy nieistniejącym ID zamiast zwracać puste `Optional`/pozwalać na wyjątek z `deleteById`.
- **`controller/ClientController.java`, `controller/ServiceTypeController.java`, `controller/ReservationCommentController.java`** — analogicznie: `getXById` rzuca wyjątek zamiast zwracać `Optional`, `deleteX` sprawdza `existsById` przed usunięciem i rzuca czytelny błąd.

### 4. Poprawka XSS w panelu admina
- **`src/main/resources/static/admin.html`** — dodano funkcję `escapeHtml()` (nowy fragment przed `let reservationsCache`) i użyto jej przy wstawianiu danych rezerwacji do `innerHTML` w `renderReservations()` (imię, nazwisko, email, telefon, status, pojazd, usługa, notatki). Wcześniej złośliwy tekst wpisany przez klienta w formularzu (np. w polu notatki) mógłby wykonać się jako kod w przeglądarce admina.

### Czego NIE zrobiono w tym etapie (wymaga Twojej akcji)

- [ ] **TODO (do zrobienia przez właściciela projektu): fizyczna rotacja hasła do bazy danych na Railway.** Wymaga zalogowania się do panelu Railway, co jest poza zasięgiem automatycznych zmian w kodzie. Stare hasło, które było w historii commitów, nadal jest aktywne, dopóki nie zostanie zmienione. Obecnie odłożone — nie ma to wpływu na dalsze prace nad kodem.
- Migracje bazy danych (Flyway/Liquibase) i wyłączenie `ddl-auto=update` na produkcji — odłożone na później.

---

## Etap: testy jednostkowe i CI/CD (bez logowania do usług trzecich)

### 1. Testy jednostkowe
- **`pom.xml`** — dodano zależności testowe: `spring-security-test` i `h2` (baza danych w pamięci, tylko do testów — produkcja wciąż używa PostgreSQL).
- **`src/test/resources/application.properties`** (nowy plik) — konfiguracja używana wyłącznie podczas testów: baza H2 w pamięci zamiast prawdziwego Postgresa na Railway, dzięki czemu testy (i `WarsztatApplicationTests.contextLoads()`) działają offline, bez potrzeby dostępu do żywej bazy danych i bez podawania prawdziwych danych logowania.
- **`src/test/java/com/crp/warsztat/controller/ReservationApiControllerTest.java`** (nowy plik) — 7 testów najważniejszego kontrolera (`ReservationApiController`), m.in.: `POST` zawsze wymusza status `PENDING` niezależnie od tego, co przyśle klient; formularz bez wymaganego pola (email) zwraca 400 (dzięki walidacji z poprzedniego etapu); pobranie/usunięcie nieistniejącej rezerwacji zwraca 404 (dzięki `GlobalExceptionHandler`); zmiana statusu na niepoprawną wartość zwraca 400, a na poprawną — 200. Testy działają na mocku repozytorium (`@MockBean`) — nie potrzebują żadnej bazy danych — i z wyłączonymi filtrami bezpieczeństwa (`@AutoConfigureMockMvc(addFilters = false)`), bo sprawdzają logikę kontrolera, a nie autoryzację.

### 2. CI/CD
- **`.github/workflows/ci.yml`** (nowy plik) — workflow GitHub Actions, uruchamiany automatycznie przy każdym `push` i każdym pull requeście: instaluje JDK 21, buduje projekt i uruchamia wszystkie testy (`./mvnw verify`). Dzięki testom działającym na H2 (patrz wyżej) workflow nie wymaga żadnych sekretów ani dostępu do prawdziwej bazy danych — działa "od zera" na czystym repozytorium.

### Nadal odłożone na później
- Testy dla pozostałych kontrolerów (`ClientController`, `ServiceTypeController`, `ReservationCommentController`) i dla `SecurityConfig`.
- Migracje bazy danych (Flyway/Liquibase), wyłączenie `ddl-auto=update` na produkcji.
- Rotacja hasła do bazy na Railway (patrz TODO wyżej).

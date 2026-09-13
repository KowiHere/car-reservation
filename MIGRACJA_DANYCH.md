# Stare dane po zmianie modelu rezerwacji

Po wprowadzeniu silnika planowania (`SchedulingService`) pole `serviceType` w tabeli `reservation` zmieniło się z tekstu (np. `"PRZEGLAD"`) na relację do tabeli `service_type` (kolumna `service_type_id`). Stare rezerwacje, które istniały w bazie **przed** tą zmianą, będą miały:

- pustą kolumnę `service_type_id` (bo stary tekst nie dało się automatycznie dopasować do konkretnego wiersza `service_type`),
- puste `end_date`, `end_time`, `station_number` (bo nikt jeszcze nie przeliczył dla nich terminu).

**Aplikacja się na tym nie wywali** — takie rekordy są pomijane w publicznym kalendarzu (`/api/reservations/calendar/fullcalendar`) i wyświetlają się jako "-" w panelu admina. Ale nie są uwzględniane przy sprawdzaniu zajętości stanowisk, więc jeśli w bazie są **realne, aktualne** rezerwacje sprzed tej zmiany, mogą zostać "zgubione" (nowa rezerwacja może zostać przydzielona na stanowisko, które w rzeczywistości jest już zajęte przez starą, nieprzeliczoną rezerwację).

## Co zrobić (wymaga dostępu do panelu Railway → ja tego nie mogę wykonać)

1. Sprawdź, czy w tabeli `reservation` są wiersze z pustym `service_type_id` i statusem `PENDING`/`ACCEPTED`:
   ```sql
   SELECT id, first_name, last_name, visit_date, visit_time, status
   FROM reservation
   WHERE service_type_id IS NULL AND status IN ('PENDING', 'ACCEPTED');
   ```
2. Jeśli takie wiersze istnieją i są aktualne (dotyczą przyszłych wizyt), dla każdego ręcznie:
   - przypisz pasujący `service_type_id` (zobacz `SELECT id, name FROM service_type;`),
   - przelicz `end_date`/`end_time`/`station_number` — najprościej: otwórz rezerwację w panelu admina i zapisz ją ponownie (edycja przez `PUT /api/reservations/{id}` automatycznie przeliczy te pola przez `SchedulingService`).
3. Jeśli takich wierszy nie ma (np. baza jest testowa/pusta) — nic nie trzeba robić.

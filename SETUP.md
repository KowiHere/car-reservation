# Konfiguracja lokalna

Aplikacja wymaga zmiennych środowiskowych do połączenia z bazą danych — nie są już zapisane w repozytorium.

## Lokalna baza danych (Docker) — bez logowania do Railway

Do dalszej pracy nad funkcjonalnością nie trzeba łączyć się z bazą produkcyjną na Railway. `docker-compose.yml` w repozytorium stawia lokalnego PostgreSQL (dane trzymane na dysku w wolumenie Dockera, przetrwają restart kontenera):

```bash
docker compose up -d          # uruchamia lokalnego Postgresa w tle
cp .env.example .env           # domyślne dane logowania pasujące do docker-compose.yml
export $(grep -v '^#' .env | xargs)
./mvnw spring-boot:run
```

Baza wystartuje pusta — Hibernate utworzy tabele (`ddl-auto=update`), a `data.sql` wstawi domyślne typy usług. Żeby zatrzymać/usunąć lokalną bazę:

```bash
docker compose down       # zatrzymuje kontener, dane zostają w wolumenie
docker compose down -v    # zatrzymuje i usuwa dane lokalne od zera
```

Baza na Railway (produkcyjna) zostaje nietknięta — używana jest tylko wtedy, gdy `DB_URL`/`DB_USERNAME`/`DB_PASSWORD` wskazują na nią (patrz niżej).

Wymagane zmienne środowiskowe:

| Zmienna | Opis |
|---|---|
| `DB_URL` | Adres JDBC bazy PostgreSQL, np. `jdbc:postgresql://host:port/nazwa_bazy` |
| `DB_USERNAME` | Login do bazy danych |
| `DB_PASSWORD` | Hasło do bazy danych |

Opcjonalne (logowanie do panelu admina, domyślnie `admin`/`admin` — **zmień na produkcji**):

| Zmienna | Opis |
|---|---|
| `ADMIN_USERNAME` | Login administratora |
| `ADMIN_PASSWORD` | Hasło administratora |

## Uwaga dot. bezpieczeństwa

Poprzednio hasło do bazy danych (Railway) było zapisane w czystym tekście w `application.properties` i trafiło do historii repozytorium Git. **Jeśli tego jeszcze nie zrobiono, należy zrotować (zmienić) to hasło w panelu Railway** — samo usunięcie go z pliku nie unieważnia starego hasła, które nadal jest widoczne w historii commitów.

## Uruchomienie z inną bazą (np. produkcyjną na Railway)

Jeśli mimo to trzeba połączyć się z konkretną, inną bazą (np. produkcyjną), ustaw zmienne ręcznie zamiast korzystać z `.env`:

```bash
export DB_URL=jdbc:postgresql://host:port/nazwa_bazy
export DB_USERNAME=...
export DB_PASSWORD=...
export ADMIN_USERNAME=admin
export ADMIN_PASSWORD=twoje_bezpieczne_haslo
./mvnw spring-boot:run
```

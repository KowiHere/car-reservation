# Konfiguracja lokalna

Aplikacja wymaga zmiennych środowiskowych do połączenia z bazą danych — nie są już zapisane w repozytorium.

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

## Przykład uruchomienia lokalnego

```bash
export DB_URL=jdbc:postgresql://localhost:5432/warsztat
export DB_USERNAME=postgres
export DB_PASSWORD=twoje_haslo
export ADMIN_USERNAME=admin
export ADMIN_PASSWORD=twoje_bezpieczne_haslo
./mvnw spring-boot:run
```

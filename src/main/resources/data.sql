-- Domyślne typy usług (idempotentne — nie duplikuje wpisów przy kolejnych startach)
INSERT INTO service_type (name, description, duration_minutes, category)
SELECT 'Przegląd', 'Przegląd okresowy pojazdu', 60, 'Przeglądy'
WHERE NOT EXISTS (SELECT 1 FROM service_type WHERE name = 'Przegląd');

INSERT INTO service_type (name, description, duration_minutes, category)
SELECT 'Naprawa', 'Naprawa mechaniczna', 240, 'Naprawy'
WHERE NOT EXISTS (SELECT 1 FROM service_type WHERE name = 'Naprawa');

INSERT INTO service_type (name, description, duration_minutes, category)
SELECT 'Modyfikacja', 'Modyfikacja/lift kit — praca wielodniowa', 2880, 'Modernizacje'
WHERE NOT EXISTS (SELECT 1 FROM service_type WHERE name = 'Modyfikacja');

INSERT INTO service_type (name, description, duration_minutes, category)
SELECT 'Diagnostyka', 'Diagnostyka komputerowa', 30, 'Przeglądy'
WHERE NOT EXISTS (SELECT 1 FROM service_type WHERE name = 'Diagnostyka');

INSERT INTO service_type (name, description, duration_minutes, category)
SELECT 'Montaż akcesoriów', 'Montaż akcesoriów i wyposażenia dodatkowego', 120, 'Modernizacje'
WHERE NOT EXISTS (SELECT 1 FROM service_type WHERE name = 'Montaż akcesoriów');

-- Crear la tabla con el nombre correcto
CREATE TABLE ranks (
    rank VARCHAR(20) NOT NULL PRIMARY KEY,
    min_xp INTEGER NOT NULL,
    max_xp INTEGER NULL  -- Permitir NULL para el rango más alto
);
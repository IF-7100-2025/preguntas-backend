ALTER TABLE users ADD COLUMN last_activity DATE DEFAULT CURRENT_DATE NOT NULL;
ALTER TABLE users ADD COLUMN daily_streak INTEGER DEFAULT 1 NOT NULL;

-- Agregar columna para guardar el nombre del rango actual
ALTER TABLE users ADD COLUMN current_rank VARCHAR(20);

-- Setear el rango inicial según XP actual (por defecto será 0 → Aprendiz)
UPDATE users
SET current_rank = (
    SELECT rank FROM ranks
    WHERE xp_amount >= ranks.min_xp
      AND (ranks.max_xp IS NULL OR xp_amount <= ranks.max_xp)
    LIMIT 1
);

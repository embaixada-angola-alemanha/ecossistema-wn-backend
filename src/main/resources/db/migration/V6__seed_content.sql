-- ═══════════════════════════════════════════════════════════════
-- V6 — Enhance seed content: featured flags, realistic dates,
--       Czech translations for categories
-- ═══════════════════════════════════════════════════════════════

-- ─── Featured articles ────────────────────────────────────────
UPDATE articles SET featured = true
WHERE slug IN (
  'berlim-homenageia-embaixadora-cessante-de-angola-balbina-dias-da-silva-em-distintas-cerimonias-de-despedida',
  'angola-marca-presenca-historica-na-fruit-logistica-2025-em-berlim',
  'embaixada-de-angola-em-berlim-celebra-49-anos-de-independencia-com-evento-cultural-memoravel',
  'a-rica-biodiversidade-de-angola'
);

-- ─── Spread published_at over realistic date range ────────────
-- Distribute 166 articles evenly from 2021-03-01 to 2025-04-14
WITH numbered AS (
  SELECT id,
         ROW_NUMBER() OVER (ORDER BY created_at ASC) AS rn,
         COUNT(*) OVER () AS total
  FROM articles
)
UPDATE articles a
SET published_at = '2021-03-01 10:00:00+01'::timestamptz
    + ((n.rn - 1)::double precision / GREATEST(n.total - 1, 1)::double precision)
      * INTERVAL '1505 days'
FROM numbered n
WHERE a.id = n.id;

-- ─── Czech translations for categories ────────────────────────
UPDATE categories SET nome_cs = 'Diplomacie'              WHERE slug = 'diplomacia';
UPDATE categories SET nome_cs = 'Politika'                WHERE slug = 'politica';
UPDATE categories SET nome_cs = 'Ekonomika'               WHERE slug = 'economia';
UPDATE categories SET nome_cs = 'Kultura'                 WHERE slug = 'cultura';
UPDATE categories SET nome_cs = 'Diaspora'                WHERE slug = 'diaspora';
UPDATE categories SET nome_cs = 'Turismus'                WHERE slug = 'turismo';
UPDATE categories SET nome_cs = 'Sport'                   WHERE slug = 'desporto';
UPDATE categories SET nome_cs = 'Hlavní zprávy'           WHERE slug = 'destaques';
UPDATE categories SET nome_cs = 'Reportáže'               WHERE slug = 'reportagem';
UPDATE categories SET nome_cs = 'Webináře'                WHERE slug = 'webinares';
UPDATE categories SET nome_cs = 'Společenská odpovědnost' WHERE slug = 'responsabilidade-social';

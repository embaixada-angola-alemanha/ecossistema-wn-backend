-- ═══════════════════════════════════════════════════════════════
-- V7 — Convert ALL CAPS / Title Case titles to Sentence case
-- ═══════════════════════════════════════════════════════════════

-- Sentence case: lowercase everything, capitalise first letter of each sentence.
CREATE OR REPLACE FUNCTION pg_temp.to_sentence_case(input TEXT)
RETURNS TEXT AS $$
DECLARE
    result TEXT;
    i INTEGER;
    ch TEXT;
    capitalize_next BOOLEAN;
BEGIN
    IF input IS NULL OR input = '' THEN RETURN input; END IF;

    result := lower(input);
    -- Capitalize the very first letter
    result := upper(left(result, 1)) || substring(result from 2);

    -- Capitalize after sentence boundaries: . ? ! : – —
    capitalize_next := false;
    i := 2;
    WHILE i <= length(result) LOOP
        ch := substring(result from i for 1);
        IF capitalize_next AND ch ~ '[a-záàâãéèêíïóôõúüçñ]' THEN
            result := left(result, i - 1) || upper(ch) || substring(result from i + 1);
            capitalize_next := false;
        ELSIF ch IN ('.', '?', '!', ':', '–', '—') THEN
            capitalize_next := true;
        ELSIF ch <> ' ' AND capitalize_next THEN
            -- Non-letter, non-space after punctuation — keep waiting only for letters
            IF ch !~ '[""''«»(]' THEN
                capitalize_next := false;
            END IF;
        END IF;
        i := i + 1;
    END LOOP;

    RETURN result;
END;
$$ LANGUAGE plpgsql;

-- Word-boundary-aware proper noun fixer.
-- Uses \m (start of word) and \M (end of word) PostgreSQL regex anchors.
CREATE OR REPLACE FUNCTION pg_temp.fix_proper_nouns(input TEXT)
RETURNS TEXT AS $$
DECLARE
    result TEXT := input;
BEGIN
    IF input IS NULL OR input = '' THEN RETURN input; END IF;

    -- ── Acronyms (whole-word only) ──────────────────────────
    result := regexp_replace(result, '\mue\M',       'UE',       'gi');
    result := regexp_replace(result, '\mifi\M',      'IFI',      'gi');
    result := regexp_replace(result, '\mrtp\M',      'RTP',      'gi');
    result := regexp_replace(result, '\msadc\M',     'SADC',     'gi');
    result := regexp_replace(result, '\mcplp\M',     'CPLP',     'gi');
    result := regexp_replace(result, '\monu\M',      'ONU',      'gi');
    result := regexp_replace(result, '\mpib\M',      'PIB',      'gi');
    result := regexp_replace(result, '\mbp\M',       'BP',       'gi');
    result := regexp_replace(result, '\meni\M',      'ENI',      'gi');
    result := regexp_replace(result, '\maipex\M',    'AIPEX',    'gi');
    result := regexp_replace(result, '\munesco\M',   'UNESCO',   'gi');
    result := regexp_replace(result, '\mcovid-19\M', 'COVID-19', 'gi');
    result := regexp_replace(result, '\mdj\M',       'DJ',       'gi');
    result := regexp_replace(result, '\mdw\M',       'DW',       'gi');
    result := regexp_replace(result, '\moms\M',      'OMS',      'gi');
    result := regexp_replace(result, '\moua\M',      'OUA',      'gi');
    result := regexp_replace(result, '\mfapla\M',    'FAPLA',    'gi');
    result := regexp_replace(result, '\munita\M',    'UNITA',    'gi');
    result := regexp_replace(result, '\mmpla\M',     'MPLA',     'gi');
    result := regexp_replace(result, '\miaa\M',      'IAA',      'gi');
    result := regexp_replace(result, '\mgmbh\M',     'GmbH',     'gi');
    result := regexp_replace(result, '\magtsa\M',    'AGTSA',    'gi');
    result := regexp_replace(result, '\musd\M',      'USD',      'gi');
    result := regexp_replace(result, '\mpgr\M',      'PGR',      'gi');
    result := regexp_replace(result, '\makz\M',      'AKZ',      'gi');

    -- ── Countries, cities, continents (capitalise first letter) ───
    result := regexp_replace(result, '\mangola\M',       'Angola',       'gi');
    result := regexp_replace(result, '\malemanha\M',     'Alemanha',     'gi');
    result := regexp_replace(result, '\mberlim\M',       'Berlim',       'gi');
    result := regexp_replace(result, '\mluanda\M',       'Luanda',       'gi');
    result := regexp_replace(result, '\mportugal\M',     'Portugal',     'gi');
    result := regexp_replace(result, '\mbrasil\M',       'Brasil',       'gi');
    result := regexp_replace(result, '\mmoçambique\M',   'Moçambique',   'gi');
    result := regexp_replace(result, '\meuropa\M',       'Europa',       'gi');
    result := regexp_replace(result, '\máfrica\M',       'África',       'gi');
    result := regexp_replace(result, '\mparis\M',        'Paris',        'gi');
    result := regexp_replace(result, '\mturquia\M',      'Turquia',      'gi');
    result := regexp_replace(result, '\mbélgica\M',      'Bélgica',      'gi');
    result := regexp_replace(result, '\meslovénia\M',    'Eslovénia',    'gi');
    result := regexp_replace(result, '\mchina\M',        'China',        'gi');
    result := regexp_replace(result, '\misrael\M',       'Israel',       'gi');
    result := regexp_replace(result, '\mcongo\M',        'Congo',        'gi');
    result := regexp_replace(result, '\mbotswana\M',     'Botswana',     'gi');
    result := regexp_replace(result, '\megipto\M',       'Egipto',       'gi');
    result := regexp_replace(result, '\mcunene\M',       'Cunene',       'gi');
    result := regexp_replace(result, '\mcuanza-sul\M',   'Cuanza-Sul',   'gi');
    result := regexp_replace(result, '\mcuanza-norte\M', 'Cuanza-Norte', 'gi');
    result := regexp_replace(result, '\mzaire\M',        'Zaire',        'gi');
    result := regexp_replace(result, '\msaurimo\M',      'Saurimo',      'gi');
    result := regexp_replace(result, '\mcafunfo\M',      'Cafunfo',      'gi');
    result := regexp_replace(result, '\mmacau\M',        'Macau',        'gi');
    result := regexp_replace(result, '\mangolana\M',     'Angolana',     'gi');
    result := regexp_replace(result, '\mangolanos\M',    'Angolanos',    'gi');
    result := regexp_replace(result, '\mangolano\M',     'Angolano',     'gi');

    -- ── People ──────────────────────────────────────────────
    result := regexp_replace(result, '\mjoão lourenço\M',           'João Lourenço',           'gi');
    result := regexp_replace(result, '\mbalbina dias da silva\M',   'Balbina Dias da Silva',   'gi');
    result := regexp_replace(result, '\mmanuela sambo\M',           'Manuela Sambo',           'gi');
    result := regexp_replace(result, '\mraul danda\M',              'Raul Danda',              'gi');
    result := regexp_replace(result, '\mbento ribeiro\M',           'Bento Ribeiro',           'gi');
    result := regexp_replace(result, '\mana paula sacramento\M',    'Ana Paula Sacramento',    'gi');
    result := regexp_replace(result, '\mtéte antónio\M',            'Téte António',            'gi');
    result := regexp_replace(result, '\mmatshidiso moeti\M',        'Matshidiso Moeti',        'gi');
    result := regexp_replace(result, '\mmerkel\M',                  'Merkel',                  'gi');
    result := regexp_replace(result, '\mmandume\M',                 'Mandume',                 'gi');
    result := regexp_replace(result, '\mmafrano\M',                 'Mafrano',                 'gi');
    result := regexp_replace(result, '\mlito bolton\M',             'Lito Bolton',             'gi');
    result := regexp_replace(result, '\mschulze\M',                 'Schulze',                 'gi');
    result := regexp_replace(result, '\msilvia lutucuta\M',         'Silvia Lutucuta',         'gi');
    result := regexp_replace(result, '\msílvia lutucuta\M',         'Sílvia Lutucuta',         'gi');

    -- ── Organisations ───────────────────────────────────────
    result := regexp_replace(result, '\mafreximbank\M',      'Afreximbank',      'gi');
    result := regexp_replace(result, '\melsewedy\M',         'Elsewedy',         'gi');
    result := regexp_replace(result, '\mafricell\M',         'Africell',         'gi');
    result := regexp_replace(result, '\msunavest\M',         'Sunavest',         'gi');
    result := regexp_replace(result, '\minfotur\M',          'Infotur',          'gi');
    result := regexp_replace(result, '\mfruit logistica\M',  'Fruit Logistica',  'gi');
    result := regexp_replace(result, '\mtony blair\M',       'Tony Blair',       'gi');
    result := regexp_replace(result, '\mwelwitschia\M',      'Welwitschia',      'gi');
    result := regexp_replace(result, '\mfriedensdorf\M',     'Friedensdorf',     'gi');
    result := regexp_replace(result, '\mafrika verein\M',    'Afrika Verein',    'gi');
    result := regexp_replace(result, '\mafrika-verein\M',   'Afrika-Verein',    'gi');
    result := regexp_replace(result, '\mreino unido\M',     'Reino Unido',      'gi');
    result := regexp_replace(result, '\mbanco mundial\M',    'Banco Mundial',    'gi');
    result := regexp_replace(result, '\munião africana\M',   'União Africana',   'gi');

    -- ── Portuguese months (always capitalised in PT) ────────
    result := regexp_replace(result, '\mjaneiro\M',   'Janeiro',   'gi');
    result := regexp_replace(result, '\mfevereiro\M', 'Fevereiro', 'gi');
    result := regexp_replace(result, '\mmarço\M',     'Março',     'gi');
    result := regexp_replace(result, '\mabril\M',     'Abril',     'gi');
    result := regexp_replace(result, '\mmaio\M',      'Maio',      'gi');
    result := regexp_replace(result, '\mjunho\M',     'Junho',     'gi');
    result := regexp_replace(result, '\mjulho\M',     'Julho',     'gi');
    result := regexp_replace(result, '\magosto\M',    'Agosto',    'gi');
    result := regexp_replace(result, '\msetembro\M',  'Setembro',  'gi');
    result := regexp_replace(result, '\moutubro\M',   'Outubro',   'gi');
    result := regexp_replace(result, '\mnovembro\M',  'Novembro',  'gi');
    result := regexp_replace(result, '\mdezembro\M',  'Dezembro',  'gi');

    RETURN result;
END;
$$ LANGUAGE plpgsql;

-- ─── Step 1: Sentence case ──────────────────────────────────
UPDATE articles SET titulo_pt = pg_temp.to_sentence_case(titulo_pt)
WHERE titulo_pt IS NOT NULL;

UPDATE articles SET meta_titulo_pt = pg_temp.to_sentence_case(meta_titulo_pt)
WHERE meta_titulo_pt IS NOT NULL;

UPDATE articles SET excerto_pt = pg_temp.to_sentence_case(excerto_pt)
WHERE excerto_pt IS NOT NULL;

-- ─── Step 2: Fix proper nouns and acronyms ──────────────────
UPDATE articles SET titulo_pt = pg_temp.fix_proper_nouns(titulo_pt)
WHERE titulo_pt IS NOT NULL;

UPDATE articles SET meta_titulo_pt = pg_temp.fix_proper_nouns(meta_titulo_pt)
WHERE meta_titulo_pt IS NOT NULL;

UPDATE articles SET excerto_pt = pg_temp.fix_proper_nouns(excerto_pt)
WHERE excerto_pt IS NOT NULL;

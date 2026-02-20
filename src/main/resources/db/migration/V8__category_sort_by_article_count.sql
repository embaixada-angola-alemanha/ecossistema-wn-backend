-- V8: Sort categories by article count (most articles first)
UPDATE categories c
SET sort_order = sub.rank
FROM (
    SELECT cat.id AS category_id,
           ROW_NUMBER() OVER (ORDER BY COUNT(a.id) DESC) AS rank
    FROM categories cat
    LEFT JOIN articles a ON a.category_id = cat.id
    GROUP BY cat.id
) sub
WHERE c.id = sub.category_id;

-- V2: Editorial workflow — versioning + internal comments

CREATE TABLE article_versions (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    article_id  UUID NOT NULL REFERENCES articles(id) ON DELETE CASCADE,
    version     INT NOT NULL,
    titulo_pt   VARCHAR(300),
    conteudo_pt TEXT,
    conteudo_en TEXT,
    conteudo_de TEXT,
    excerto_pt  VARCHAR(500),
    snapshot    TEXT,
    change_summary VARCHAR(500),
    created_by  VARCHAR(200),
    created_at  TIMESTAMP WITH TIME ZONE DEFAULT now(),
    updated_at  TIMESTAMP WITH TIME ZONE DEFAULT now(),

    UNIQUE(article_id, version)
);

CREATE INDEX idx_article_versions_article ON article_versions(article_id);

CREATE TABLE editorial_comments (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    article_id  UUID NOT NULL REFERENCES articles(id) ON DELETE CASCADE,
    author_id   UUID NOT NULL REFERENCES authors(id),
    author_name VARCHAR(200) NOT NULL,
    conteudo    TEXT NOT NULL,
    tipo        VARCHAR(20) DEFAULT 'COMMENT',
    parent_id   UUID REFERENCES editorial_comments(id),
    resolved    BOOLEAN DEFAULT false,
    created_at  TIMESTAMP WITH TIME ZONE DEFAULT now(),
    updated_at  TIMESTAMP WITH TIME ZONE DEFAULT now()
);

CREATE INDEX idx_editorial_comments_article ON editorial_comments(article_id);
CREATE INDEX idx_editorial_comments_author ON editorial_comments(author_id);

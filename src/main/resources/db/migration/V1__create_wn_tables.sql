-- Categories
CREATE TABLE categories (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    slug VARCHAR(100) NOT NULL UNIQUE,
    nome_pt VARCHAR(200) NOT NULL,
    nome_en VARCHAR(200),
    nome_de VARCHAR(200),
    nome_cs VARCHAR(200),
    descricao_pt VARCHAR(500),
    descricao_en VARCHAR(500),
    descricao_de VARCHAR(500),
    cor VARCHAR(50),
    sort_order INTEGER DEFAULT 0,
    activo BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    version BIGINT DEFAULT 0
);

-- Tags
CREATE TABLE tags (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    slug VARCHAR(100) NOT NULL UNIQUE,
    nome_pt VARCHAR(100) NOT NULL,
    nome_en VARCHAR(100),
    nome_de VARCHAR(100),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    version BIGINT DEFAULT 0
);

-- Authors
CREATE TABLE authors (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nome VARCHAR(200) NOT NULL,
    slug VARCHAR(100) UNIQUE,
    bio_pt TEXT,
    bio_en TEXT,
    bio_de TEXT,
    email VARCHAR(200),
    avatar_id UUID,
    keycloak_id VARCHAR(200),
    role VARCHAR(50),
    activo BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    version BIGINT DEFAULT 0
);

CREATE INDEX idx_authors_keycloak ON authors(keycloak_id);

-- Media files
CREATE TABLE media_files (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    file_name VARCHAR(500) NOT NULL,
    original_name VARCHAR(500) NOT NULL,
    mime_type VARCHAR(100) NOT NULL,
    size BIGINT NOT NULL,
    bucket VARCHAR(100) NOT NULL,
    object_key VARCHAR(500) NOT NULL,
    alt_pt VARCHAR(300),
    alt_en VARCHAR(300),
    alt_de VARCHAR(300),
    width INTEGER,
    height INTEGER,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    version BIGINT DEFAULT 0
);

-- Articles
CREATE TABLE articles (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    slug VARCHAR(300) NOT NULL UNIQUE,
    titulo_pt VARCHAR(300) NOT NULL,
    titulo_en VARCHAR(300),
    titulo_de VARCHAR(300),
    titulo_cs VARCHAR(300),
    conteudo_pt TEXT,
    conteudo_en TEXT,
    conteudo_de TEXT,
    conteudo_cs TEXT,
    excerto_pt VARCHAR(500),
    excerto_en VARCHAR(500),
    excerto_de VARCHAR(500),
    meta_titulo_pt VARCHAR(160),
    meta_descricao_pt VARCHAR(320),
    meta_keywords VARCHAR(500),
    estado VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    category_id UUID REFERENCES categories(id),
    author_id UUID REFERENCES authors(id),
    featured_image_id UUID REFERENCES media_files(id),
    featured BOOLEAN NOT NULL DEFAULT false,
    published_at TIMESTAMPTZ,
    scheduled_at TIMESTAMPTZ,
    view_count BIGINT DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    version BIGINT DEFAULT 0
);

CREATE INDEX idx_articles_slug ON articles(slug);
CREATE INDEX idx_articles_estado ON articles(estado);
CREATE INDEX idx_articles_published ON articles(published_at DESC);
CREATE INDEX idx_articles_category ON articles(category_id);
CREATE INDEX idx_articles_author ON articles(author_id);
CREATE INDEX idx_articles_featured ON articles(featured) WHERE featured = true;
CREATE INDEX idx_articles_scheduled ON articles(scheduled_at) WHERE scheduled_at IS NOT NULL AND estado != 'PUBLISHED';

-- Article-Tag many-to-many
CREATE TABLE article_tags (
    article_id UUID NOT NULL REFERENCES articles(id) ON DELETE CASCADE,
    tag_id UUID NOT NULL REFERENCES tags(id) ON DELETE CASCADE,
    PRIMARY KEY (article_id, tag_id)
);

CREATE INDEX idx_article_tags_tag ON article_tags(tag_id);

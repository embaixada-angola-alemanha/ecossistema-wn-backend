-- V3: Newsletter subscribers

CREATE TABLE newsletter_subscribers (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email               VARCHAR(300) NOT NULL UNIQUE,
    nome                VARCHAR(200),
    idioma              VARCHAR(10) DEFAULT 'pt',
    activo              BOOLEAN DEFAULT true,
    confirmation_token  VARCHAR(100),
    confirmed           BOOLEAN DEFAULT false,
    created_at          TIMESTAMP WITH TIME ZONE DEFAULT now(),
    updated_at          TIMESTAMP WITH TIME ZONE DEFAULT now()
);

CREATE INDEX idx_newsletter_email ON newsletter_subscribers(email);
CREATE INDEX idx_newsletter_activo ON newsletter_subscribers(activo, confirmed);

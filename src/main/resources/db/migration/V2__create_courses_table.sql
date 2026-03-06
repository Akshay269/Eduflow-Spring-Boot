CREATE TABLE courses (
    id              BIGSERIAL PRIMARY KEY,
    title           VARCHAR(200) NOT NULL,
    description     TEXT,
    thumbnail_url   VARCHAR(500),
    price           DECIMAL(10,2) DEFAULT 0.00,
    is_published    BOOLEAN DEFAULT FALSE,
    instructor_id   BIGINT NOT NULL REFERENCES users(id),
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE sections (
    id          BIGSERIAL PRIMARY KEY,
    title       VARCHAR(200) NOT NULL,
    order_index INTEGER NOT NULL,
    course_id   BIGINT NOT NULL REFERENCES courses(id) ON DELETE CASCADE,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE lessons (
    id           BIGSERIAL PRIMARY KEY,
    title        VARCHAR(200) NOT NULL,
    content_url  VARCHAR(500),
    content_type VARCHAR(50),
    duration     INTEGER,
    order_index  INTEGER NOT NULL,
    is_free      BOOLEAN DEFAULT FALSE,
    section_id   BIGINT NOT NULL REFERENCES sections(id) ON DELETE CASCADE,
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
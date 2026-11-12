-- 커뮤니티 P1 스키마 (PostgreSQL)
-- 운영 ddl-auto=validate 환경에서는 배포 전 이 스크립트를 실행하세요.

CREATE TABLE IF NOT EXISTS community_posts (
    post_id         BIGSERIAL PRIMARY KEY,
    user_id         BIGINT NOT NULL REFERENCES "Users"(user_id),
    category        VARCHAR(30) NOT NULL,
    title           VARCHAR(50) NOT NULL,
    body            VARCHAR(4000) NOT NULL,
    tags            TEXT,
    like_count      INT NOT NULL DEFAULT 0,
    comment_count   INT NOT NULL DEFAULT 0,
    view_count      INT NOT NULL DEFAULT 0,
    status          VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at      TIMESTAMP,
    updated_at      TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_community_posts_user ON community_posts(user_id);
CREATE INDEX IF NOT EXISTS idx_community_posts_category ON community_posts(category);
CREATE INDEX IF NOT EXISTS idx_community_posts_status_created ON community_posts(status, created_at);

CREATE TABLE IF NOT EXISTS community_post_images (
    post_image_id   BIGSERIAL PRIMARY KEY,
    post_id         BIGINT NOT NULL REFERENCES community_posts(post_id) ON DELETE CASCADE,
    image_url       VARCHAR(500) NOT NULL,
    sort_order      INT NOT NULL DEFAULT 0,
    created_at      TIMESTAMP,
    updated_at      TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_community_post_images_post ON community_post_images(post_id);

CREATE TABLE IF NOT EXISTS community_post_likes (
    post_like_id    BIGSERIAL PRIMARY KEY,
    post_id         BIGINT NOT NULL REFERENCES community_posts(post_id) ON DELETE CASCADE,
    user_id         BIGINT NOT NULL REFERENCES "Users"(user_id),
    created_at      TIMESTAMP,
    updated_at      TIMESTAMP,
    CONSTRAINT uk_community_post_likes_post_user UNIQUE (post_id, user_id)
);

CREATE TABLE IF NOT EXISTS community_post_bookmarks (
    post_bookmark_id BIGSERIAL PRIMARY KEY,
    post_id          BIGINT NOT NULL REFERENCES community_posts(post_id) ON DELETE CASCADE,
    user_id          BIGINT NOT NULL REFERENCES "Users"(user_id),
    created_at       TIMESTAMP,
    updated_at       TIMESTAMP,
    CONSTRAINT uk_community_post_bookmarks_post_user UNIQUE (post_id, user_id)
);

CREATE TABLE IF NOT EXISTS community_post_views (
    post_view_id    BIGSERIAL PRIMARY KEY,
    post_id         BIGINT NOT NULL REFERENCES community_posts(post_id) ON DELETE CASCADE,
    user_id         BIGINT NOT NULL REFERENCES "Users"(user_id),
    created_at      TIMESTAMP,
    updated_at      TIMESTAMP,
    CONSTRAINT uk_community_post_views_post_user UNIQUE (post_id, user_id)
);

CREATE TABLE IF NOT EXISTS community_comments (
    comment_id          BIGSERIAL PRIMARY KEY,
    post_id             BIGINT NOT NULL REFERENCES community_posts(post_id) ON DELETE CASCADE,
    user_id             BIGINT NOT NULL REFERENCES "Users"(user_id),
    parent_comment_id   BIGINT REFERENCES community_comments(comment_id),
    body                VARCHAR(1000) NOT NULL,
    like_count          INT NOT NULL DEFAULT 0,
    status              VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at          TIMESTAMP,
    updated_at          TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_community_comments_post ON community_comments(post_id);
CREATE INDEX IF NOT EXISTS idx_community_comments_parent ON community_comments(parent_comment_id);

CREATE TABLE IF NOT EXISTS community_comment_likes (
    comment_like_id BIGSERIAL PRIMARY KEY,
    comment_id      BIGINT NOT NULL REFERENCES community_comments(comment_id) ON DELETE CASCADE,
    user_id         BIGINT NOT NULL REFERENCES "Users"(user_id),
    created_at      TIMESTAMP,
    updated_at      TIMESTAMP,
    CONSTRAINT uk_community_comment_likes_comment_user UNIQUE (comment_id, user_id)
);

CREATE TABLE IF NOT EXISTS community_post_reports (
    post_report_id  BIGSERIAL PRIMARY KEY,
    post_id         BIGINT NOT NULL REFERENCES community_posts(post_id) ON DELETE CASCADE,
    reporter_id     BIGINT NOT NULL REFERENCES "Users"(user_id),
    reason          VARCHAR(30) NOT NULL,
    detail          VARCHAR(500),
    created_at      TIMESTAMP,
    updated_at      TIMESTAMP
);

CREATE TABLE IF NOT EXISTS community_user_blocks (
    community_user_block_id BIGSERIAL PRIMARY KEY,
    blocker_id              BIGINT NOT NULL REFERENCES "Users"(user_id),
    blocked_id              BIGINT NOT NULL REFERENCES "Users"(user_id),
    created_at              TIMESTAMP,
    updated_at              TIMESTAMP,
    CONSTRAINT uk_community_user_blocks_blocker_blocked UNIQUE (blocker_id, blocked_id)
);

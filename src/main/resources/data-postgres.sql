CREATE TABLE users (
	user_id BIGSERIAL PRIMARY KEY,
	username VARCHAR(100) NOT NULL
);

CREATE TABLE role (
	role_id BIGSERIAL PRIMARY KEY,
	name VARCHAR(50) NOT NULL
);

CREATE TABLE users_roles (
	user_id BIGINT REFERENCES users,
	role_id BIGINT REFERENCES role,
	PRIMARY KEY (user_id, role_id)
);

CREATE TABLE task (
    task_id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL
        CONSTRAINT task_status_check CHECK (status IN ('OPEN', 'IN_PROGRESS', 'DONE', 'CLOSED')),
    created_by BIGINT NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
	FOREIGN KEY (created_by) REFERENCES users (user_id)
);
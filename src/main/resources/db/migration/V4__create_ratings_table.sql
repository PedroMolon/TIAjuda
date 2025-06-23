CREATE TABLE ratings (
    id BIGSERIAL PRIMARY KEY,
    score INTEGER NOT NULL,
    comment VARCHAR(500),
    service_request_id BIGINT NOT NULL,
    technician_id BIGINT NOT NULL,
    client_id BIGINT NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),

    -- Chave estrangeira para ServiceRequest
    CONSTRAINT fk_service_request
        FOREIGN KEY (service_request_id)
        REFERENCES service_requests (id),

    -- Chave estrangeira para Technician (user)
    CONSTRAINT fk_technician
        FOREIGN KEY (technician_id)
        REFERENCES users (id),

    -- Chave estrangeira para Client (user)
    CONSTRAINT fk_client
        FOREIGN KEY (client_id)
        REFERENCES users (id),

    -- Restrição para garantir que um cliente só pode avaliar um service_request uma vez
    CONSTRAINT uq_client_service_request_rating
        UNIQUE (service_request_id, client_id)
);
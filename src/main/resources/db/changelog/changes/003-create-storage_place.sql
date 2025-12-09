CREATE TABLE storage_place
(
    id           UUID PRIMARY KEY,

    courier_id   UUID         NOT NULL,
    name         VARCHAR(255) NOT NULL,
    total_volume INTEGER      NOT NULL,
    order_id     UUID         NULL,

    CONSTRAINT storage_place_total_volume_chk CHECK (total_volume >= 1),
    CONSTRAINT storage_place_courier_fk
        FOREIGN KEY (courier_id) REFERENCES courier (id) ON DELETE CASCADE
);

CREATE INDEX storage_place_courier_id_idx ON storage_place (courier_id);
CREATE INDEX storage_place_order_id_idx ON storage_place (order_id);
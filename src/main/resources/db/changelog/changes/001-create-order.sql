CREATE TABLE orders
(
    id         UUID PRIMARY KEY,

    location_x INTEGER     NOT NULL,
    location_y INTEGER     NOT NULL,

    volume     INTEGER     NOT NULL,
    status     VARCHAR(30) NOT NULL,

    courier_id UUID,

    CONSTRAINT orders_volume_chk CHECK (volume > 0)
);

CREATE INDEX orders_courier_id_idx ON orders (courier_id);
CREATE INDEX orders_status_idx ON orders (status);
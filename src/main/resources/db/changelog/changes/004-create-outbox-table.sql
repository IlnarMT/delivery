create table outbox
(
    id               uuid primary key,
    event_type       varchar(255) not null,
    aggregate_id     varchar(255) not null,
    aggregate_type   varchar(255) not null,
    payload          text         not null,
    occurred_on_utc  timestamptz  not null,
    processed_on_utc timestamptz null
);

create index idx_outbox_processed_on_utc on outbox (processed_on_utc);
create index idx_outbox_occurred_on_utc on outbox (occurred_on_utc);

create index idx_outbox_unprocessed on outbox (occurred_on_utc) where processed_on_utc is null;
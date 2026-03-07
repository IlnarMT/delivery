package libs.ddd;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public abstract class DomainEvent {

    private final UUID eventId = UUID.randomUUID();
    private final Instant occurredOnUtc = Instant.now();
}
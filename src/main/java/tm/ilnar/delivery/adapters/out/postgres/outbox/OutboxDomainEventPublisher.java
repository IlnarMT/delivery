package tm.ilnar.delivery.adapters.out.postgres.outbox;

import com.fasterxml.jackson.databind.ObjectMapper;
import libs.ddd.Aggregate;
import libs.ddd.AggregateRoot;
import org.springframework.stereotype.Repository;
import tm.ilnar.delivery.DomainEventPublisher;

@Repository
public class OutboxDomainEventPublisher implements DomainEventPublisher {
    private final OutboxJpaRepository jpa;
    private final ObjectMapper objectMapper;

    public OutboxDomainEventPublisher(OutboxJpaRepository jpa, ObjectMapper objectMapper) {
        this.jpa = jpa;
        this.objectMapper = objectMapper;

    }

    public void saveForPublish(Iterable<Aggregate<?>> aggregates) {
        if (!aggregates.iterator().hasNext()) {
            return;
        }

        try {
            for (AggregateRoot<?> aggregate : aggregates) {
                aggregate.getDomainEvents().forEach(domainEvent -> {
                    try {
                        var payload = objectMapper.writeValueAsString(domainEvent);

                        var outboxMessage = new OutboxMessage(domainEvent.getEventId(),
                                domainEvent.getClass().getName(), aggregate.getId().toString(),
                                aggregate.getClass().getSimpleName(), payload, domainEvent.getOccurredOnUtc());
                        jpa.save(outboxMessage);
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to serialize domainEvent for Outbox", e);
                    }
                });

                aggregate.clearDomainEvents();
            }
        } catch (Exception e) {
            throw new RuntimeException("Persist events is failed", e);
        }
    }
}


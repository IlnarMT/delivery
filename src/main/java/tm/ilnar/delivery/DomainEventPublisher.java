package tm.ilnar.delivery;

import libs.ddd.Aggregate;

public interface DomainEventPublisher {
    void publish(Iterable<Aggregate<?>> aggregates);
}


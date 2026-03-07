package tm.ilnar.delivery;

import libs.ddd.Aggregate;

public interface DomainEventPublisher {

    void saveForPublish(Iterable<Aggregate<?>> aggregates);
}


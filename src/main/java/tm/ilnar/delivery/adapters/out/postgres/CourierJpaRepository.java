package tm.ilnar.delivery.adapters.out.postgres;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import tm.ilnar.delivery.core.domain.model.courier.Courier;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CourierJpaRepository extends JpaRepository<Courier, UUID> {

    @EntityGraph(attributePaths = "storagePlaces")
    Optional<Courier> findById(UUID id);

    @EntityGraph(attributePaths = "storagePlaces")
    @Query("""
        select distinct c
        from Courier c
            left join fetch c.storagePlaces sp
        where not exists (
            select 1
            from Courier c2
                join c2.storagePlaces sp2
            where c2 = c
              and sp2.orderId is not null
        )
        """)
    List<Courier> findAllWithFreeStorage();
}

package tm.ilnar.delivery.adapters.out.postgres;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tm.ilnar.delivery.core.domain.model.order.Order;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderJpaRepository extends JpaRepository<Order, UUID> {

    @Query(value = """
        SELECT *
        FROM orders
        WHERE status = :orderStatus
        LIMIT 1
        """, nativeQuery = true)
    Optional<Order> findAnyByStatus(@Param("orderStatus") String orderStatus);

    @Query(value = """
        SELECT *
        FROM orders
        WHERE status = :orderStatus
        """, nativeQuery = true)
    List<Order> findAllByStatus(@Param("orderStatus") String orderStatus);
}

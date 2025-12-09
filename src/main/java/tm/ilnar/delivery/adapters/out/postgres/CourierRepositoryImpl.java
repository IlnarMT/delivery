package tm.ilnar.delivery.adapters.out.postgres;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import tm.ilnar.delivery.core.domain.model.courier.Courier;
import tm.ilnar.delivery.core.ports.CourierRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Repository
public class CourierRepositoryImpl implements CourierRepository {

    private final CourierJpaRepository courierJpaRepository;

    @Override
    public void save(Courier courier) {
        courierJpaRepository.save(courier);
    }

    @Override
    public Optional<Courier> findById(UUID id) {
        return courierJpaRepository.findById(id);
    }

    @Override
    public List<Courier> findAllWithFreeStorage() {
        return courierJpaRepository.findAllWithFreeStorage();
    }
}

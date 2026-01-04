package tm.ilnar.delivery.adapters.in.kafka;

import com.google.protobuf.util.JsonFormat;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import queues.basket.BasketEventsProto;
import tm.ilnar.delivery.core.application.commands.CreateOrderCommand;
import tm.ilnar.delivery.core.application.commands.CreateOrderCommandHandler;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class BasketsEventsConsumer {

    private final CreateOrderCommandHandler createOrderCommandHandler;
    @KafkaListener(topics = "${app.kafka.basket-events-topic}")
    public void listen(String message) {
        log.info("Raw event received: {}", message);

        try {
            // Десериализация
            var builder = BasketEventsProto.BasketConfirmedIntegrationEvent.newBuilder();
            JsonFormat.parser().merge(message, builder);
            var event = builder.build();

            // Создаем команду
            var createOrderCommanResult = CreateOrderCommand.create(UUID.randomUUID(), event.getAddress().getStreet(), event.getVolume());
            if (createOrderCommanResult.isFailure()) {
                throw new RuntimeException("Invalid command: " + createOrderCommanResult.getError());
            }
            var command = createOrderCommanResult.getValue();

            // Обрабатываем команду
            var handleCommandResult = this.createOrderCommandHandler.handle(command);
            if (handleCommandResult.isFailure()) {
                throw new RuntimeException("Failed to handle command: " + handleCommandResult.getError());
            }

        } catch (com.google.protobuf.InvalidProtocolBufferException ex) {
            throw new RuntimeException("Failed to parse protobuf message", ex);
        }
    }

}

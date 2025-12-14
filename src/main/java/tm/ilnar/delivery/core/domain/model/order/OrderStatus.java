package tm.ilnar.delivery.core.domain.model.order;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum OrderStatus {

    CREATED("created"),
    ASSIGNED("assigned"),
    COMPLETED("completed");

    private final String value;

    public static OrderStatus fromValue(String value) {
        return Arrays.stream(OrderStatus.values())
            .filter(status -> status.getValue().equals(value))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("Unknown OrderStatus value: "+ value));
    }
}

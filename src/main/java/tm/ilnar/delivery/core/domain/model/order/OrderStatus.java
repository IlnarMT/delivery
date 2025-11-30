package tm.ilnar.delivery.core.domain.model.order;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum OrderStatus {

    CREATED("CREATED"),
    ASSIGNED("ASSIGNED"),
    COMPLETED("COMPLETED");

    //
    private final String name;

/*    public static Status fromValue(String value) {
            for (Status status : Status.values()) {
                if (status.name.equals(value)) {
                    return status;
                }
            }
            throw new IllegalArgumentException("Unsupported payer type " + name);
    }*/

    public static OrderStatus fromValue(String value) {
        return OrderStatus.valueOf(value.toUpperCase());
    }

    public String toValue() {
        return name().toLowerCase();
    }
}

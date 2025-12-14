package tm.ilnar.delivery.adapters.in.http.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import tm.ilnar.delivery.core.application.queries.CourierWithAssignedOrderQueryResponse;

import java.util.List;

@Mapper
public interface CourierMapper {

    CourierMapper INSTANCE = Mappers.getMapper(CourierMapper.class);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "location.x", source = "location.x")
    @Mapping(target = "location.y", source = "location.y")
    tm.ilnar.delivery.adapters.in.http.openapi.model.Courier toHttp(CourierWithAssignedOrderQueryResponse response);

    List<tm.ilnar.delivery.adapters.in.http.openapi.model.Courier> toHttp(List<CourierWithAssignedOrderQueryResponse> responses);
}

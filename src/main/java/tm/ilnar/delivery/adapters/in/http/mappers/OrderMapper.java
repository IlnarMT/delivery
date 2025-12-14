package tm.ilnar.delivery.adapters.in.http.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import tm.ilnar.delivery.core.application.queries.GetNotCompletedOrdersQueryResponse;

import java.util.List;

@Mapper
public interface OrderMapper {

    OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "location.x", source = "location.x")
    @Mapping(target = "location.y", source = "location.y")
    tm.ilnar.delivery.adapters.in.http.openapi.model.Order toHttp(GetNotCompletedOrdersQueryResponse response);

    List<tm.ilnar.delivery.adapters.in.http.openapi.model.Order> toHttp(List<GetNotCompletedOrdersQueryResponse> responses);

}

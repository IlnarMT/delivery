package tm.ilnar.delivery.adapters.out.grps;

import clients.geo.GeoGrpc;
import clients.geo.GeoProto;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import jakarta.annotation.PreDestroy;
import libs.errs.Error;
import libs.errs.Except;
import libs.errs.Result;
import org.springframework.stereotype.Service;
import tm.ilnar.delivery.config.properties.GrpcProperties;
import tm.ilnar.delivery.core.domain.model.kernel.Location;
import tm.ilnar.delivery.core.ports.GeoClient;

@Service
public class GeoClientImpl implements GeoClient {

    private final ManagedChannel channel;
    private final GeoGrpc.GeoBlockingStub stub;

    public GeoClientImpl(GrpcProperties properties) {
        this.channel = ManagedChannelBuilder
            .forAddress(
                properties.getGeoService().getHost(),
                properties.getGeoService().getPort())
            .usePlaintext()
            .build();
        this.stub = GeoGrpc.newBlockingStub(channel);
    }

    @PreDestroy
    public void shutdown() {
        if (!channel.isShutdown()) {
            channel.shutdown();
        }
    }

    @Override
    public Result<Location, Error> getLocation(String street) {
        //Почему исключение выбрасываем, а не Result
        Except.againstNullOrEmpty(street, "street");

        GeoProto.GetGeolocationRequest request = GeoProto.GetGeolocationRequest.newBuilder()
            .setStreet(street)
            .build();

        GeoProto.GetGeolocationReply response = stub.getGeolocation(request);

        return Location.create(response.getLocation().getX(), response.getLocation().getY());
    }
}

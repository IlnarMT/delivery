package tm.ilnar.delivery.adapters.in.quartz;

import lombok.RequiredArgsConstructor;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;
import tm.ilnar.delivery.core.application.commands.MoveAllCouriersCommandHandler;

@Component
@RequiredArgsConstructor
public class MoveCouriersJob implements Job {

    private final MoveAllCouriersCommandHandler useCase;

    @Override
    public void execute(JobExecutionContext context) {
        useCase.handle();
    }
}

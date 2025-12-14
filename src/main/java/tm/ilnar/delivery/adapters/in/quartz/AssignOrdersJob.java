package tm.ilnar.delivery.adapters.in.quartz;

import lombok.RequiredArgsConstructor;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;
import tm.ilnar.delivery.core.application.commands.AssignOrderToCourierCommandHandler;

@Component
@RequiredArgsConstructor
public class AssignOrdersJob implements Job {

    private final AssignOrderToCourierCommandHandler useCase;

    @Override
    public void execute(JobExecutionContext context) {
        useCase.handle();
    }
}

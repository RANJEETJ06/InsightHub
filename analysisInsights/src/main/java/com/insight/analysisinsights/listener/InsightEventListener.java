package com.insight.analysisinsights.listener;

import com.insight.analysisinsights.configs.RabbitMQConfig;
import com.insight.analysisinsights.exceptions.ProcessFailureException;
import com.insight.analysisinsights.models.CleanedDataEvent;
import com.insight.analysisinsights.services.InsightService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InsightEventListener {

    private final InsightService insightService;

    @RabbitListener(queues = RabbitMQConfig.CLEANED_DATA_QUEUE)
    public void handleCleanedData(CleanedDataEvent event) {
        try {
            insightService.analyze(event);
        } catch (Exception e) {
            throw new ProcessFailureException("Rabbit Unable to Listen", event.getOriginalFilename(),e.getMessage());
        }
    }
}

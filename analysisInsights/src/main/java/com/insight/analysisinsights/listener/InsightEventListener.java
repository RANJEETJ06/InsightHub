package com.insight.analysisinsights.listener;

import com.insight.analysisinsights.configs.RabbitMQConfig;
import com.insight.analysisinsights.exceptions.ProcessFailureException;
import com.insight.analysisinsights.models.CleanedDataEvent;
import com.insight.analysisinsights.services.InsightService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
@RequiredArgsConstructor
public class InsightEventListener {

    private final InsightService insightService;

    @RabbitListener(queues = RabbitMQConfig.CLEANED_DATA_QUEUE)
    public void handleCleanedData(CleanedDataEvent event) {
        String filePath = event.getFilePath();

        File cleanedFile = new File(filePath);

        // Check if a file exists before processing
        if (!cleanedFile.exists()) {
            // requeue the message
            throw new ProcessFailureException(
                    "File not ready yet",
                    event.getOriginalFilename(),
                    "File does not exist: " + filePath
            );
        }

        try {
            insightService.analyze(event);
        } catch (Exception e) {
            // Handle unexpected exceptions gracefully
            throw new ProcessFailureException(
                    "Analysis failed",
                    event.getOriginalFilename(),
                    e.getMessage()
            );
        }
    }
}

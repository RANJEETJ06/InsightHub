package com.insight.report.listener;

import com.insight.report.model.ReportData;
import com.insight.report.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReportEventListener {

    private final ReportService reportService;

    @RabbitListener(queues = "${report.rabbitmq.queue}")
    public void handleReportData(ReportData data) {
        System.out.println(data.toString());
        reportService.generateReports(data);
    }
}

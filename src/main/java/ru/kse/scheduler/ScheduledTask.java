package ru.kse.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class ScheduledTask {

  private static final Logger LOGGER = LoggerFactory.getLogger(ScheduledTask.class);
  private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss.SSS");

  @Value("${instance.name:DefaultName}")
  private String instanceName = "";

  @Value("${logger.address}")
  private String loggerAddress;

  @Value("${logger.port}")
  private String loggerPort;


  @Scheduled(fixedRateString = "${schedule.period}")
  public void reportCurrentTime() {
    LOGGER.info(String.format(
        "%s %s %s",
        instanceName,
        dateFormat.format(new Date()),
        "http://" + loggerAddress + ":" + loggerPort
    ));
    WebClient webClient = WebClient.create("http://" + loggerAddress + ":" + loggerPort);
    WebClient.RequestHeadersUriSpec<?> uriSpec = webClient.get();
    WebClient.RequestHeadersSpec headersSpec = uriSpec.uri("/log?name=" + instanceName);
    String response = headersSpec.retrieve().bodyToMono(String.class).block();
  }
}

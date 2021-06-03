package com.example.kafkatest;

import java.time.LocalDate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaProducerException;
import org.springframework.kafka.core.KafkaSendCallback;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;

@SpringBootApplication
@Slf4j
public class KafkaTestApplication {

  @Autowired
  private KafkaTemplate<String, String> kafkaTemplate;

  public static void main(String[] args) {
    SpringApplication.run(KafkaTestApplication.class, args);
  }

  @Bean
  public ApplicationRunner runner() {
    log.info("inside runner");
    return args -> {
      ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send("test", LocalDate.now().toString());
      future.addCallback(new KafkaSendCallback<String, String>() {
        @Override
        public void onSuccess(SendResult<String, String> sendResult) {
          log.info("onSuccess {} {}", sendResult.getProducerRecord(), sendResult.getRecordMetadata());
        }

        @Override
        public void onFailure(KafkaProducerException e) {
          log.error("onFailure {}", e.getFailedProducerRecord(), e);
        }
      });

    };
  }

  @KafkaListener(id = "my.group.id", topics = "test")
  public void listen(String in) {
    log.info("received {}", in);
  }
}

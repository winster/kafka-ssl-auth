package com.example.kafkatest;

import java.time.LocalDate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;

@SpringBootApplication
@Slf4j
public class KafkaTestApplication {

  @Autowired
  private KafkaTemplate<String, String> kafkaTemplate;

  public static void main(String[] args) {
    SpringApplication.run(KafkaTestApplication.class, args);
  }

  @Bean
  public ApplicationRunner runner(KafkaTemplate<String, String> template) {
    log.info("inside runner");
    return args -> {
      template.send("test", LocalDate.now().toString());
    };
  }

  @KafkaListener(id = "myId", topics = "test")
  public void listen(String in) {
    log.info("received {}", in);
  }
}

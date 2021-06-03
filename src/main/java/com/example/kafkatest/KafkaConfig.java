package com.example.kafkatest;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.SslConfigs;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

@Configuration
@ConditionalOnProperty(value = "custom.ssl", havingValue = "true")
public class KafkaConfig {

  @Value("${kafka.ssl.keystore.location}")
  String keystoreLocation;

  @Value("${kafka.ssl.keystore.pwd}")
  String keystoreKeyPwd;

  @Value("${kafka.ssl.truststore.location}")
  String truststoreLocation;

  @Value("${kafka.producer.bootstrap-server}")
  String producerBootstrapServer;

  @Value("${kafka.consumer.bootstrap-server}")
  String consumerBootstrapServer;

  @Value("${kafka.producer.topic}")
  String producerTopic;

  @Value("${kafka.consumer.topic}")
  String consumerTopic;

  @Autowired
  private ProducerFactory<Integer, String> producerFactory;
  @Autowired
  private ConsumerFactory<Integer, String> consumerFactory;

  public Map<String, Object> producerConfig() {
    Map<String, Object> producerConfig = new HashMap<>(producerFactory.getConfigurationProperties());
    producerConfig.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, producerBootstrapServer);
    producerConfig.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    producerConfig.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    producerConfig.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SSL");

    producerConfig.put(SslConfigs.SSL_KEYSTORE_TYPE_CONFIG, "PEM");
    producerConfig.put(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG, keystoreLocation);
    producerConfig.put(SslConfigs.SSL_KEY_PASSWORD_CONFIG, keystoreKeyPwd);
    producerConfig.put(SslConfigs.SSL_TRUSTSTORE_TYPE_CONFIG, "PEM");
    producerConfig.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, truststoreLocation);
    return producerConfig;
  }

  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() throws IOException {
    ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(new DefaultKafkaConsumerFactory<>(consumerConfig()));
    return factory;
  }

  private Map<String, Object> consumerConfig() {
    Map<String, Object> consumerConfig = new HashMap<>(consumerFactory.getConfigurationProperties());
    consumerConfig.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, producerBootstrapServer);
    consumerConfig.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    consumerConfig.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    consumerConfig.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SSL");

    consumerConfig.put(SslConfigs.SSL_KEYSTORE_TYPE_CONFIG, "PEM");
    consumerConfig.put(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG, keystoreLocation);
    consumerConfig.put(SslConfigs.SSL_KEY_PASSWORD_CONFIG, keystoreKeyPwd);
    consumerConfig.put(SslConfigs.SSL_TRUSTSTORE_TYPE_CONFIG, "PEM");
    consumerConfig.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, truststoreLocation);
    return consumerConfig;
  }

  @Bean
  public KafkaTemplate<String, String> kafkaTemplate() throws IOException {
    return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(producerConfig()));
  }
}

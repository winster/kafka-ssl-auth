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
import org.springframework.core.io.Resource;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

@Configuration
@ConditionalOnProperty(value = "custom.ssl", havingValue = "true")
public class KafkaConfig {

  @Value("classpath:kafka.client.keystore.jks")
  Resource clientKeystoreFile;

  @Value("classpath:kafka.client.truststore.jks")
  Resource clientTruststoreFile;

  @Autowired
  private ProducerFactory<Integer, String> producerFactory;
  @Autowired
  private ConsumerFactory<Integer, String> consumerFactory;

  public Map<String, Object> producerConfig() throws IOException {
    Map<String, Object> producerConfig = new HashMap<>(producerFactory.getConfigurationProperties());
    producerConfig.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9093");
    producerConfig.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    producerConfig.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    producerConfig.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SSL");

    producerConfig.put(SslConfigs.SSL_PROTOCOL_CONFIG, "TLSv1.2");
    producerConfig.put(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG, clientKeystoreFile.getFile().getAbsolutePath());
    producerConfig.put(SslConfigs.SSL_KEYSTORE_TYPE_CONFIG, "jks");
    producerConfig.put(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG, "<password>");
    producerConfig.put(SslConfigs.SSL_KEY_PASSWORD_CONFIG, "<password>");
    producerConfig.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, clientTruststoreFile.getFile().getAbsolutePath());
    producerConfig.put(SslConfigs.SSL_TRUSTSTORE_TYPE_CONFIG, "jks");
    producerConfig.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, "<password>");

    return producerConfig;
  }

  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() throws IOException {
    ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(new DefaultKafkaConsumerFactory<>(consumerConfig()));
    return factory;
  }

  private Map<String, Object> consumerConfig() throws IOException {
    Map<String, Object> consumerConfig = new HashMap<>(consumerFactory.getConfigurationProperties());
    consumerConfig.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9093");
    consumerConfig.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    consumerConfig.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    consumerConfig.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SSL");

    consumerConfig.put(SslConfigs.SSL_PROTOCOL_CONFIG, "TLSv1.2");
    consumerConfig.put(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG, clientKeystoreFile.getFile().getAbsolutePath());
    consumerConfig.put(SslConfigs.SSL_KEYSTORE_TYPE_CONFIG, "jks");
    consumerConfig.put(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG, "<password>");
    consumerConfig.put(SslConfigs.SSL_KEY_PASSWORD_CONFIG, "<password>");
    consumerConfig.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, clientTruststoreFile.getFile().getAbsolutePath());
    consumerConfig.put(SslConfigs.SSL_TRUSTSTORE_TYPE_CONFIG, "jks");
    consumerConfig.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, "<password>");
    return consumerConfig;
  }

  @Bean
  public KafkaTemplate<String, String> kafkaTemplate() throws IOException {
    return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(producerConfig()));
  }
}

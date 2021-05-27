# Read Me First
This application act as a producer and consumer of a kafka cluster configured to always authenticate via SSL certificate

# Getting Started

## Generate required files
* *create CA certificate*: `openssl req -new -x509 -keyout ca-key -out ca-cert -days {validity}`
* *import CA certificate into kafka client truststore(assume same CA signs both client and server)*: `keytool -keystore kafka.client.truststore.jks -alias CARoot -importcert -file ca-cert`
* *import CA certificate into kafka server truststore(assume same CA signs both client and server)*: `keytool -keystore kafka.server.truststore.jks -alias CARoot -importcert -file ca-cert`
* *create kafka broker keystore*: `keytool -keystore kafka.server.keystore.jks -alias localhost -keyalg RSA -validity {validity} -genkey`
* *export certificate of kafka broker keystore aka server identity*: `keytool -keystore kafka.server.keystore.jks -alias localhost -certreq -file cert-file`
* *sign kafka broker certificate using CA certificate*: `openssl x509 -req -CA ca-cert -CAkey ca-key -in cert-file -out cert-signed -days {validity} -CAcreateserial`
* *import CA certificate into kafka broker keystore*: `keytool -keystore kafka.server.keystore.jks -alias CARoot -importcert -file ca-cert`
* *import signed broker certificate into kafka broker keystore*: `keytool -keystore kafka.server.keystore.jks -alias localhost -importcert -file cert-signed`
* *create kafka client keystore*: `keytool -keystore kafka.client.keystore.jks -alias localhost -keyalg RSA -validity {validity} -genkey`
* *export certificate of kafka client keystore aka client identity*: `keytool -keystore kafka.client.keystore.jks -alias localhost -certreq -file client-cert-file`
* *sign kafka client certificate using CA certificate*: `openssl x509 -req -CA ca-cert -CAkey ca-key -in client-cert-file -out client-cert-signed -days {validity} -CAcreateserial`
* *import CA certificate into kafka client keystore*: `keytool -keystore kafka.server.keystore.jks -alias CARoot -importcert -file ca-cert`
* *import signed client certificate into kafka client keystore*:`keytool -keystore kafka.server.keystore.jks -alias localhost -importcert -file cert-signed`
  
* [Refer confluent doc for details](https://docs.confluent.io/platform/current/security/security_tutorial.html#generating-keys-certs)

## Install & run kafka, create topic, run console-producer and console-consumer

* brew install kafka
* zookeeper-server-start /usr/local/etc/kafka/zookeeper.properties
  
*Edit /usr/local/etc/kafka/server.properties with the following*

`ssl.endpoint.identification.algorithm=
security.inter.broker.protocol=SSL
listeners=SSL://:9093
ssl.truststore.location=<file_location>/kafka.server.truststore.jks
ssl.truststore.password=<password>
ssl.keystore.location=<file_location>/kafka.server.keystore.jks
ssl.keystore.password=<password>
ssl.key.password=<password>
ssl.client.auth=required`

* kafka-server-start /usr/local/etc/kafka/server.properties
* kafka-topics --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic test

*Touch a file client-ssl.properties with the following*
`bootstrap.servers=localhost:9093
security.protocol=SSL
ssl.truststore.location=<file_location>/kafka.client.truststore.jks
ssl.truststore.password=<password>
ssl.keystore.location=<file_location>/kafka.client.keystore.jks
ssl.keystore.password=<password>
ssl.key.password=<password>
ssl.endpoint.identification.algorithm=
`
* kafka-console-producer --broker-list localhost:9093 --topic test --producer.config client-ssl.properties
* kafka-console-consumer --bootstrap-server localhost:9093 --topic test --consumer.config client-ssl.properties

* [Refer confluent doc for details](https://docs.confluent.io/platform/current/kafka/authentication_ssl.html)

## Run Application

This application can be run in 2 modes. 

1. without configuration - make sure `custom.ssl=true` in application.yml
2. using spring-boot configuration - set `custom.ssl=false` in application-kafka.yml and add commandline argument `-Dspring-boot.run.profiles=kafka`

## Code review
Review? DIY ;)

### Reference Documentation
For further reference, please consider the following sections:

* [SSL gist](https://gist.github.com/winster/5d41ebe94eabc3195f56091730f01092)
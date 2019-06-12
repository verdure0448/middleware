package com.hdbsnc.smartiot.util.kafka;

import org.apache.kafka.clients.producer.Callback;

public interface IKafkaProducer {

   // void configure(Properties prop, String sync);
    void start() throws Exception;

    void produceSync(String s) throws Exception;
    void produceSync(String s, long ms) throws Exception;
    void produceSync(String s, String key, long ms) throws Exception;
    
    void produceAsync(String s, Callback c) throws Exception;
    void produceAsync(String s, Callback c, long ms) throws Exception;

    void close() throws Exception;
    void close(long ms) throws Exception;
}

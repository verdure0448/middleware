package com.hdbsnc.smartiot.util.kafka;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

public class KafkaSimpleProducerJava implements IKafkaProducer {

    private String _topic;
    private Properties _props = new Properties();
    private Producer<String, String> producer;

    public KafkaSimpleProducerJava(String topic, Properties prop) {
    	_topic = topic;
    	_props = prop;
    }

    public void start() {
        producer = new KafkaProducer<String, String>(_props);
    }

	public void produceSync(String value) throws Exception {
    	 ProducerRecord<String, String> record = new ProducerRecord<String, String>(_topic, value);
         producer.send(record).get();
         //System.out.println("Sucess producing to topic " + _topic);
	}
    
	public void produceSync(String value, long timeout) throws Exception {
    	ProducerRecord<String, String> record = new ProducerRecord<String, String>(_topic, value);
        producer.send(record).get(timeout, TimeUnit.MILLISECONDS);
        //System.out.println("Sucess producing to topic " + _topic);
	}

	public void produceSync(String value, String key, long timeout) throws Exception {
    	ProducerRecord<String, String> record = new ProducerRecord<String, String>(_topic, key, value);
        producer.send(record).get(timeout, TimeUnit.MILLISECONDS);
		
	}
    
    public void produceAsync(String value, Callback callback) throws Exception {
    	ProducerRecord<String, String> record = new ProducerRecord<String, String>(_topic, value);
        producer.send(record, callback);
    }
    
    public void produceAsync(String value, Callback callback, long timeout) throws Exception {
    	 ProducerRecord<String, String> record = new ProducerRecord<String, String>(_topic, value);
         producer.send(record, callback).get(timeout, TimeUnit.MILLISECONDS);
    }

    public void close() {
        producer.close();
    }
    
    public void close(long ms) {
        producer.close(ms, TimeUnit.MILLISECONDS);
    }
}

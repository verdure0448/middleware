//package com.hdbsnc.smartiot.util.logger.impl.logback.appender;
//
//import java.text.SimpleDateFormat;
//import java.util.Arrays;
//import java.util.Date;
//import java.util.List;
//import java.util.Properties;
//import java.util.TimeZone;
//
//import org.json.simple.JSONObject;
//import org.slf4j.Marker;
//
//import com.hdbsnc.smartiot.util.kafka.IKafkaProducer;
//import com.hdbsnc.smartiot.util.kafka.KafkaSimpleProducerJava;
//
//import ch.qos.logback.classic.spi.ILoggingEvent;
//import ch.qos.logback.classic.spi.IThrowableProxy;
//import ch.qos.logback.classic.spi.ThrowableProxyUtil;
//import ch.qos.logback.core.CoreConstants;
//import ch.qos.logback.core.UnsynchronizedAppenderBase;
//
//public class KafkaProducerAgender extends UnsynchronizedAppenderBase<ILoggingEvent> {
//
//	public static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
//
//	public static final String KEY_INTERVAL_SEC = "interval.sec";
//	public static final String KEY_PRODUCER_SEND_TIMEOUT = "producer.send.timeout";
//	public static final String KEY_TASK_NAME = "task.name";
//
//	private int KAFKA_SEND_TIMEOUT = 3000;
//	// private int _producerSendTimeout;
//	private IKafkaProducer _producer;
//
//	private String topic;
//	private String bootstrapServers;
//	private String acks;
//	private String keySerializer;
//	private String valueSerializer;
//	private int maxBlockMs;
//	private int connetionsMaxIdleMs;
//	private int requestTimeoutMs;
//	private int lingerMs;
//	private int retries;
//	private int batchSize;
//	private int bufferMemory;
//
//	private Thread connectThread;
//
//	static {
//		sdf.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
//	}
//
//	private void connect() {
//
//		Properties props = new Properties();
//		props.put("bootstrap.servers", bootstrapServers);
//		props.put("max.block.ms", maxBlockMs); // 서버가 존재하지 안는 경우
//		props.put("connections.max.idle.ms", connetionsMaxIdleMs); // 연결요청
//		props.put("request.timeout.ms", requestTimeoutMs); // 리퀘스트
//		props.put("linger.ms", lingerMs);// 복수 전송시 건별 wait시간
//
//		props.put("acks", acks);
//		props.put("retries", retries);
//		props.put("batch.size", batchSize);
//		props.put("buffer.memory", bufferMemory);
//		props.put("key.serializer", keySerializer);
//		props.put("value.serializer", valueSerializer);
//
//		_producer = new KafkaSimpleProducerJava(topic, props);
//
//		connectThread = new Thread(() -> {
//			try {
//				_producer.start();
//			} catch (Exception e1) {
//				e1.printStackTrace();
//			}
//		});
//
//		connectThread.start();
//	}
//
//	private void reconnect() {
//
//		connectThread = new Thread(() -> {
//			try {
//				_producer.start();
//			} catch (Exception e1) {
//				e1.printStackTrace();
//			}
//		});
//
//		connectThread.start();
//	}
//	
//	@Override
//	public void start() {
//		super.start();
//		connect();
//	}
//
//
//	@Override
//	protected void append(ILoggingEvent evt) {
//		if (evt == null || connectThread.isAlive())
//			return; 
//
//		JSONObject log = getJsonLog(evt);
//
//		try {
//			logException(evt.getThrowableProxy(), log);
//			_producer.produceSync(log.toJSONString(), KAFKA_SEND_TIMEOUT);
//		} catch (Exception e) {
////			StringWriter sw = new StringWriter();
////			e.printStackTrace(new PrintWriter(sw));
////			log.put("logging_error", "Could not log all the event information: " + sw.toString());
////			log.put("level", "ERROR");
//
//			reconnect();
//		}
//	}
//
//	@SuppressWarnings("unchecked")
//	private JSONObject getJsonLog(ILoggingEvent evt) {
//
//		JSONObject log = new JSONObject();
//
//		log.put("logger", evt.getLoggerName());
//		log.put("timestamp", sdf.format(new Date()));
//		log.put("level", String.valueOf(evt.getLevel())); // in case getLevel returns null
//		Marker m = evt.getMarker();
//		if (m != null) {
//			log.put("marker", m.getName());
//		}
//		log.put("thread", evt.getThreadName());
//		log.put("message", evt.getFormattedMessage());
//		return log;
//	}
//
//	private void logException(IThrowableProxy tp, JSONObject log) {
//		if (tp == null)
//			return;
//		String tpAsString = ThrowableProxyUtil.asString(tp); // the stack trace basically
//		List<String> stackTrace = Arrays.asList(tpAsString.replace("\t", "").split(CoreConstants.LINE_SEPARATOR));
//		if (stackTrace.size() > 0) {
//			log.put("exception", stackTrace.get(0));
//		}
//		if (stackTrace.size() > 1) {
//			log.put("stacktrace", stackTrace.subList(1, stackTrace.size()));
//		}
//	}
//
//	public String getTopic() {
//		return topic;
//	}
//
//	public void setTopic(String topic) {
//		this.topic = topic;
//	}
//
//	public String getBootstrapServers() {
//		return bootstrapServers;
//	}
//
//	public void setBootstrapServers(String bootstrapServers) {
//		this.bootstrapServers = bootstrapServers;
//	}
//
//	public String getAcks() {
//		return acks;
//	}
//
//	public void setAcks(String acks) {
//		this.acks = acks;
//	}
//
//	public String getKeySerializer() {
//		return keySerializer;
//	}
//
//	public void setKeySerializer(String keySerializer) {
//		this.keySerializer = keySerializer;
//	}
//
//	public String getValueSerializer() {
//		return valueSerializer;
//	}
//
//	public void setValueSerializer(String valueSerializer) {
//		this.valueSerializer = valueSerializer;
//	}
//
//	public int getMaxBlockMs() {
//		return maxBlockMs;
//	}
//
//	public void setMaxBlockMs(int maxBlockMs) {
//		this.maxBlockMs = maxBlockMs;
//	}
//
//	public int getConnetionsMaxIdleMs() {
//		return connetionsMaxIdleMs;
//	}
//
//	public void setConnetionsMaxIdleMs(int connetionsMaxIdleMs) {
//		this.connetionsMaxIdleMs = connetionsMaxIdleMs;
//	}
//
//	public int getRequestTimeoutMs() {
//		return requestTimeoutMs;
//	}
//
//	public void setRequestTimeoutMs(int requestTimeoutMs) {
//		this.requestTimeoutMs = requestTimeoutMs;
//	}
//
//	public int getLingerMs() {
//		return lingerMs;
//	}
//
//	public void setLingerMs(int lingerMs) {
//		this.lingerMs = lingerMs;
//	}
//
//	public int getRetries() {
//		return retries;
//	}
//
//	public void setRetries(int retries) {
//		this.retries = retries;
//	}
//
//	public int getBatchSize() {
//		return batchSize;
//	}
//
//	public void setBatchSize(int batchSize) {
//		this.batchSize = batchSize;
//	}
//
//	public int getBufferMemory() {
//		return bufferMemory;
//	}
//
//	public void setBufferMemory(int bufferMemory) {
//		this.bufferMemory = bufferMemory;
//	}
//
//}

//package com.hdbsnc.smartiot.util.logger.impl.logback.appender;
//
//import java.io.PrintWriter;
//import java.io.StringWriter;
//import java.net.UnknownHostException;
//import java.text.SimpleDateFormat;
//import java.util.Arrays;
//import java.util.Date;
//import java.util.List;
//import java.util.TimeZone;
//
//import org.slf4j.Marker;
//
//import com.mongodb.BasicDBObject;
//import com.mongodb.DB;
//import com.mongodb.DBCollection;
//import com.mongodb.DBObject;
//import com.mongodb.MongoClient;
//import com.mongodb.MongoException;
//
//import ch.qos.logback.classic.spi.ILoggingEvent;
//import ch.qos.logback.classic.spi.IThrowableProxy;
//import ch.qos.logback.classic.spi.ThrowableProxyUtil;
//import ch.qos.logback.core.CoreConstants;
//import ch.qos.logback.core.UnsynchronizedAppenderBase;
//
//public class MongoDBAgender extends UnsynchronizedAppenderBase<ILoggingEvent>{
//
//    private String host;
//    private int port;
//    private String db;
//    private String collection;
//    private DBCollection logCollection;
//    
//    public static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
//	static {
//		sdf.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));	
//	}
//	
//    
//    
//    @Override
//    public void start() {
//		super.start();
//        Thread test = new Thread(new Runnable() {
//				
//				@Override
//				public void run() {
//
//				try {
//					connect();
//				} catch (UnknownHostException | MongoException e) {
//					addError("Can't connect to mongo: host=" + host + ", port=" + port, e);
//				}
//			}
//		});
//        test.start();
//    }
// 
//    private void connect() throws UnknownHostException {
//        MongoClient client = new MongoClient(host, port);
//        DB mongoDb = client.getDB(db == null ? "log" : db);
//        logCollection = mongoDb.getCollection(collection == null ? "log" : collection);
//    }
// 
//    @Override
//    protected void append(ILoggingEvent evt) {
//        if (evt == null) return; //just in case
// 
//        DBObject log = getBasicLog(evt);
//        try {
//            logException(evt.getThrowableProxy(), log);
//            logCollection.insert(log);
//        } catch (Exception e) {
//            try {
//                StringWriter sw = new StringWriter();
//                e.printStackTrace(new PrintWriter(sw));
//                log.put("logging_error", "Could not log all the event information: " + sw.toString());
//                log.put("level", "ERROR");
//                logCollection.insert(log);
//            } catch (Exception e2) { //really not working
//                addError("Could not insert log to mongo: " + evt, e2);
//            }
//        }
//    }
// 
//    private DBObject getBasicLog(ILoggingEvent evt) {
//        DBObject log = new BasicDBObject();
//        log.put("logger", evt.getLoggerName());
//        log.put("timestamp", sdf.format(new Date()));
//        log.put("level", String.valueOf(evt.getLevel())); //in case getLevel returns null
//        Marker m = evt.getMarker();
//        if (m != null) {
//            log.put("marker", m.getName());
//        }
//        log.put("thread", evt.getThreadName());
//        log.put("message", evt.getFormattedMessage());
//        return log;
//    }
// 
//    private void logException(IThrowableProxy tp, DBObject log) {
//        if (tp == null) return;
//        String tpAsString = ThrowableProxyUtil.asString(tp); //the stack trace basically
//        List<String> stackTrace = Arrays.asList(tpAsString.replace("\t","").split(CoreConstants.LINE_SEPARATOR));
//        if (stackTrace.size() > 0) {
//            log.put("exception", stackTrace.get(0));
//        }
//        if (stackTrace.size() > 1) {
//            log.put("stacktrace", stackTrace.subList(1, stackTrace.size()));
//        }
//    }
// 
//    public String getHost() {
//        return host;
//    }
// 
//    public void setHost(String host) {
//        this.host = host;
//    }
// 
//    public int getPort() {
//        return port;
//    }
// 
//    public void setPort(int port) {
//        this.port = port;
//    }
// 
//    public String getDb() {
//        return db;
//    }
// 
//    public void setDb(String db) {
//        this.db = db;
//    }
// 
//    public String getCollection() {
//        return collection;
//    }
// 
//    public void setCollection(String collection) {
//        this.collection = collection;
//    }
//}

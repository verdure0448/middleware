$SMARTIOT_HOME/jre/bin/java -Xmx1024m -Xms512m -XX:+HeapDumpOnOutOfMemoryError \
-XX:HeapDumpPath=$SMARTIOT_HOME -XshowSettings:all -Dfile.encoding=UTF-8 -Dosgi.bundles=\
$SMARTIOT_HOME/lib/javax.servlet_3.1.0.v201410161800.jar@1:start,\
$SMARTIOT_HOME/lib/org.apache.felix.gogo.command_0.10.0.v201209301215.jar@1:start,\
$SMARTIOT_HOME/lib/org.apache.felix.gogo.runtime_0.10.0.v201209301036.jar@1:start,\
$SMARTIOT_HOME/lib/org.apache.felix.gogo.shell_0.10.0.v201212101605.jar@1:start,\
$SMARTIOT_HOME/lib/org.eclipse.equinox.console_1.1.100.v20141023-1406.jar@1:start,\
$SMARTIOT_HOME/lib/com.hdbsnc.smartiot_1.0.0.jar@2:start,\
$SMARTIOT_HOME/lib/com.hdbsnc.smartiot.em_1.0.0.jar@2:start,\
$SMARTIOT_HOME/lib/com.hdbsnc.smartiot.ism_1.0.0.jar@2:start,\
$SMARTIOT_HOME/lib/com.hdbsnc.smartiot.pdm_1.0.0.jar@2:start,\
$SMARTIOT_HOME/lib/com.hdbsnc.smartiot.pm_1.0.0.jar@2:start,\
$SMARTIOT_HOME/lib/com.hdbsnc.smartiot.adapter.mb.mc.bin_1.0.0.jar@3:start,\
$SMARTIOT_HOME/lib/com.hdbsnc.smartiot.adapter.zeromq_1.0.0.jar@3:start,\
$SMARTIOT_HOME/lib/com.hdbsnc.smartiot.service.auth_1.0.0.jar@4:start,\
$SMARTIOT_HOME/lib/com.hdbsnc.smartiot.service.master_1.0.0.jar@4:start,\
$SMARTIOT_HOME/lib/com.hdbsnc.smartiot.service.slave_1.0.0.jar@4:start,\
$SMARTIOT_HOME/lib/com.hdbsnc.smartiot.service.command_1.0.0.jar@4:start,\
$SMARTIOT_HOME/lib/com.hdbsnc.smartiot.service.autostart_1.0.0.jar@5:start \
-jar $SMARTIOT_HOME/lib/org.eclipse.osgi_3.10.101.v20150820-1432.jar -console 9999 &

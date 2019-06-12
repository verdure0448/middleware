@echo off
>nul 2>&1 "%SYSTEMROOT%\system32\cacls.exe" "%SYSTEMROOT%\system32\config\system"
if '%errorlevel%' NEQ '0' (
    echo administrator active request....
    goto UACPrompt
) else ( goto gotAdmin )
:UACPrompt
    echo Set UAC = CreateObject^("Shell.Application"^) > "%temp%\getadmin.vbs"
    set params = %*:"=""
    echo UAC.ShellExecute "cmd.exe", "/c %~s0 %params%", "", "runas", 1 >> "%temp%\getadmin.vbs"


    "%temp%\getadmin.vbs"
    exit /B

:gotAdmin
pushd "%CD%"
    CD /D "%~dp0"

echo Windows ID : %username%

echo HOME Directory : %SMARTIOT_HOME%

nssm install SmartIoT "%SMARTIOT_HOME%\jre1.8.0_131\bin\java.exe" -Xmx1024m -Xms512m -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=C:/Users/mic/smartiot -XshowSettings:all -Dosgi.bundles="..\lib\javax.servlet_3.1.0.v201410161800.jar@1:start,..\lib\org.apache.felix.gogo.command_0.10.0.v201209301215.jar@1:start,..\lib\org.apache.felix.gogo.runtime_0.10.0.v201209301036.jar@1:start,..\lib\org.apache.felix.gogo.shell_0.10.0.v201212101605.jar@1:start,..\lib\org.eclipse.equinox.console_1.1.100.v20141023-1406.jar@1:start,..\lib\com.hdbsnc.smartiot_1.0.0.jar@2:start,..\lib\com.hdbsnc.smartiot.ecm_1.0.0.jar@2:start,..\lib\com.hdbsnc.smartiot.em_1.0.0.jar@2:start,..\lib\com.hdbsnc.smartiot.ism_1.0.0.jar@2:start,..\lib\com.hdbsnc.smartiot.pdm_1.0.0.jar@2:start,..\lib\com.hdbsnc.smartiot.pm_1.0.0.jar@2:start,..\lib\com.hdbsnc.smartiot.redis_1.0.0.jar@2:start,..\lib\com.hdbsnc.smartiot.adapter.dbf_1.0.0.jar@3:start,..\lib\com.hdbsnc.smartiot.adapter.websocketapi_1.0.0.jar@3:start,..\lib\com.hdbsnc.smartiot.adapter.mb.mc.bin_1.0.0.jar@3:start,..\lib\com.hdbsnc.smartiot.adapter.time.syncronization.client_1.0.0.jar@3:start,..\lib\com.hdbsnc.smartiot.adapter.time.syncronization.server_1.0.0.jar@3:start,..\lib\com.hdbsnc.smartiot.adapter.mqtt.subscribe_1.0.0.jar@3:start,..\lib\com.hdbsnc.smartiot.service.auth_1.0.0.jar@4:start,..\lib\com.hdbsnc.smartiot.service.master_1.0.0.jar@4:start,..\lib\com.hdbsnc.smartiot.service.slave_1.0.0.jar@4:start,..\lib\com.hdbsnc.smartiot.service.kafka.producer_1.0.0.jar@4:start,..\lib\com.hdbsnc.smartiot.service.redis.buffer_1.0.0.jar@4:start,..\lib\com.hdbsnc.smartiot.service.ui_1.0.0.jar@4:start,..\lib\com.hdbsnc.smartiot.service.autostart_1.0.0.jar@5:start" -jar lib\org.eclipse.osgi_3.10.101.v20150820-1432.jar -console 9999
nssm set SmartIoT AppDirectory "%SMARTIOT_HOME%"
nssm set SmartIoT ObjectName LocalSystem
nssm set SmartIoT Type SERVICE_WIN32_OWN_PROCESS
nssm set SmartIoT Description "Hyundai BS&C SmartIoT2.0 Middleware"
nssm set SmartIoT Start SERVICE_AUTO_START
nssm start SmartIoT
pause
introscope.autoprobe.enable=true
introscope.autoprobe.dynamicinstrument.enabled=true 
introscope.autoprobe.dynamicinstrument.pollIntervalMinutes=1 
introscope.autoprobe.dynamicinstrument.classFileSizeLimitInMegs=1 
introscope.autoprobe.dynamic.limitRedefinedClassesPerBatchTo=10


introscope.autoprobe.directivesFile=ctile.pbd,hotdeploy

log4j.logger.IntroscopeAgent=INFO, console
log4j.additivity.IntroscopeAgent=false
log4j.appender.console=com.wily.org.apache.log4j.ConsoleAppender
log4j.appender.console.layout=com.wily.org.apache.log4j.PatternLayout
log4j.appender.console.layout.ConversionPattern=%d{M/dd/yy hh:mm:ss a z} [%-3p] [%c] %m%n
log4j.appender.console.target=System.err

introscope.agent.enterprisemanager.connectionorder=DEFAULT


introscope.agent.enterprisemanager.transport.tcp.host.DEFAULT=localhost
introscope.agent.enterprisemanager.transport.tcp.port.DEFAULT=5001
introscope.agent.enterprisemanager.transport.tcp.socketfactory.DEFAULT=com.wily.isengard.postofficehub.link.net.DefaultSocketFactory

introscope.agent.customProcessName=HIEXProcess
introscope.agent.agentName=CTileAgent

introscope.agent.extensions.directory=ext
introscope.agent.clonedAgent=false

introscope.agent.platform.monitor.system=LinuxIntelAmd64 



introscope.agent.jmx.enable=true
introscope.agent.remoteagentdynamicinstrumentation.enabled=true 

introscope.autoprobe.deepinheritance.enabled=true
  
  
introscope.autoprobe.hierarchysupport.enabled=true  
introscope.autoprobe.hierarchysupport.pollIntervalMinutes=5
introscope.autoprobe.hierarchysupport.executionCount=3 
introscope.autoprobe.hierarchysupport.disableLogging=true 



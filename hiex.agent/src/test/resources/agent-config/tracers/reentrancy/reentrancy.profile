########################################################################
#                                                                      
# Introscope AutoProbe and Agent Configuration                         
#                                                                      
# CA Wily Introscope(R) Version 9.0 Release 9.0.0.0
# Copyright (c) 2010 CA. All Rights Reserved.
# Introscope(R) is a registered trademark of CA.
########################################################################

########################
# AutoProbe Properties #
########################

#######################
# On/Off Switch
#
# ================
# This boolean property gives you the ability to disable
# Introscope AutoProbe by settings the property value
# to false.
# You must restart the managed application before changes to this property take effect.

introscope.autoprobe.enable=true


#######################
# Custom Log File Location
#
# ================
# Introscope AutoProbe will always attempt to log the changes
# it makes.  Set this property to move the location of the
# log file to something other than the default.  Non-absolute
# names are resolved relative to the location of this
# properties file.
# You must restart the managed application before changes to this property take effect.

introscope.autoprobe.logfile=logs/AutoProbe.log


#######################
# Directives Files
#
# ================
# This property specifies all the directives files that determine
# how Introscope AutoProbe performs the instrumentation.  Specify
# a single entry, or a comma-delimited list of entries. The list 
# may include any combination of:
#    - directives (.pbd) files
#    - directives list (.pbl) files 
#    - directories that will be scanned about once per minute for  
#      .pbd files. Directives files placed in a listed directory
#      will be loaded automatically, without any need to edit this 
#      Agent profile. If dynamic instrumentation is enabled, the 
#      directives will take effect immediately without an app reboot.    
# Non-absolute names will be resolved relative to the location of 
# this properties file.
# IMPORTANT NOTE: This is a required parameter and it MUST be set
# to a valid value.  
#    - If the property is not specified or the values are invalid, 
#      the Introscope Agent will not run!  
#    - If the property is set to include a directory, and invalid 
#      directives files are placed in the directory, AutoProbe  
#      metrics will no longer be reported!
#    - If the property is set to include a directory, and loaded 
#      directives files are removed from the directory, AutoProbe  
#      metrics will no longer be reported!
# You must restart the managed application before changes to this property 
# take effect. However, if the property includes one or more directories, 
# and dynamic instrumentation is enabled, the Introscope Agent will load 
# directives files from the specified directories without an app restart, 
# as noted above.

introscope.autoprobe.directivesFile=reentrancy.pbd,hotdeploy


#######################
# Agent Properties    #
#######################


#################################
# Logging Configuration
#
# ================
# Changes to this property take effect immediately and do not require the managed application to be restarted.
# This property controls both the logging level and the output location.
# To increase the logging level, set the property to:
# log4j.logger.IntroscopeAgent=VERBOSE#com.wily.util.feedback.Log4JSeverityLevel, console, logfile
# To send output to the console only, set the property to:
# log4j.logger.IntroscopeAgent=INFO, console
# To send output to the logfile only, set the property to:
# log4j.logger.IntroscopeAgent=INFO, logfile

log4j.logger.IntroscopeAgent=INFO, console, logfile

# If "logfile" is specified in "log4j.logger.IntroscopeAgent",
# the location of the log file is configured using the
# "log4j.appender.logfile.File" property.
# System properties (Java command line -D options)
# are expanded as part of the file name.  For example,
# if Java is started with "-Dmy.property=Server1", then
# "log4j.appender.logfile.File=logs/Introscope-${my.property}.log"
# is expanded to:
# "log4j.appender.logfile.File=logs/Introscope-Server1.log".

log4j.appender.logfile.File=logs/IntroscopeAgent.log
 
########## See Warning below ##########
# Warning: The following properties should not be modified for normal use.
# You must restart the managed application before changes to this property take effect.
log4j.additivity.IntroscopeAgent=false
log4j.appender.console=com.wily.org.apache.log4j.ConsoleAppender
log4j.appender.console.layout=com.wily.org.apache.log4j.PatternLayout
log4j.appender.console.layout.ConversionPattern=%d{M/dd/yy hh:mm:ss a z} [%-3p] [%c] %m%n
log4j.appender.console.target=System.err
log4j.appender.logfile=com.wily.introscope.agent.AutoNamingRollingFileAppender
log4j.appender.logfile.layout=com.wily.org.apache.log4j.PatternLayout
log4j.appender.logfile.layout.ConversionPattern=%d{M/dd/yy hh:mm:ss a z} [%-3p] [%c] %m%n
log4j.appender.logfile.MaxBackupIndex=4
log4j.appender.logfile.MaxFileSize=2MB
#########################################


#################################
# Enterprise Manager Connection Order
#
# ================
# The Enterprise Manager connection order list the Agent uses if it 
# is disconnected from its Enterprise Manager.
# You must restart the managed application before changes to this property take effect.

introscope.agent.enterprisemanager.connectionorder=DEFAULT


#################################
# Enterprise Manager Locations and Names 
# (names defined in this section are only used in the 
# introscope.agent.enterprisemanager.connectionorder property)
#
# ================
# Settings the Introscope Agent uses to find the Enterprise Manager 
# and names given to host and port combinations.
# You must restart the managed application before changes to this property take effect.

introscope.agent.enterprisemanager.transport.tcp.host.DEFAULT=localhost
introscope.agent.enterprisemanager.transport.tcp.port.DEFAULT=5001
introscope.agent.enterprisemanager.transport.tcp.socketfactory.DEFAULT=com.wily.isengard.postofficehub.link.net.DefaultSocketFactory

# The following connection properties enable the Agent to tunnel communication 
# to the Enterprise Manager over HTTP.
#
# WARNING: This type of connection will impact Agent and Enterprise Manager 
# performance so it should only be used if a direct socket connection to the 
# the Enterprise Manager is not feasible. This may be the case if the Agent 
# is isolated from the Enterprise Manager with a firewall blocking all but 
# HTTP traffic.
# 
# When enabling the HTTP tunneling Agent, uncomment the following host, port, 
# and socket factory properties, setting the host name and port for the 
# Enterprise Manager Web Server. Comment out any other connection properties 
# assigned to the "DEFAULT" channel and confirm that the "DEFAULT" channel is 
# assigned as a value for the "introscope.agent.enterprisemanager.connectionorder" 
# property.
# You must restart the managed application before changes to this property take effect.
#introscope.agent.enterprisemanager.transport.tcp.host.DEFAULT=localhost
#introscope.agent.enterprisemanager.transport.tcp.port.DEFAULT=8081
#introscope.agent.enterprisemanager.transport.tcp.socketfactory.DEFAULT=com.wily.isengard.postofficehub.link.net.HttpTunnelingSocketFactory

# The following properties are used only when the Agent is tunneling over HTTP 
# and the Agent must connect to the Enterprise Manager through a proxy server 
# (forward proxy). Uncomment and set the appropriate proxy host and port values. 
# If the proxy server cannot be reached at the specified host and port, the 
# Agent will try a direct HTTP tunneled connection to the Enterprise Manager 
# before failing the connection attempt.
# You must restart the managed application before changes to this property take effect.
#introscope.agent.enterprisemanager.transport.http.proxy.host=
#introscope.agent.enterprisemanager.transport.http.proxy.port=

# The following properties are used only when the proxy server requires 
# authentication. Uncomment and set the user name and password properties.
# You must restart the managed application before changes to this property take effect.
#introscope.agent.enterprisemanager.transport.http.proxy.username=
#introscope.agent.enterprisemanager.transport.http.proxy.password=

# To connect to the Enterprise Manager using HTTPS (HTTP over SSL),
# uncomment these properties and set the host and port to the EM's secure https listener host and port.
#introscope.agent.enterprisemanager.transport.tcp.host.DEFAULT=localhost
#introscope.agent.enterprisemanager.transport.tcp.port.DEFAULT=8444
#introscope.agent.enterprisemanager.transport.tcp.socketfactory.DEFAULT=com.wily.isengard.postofficehub.link.net.HttpsTunnelingSocketFactory

# To connect to the Enterprise Manager using SSL,
# uncomment these properties and set the host and port to the EM's SSL server socket host and port.
#introscope.agent.enterprisemanager.transport.tcp.host.DEFAULT=localhost
#introscope.agent.enterprisemanager.transport.tcp.port.DEFAULT=5443
#introscope.agent.enterprisemanager.transport.tcp.socketfactory.DEFAULT=com.wily.isengard.postofficehub.link.net.SSLSocketFactory


# Additional properties for connecting to the Enterprise Manager using SSL.
#
# Location of a truststore containing trusted EM certificates.
# If no truststore is specified, the agent trusts all certificates.
# Either an absolute path or a path relative to the agent's working directory.
# On Windows, backslashes must be escaped.  For example: C:\\keystore
#introscope.agent.enterprisemanager.transport.tcp.truststore.DEFAULT=
# The password for the truststore
#introscope.agent.enterprisemanager.transport.tcp.trustpassword.DEFAULT=
# Location of a keystore containing the agent's certificate.
# A keystore is needed if the EM requires client authentication.
# Either an absolute path or a path relative to the agent's working directory.
# On Windows, backslashes must be escaped.  For example: C:\\keystore
#introscope.agent.enterprisemanager.transport.tcp.keystore.DEFAULT=
# The password for the keystore
#introscope.agent.enterprisemanager.transport.tcp.keypassword.DEFAULT=
# Set the enabled cipher suites.
# A comma-separated list of cipher suites.
# If not specified, use the default enabled cipher suites.
#introscope.agent.enterprisemanager.transport.tcp.ciphersuites.DEFAULT=


#################################
# Enterprise Manager Failback Retry Interval
#
# ================
# When the Agent is configured to have multiple Enterprise Managers
# in its connection order and this property is enabled, the Introscope 
# Agent will automatically attempt to reconnect to the first Enterprise
# Manager in its connection order when it is connected to any other
# Enterprise Manager. The reconnection attempt will occur on a regular
# interval as specified.
# You must restart the managed application before changes to this property take effect.

#introscope.agent.enterprisemanager.failbackRetryIntervalInSeconds=120


#######################
# Custom Process Name
#
# ================
# Specify the process name as it should appear in the
# Introscope Enterprise Manager and Workstation.
# You must restart the managed application before changes to this property take effect.

introscope.agent.customProcessName=HIEXProcess


#######################
# Default Process Name
#
# ================
# If no custom process name is provided and the
# agent is unable to determine the name of the
# main application class, this value will be
# used for the process name.
# You must restart the managed application before changes to this property take effect.

introscope.agent.defaultProcessName=UnknownProcess


#######################
# Agent Name
#
# ================
# Specify the name of this agent as it appears in the
# Introscope Enterprise Manager and Workstation.

# Use this property if you want to specify the Agent
# Name using the value of a Java System Property.
# You must restart the managed application before changes to this property take effect.
introscope.agent.agentNameSystemPropertyKey=

# This enables/disables auto naming of the agent using
# an Application Server custom service.
# You must restart the managed application before changes to this property take effect.
introscope.agent.agentAutoNamingEnabled=false

# The amount of time to delay connecting to the Introscope Enterprise
# Manager while Agent Auto Naming is attempted.
# You must restart the managed application before changes to this property take effect.
introscope.agent.agentAutoNamingMaximumConnectionDelayInSeconds=120

# When Agent Auto Naming is enabled, the Agent will check for 
# a new Application Server determined name on the specified interval.
# You must restart the managed application before changes to this property take effect.
introscope.agent.agentAutoRenamingIntervalInMinutes=10

# Auto name of log files (Agent, AutoProbe and LeakHunter) with
# the Agent name or a timestamp can be disabled by setting the 
# value of this property to 'true'.  Log file auto naming only 
# takes effect when the Agent name can be determined using a 
# Java System Property or an Application Server custom service.
# You must restart the managed application before changes to this property take effect.
introscope.agent.disableLogFileAutoNaming=false

# Uncomment this property to provide a default Agent Name 
# if the other methods fail.
# You must restart the managed application before changes to this property take effect.
introscope.agent.agentName=ReentrancyAgent

# Fully Qualified Domain Name (FQDN) can be enabled by setting this property  
# value to 'true'. By Default (false) it will display HostName.
# You must restart the managed application before changes to this property take effect.
introscope.agent.display.hostName.as.fqdn=false


#######################
# Agent Socket Rate Metrics
#
# ================
# Set to true to enable the reporting of input and
# output bandwidth rate metrics for individual sockets.   NOTE: this parameter is only
# used if PDB flag ManagedSocketTracing is 'on'.
# You must restart the managed application before changes to this property take effect.
introscope.agent.sockets.reportRateMetrics=true

############################### 
#Agent Memory Overhead Setting 
# 
# ======================= 
# Set to true if you want to attempt to reduce the agent memory overhead introduced by architectural improvements to the 8.x Agent.
# Increased Agent memory overhead only occurs in certain extreme cases.
# The trade-off for the possible lower memory consumption is a possible increase in response time.  
# Each application is unique and will experience different variations in Memory vs. Response Time trade-offs.
# This property is set to false by default and out of the box is commented out. This is not a hot property 
# and the managed application needs to be restarted for this flag to take effect. 
     
#introscope.agent.reduceAgentMemoryOverhead=true 

#######################
# Agent I/O Socket Metrics
# Generation of I/O Socket metrics may be restricted by the following parameters.
# Each parameter consists of a comma-separate list of values.  If any individual value is
# invalid, it will be ignored.  If any parameter is not defined, or after exclusion of any
# invalid values is an empty list, no restriction will apply to that parameter.  All 
# parameters are 'hot', i.e. no restart of the managed application is required before the
# changed value is used.
#
# Restrict socket client connections instrumented to those with specified remote hosts
introscope.agent.io.socket.client.hosts=
# Restrict socket client connections instrumented to those with specified remote ports
introscope.agent.io.socket.client.ports=
# Restrict socket client connections instrumented to those using specified local ports.
introscope.agent.io.socket.server.ports=


#######################
# Agent NIO Metrics
# Generation of Datagram and Socket metrics may be restricted by the following parameters.
# Each parameter consists of a comma-separate list of values.  If any individual value is
# invalid, it will be ignored.  If any parameter is not defined, or after exclusion of any
# invalid values is an empty list, no restriction will apply to that parameter.  All 
# parameters are 'hot', i.e. no restart of the managed application is required before the
# changed value is used.
#
# Restrict datagram client connections instrumented to those with specified remote hosts
introscope.agent.nio.datagram.client.hosts=
# Restrict datagram client connections instrumented to those with specified remote ports
introscope.agent.nio.datagram.client.ports=
# Restrict datagram client connections instrumented to those using specified local ports.
introscope.agent.nio.datagram.server.ports=
# Restrict socket client connections instrumented to those with specified remote hosts
introscope.agent.nio.socket.client.hosts=
# Restrict socket client connections instrumented to those with specified remote ports
introscope.agent.nio.socket.client.ports=
# Restrict socket client connections instrumented to those using specified local ports.
introscope.agent.nio.socket.server.ports=


#######################
# Agent Extensions Directory
#
# ================
# This property specifies the location of all extensions to be loaded
# by the Introscope Agent.  Non-absolute names are resolved relative 
# to the location of this properties file.
# You must restart the managed application before changes to this property take effect.

introscope.agent.extensions.directory=ext


#######################
# Agent Thread Priority
#
# ================
# Controls the priority of agent threads.  Varies
# from 1 (low) to 10 (high). Default value if unspecified is Thread.NORM_PRIORITY (5)
# You must restart the managed application before changes to this property take effect.

#introscope.agent.thread.all.priority=5


#######################
# Cloned Agent Configuration
#
# ================
# Set to true when running identical copies of an application on the same machine.
# You must restart the managed application before changes to this property take effect.

introscope.agent.clonedAgent=false

#######################
# Blame Type Configuration
#
# ================
# Allowable values: standard, boundary
# Default is boundary
# You must restart the managed application before changes to this property take effect.
introscope.agent.blame.type=boundary

#######################
# Platform Monitor Configuration
#
# ================
# Use this property to override the Agent's default Platform Monitor
# detection. To override the default, uncomment the relevant
# definition of introscope.agent.platform.monitor.system from those
# shown below.
# You must restart the managed application before changes to this property take effect.

#introscope.agent.platform.monitor.system=SolarisAmd32
#introscope.agent.platform.monitor.system=SolarisAmd64
#introscope.agent.platform.monitor.system=SolarisSparc32
#introscope.agent.platform.monitor.system=SolarisSparc64
#introscope.agent.platform.monitor.system=AIXPSeries32
#introscope.agent.platform.monitor.system=AIXPSeries64
#introscope.agent.platform.monitor.system=HP-UXItanium
#introscope.agent.platform.monitor.system=HP-UXParisc32
#introscope.agent.platform.monitor.system=HP-UXParisc64
#introscope.agent.platform.monitor.system=WindowsIntelAmd32
#introscope.agent.platform.monitor.system=WindowsIntelAmd64
#introscope.agent.platform.monitor.system=LinuxIntelAmd32
introscope.agent.platform.monitor.system=LinuxIntelAmd64 


#######################
# WebSphere 4.0 and 5.0 PMI Configuration
#
# ================
# Controls collection of WebSphere's internal
# performance monitoring infrastructure (PMI) data;
# set to true to gather data from WebSphere PMI
# in the Introscope Agent.
# You must restart the managed application before changes to this property take effect.
introscope.agent.pmi.enable=false

# Enable collection of different PMI data categories
# by setting introscope.agent.pmi.enable.categoryName to true
# (The data category must be turned on in WebSphere to
# be visible as Introscope data).
# You must restart the managed application before changes to this property take effect.
introscope.agent.pmi.enable.threadPool=true
introscope.agent.pmi.enable.servletSessions=true
introscope.agent.pmi.enable.connectionPool=true
introscope.agent.pmi.enable.bean=false
introscope.agent.pmi.enable.transaction=false
introscope.agent.pmi.enable.webApp=false
introscope.agent.pmi.enable.jvmRuntime=false
# The below properties are only for WebSphere 5.0, 6.0 
# Note that PMI interfaces are deprecated in WAS 6.0
introscope.agent.pmi.enable.system=false 
introscope.agent.pmi.enable.cache=false 
introscope.agent.pmi.enable.orbPerf=false 
introscope.agent.pmi.enable.j2c=false 
introscope.agent.pmi.enable.webServices=false 
introscope.agent.pmi.enable.wlm=false 
introscope.agent.pmi.enable.wsgw=false 
introscope.agent.pmi.enable.alarmManager=false
introscope.agent.pmi.enable.hamanager=false
introscope.agent.pmi.enable.objectPool=false
introscope.agent.pmi.enable.scheduler=false

# introscope.agent.pmi.enable.jvmpi=false # not default in WebSphere 5.0


#######################
# JMX Configuration 
#
# ================
# Controls collection of data from JMX MBeans;
# set to true to gather JMX data in the Introscope Agent.
# You must restart the managed application before changes to this property take effect.

introscope.agent.jmx.enable=false


# Configure primary name keys to use for conversion of 
# MBean names into Introscope metric names;
# A comma-separated, ordered list of keys which
# should uniquely identify a particular MBean.
# You must restart the managed application before changes to this property take effect.

#introscope.agent.jmx.name.primarykeys=

# Controls which JMX data is gathered - a comma-separated
# list of desired keywords  If the Introscope metric name contains 
# one of them, the metric will be polled by the Introscope Agent.
# Leave empty to include all MBean data available in the system.
# * and ? wildcard characters are supported and can be escaped with \\.
# You must restart the managed application before changes to this property take effect.

introscope.agent.jmx.name.filter=

# Controls which (if any) JMX MBean attributes are to be ignored.
# A comma-separated list of desired keywords. If an MBean attribute
# name matches one in this list then it will be ignored.
# Leave empty to include all MBean attributes.
# You must restart the managed application before changes to this property take effect.

#introscope.agent.jmx.ignore.attributes=server

# Controls whether or not to include string-valued metrics.
# Excluding string-valued metrics reduces the overall metric
# count, improving agent and EM performance.  To enable
# string-valued metrics, set this property value to false.
# You must restart the managed application before changes to this property take effect.
introscope.agent.jmx.excludeStringMetrics=true

#######################
# LeakHunter Configuration
#
# ================
# Configuration settings for Introscope LeakHunter

# Controls whether the feature is enabled if the LeakHunter Add-on is present.
# Set the value to true to enable LeakHunter.
# You must restart the managed application before changes to this property take effect.

introscope.agent.leakhunter.enable=false

# Controls the location for the LeakHunter log file.
# Relative filenames are relative to the application working directory.
# Leave the value blank if you do not want LeakHunter to record data
# to a log file.
# You must restart the managed application before changes to this property take effect.

introscope.agent.leakhunter.logfile.location=logs/LeakHunter.log

# Controls whether LeakHunter will append or overwrite the log file.
# Set the value to true to append to the log file.
# You must restart the managed application before changes to this property take effect.

introscope.agent.leakhunter.logfile.append=false

# Controls the sensitivity of the leak detection algorithm.
# The value should be an integer from 1-10.  A higher
# sensitivity setting will result in more potential leaks
# reported and a lower sensitivity will result in fewer
# potential leaks reported.
# You must restart the managed application before changes to this property take effect.

introscope.agent.leakhunter.leakSensitivity=5

# Controls the length of time LeakHunter spends looking for new
# potential leaks.  After the timeout, LeakHunter will stop looking
# for new potential leaks and just continue tracking the previously
# identified potential leaks.  Set the value to zero if you want
# LeakHunter to always look for new potential leaks.
# You must restart the managed application before changes to this property take effect.

introscope.agent.leakhunter.timeoutInMinutes=120

# Controls whether LeakHunter generates allocation stack traces for
# potential leaks.  Turning this on gives you more precise data about
# the potential leak's allocation, but requires additional memory and
# CPU overhead.  For this reason, the default setting is false.
# Changes to this property take effect immediately and do not require the managed application to be restarted.
introscope.agent.leakhunter.collectAllocationStackTraces=false

# Changes to this property take effect immediately and do not require the managed application to be restarted.
introscope.agent.leakhunter.ignore.0=org.apache.taglibs.standard.lang.jstl.*
introscope.agent.leakhunter.ignore.1=com.bea.medrec.entities.RecordEJB_xwcp6o__WebLogic_CMP_RDBMS
introscope.agent.leakhunter.ignore.2=net.sf.hibernate.collection.*
introscope.agent.leakhunter.ignore.3=org.jnp.interfaces.FastNamingProperties
introscope.agent.leakhunter.ignore.4=java.util.SubList
introscope.agent.leakhunter.ignore.5=com.sun.faces.context.BaseContextMap$EntrySet
introscope.agent.leakhunter.ignore.6=com.sun.faces.context.BaseContextMap$KeySet
introscope.agent.leakhunter.ignore.7=com.sun.faces.context.SessionMap
introscope.agent.leakhunter.ignore.8=java.util.Collections$UnmodifiableMap
introscope.agent.leakhunter.ignore.9=org.hibernate.collection.PersistentSet




#######################
# SQL Agent Configuration
#
# ================
# Configuration settings for Introscope SQL Agent

# The following setting configures SQL Agent to optionally participate
# in the Introscope blame stack, thus creating blame metrics.
# You must restart the managed application before changes to this property take effect.

introscope.agent.sqlagent.useblame=true


######################################
# SQL Agent Normalizer extension
#
# ================
# Configuration settings for SQL Agent normalizer extension


# Specifies the name of the sql normalizer extension that will be used 
# to override the preconfigured normalization scheme. To make custom 
# normalization extension work, the value of its manifest attribute 
# com-wily-Extension-Plugin-{pluginName}-Name should match with the 
# value given to this property. If you specify a comma separated list 
# of names, only the first name will be used. Example, 
# introscope.agent.sqlagent.normalizer.extension=ext1, ext2
# Only ext1 will be used for normalization. By default we now ship the  
# RegexSqlNormalizer extension
# Changes to this property take effect immediately and do not 
# require the managed application to be restarted.

#introscope.agent.sqlagent.normalizer.extension=RegexSqlNormalizer

##############################
# RegexSqlNormalizer extension
#
# ==================
# The following properties pertain to RegexSqlNormalizer which 
# uses regex patterns and replace formats to normalize the sql in 
# a user defined way. 


# This property if set to true will make sql strings to be
# evaluated against all the regex key groups. The implementation
# is chained. Hence, if the sql matches multiple key groups, the
# normalized sql output from group1 is fed as input to group2 and 
# so on. If the property is set to 'false', as soon as a key group  
# matches, the normalized sql output from that group is returned
# Changes to this property take effect immediately and do not require 
# the managed application to be restarted.
# Default value is 'false'
#introscope.agent.sqlagent.normalizer.regex.matchFallThrough=true

# This property specifies the regex group keys. They are evaluated in order
# Changes to this property take effect immediately and do not 
# require the managed application to be restarted.
#introscope.agent.sqlagent.normalizer.regex.keys=key1

# This property specifies the regex pattern that will be used
# to match against the sql. All valid regex alowed by java.util.Regex
# package can be used here.
# Changes to this property take effect immediately and do not 
# require the managed application to be restarted.
# eg: (\\b[0-9,.]+\\b) will filter all number values, ('.*?') will filter
# anything between single quotes, ((?i)\\bTRUE\\b|\\bFALSE\\b) will filter
# boolean values from the query.
#introscope.agent.sqlagent.normalizer.regex.key1.pattern=(".*?")|('.*?')|(\\b[0-9,.]+\\b)|((?i)\\bTRUE\\b|\\bFALSE\\b)

# This property if set to 'false' will replace the first occurrence of the
# matching pattern in the sql with the replacement string. If set to 'true'
# it will replace all occurrences of the matching pattern in the sql with
# replacement string
# Changes to this property take effect immediately and do not 
# require the managed application to be restarted.
# Default value is 'false'
#introscope.agent.sqlagent.normalizer.regex.key1.replaceAll=true

# This property specifies the replacement string format. All valid 
# regex allowed by java.util.Regex package java.util.regex.Matcher class
# can be used here.
# eg: The default normalizer replaces the values with a question mark (?)
# Changes to this property take effect immediately and do not 
# require the managed application to be restarted.
#introscope.agent.sqlagent.normalizer.regex.key1.replaceFormat=?

# This property specifies whether the pattern match is sensitive to case
# Changes to this property take effect immediately and do not 
# require the managed application to be restarted.
#introscope.agent.sqlagent.normalizer.regex.key1.caseSensitive=false


#######################
# Agent Metric Clamp Configuration
#
# ================
# The following setting configures the Agent to approximately clamp the number of metrics sent to the EM  
# If the number of metrics pass this metric clamp value then no new metrics will be created.  Old metrics will still report values.
# If the property is not set then no metric clamping will occur. 
# Changes to this property take effect immediately and do not require the managed application to be restarted. 
# introscope.agent.metricClamp=5000


#######################
# Transaction Tracer Configuration
#
# ================
# Configuration settings for Introscope Transaction Tracer

# The following settings configure Transaction Tracer to optionally
# capture the user ID used to invoke servlets and JSPs if it is stored
# in one of the three following ways.  Uncomment the set of properties
# that correspond to how user IDs are stored in your application.
# Make sure only one set of properties are uncommented or the wrong
# properties could be used.

# Uncomment the following property if the user ID is accessed through HttpServletRequest.getRemoteUser.
# You must restart the managed application before changes to this property take effect.

#introscope.agent.transactiontracer.userid.method=HttpServletRequest.getRemoteUser

# Uncomment the following properties if the user ID is accessed through HttpServletRequest.getHeader.
# Make sure to set the key that is used by your application.
# You must restart the managed application before changes to this property take effect.

#introscope.agent.transactiontracer.userid.method=HttpServletRequest.getHeader
#introscope.agent.transactiontracer.userid.key=<application defined key string>

# Uncomment the following properties if the user ID is accessed through HttpSession.getValue.
# Make sure to set the key that is used by your application.
# You must restart the managed application before changes to this property take effect.

#introscope.agent.transactiontracer.userid.method=HttpSession.getValue
#introscope.agent.transactiontracer.userid.key=<application defined key string>

# Uncomment the following properties to record specific http request headers, parameters or session
#  attributes in the Transaction Tracer data.
# You must restart the managed application before changes to this property take effect.

#introscope.agent.transactiontracer.parameter.httprequest.headers=User-Agent
#introscope.agent.transactiontracer.parameter.httprequest.parameters=parameter1,parameter2
#introscope.agent.transactiontracer.parameter.httpsession.attributes=attribute1,attribute2

# Uncomment the following property to specify the maximum number of components allowed in a Transaction 
# Trace.  By default, the clamp is set at 5000.   
# Note that any Transaction Trace exceeding the clamp will be discarded at the agent, 
# and a warning message will be logged in the Agent log file.
# Warning: If this clamp size is increased, the requirement on the memory will be higher and
# as such, the max heap size for the JVM may need to be adjusted accordingly, or else the 
# managed application may run out of memory.
# Changes to this property take effect immediately and do not require the managed 
# application to be restarted.
#introscope.agent.transactiontrace.componentCountClamp=5000

# Uncomment the following property to specify the maximum depth of components allowed in
# head filtering, which is the process of examining the start of a transaction for
# the purpose of potentially collecting the entire transaction.  Head filtering will
# check until the first blamed component exits, which can be a problem on very deep
# call stacks when no clamping is done.  The clamp value will limit the memory and
# CPU utilization impact of this behavior by forcing the agent to only look up to a
# fixed depth.  By default, the clamp is set at 30.   
# Note that any Transaction Trace whose depth exceeds the clamp will no longer be examined
# for possible collection UNLESS some other mechanism, such as sampling or user-initiated
# transaction tracing, is active to select the transaction for collection.
# Warning: If this clamp size is increased, the requirement on the memory will be higher and
# as such, garbage collection behavior may be affected, which will have an application-wide
# performance impact.
# Changes to this property take effect immediately and do not require the managed application to be restarted.
#introscope.agent.transactiontrace.headFilterClamp=30

#
# Use compression to reduce the size of cross process tracing data. This option will increase agent CPU overhead, 
# but reduce the size of interprocess headers.
# Valid options are lzma, gzip or none. 
# LZMA compression is more efficient than GZIP, but may use more CPU.
# Note that .NET agents do not support gzip option, so if interoperability is required, do not use gzip.
introscope.agent.crossprocess.compression=lzma
# Minimum length of cross process parameter data length for which to apply compression
introscope.agent.crossprocess.compression.minlimit=1500
# Maximum size of cross process parameter data allowed.
# If total size of cross process parameter even after applying compression (if allowed) is more than this limit,
# some data will be dropped and some cross process correlation functionality will not work properly.
# However, this settings will protect user transactions from failing in network transmission due to too large header size.
introscope.agent.crossprocess.correlationid.maxlimit=4096
# Changes to above 3 properties take effect immediately and do not require the managed application to be restarted

# Uncomment the following property to disable Transaction Tracer Sampling
# Changes to this property take effect immediately and do not require the managed application to be restarted.
#introscope.agent.transactiontracer.sampling.enabled=false

# The following property limits the number of transactions that are reported by the agent 
# per reporting cycle. The default value if the property is not set is 200.
# You must restart the managed application before changes to this property take effect.
introscope.agent.ttClamp=50


#################################
# Cross Process Transaction Trace
# ===============================
# This property controls whether the presence of a tail filter triggers 
# automatic collection of traces from downstream agents or not. This property 
# does not affect collection of automatic downstream traces due to passing of
# head filters. 
# Uncomment the following property to enable automatic collection of downstream 
# traces due to tail filter. Its disabled by default. Enabling this property
# and running long periods of Transaction Trace session with tail filters can 
# cause large number of unwanted traces to be sent to the EM
# Changes to this property take effect immediately and do not require the managed 
# application to be restarted.

#introscope.agent.transactiontracer.tailfilterPropagate.enable=false
 
########################
# TT Sampling
# ================
# These are normally configured in the EM. Configuring in the Agent disables configuring
# them in the EM
# You must restart the managed application before changes to this property take effect.
#
#introscope.agent.transactiontracer.sampling.perinterval.count=1
#introscope.agent.transactiontracer.sampling.interval.seconds=120

#######################
# URL Grouping Configuration
#
# ================
# Configuration settings for Frontend naming.  By default, all frontends
# go into the "Default" group.  This is done so that invalid URLs (i.e.
# those that would generate a 404 error) do not create unique, one-time
# metrics -- this can bloat the EM's memory.  To get more meaningful
# metrics out of the Frontends|Apps|URLs tree, set up URL groups that
# are relevant to the deployment
# Changes to this property take effect immediately and do not require the managed application to be restarted.
introscope.agent.urlgroup.keys=default
introscope.agent.urlgroup.group.default.pathprefix=*
introscope.agent.urlgroup.group.default.format=Default

#######################
# Error Detector Configuration
#
# ================
# Configuration settings for Error Detector

# Please include errors.pbd in your pbl (or in introscope.autoprobe.directivesFile)

# The error snapshot feature captures transaction details about serious errors
# and enables recording of error count metrics.
# Changes to this property take effect immediately and do not require the managed application to be restarted.
introscope.agent.errorsnapshots.enable=true

# The following setting configures the maximum number of error snapshots
# that the Agent can send in a 15-second period.
# Changes to this property take effect immediately and do not require the managed application to be restarted.
introscope.agent.errorsnapshots.throttle=10

# The following series of properties lets you specify error messages 
# to ignore.  Error snapshots will not be generated or sent for
# errors with messages matching these filters.  You may specify
# as many as you like (using .0, .1, .2 ...). You may use wildcards (*).  
# The following are examples only.
# Changes to this property take effect immediately and do not require the managed application to be restarted.
#introscope.agent.errorsnapshots.ignore.0=*com.company.HarmlessException*
#introscope.agent.errorsnapshots.ignore.1=*HTTP Error Code: 404*

# Minimum threshold for stall event duration
# Changes to this property take effect immediately and do not require the managed application to be restarted.
introscope.agent.stalls.thresholdseconds=30

# Frequency that the agent checks for stall events
# Changes to this property take effect immediately and do not require the managed application to be restarted.
introscope.agent.stalls.resolutionseconds=10

#######################
# Remote Configuration Settings
#
# ================
# Configuration settings for remote configuration

# Enable/disable remote configuration of agent
# Changes to this property take effect immediately and do not require the managed application to be restarted.
introscope.agent.remoteagentconfiguration.enabled=true

# The exact list of files that are allowed to be remotely transferred to this agent
# Changes to this property take effect immediately and do not require the managed application to be restarted.
introscope.agent.remoteagentconfiguration.allowedFiles=domainconfig.xml

#######################
# Bootstrap Classes Instrumentation Manager
#
# ================
# Configuration settings for the bootstrap classes instrumentation manager

#enable/disable bootstrap manager. If set to false, no system classes will be 
#instrumented. If the property is not set, the default value is false.
#You must restart the managed application before changes to this property take effect.
introscope.bootstrapClassesManager.enabled=true 

#Define a wait time in seconds at startup before instrumenting bootstrap classes 
introscope.bootstrapClassesManager.waitAtStartup=5

#######################
# Remote Dynamic Instrumentation Settings
#
# ================
# Configuration settings for remote dynamic instrumentation 

# Enable/disable the remote management of dynamic instrumentation 
# You must restart the managed application before changes to this property take effect.
introscope.agent.remoteagentdynamicinstrumentation.enabled=true 

#######################
# Dynamic Instrumentation Settings 
# ================================= 
# This feature enables changes to PBDs to take effect without restarting the application server or the agent process.  
# This is a very CPU intensive operation, and it is highly recommended to use configuration to minimize the classes that are 
# being redefined.PBD editing is all that is required to trigger this process. 
  
# Enable/disable the dynamic instrumentation feature. 
# You must restart the managed application before changes to this property take effect.
#introscope.autoprobe.dynamicinstrument.enabled=true 
 
# The polling interval in minutes to poll for PBD changes 
# You must restart the managed application before changes to this property take effect.
#introscope.autoprobe.dynamicinstrument.pollIntervalMinutes=1 
    
# Some classloader implementations have been observed to return huge class files.This is to prevent memory errors.
# You must restart the managed application before changes to this property take effect. 
#introscope.autoprobe.dynamicinstrument.classFileSizeLimitInMegs=1 

# Re-defining too many classes at a time might be very CPU intensive. In cases where the changes in PBDs trigger 
# a re-definition of a large number of classes,this batches the process at a comfortable rate.
#introscope.autoprobe.dynamic.limitRedefinedClassesPerBatchTo=10


# Deep Inheritance Settings
# =========================
# This property enables deep inheritance instrumentation through PBD directives. If set to false, deep inheritance directives
# will behave as shallow inheritance, i.e. will not recognize inheritance relations beyond the immediate superclass or interfaces.
# The default value is true.
# You must restart the managed application before changes to this property take effect.
introscope.autoprobe.deepinheritance.enabled=true
  
  
# Multiple Inheritance Settings 
# ============================== 
# For directives based on interfaces or super classes the agent is unable to detect  
# multiple inheritance and hence those classes are not instrumented.Enable this feature to  
# determine those cases after the appserver or the agent process starts up.This feature logs the  
# classes which need to be instrumented but have not been and relies on dynamic instrumentation to affect the changes. 
  
  
# Enable/disable the hierarchy support instrumentation feature. 
# You must restart the managed application before changes to this property take effect.
#introscope.autoprobe.hierarchysupport.enabled=true 
  
# Since most cases have the applications already deployed, the behavior needs to run only once 
# to detect uninstrumented classes. Unless new applications are deployed after this behavior runs, 
# it need not be run again.Change this to true only if you need detection on a periodic basis. 
# The default value is true, i.e. it runs only once. 
# You must restart the managed application before changes to this property take effect.
#introscope.autoprobe.hierarchysupport.runOnceOnly=false 
  
# The polling interval to check for classes which could not be instrumented due to multiple inheritance. 
# Since in most cases this will happen only once, a conservative value is recommended to account for  
# app server initialization. 
# You must restart the managed application before changes to this property take effect.
#introscope.autoprobe.hierarchysupport.pollIntervalMinutes=5
  
# If you need the behavior to run a finite times instead of running it only once/ running it periodically always 
# use this property to specify the exact number of times it should run.Using this over-rides the run once only setting. 
# You must restart the managed application before changes to this property take effect.
#introscope.autoprobe.hierarchysupport.executionCount=3 
  
# Uncomment this if you dont need to log the classes being detected.This would make sense only  
# if dynamic instrumentation is enabled. 
# You must restart the managed application before changes to this property take effect.
#introscope.autoprobe.hierarchysupport.disableLogging=true 
  
# Uncomment this to only log the changes and disable the triggering of dynamic instrumentation. 
# You must restart the managed application before changes to this property take effect.
#introscope.autoprobe.hierarchysupport.disableDirectivesChange=true 
  
# Log4j Settings for this feature- these settings would create a log file called pbdupdate.log in  
# the current directory of the application. 
# You must restart the managed application before changes to this property take effect.
#log4j.additivity.IntroscopeAgent.inheritance=false 
#log4j.logger.IntroscopeAgent.inheritance=INFO,pbdlog 
  
#log4j.appender.pbdlog.File=pbdupdate.log 
#log4j.appender.pbdlog=com.wily.introscope.agent.AutoNamingRollingFileAppender 
#log4j.appender.pbdlog.layout=com.wily.org.apache.log4j.PatternLayout 
#log4j.appender.pbdlog.layout.ConversionPattern=%d{M/dd/yy hh:mm:ss a z} [%-3p] [%c] %m%n_ 



################################
# Agent Metric Aging
# ==============================
# Detects metrics that are not being updated consistently with new data and removes these metrics.
# By removing these metrics you can avoid metric explosion.    
# Metrics that are in a group will be removed only if all metrics under this group are considered candidates for removal.
# BlamePointTracer metrics are considered a group.  
#
# Enable/disable the metric agent aging feature. 
# Changes to this property take effect immediately and do not require the managed application to be restarted.
introscope.agent.metricAging.turnOn=true
#
# The time interval in seconds when metrics are checked for removal
# You must restart the managed application before changes to this property take effect.
introscope.agent.metricAging.heartbeatInterval=1800
#
# During each interval, the number of metrics that are checked for metric removal
# Changes to this property take effect immediately and do not require the managed application to be restarted.
introscope.agent.metricAging.dataChunk=500
#
# The metric becomes a candidate for removal when it reaches the number of intervals set (numberTimeslices) and has not invoked any new data points during that period.  
# If the metric does invoke a new data point during that period then the numberTimeslices resets and starts over.  
# Changes to this property take effect immediately and do not require the managed application to be restarted.
introscope.agent.metricAging.numberTimeslices=3000
#
# You can choose to ignore metrics from removal by adding the metric name or metric filter to the list below.  
# Changes to this property take effect immediately and do not require the managed application to be restarted.
introscope.agent.metricAging.metricExclude.ignore.0=Threads*

#########################################
# Servlet Header Decorator
# =======================================
# On/Off Switch
#
# ================
# If this Boolean vlaue is set to true, it configures the agent to add
# additional performance monitoring information to HTTP response
# headers.  ServletHeaderDecorator attaches the GUID to each transaction 
# and inserts the GUID into an HTTP header, x-wily-info
# This enables the correlation of transaction between Wily CEM and Wily Introscope
introscope.agent.decorator.enabled=false
#######################
# Security
#
# Determine the format of decorated HTTP response headers, which are sent to Wily CEM.
# clear - clear text encoding
# encrypted - header data is encrypted
# default is clear
#
introscope.agent.decorator.security=clear


#########################################
# ChangeDetector configuration properties
# =======================================
# On/Off Switch
#
# ================
# This boolean property gives you the ability to enable
# Introscope ChangeDetector by settings the property value
# to true. It is set to false by default.
# You must restart the managed application before changes to this property take effect.
#introscope.changeDetector.enable=false
#######################
# Root directory 
#
# ================
# The root directory is the folder where ChangeDetector creates its local cache files. 
# Use a backslash to escape the backslash character, as in the example.   
#introscope.changeDetector.rootDir=c:\\sw\\AppServer\\wily\\change_detector
#######################
# Startup wait time 
#
# ================
# Time to wait after agent starts before trying to connect to the Enterprise manager
#introscope.changeDetector.isengardStartupWaitTimeInSec=15
#######################
# Interval between connection attempts
#
# ================
# Specify the number of seconds to wait before retrying connection to the Enterprise manager
#introscope.changeDetector.waitTimeBetweenReconnectInSec=10
#######################
# Agent ID
#
# ================
# A string used by ChangeDetector to identify this agent
#introscope.changeDetector.agentID=SampleApplicationName
#
#######################
# Data source configuration file path 
#
# ================
# The absolute or relative path to the ChangeDetector datasources configuration file.
# Use a backslash to escape the backslash character.   
#introscope.changeDetector.profile=ChangeDetector-config.xml
#
#######################
# Data source configuration file directory
#
# ================
# The absolute or relative path to the datasource configuration file(s) directory.
# Use a backslash to escape the backslash character.
# All datasource configuration file(s) from this directory will be used in addition
# to any file specified by introscope.changeDetector.profile property.
#introscope.changeDetector.profileDir=changeDetector_profiles
#
#######################
# Data Compression 
#
# ================
# Enabling these properties will allow compression on the 
# Change Detector data buffer. This could be useful 
# if you experience memory consumption at start-up 
# Default property value is "false"
# You must restart the managed application before changes to this property take effect.
#
#introscope.changeDetector.compressEntries.enable=true
#
# The following property defines the batch size for the compression job
# You must restart the managed application before changes to this property take effect.
#introscope.changeDetector.compressEntries.batchSize=1000

#######################
# Application Map Transaction sampling 
#
# ================
# Transaction sampling currently affects only Application Map tracers.
# After agent start for the initial period all trnsactions are tracerd 
# (i.e. sampling rate of 1:1). Then for duration of same period the sampling 
# rate is gradually incresed until it reaches maxrate.
#
# Note that the periods are given in number of transactions detected rather
# than seconds.
#
# Changes to following properties take effect immediately and do not require the managed application to be restarted.
#
# Maximum sampling rate to use for sampling. By default 1 out 10 transactions are
# monitored. Set value to 0 to disable sampling and monitor all transactions.
#introscope.agent.tracer.sampling.maxrate=10
#
# Period for which all transaction is traced in number of transactions passed. 
# Default value is 100 
#introscope.agent.tracer.sampling.initial.period=100
#
# Period after which sampling counter is reset, and after which all transactions 
# are monitored again for number of transactions specified in initial period.
# Default value is 10000
#introscope.agent.tracer.sampling.reset.period=10000

#######################
# Application Map Agent Side
#
# ================
#Enable/disable tracking in the monitored code for Application Map
introscope.agent.appmap.enabled=true

#Enable/disable tracking of metrics for app map nodes. 
#Default value is false
#introscope.agent.appmap.metrics.enabled=true

#Enable/disable sending additional information for integration with catalyst
#default value is false
#introscope.agent.appmap.catalystIntegration.enabled=true

#Set the buffer size for app map data
#default value is 1000. Must be a positive integer. If the value is set to 0, the buffer is 
#unbounded. 
#introscope.agent.appmap.queue.size=1000

#Set the frequency in milliseconds for sending app map data to the EM.
#default value is 1000. Must be a positive integer. 
# If the value is set to 0, the default value is used.
#introscope.agent.appmap.queue.period=1000

#Enable/disable sending additional intermediate nodes between application frontend and 
# backend nodes.
#Default value is false.
# Change to this property takes effect immediately and do not require the managed application to be restarted.
#introscope.agent.appmap.intermediateNodes.enabled=true

######################
# Application Map and Socket
#
# ==================
# Enable/Disable sockets to appear in application map
# All the properties below need restart to the managed application 
# before changes to this property take effect.

#Enables Managed sockets to appear in Application map
#Default value true
introscope.agent.sockets.managed.reportToAppmap=true

#Enables Managed sockets to report Class level application edge to Application map
#Default value false
introscope.agent.sockets.managed.reportClassAppEdge=false


#Enables Managed sockets to report Method level application edge to Application map
#Default value true
introscope.agent.sockets.managed.reportMethodAppEdge=true


#Enables Managed sockets to report Class level Business Txn edge to Application map 
#Default value false 
introscope.agent.sockets.managed.reportClassBTEdge=false

#Enables Managed sockets to report Method level Business Txn edge to Application map 
#Default value true
introscope.agent.sockets.managed.reportMethodBTEdge=true

#######################
# Business Recording 
#
# ================
# Enable/disable business recording
# You must restart the managed application before changes to this property take effect.
introscope.agent.bizRecording.enabled=true
#
# Match POST parameter patterns before the servlets execute; THIS CAN POTENTIALLY BREAK USER APPLICATIONS
# Please consult a technical CA Wily representative or CA Support before enabling
#introscope.agent.bizdef.matchPost = before
# Never attempt to match POST parameters. This option is fastest, but may result in inaccurate business transaction component matching
#introscope.agent.bizdef.matchPost = never
# Match POST parameter patterns after servlet has executed. Cross process mapping and some metrics will not be available
introscope.agent.bizdef.matchPost = after

#######################
# SOA Extension For Tibco BW Properties
#
# =============================================

# Agent Metric Aging

#introscope.agent.metricAging.metricExclude.ignore.X+1=Tibco\|Processes\|*.*:Concurrent Invocations. Note: Here X is the existing sequence number for the introscope.agent.metricAging.metricExclude.ignore property of the IntroscopeAgent.profile file.

# Tibco BW Hawk Metrics Properties
#com.wily.soaextension.tibcobw.hawkmonitor.enabled=false  
#com.wily.soaextension.tibcobw.hawkmointor.frequency=30000

# Tibco BW Job Metrics Properties
#com.wily.soaextension.tibcobw.jobmonitor.enabled=false
#com.wily.soaextension.tibcobw.jobmointor.frequency=30000

# Tibco BW MBBS Feature Properties
#com.wily.soaextension.tibcobw.mbbs.enabled=true

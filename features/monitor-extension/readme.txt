
This module contains an example of how to add a custom monitor appender. The appender is a system component that writes monitor events to a file. The custom
monitor can be accessed by configuring a monitor destination in a composite or systemConfig:

     <f3:monitor name="ApplicationDestination">
         <appenders>
             <appender.component name="SampleFileAppender"/>
         </appenders>
     </f3:monitor>


Appender extensions can write monitor messages to custom output sinks such as a socket or messaging system.

To install, build this module and copy the jar to the <runtime install>/extensions directory before starting the Fabric3 server. The fabric3-samples-monitor
composite can be modified to use the monitor extension.

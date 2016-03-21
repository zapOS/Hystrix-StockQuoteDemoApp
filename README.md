# Hystrix-StockQuoteDemoApp

This is a simple app created to  experiment with Hystrix.  I first built a simple jsp page that prints stock quotes using
Yahoo finance REST API to fetch the same. If the call to Yahoo times out, I would like to switch to the Google Finance API 
which provides some less information.

The following links provide more insight into Hystrix:<br/>
https://github.com/Netflix/Hystrix/wiki/<br/>
https://ahus1.github.io/hystrix-examples/manual.html<br/>


The application can be run on jetty using :

`java  -Darchaius.configurationSource.additionalUrls=file:///c:/temp/config.properties -Darchaius.fixedDelayPollingScheduler.delayMills=1000 -Darchaius.fixedDelayPollingScheduler.initialDelayMills=1000 -Dhttp.proxyHost=GENPROXY.AMDOCS.COM -Dhttp.proxyPort=8080 -Dhttps.proxyHost=GENPROXY.AMDOCS.COM -Dhttps.proxyPort=8080 -jar start.jar jetty.http.port=8081
`




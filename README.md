Code from https://github.com/lzp4ever/IBM_WebSphere_MQ_Spring_Boot_JMS modified to demonstrate packaging of "MQ stuff" in separate module.
 
Config provided for a default installation of https://hub.docker.com/r/ibmcom/mq/ as per time of writing. Tests depend on a running container.

Depends on the IBM MQ classes for Java and JMS which must be downloaded and installed manually in you Maven repo, see http://www-01.ibm.com/support/docview.wss?uid=swg21683398.

This project installed the IBM classes using package `com.ibm`, artifact ID `mq.allclient`, version `1.0`. Change in `lib/pom.xml` if necessary if you have an existing installation.
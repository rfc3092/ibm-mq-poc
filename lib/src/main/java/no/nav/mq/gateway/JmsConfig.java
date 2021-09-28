package no.nav.mq.gateway;

import com.ibm.mq.jms.MQQueueConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.connection.JmsTransactionManager;
import org.springframework.jms.connection.UserCredentialsConnectionFactoryAdapter;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.transaction.PlatformTransactionManager;

import javax.jms.JMSException;

import static com.ibm.msg.client.wmq.common.CommonConstants.WMQ_CM_CLIENT;

@Configuration
public class JmsConfig {

    private static final Logger LOG = LoggerFactory.getLogger(JmsConfig.class);

    @Value("${ibm.mq.host}")
    private String host;

    @Value("${ibm.mq.port}")
    private int port;

    @Value("${ibm.mq.queue-manager}")
    private String queueManager;

    @Value("${ibm.mq.channel}")
    private String channel;

    @Value("${ibm.mq.username}")
    private String username;

    @Value("${ibm.mq.password}")
    private String password;

    @Value("${ibm.mq.queue}")
    private String queue;

    @Value("${ibm.mq.receive-timeout}")
    private long receiveTimeout;

    @Value("${ibm.mq.ccsid:1208}")
    private int ccsid;

    @Bean
    public MQQueueConnectionFactory mqQueueConnectionFactory()
            throws JMSException {

        MQQueueConnectionFactory factory = new MQQueueConnectionFactory();
        factory.setHostName(host);
        factory.setTransportType(WMQ_CM_CLIENT);
        factory.setCCSID(ccsid);
        factory.setChannel(channel);
        factory.setPort(port);
        factory.setQueueManager(queueManager);
        return factory;

    }

    @Bean
    UserCredentialsConnectionFactoryAdapter userCredentialsConnectionFactoryAdapter(MQQueueConnectionFactory factory) {

        UserCredentialsConnectionFactoryAdapter adapter = new UserCredentialsConnectionFactoryAdapter();
        adapter.setUsername(username);
        adapter.setPassword(password);
        adapter.setTargetConnectionFactory(factory);
        return adapter;

    }

    @Bean
    @Primary
    public CachingConnectionFactory cachingConnectionFactory(UserCredentialsConnectionFactoryAdapter adapter) {

        CachingConnectionFactory factory = new CachingConnectionFactory();
        factory.setTargetConnectionFactory(adapter);
        factory.setSessionCacheSize(500);
        factory.setReconnectOnException(true);
        return factory;

    }

    @Bean
    public PlatformTransactionManager jmsTransactionManager(CachingConnectionFactory factory) {

        JmsTransactionManager manager = new LoggingTransactionManager();
        manager.setConnectionFactory(factory);
        return manager;

    }

    @Bean
    public JmsTemplate jmsTemplate(CachingConnectionFactory factory) {

        JmsTemplate template = new JmsTemplate(factory);
        template.setDefaultDestinationName(queue);
        template.setReceiveTimeout(receiveTimeout);
        return purge(template);

    }

    /**
     * Hack to purge the queue when initializing the service, since we're running tests against an actual MQ instance
     * and we're putting all sorts of different objects onto the same queue. In a Real World application we wouldn't do
     * this of course, instead just beginning to consume existing messages.
     * <br/><br/>
     * Might be argued that we should do this in a @Before in each test class, but I'm lazy.
     */
    private static JmsTemplate purge(JmsTemplate template) {


        while (true) {
            Object o = template.receiveAndConvert();
            if (o == null) {
                break;
            }
            LOG.warn("Queue wasn't empty, but containted {}", o);
        }
        return template;

    }

}
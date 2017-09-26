package no.nav.mq.gateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.connection.JmsTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.DefaultTransactionStatus;

public class LoggingTransactionManager extends JmsTransactionManager {

    private static final Logger LOG = LoggerFactory.getLogger(LoggingTransactionManager.class);

    @Override
    protected void doBegin(Object transaction, TransactionDefinition definition) {
        LOG.info("doBegin");
        super.doBegin(transaction, definition);
    }

    @Override
    protected void doCommit(DefaultTransactionStatus status) {
        LOG.info("doCommit");
        super.doCommit(status);
    }

    @Override
    protected void doRollback(DefaultTransactionStatus status) {
        LOG.info("doRollback");
        super.doRollback(status);
    }

}

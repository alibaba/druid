package com.alibaba.druid.pool.xa;


/**
 * A listener for transaction completion events.
 * 
 * @author Dain Sundstrom
 * @version $Revision$
 */
public interface TransactionContextListener {

    /**
     * Occurs after the transaction commits or rolls back.
     * 
     * @param transactionContext the transaction context that completed
     * @param commited true if the transaction committed; false otherwise
     */
    void afterCompletion(TransactionContext transactionContext, boolean commited);
}

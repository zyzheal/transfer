package com.lagou.edu.utils;

import com.lagou.edu.annotation.Autowired;
import com.lagou.edu.annotation.Transactional;

import java.sql.SQLException;

/**
 * 事务管理类，
 * @Description TODO
 **/
@Transactional("transactionManager")
public class TransactionManager {
    @Autowired
    private ConnectionUtils connectionUtils;

    public void setConnectionUtils(ConnectionUtils connectionUtils) {
        this.connectionUtils = connectionUtils;
    }

    /**
     * 开启事务控制
     * @throws SQLException
     */
    public void beginTransaction() throws SQLException {
        connectionUtils.getCurrentThreadCon().setAutoCommit(false);
    }
    //提交事务
    public void commit() throws SQLException {
        connectionUtils.getCurrentThreadCon().commit();
    }
    //回滚事务
    public void rollback() throws SQLException {
        connectionUtils.getCurrentThreadCon().rollback();
    }
}

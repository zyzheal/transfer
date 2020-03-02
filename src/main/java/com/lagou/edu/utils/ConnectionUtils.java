package com.lagou.edu.utils;

import com.lagou.edu.annotation.Autowired;
import com.lagou.edu.annotation.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @Description TODO
 **/
@Component("connectionUtils")
public class ConnectionUtils {

    private ThreadLocal<Connection> threadLocal =new ThreadLocal<>();

    /**
     * 从当前线程获取连接
     * @return
     */
    public Connection getCurrentThreadCon() throws SQLException {
        Connection connection = threadLocal.get();
        /**
         * 判断当前线程中是否已经绑定连接，如果没有绑定，需要从连接池获取一个连接绑定到当前线程
         */
        if(connection ==null){
            // 从连接池拿连接并绑定到线程
            connection = DruidUtils.getInstance().getConnection();
            // 绑定到当前线程
            threadLocal.set(connection);
        }
        return connection;
    }

}

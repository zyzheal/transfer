package com.lagou.edu.factory;

import com.lagou.edu.annotation.Autowired;
import com.lagou.edu.annotation.Component;
import com.lagou.edu.annotation.Transactional;
import com.lagou.edu.service.TransferService;
import com.lagou.edu.utils.TransactionManager;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * 代理工厂
 */
@Component("proxyFactory")
public class ProxyFactory {
    public ProxyFactory(){}
    @Autowired
    private TransactionManager transactionManager;

    public void setTransactionManager(TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    public Object getJdkProxy(Object obj){
        //获取代理对象
        return Proxy.newProxyInstance(ProxyFactory.class.getClassLoader(),
                obj.getClass().getInterfaces(), new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        Object result = null;
                        try{
                            //开启事务
                            transactionManager.beginTransaction();
                            result = method.invoke(obj,args);
                            //提交事务
                            transactionManager.commit();

                        }catch (Exception e){
                            //回滚事务
                            transactionManager.rollback();
                            throw e;
                        }
                        return result;
                    }
                });
    }

    /**
     * 动态代理
     * @param obj
     * @return
     */
    public Object getCGlibProxy(Object obj){
        return Enhancer.create(obj.getClass(), new MethodInterceptor() {
            @Override
            public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
                Object result = null;
                try{
                    //开启事务
                    transactionManager.beginTransaction();
                    result = method.invoke(obj,objects);
                    //提交事务
                    transactionManager.commit();

                }catch (Exception e){
                    //回滚事务
                    transactionManager.rollback();
                    throw e;
                }
                return result;
            }
        });

    }
}

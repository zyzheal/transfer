package com.lagou.edu.factory;

import com.alibaba.druid.util.StringUtils;
import com.lagou.edu.annotation.*;
import com.lagou.edu.utils.ClassUtil;

import java.io.File;
import java.io.FileFilter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description TODO
 **/
public class BeanFactory {
    //存储单例对象
    private static Map<String,Object> singletonObjects = new ConcurrentHashMap<>();
    private static final String PROXYFACTORY1 = "com.lagou.edu.factory.ProxyFactory$1";
    private static final String PROXYFACTORY2 = "com.lagou.edu.factory.ProxyFactory$2";

    static{
        try {
            doScan("com.lagou.edu");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 扫描指定包,找到包中的类文件。
     * 对于标准(类上有定义注解的)类文件反射加载创建类定义对象并放入容器中
     */
    private static void doScan(final String pkg) throws Exception {
        List<Class<?>> list = ClassUtil.getAllClassByPackageName("com.lagou.edu");
        if(list!=null && list.size()>0){
            for (Class<?> cls : list) {
                if(cls.isInterface())
                    continue;
                if(cls.getName().equals(PROXYFACTORY1) ||
                    PROXYFACTORY2.equals(cls.getName())){
                    continue;
                }
                if(cls.isAnnotationPresent(Service.class)){
                    //拿到beanId
                    String beanId = cls.getAnnotation(Service.class).value();
                    Object instance = cls.newInstance();
                    singletonObjects.put(beanId,instance);
                }
                if(cls.isAnnotationPresent(Repository.class)){
                    //拿到beanId
                    String beanId = cls.getAnnotation(Repository.class).value();
                    Object instance = cls.newInstance();
                    singletonObjects.put(beanId,instance);
                }
                if(cls.isAnnotationPresent(Component.class)){
                    //拿到beanId
                    String beanId = cls.getAnnotation(Component.class).value();
                    Object instance = cls.newInstance();
                    singletonObjects.put(beanId,instance);
                }
                if(cls.isAnnotationPresent(Transactional.class)){
                    //拿到beanId
                    String beanId = cls.getAnnotation(Transactional.class).value();
                    Object instance = cls.newInstance();
                    singletonObjects.put(beanId,instance);
                }
            }

            for (Class<?> aClass : list) {
                setDependencyInject(aClass);
            }
        }
    }

    /**
     * 依赖注入
     */
    private static void setDependencyInject(Class<?> clazz) throws Exception{
        //获取类中所有的成员属性
        Field[] fields = clazz.getDeclaredFields();
        //遍历所有属性
        for (Field field : fields) {
            //如果此属性有Autowired注解修饰，对其进行操作
            if(field.isAnnotationPresent(Autowired.class)){
                //获取属性名
                String beanId = field.getName();
                boolean autowired = field.getAnnotation(Autowired.class).value();
                //如果autowired为true 则进行依赖注入
                if(autowired){
                    String fieldName = field.getName();
                    //将属性名改为以大写字母开头，如：id改为ID，name改为Name
                    fieldName = String.valueOf(fieldName.charAt(0)).toUpperCase()+fieldName.substring(1);
                    //set方法名称，如：setId,setName...
                    String setterName = "set" + fieldName;
                    //根据方法名称和参数类型获取对应的set方法对象
                    Method method = clazz.getDeclaredMethod(setterName, field.getType());
                    String value="";
                    Annotation annotation = clazz.getAnnotations()[0];
                    if(clazz.isAnnotationPresent(Service.class)){
                        value = clazz.getAnnotation(Service.class).value();
                    }
                    if(clazz.isAnnotationPresent(Component.class)){
                        value = clazz.getAnnotation(Component.class).value();
                    }
                    if(clazz.isAnnotationPresent(Repository.class)){
                        value = clazz.getAnnotation(Repository.class).value();
                    }
                    if(clazz.isAnnotationPresent(Transactional.class)){
                        value = clazz.getAnnotation(Transactional.class).value();
                    }
                    if(StringUtils.isEmpty(value))
                        continue;
                    method.invoke(singletonObjects.get(value),singletonObjects.get(beanId));
                }
            }
        }
    }

    /**
     * 根据传入的bean的id值获取容器中的对象，类型为Object
     */
    public static Object getBean(String beanId){
        return singletonObjects.get(beanId);
    }

    public static void clear(){
        singletonObjects.clear();
    }

}

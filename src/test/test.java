package test;

import java.lang.reflect.InvocationTargetException;

public class test {

    public static void main(String[] args) throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        String[] locations = {"bean.xml"};
        ApplicationContext ctx = 
		    new ClassPathXmlApplicationContext(locations);
        boss boss = (boss) ctx.getBean("boss");
       
        System.out.println(boss.tostring());
    }
}
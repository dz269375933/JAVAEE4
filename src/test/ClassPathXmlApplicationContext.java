package test;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.tools.JavaCompiler;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import javax.tools.JavaCompiler.CompilationTask;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.sun.java_cup.internal.runtime.Symbol;

public class ClassPathXmlApplicationContext extends ApplicationContext {
	
	public ClassPathXmlApplicationContext(String[] locations) throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		SAXReader reader = new SAXReader();
		  File file = new File("src/"+locations[0]);
		  Document document = null;
		try {
			document = reader.read(file);
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		  Element root = document.getRootElement();
		  List<Element> childElements = root.elements();
		  
		  for (Element child : childElements) {  
			   List<Element> elementList = child.elements();
			   if(child.attributeValue("id")!=null){
				   classNames.add(child.attributeValue("class"));
				  
				   String beanName=child.attributeValue("id");
				   for (Element ele : elementList) {
					   String name=ele.attributeValue("name");
					   String value=ele.attributeValue("value");
					 // System.out.println(name+":"+value);
					   if(value!=null)
					   { 
							this.putMap(beanName, install(beanName,name,value));							
					   }			    	
				   }				   
			   }
			   
		  }
		  
		  //注解实现
		  car car=null;
			 office office=null;
		  for(String className:classNames){
			  
			 // System.out.println(className);
			  Class<?> cl=Class.forName(className);
			  
			  for (Constructor<?> con : cl.getConstructors()) {
				  if (con.isAnnotationPresent(Autowired.class)) {
					  //List<String> names=new ArrayList<String>();
					  Class [] parameterTypes = con.getParameterTypes();
					 for(Class c:parameterTypes){
						 //将属性依次实例化
						 if(beans.get(c.getName())==null){
							 
							 this.putMap(c.getName(), install(c.getName(), null, null));
						 }
						//names.add(c.getName());
						//System.out.println(c.getName());
						 //实例化结束
					 }
					car=(car) beans.get("car");
					office=(office) beans.get("office");
					/* Object[] objects=new Object[names.size()];
					 for(int i=0;i<names.size();i++){
						 objects[i]=names.get(i);
						//System.out.println(objects[i]);
					 }*/
					
					 Object obj=con.newInstance(car,office);
					 
					 this.putMap(className, obj);
					
					} 
				}
			 
			  
			  
		  }
		  
		  
		  
	
	}
    
	
	private Object install(String classname,String valueName,String value) throws NoSuchMethodException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		/*System.out.println(classname);
		Object obj=null;
		JavaCompiler complier = ToolProvider.getSystemJavaCompiler();     
		//System.out.println(complier);
        StandardJavaFileManager sjf =   
                complier.getStandardFileManager(null, null, null);  
        Iterable it = sjf.getJavaFileObjects(classname+".java");
        CompilationTask task = complier.getTask(null, sjf, null, null, null, it);  
        task.call();  //调用创建  ,创建class文件
        sjf.close();  
           
        URL urls[] = new URL[]{ new URL("file:"+System.getProperty("user.dir")+"\\src\\test\\")}; //储存文件目录的地址
        System.out.println("file:"+System.getProperty("user.dir")+"\\src\\test");
        URLClassLoader uLoad = new URLClassLoader(urls);  //classloader从哪个目录找？ 
        //uLoad.loadClass("Hello");
        Class c = uLoad.loadClass(classname);  //找哪个class文件 注意不带后缀名  
        obj=c.newInstance();  //创建一个实例  
        if(!valueName.isEmpty()){
        	String m="set"+ valueName.substring(0, 1).toUpperCase() + valueName.substring(1);
        	
            try {
            	Method setBrand = c.getMethod(m,String.class); 
				setBrand.invoke(obj,value);
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
        }
        */
		
		//通过类加载器获取Car类对象 
		JavaCompiler javaCompiler = ToolProvider.getSystemJavaCompiler();  
		if(classname.startsWith("test.")){
			classname=classname.substring("test.".length());
		}
        int result = javaCompiler.run(null, null, null, "-d","./src/","./src/test/"+classname+".java");  
       // System.out.println( result == 0 ? "恭喜编译成功" : "对不起编译失败");  
	       ClassLoader loader = Thread.currentThread().getContextClassLoader(); 
	       Class clazz;
	       Object obj=null;
		/*if(classname.startsWith("office")){
			System.out.println("this is office");
			System.out.println(classname);
			clazz = loader.loadClass("test."+classname);
		}else{
			
			System.out.println("this is not office");
			System.out.println(classname);
			clazz =loader.loadClass(classname);
		}*/
		
	       clazz = loader.loadClass("test."+classname);
				//System.out.println(clazz);
				//获取类的默认构造器对象并通过它实例化Car 
			       Constructor cons = clazz.getDeclaredConstructor((Class[])null); 
			      obj = cons.newInstance();
			      
			       if(valueName!=null && value!=null){
			    	   String m="set"+ valueName.substring(0, 1).toUpperCase() + valueName.substring(1);
			    	   //通过反射方法设置属性 
				       Method setBrand = clazz.getMethod(m,String.class); 
				       setBrand.invoke(obj,value); 
			       }
			       
			
        return obj;
	}
	
	private void putMap(String name,Object obj){
		
		if(name.startsWith("test.")){
			name=name.substring("test.".length());
		}
		
		if(beans.get(name)==null){
			beans.put(name, obj);
			
		}
	}
	public Object getBean(String string) {
		
		return beans.get(string);
	}
}

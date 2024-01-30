import com.esotericsoftware.reflectasm.MethodAccess;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Test2 {

    public static void main(String[] args) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        long now;
        Test1 t = new Test1();
       long sum=0;
        now = System.currentTimeMillis();
        for(int i = 0; i<500000000; ++i){
            t.setNum(i);
        }
        System.out.println("get-set耗时"+(System.currentTimeMillis() - now) + "ms秒，和是" +sum);

        now = System.currentTimeMillis();

        for(int i = 0; i<5000000; ++i){
            Class<?> c = Class.forName("Test1");
            Class<?>[] argsType = new Class[1];
            argsType[0] = int.class;
            Method m = c.getMethod("setNum", argsType);
            m.invoke(t, i);
        }
        System.out.println("标准反射耗时"+(System.currentTimeMillis() - now) + "ms，和是" +sum);
        now = System.currentTimeMillis();
        Class<?> c = Class.forName("Test1");
        Class<?>[] argsType = new Class[1];
        argsType[0] = int.class;
        Method m = c.getMethod("setNum", argsType);
        System.out.println("缓存 初始化"+(System.currentTimeMillis() - now) + "ms，和是" );
        now = System.currentTimeMillis();

        for(int i = 0; i<500000000; ++i){
            m.invoke(t, i);
        }
        System.out.println("缓存反射耗时"+(System.currentTimeMillis() - now) + "ms，和是" );
        now = System.currentTimeMillis();
        MethodAccess ma = MethodAccess.get(Test1.class);
        System.out.println("reflectasm初始化 "+(System.currentTimeMillis() - now) + "ms，和是" );
        int index = ma.getIndex("setNum");

        System.out.println("reflectasm初始化 "+(System.currentTimeMillis() - now) + "ms，和是" );

        now = System.currentTimeMillis();
        for(int i = 0; i<500000000; ++i){
            ma.invoke(t, index, i);
        }
        System.out.println("reflectasm反射耗时"+(System.currentTimeMillis() - now) + "ms，和是" );
        now = System.currentTimeMillis();
        for(int i = 0; i<500000000; ++i){
            ma.invoke(t, "setNum", i);
        }
        System.out.println("reflectasm反射耗时"+(System.currentTimeMillis() - now) + "ms，和是" );
        //InputStream inputStream = .getClassLoader().getResourceAsStream("test.xml");

    }
}
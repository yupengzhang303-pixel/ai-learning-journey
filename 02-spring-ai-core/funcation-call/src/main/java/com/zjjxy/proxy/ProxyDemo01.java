package com.zjjxy.proxy;

import com.zjjxy.proxy.impl.ChatServiceImpl;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class ProxyDemo01 {

    public static void main(String[] args) {
        // 真实对象
        ChatServiceImpl target = new ChatServiceImpl();

        // 用 Proxy 在运行时生成接口的实现（代理对象）
        ChatService proxy = (ChatService) Proxy.newProxyInstance(
                // 类加载器
                ChatService.class.getClassLoader(),
                // 要实现的接口
                new Class[]{ChatService.class},
                // 拦截器：方法被调用时，先经过这里
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] methodArgs) throws Throwable {
                        // 调用前增强
                        System.out.println("【动态代理】调用前，方法：" + method.getName()
                                + "，参数：" + methodArgs[0]);

                        // 真正调用目标方法
                        Object result = method.invoke(target, methodArgs);

                        // 调用后增强
                        System.out.println("【动态代理】调用后，结果：" + result);

                        return result;
                    }
                }
        );

        // 调用接口方法（实际走的是拦截器）
        System.out.println("最终返回: " + proxy.chat("今天杭州的天气如何？"));
    }
}
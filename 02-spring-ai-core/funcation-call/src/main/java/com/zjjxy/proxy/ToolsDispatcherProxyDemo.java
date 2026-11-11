package com.zjjxy.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.time.LocalDateTime;

public class ToolsDispatcherProxyDemo {

    // 工具接口
    public interface WeatherTools {
        String getWeather(String city);
        String getCurrentTimes();
    }

    public static void main(String[] args) {
        // 用动态代理"凭空"造一个 WeatherTools 实现类
        WeatherTools tools = (WeatherTools) Proxy.newProxyInstance(
                WeatherTools.class.getClassLoader(),
                new Class[]{WeatherTools.class},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] methodArgs) throws Throwable {
                        // 根据方法名分发（模拟 Function Call 的工具分发）
                        String methodName = method.getName();
                        System.out.println("【工具分发】调用: " + methodName);
                        return switch (methodName) {
                            case "getWeather" ->
                                    "杭州今天晴，25℃（城市=" + methodArgs[0] + "）";
                            case "getCurrentTimes" ->
                                    LocalDateTime.now().toString();
                            default -> throw new IllegalStateException("未知的工具: " + methodName);
                        };
                    }
                }
        );

        // 使用代理对象执行
        System.out.println(tools.getWeather("杭州"));
        System.out.println(tools.getCurrentTimes());
    }
}
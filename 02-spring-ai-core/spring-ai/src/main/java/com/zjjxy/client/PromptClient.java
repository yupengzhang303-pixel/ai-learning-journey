package com.zjjxy.client;
import com.zjjxy.prompt.PromptTemplates;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
/**
 * 客户端类：给大模型发送消息

 */
@Component
public class PromptClient {
    @Autowired
    private LlmClient llmClient;
    //使用Prompt模板
    public String promptChat(){
        String prompt =  PromptTemplates.CUSTOMER_SERVICE
                .replace("{knowledge}", "入职少于一年，有5天年假；超过1年，有7天年假；")
                .replace("{question}","我的年假是多少天");
        String answer = llmClient.chatOnce(prompt);
        System.out.println("客服回答："+answer);
        return answer;
    }
}

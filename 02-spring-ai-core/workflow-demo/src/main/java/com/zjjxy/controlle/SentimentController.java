package com.zjjxy.controlle;

import com.zjjxy.service.AgentWorkFlowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/agent")
@CrossOrigin(origins = "*")
public class SentimentController {

    @Autowired
    private AgentWorkFlowService agentWorkFlowService;

    @GetMapping("/sentiment")
    public String sentiment(@RequestParam String content) {
        return agentWorkFlowService.process(content);
    }
}
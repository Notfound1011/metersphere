package io.metersphere.controller;

import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class KafkaController {

    KafkaListenerEndpointRegistry registry;
    public static final String CONSUME_ID = "load-test-data";
    /**
     * 开启监听
     */
    @GetMapping("/start")
    public void start() {
        // 判断监听容器是否启动，未启动则将其启动
        if (!registry.getListenerContainer(CONSUME_ID).isRunning()) {
            registry.getListenerContainer(CONSUME_ID).start();
        }
        // 将其恢复
        registry.getListenerContainer(CONSUME_ID).resume();
    }

    /**
     * 关闭监听
     */
    @GetMapping("/stop")
    public void stop() {
        // 暂停监听
        registry.getListenerContainer(CONSUME_ID).pause();
    }
}

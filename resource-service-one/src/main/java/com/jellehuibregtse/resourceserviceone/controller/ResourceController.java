package com.jellehuibregtse.resourceserviceone.controller;

import com.google.gson.Gson;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.env.Environment;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.MessageFormat;
import java.util.Map;

@Log4j2
@RestController
@RequestMapping("/resource")
public class ResourceController {

    private final Environment environment;

    public ResourceController(final Environment environment) {
        this.environment = environment;
    }

    @GetMapping("/status/check")
    public String status() {
        var port = environment.getProperty("local.server.port");
        log.info("Resource status check called on port {}", port);

        return MessageFormat.format("Resource service one is working on port {0}!", port);
    }

    @MessageMapping("/message")
    @SendTo("/topic/reply")
    public String processMessageFromClient(@Payload String message){
        String name = new Gson().fromJson(message, Map.class).get("name").toString();
        return "Your message was: " + message;
    }

    @MessageExceptionHandler
    @SendToUser("/topic/errors")
    public String handleException(Throwable exception) {
        return exception.getMessage();
    }
}

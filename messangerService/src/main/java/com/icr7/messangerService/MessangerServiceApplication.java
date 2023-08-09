package com.icr7.messangerService;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@SpringBootApplication
public class MessangerServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MessangerServiceApplication.class, args);
    }

    @Bean
    public NewTopic icr7Topic() {
        return TopicBuilder.name("icr7-topic").build();
    }

    @Bean
    public NewTopic sureshTopic() {
        return TopicBuilder.name("suresh-topic").build();
    }


}

//@Configuration
//class CorsConfig implements WebMvcConfigurer {
//
//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping("/**")
//                .allowedMethods("*");
//    }
//}

@Service
class KafkaProducer {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void sendMessage(String message) {

        kafkaTemplate.send("icr7-topic", message);
    }
}

@RestController
@RequestMapping("/app1")
class KafkaProducerController {

    @Autowired
    private KafkaProducer kafkaProducer;

    @GetMapping("/send/{message}")
    public String send(@PathVariable("message") String message) {
        System.out.println("sending : " + message);
        kafkaProducer.sendMessage(message);
        return "message sent successfully";
    }
}



@Configuration
@EnableWebSocketMessageBroker
class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/websocket").setAllowedOrigins("http://localhost:63342").withSockJS();

    }
}


@Service
class KafKaConsumer {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @KafkaListener(topics = "suresh-topic", groupId = "group-id")
    public void consumeMessage(String message) {
        System.out.println("received message: " + message);
        messagingTemplate.convertAndSend("/topic/messages", message);
    }
}


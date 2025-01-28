package com.uni.soa.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

  @Bean
  public TopicExchange postExchange() {
    return new TopicExchange("post-exchange");
  }

  @Bean
  public Queue postNotificationQueue() {
    return new Queue("post-notifications-queue", true);
  }

  @Bean
  public Binding postNotificationBinding(
          TopicExchange postExchange,
          Queue postNotificationQueue
  ) {
    return BindingBuilder
            .bind(postNotificationQueue)
            .to(postExchange)
            .with("post.notification");
  }

  @Bean
  public Queue postLikeNotificationQueue() {
    return new Queue("post-like-notifications-queue", true);
  }

  @Bean
  public Binding postLikeNotificationBinding(
          TopicExchange postExchange,
          Queue postLikeNotificationQueue
  ) {
    // Bind this new queue to the same exchange ("post-exchange"),
    // but with the routing key "post.like.notification"
    return BindingBuilder
            .bind(postLikeNotificationQueue)
            .to(postExchange)
            .with("post.like.notification");
  }


  // 1) Provide a Jackson2JsonMessageConverter bean
  @Bean
  public MessageConverter jackson2JsonMessageConverter() {
    return new Jackson2JsonMessageConverter();
  }

  // 2) Let RabbitTemplate use Jackson2Json for message conversion
  @Bean
  public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                       MessageConverter messageConverter) {
    RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
    rabbitTemplate.setMessageConverter(messageConverter);
    return rabbitTemplate;
  }
}

package com.wrathur.common.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
    // 课程统计交换机、队列、路由键
    public static final String COURSE_EXCHANGE = "statistic.course.exchange";
    public static final String COURSE_QUEUE = "statistic.course.queue";
    public static final String COURSE_ROUTING_KEY = "statistic.course.routing.key";

    // 教学统计交换机、队列、路由键
    public static final String INSTRUCTION_EXCHANGE = "statistic.instruction.exchange";
    public static final String INSTRUCTION_QUEUE = "statistic.instruction.queue";
    public static final String INSTRUCTION_ROUTING_KEY = "statistic.instruction.routing.key";

    @Bean
    public TopicExchange courseExchange() {
        return new TopicExchange(COURSE_EXCHANGE);
    }

    @Bean
    public Queue courseQueue() {
        return new Queue(COURSE_QUEUE);
    }

    @Bean
    public Binding courseBinding() {
        return BindingBuilder.bind(courseQueue())
                .to(courseExchange())
                .with(COURSE_ROUTING_KEY);
    }

    @Bean
    public TopicExchange instructionExchange() {
        return new TopicExchange(INSTRUCTION_EXCHANGE);
    }

    @Bean
    public Queue instructionQueue() {
        return new Queue(INSTRUCTION_QUEUE);
    }

    @Bean
    public Binding instructionBinding() {
        return BindingBuilder.bind(instructionQueue())
                .to(instructionExchange())
                .with(INSTRUCTION_ROUTING_KEY);
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(new Jackson2JsonMessageConverter());
        return factory;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
        return rabbitTemplate;
    }
}
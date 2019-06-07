package com.imocc.test.rabbitmq;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by kqyang on 2019/6/7.
 */
public class Producer03_routing {
    private static final String QUEUE_INFORM_EMAIL = "queue_inform_email";
    private static final String QUEUE_INFORM_SMS = "queue_inform_sms";
    private static final String EXCHANGE_ROUTING_INFORM = "exchange_routing_inform";
    private static final String ROUTINGKEY_EMAIL = "routingkey_email";
    private static final String ROUTINGKEY_SMS = "routingkey_sms";

    public static void main(String[] args) {
        // 通过连接工厂创建新的连接
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("127.0.0.1");
        factory.setPort(5672);
        factory.setUsername("guest");
        factory.setPassword("guest");
        // 设置虚拟机 一个mq服务可以设置多个虚拟机 每个虚拟机相当于一个独立的mq
        factory.setVirtualHost("/");
        // 建立连接
        Connection connection = null;
        Channel channel = null;
        try {
            // 创建会话通道，生产者和mq服务的所有通信都在channel通道中完成
            connection = factory.newConnection();
            channel = connection.createChannel();
            // 声明队列 如果队列在mq中没有则要创建
            /**
             * String queue:队列名称
             * boolean durable:是否持久化 如果持久化，mq重启后队列还在
             * boolean exclusive:是否独占 如果connect连接关闭队列则自动删除，如果设置成true可用于临时队列的创建
             * boolean autoDelete:自动删除 队列不再使用时是否自动删除此队列，如果设置成true就可以实现临时队列（队列不用了就自动删除）
             * Map<String, Object> arguments:参数 可以设置一个队列的扩展参数，比如可以设置存活时间
             */
            // 参数：String queue:队列名称, boolean durable:是否持久化, boolean exclusive, boolean autoDelete, Map<String, Object> arguments
            channel.queueDeclare(QUEUE_INFORM_EMAIL, true, false, false, null);
            channel.queueDeclare(QUEUE_INFORM_SMS, true, false, false, null);
            // 声明交换机
            /**
             * String exchange 交换机名称
             * String type 交换机的类型
             *
             * type-
             * fanout:对应rabbitmq的工作环境是 publish/subscribe
             * direct:对应rabbitmq的工作环境是 routing
             * topic:对应rabbitmq的工作环境是 topics
             * headers:对应rabbitmq的工作环境是 headers
             */
            channel.exchangeDeclare(EXCHANGE_ROUTING_INFORM, BuiltinExchangeType.DIRECT);
            // 进行交换机和队列进行绑定
            /**
             * String queue
             * String exchange
             * String routingKey 路由key，发布订阅模式设置为空字符串(作用：交换机根据路由key的值将消息转发到制定队列中)
             */
            channel.queueBind(QUEUE_INFORM_EMAIL, EXCHANGE_ROUTING_INFORM, ROUTINGKEY_EMAIL);
            channel.queueBind(QUEUE_INFORM_EMAIL, EXCHANGE_ROUTING_INFORM, ROUTINGKEY_SMS);
            channel.queueBind(QUEUE_INFORM_SMS, EXCHANGE_ROUTING_INFORM, ROUTINGKEY_SMS);
            // 发送消息
            /**
             * String exchange:交换机 不指定的话会使用默认交换机
             * String routingKey:路由key 交换机根基路由key来将消息转发到指定的队列，如果使用默认交换机，routingKey设置为队列的名称
             * BasicProperties props:消息的属性
             * byte[] body:消息内容
             */
            /*for (int i = 0; i < 5; i++) {
                String message = "send inform message to user";
                // 参数：String exchange, String routingKey, boolean mandatory, boolean immediate, BasicProperties props, byte[] body
                channel.basicPublish(EXCHANGE_ROUTING_INFORM, ROUTINGKEY_EMAIL, null, message.getBytes());
                System.out.println("发送消息：" + message);
            }
            for (int i = 0; i < 5; i++) {
                String message = "send inform message to user";
                // 参数：String exchange, String routingKey, boolean mandatory, boolean immediate, BasicProperties props, byte[] body
                channel.basicPublish(EXCHANGE_ROUTING_INFORM, ROUTINGKEY_SMS, null, message.getBytes());
                System.out.println("发送消息：" + message);
            }*/
            for (int i = 0; i < 5; i++) {
                String message = "send inform message to user";
                // 参数：String exchange, String routingKey, boolean mandatory, boolean immediate, BasicProperties props, byte[] body
                channel.basicPublish(EXCHANGE_ROUTING_INFORM, ROUTINGKEY_SMS, null, message.getBytes());
                System.out.println("发送消息：" + message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                channel.close();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
            }
            try {
                connection.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }
}

package pers.project.blog.constant;

/**
 * RabbitMQ 常量
 *
 * @author Luo Fei
 * @date 2023/1/15
 */
public abstract class RabbitConstant {

    /**
     * maxwell交换机
     */
    public static final String MAXWELL_EXCHANGE = "maxwell_exchange";

    /**
     * maxwell队列
     */
    public static final String MAXWELL_QUEUE = "maxwell_queue";

    /**
     * email交换机
     */
    public static final String EMAIL_EXCHANGE = "email_exchange";

    /**
     * 邮件队列
     */
    public static final String EMAIL_QUEUE = "email_queue";

}

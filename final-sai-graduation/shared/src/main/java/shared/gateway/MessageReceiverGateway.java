package shared.gateway;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

public class MessageReceiverGateway {

    private Connection connection; // to connect to the JMS
    private Session session; // session for creating consumers
    private Destination receiveDestination; //reference to a queue/topic destination
    private MessageConsumer consumer; // for receiving messages

    public MessageReceiverGateway(String queue)
    {
        try {
            ConnectionFactory connectionFactory;
            connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");

            connection = connectionFactory.createConnection();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            // connect to the receiver destination queue
            receiveDestination = session.createQueue(queue);
            consumer = session.createConsumer(receiveDestination);
        }
        catch (JMSException e) {
            e.printStackTrace();
        }
    }

    public void setListener(MessageListener ml)
    {
        try {
            consumer.setMessageListener(ml);
            connection.start(); // this is needed to start receiving messages

        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    public void closeConnection()
    {
        try {
            if (consumer != null) {
                consumer.close();
            }

            if (session != null) {
                session.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
        catch (JMSException e) {
            e.printStackTrace();
        }
    }

    //added to get the receiver
    public Destination getReceiver()
    {
        return receiveDestination;
    }
}

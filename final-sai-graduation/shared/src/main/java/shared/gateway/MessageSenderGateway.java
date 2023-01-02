package shared.gateway;

import com.google.gson.Gson;
import org.apache.activemq.ActiveMQConnectionFactory;
import shared.model.approval.ApprovalReply;
import shared.model.approval.ApprovalRequest;
import shared.model.client.GraduationReply;
import shared.model.client.GraduationRequest;

import javax.jms.*;

public class MessageSenderGateway {

    private Connection connection; // to connect to the ActiveMQ
    private Session session; // session for creating messages
    private Destination sendDestination; // reference to a queue/topic destination
    private MessageProducer producer; // for sending messages

    public MessageSenderGateway(String queue)
    {
        try {
            ConnectionFactory connectionFactory;
            connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");
            connection = connectionFactory.createConnection();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            // connect to the sender destination queue
            sendDestination = session.createQueue(queue);
            producer = session.createProducer(sendDestination);
        } catch (JMSException e) {
            e.printStackTrace();
        }

    }

    // if queue name is not set use for multiple gateways
    public MessageSenderGateway() {
        try {
            ConnectionFactory connectionFactory;
            connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");
            connection = connectionFactory.createConnection();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            // connect to the sender destination queue
            //sendDestination = session.createQueue(queue);
            producer = session.createProducer(null);
        } catch (JMSException e) {
            e.printStackTrace();
        }

    }

    public Message createTextMessage(GraduationRequest graduationRequest)
    {
        Message msg = null;
        //Create JSON from graduationRequest
        //Create message from json
        try {
            Gson gson = new Gson();
            String json = gson.toJson(graduationRequest);
            msg = session.createTextMessage(json);

        }
        catch (JMSException e) {
            e.printStackTrace();
        }
        return msg;
    }

    public Message createTextMessage(ApprovalRequest approvalRequest)
    {
        Message msg = null;
        //Create JSON from approvalRequest
        //Create message from json
        try {
            Gson gson = new Gson();
            String json = gson.toJson(approvalRequest);
            msg = session.createTextMessage(json);

        }
        catch (JMSException e) {
            e.printStackTrace();
        }
        return msg;
    }

    public Message createTextMessage(ApprovalReply approvalReply)
    {
        Message msg = null;
        //Create JSON from loanRequest
        //Create message from json
        try {
            Gson gson = new Gson();
            String json = gson.toJson(approvalReply);
            msg = session.createTextMessage(json);
        }
        catch (JMSException e) {
            e.printStackTrace();
        }
        return msg;
    }

    public Message createTextMessage(GraduationReply graduationReply)
    {
        Message msg = null;
        //Create JSON from graduationReply
        //Create message from json
        try {
            Gson gson = new Gson();
            String json = gson.toJson(graduationReply);
            msg = session.createTextMessage(json);
        }
        catch (JMSException e) {
            e.printStackTrace();
        }
        return msg;
    }

    public void send(Message msg) throws JMSException
    {
        //send graduationRequest via JMS
        producer.send(msg);
        //message sent
        System.out.println(msg);
    }

    public void send(Message msg, String queueName) throws JMSException
    {
        Destination destination = session.createQueue(queueName);
        //send graduationRequest via JMS to queueName destination
        producer.send(destination,msg);
        //message sent
        System.out.println(msg);
    }


    public void closeConnection()
    {
        try {
            if (producer != null) {
                producer.close();
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
}

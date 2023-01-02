package graduation.client.gateway;

import com.google.gson.Gson;
import shared.gateway.MessageReceiverGateway;
import shared.gateway.MessageSenderGateway;
import shared.model.client.GraduationReply;
import shared.model.client.GraduationRequest;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.HashMap;

public abstract class GraduationApplicationGateway {

    private MessageSenderGateway messageSenderGateway;
    private MessageReceiverGateway messageReceiverGateway;
    private HashMap<String, GraduationRequest> sentMessages = new HashMap<>(); //to each request save the message id

    public GraduationApplicationGateway()
    {
        messageSenderGateway = new MessageSenderGateway("graduationRequestQueue");
        messageReceiverGateway = new MessageReceiverGateway("graduationReplyQueue"); //change this for each client!

        //if reply is received
        MessageListener messageListener = new MessageListener() {
            @Override
            public void onMessage(Message message) {
                System.out.println("received: " + message);
                String msgBody = "";
                Gson gson = new Gson();
                try {
                    msgBody = ((TextMessage) message).getText();
                    //get the reply
                    GraduationReply graduationReply = gson.fromJson(msgBody, GraduationReply.class);
                    String messageId = message.getJMSCorrelationID();
                    //get the corresponding graduation request
                    GraduationRequest graduationRequest = getGraduationRequestByMessageId(messageId);
                    onGraduationReplyReceived(graduationRequest,graduationReply);

                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
        };
        this.messageReceiverGateway.setListener(messageListener);
    }

    public void submitRequest(GraduationRequest graduationRequest)
    {
        try {
            Message message = messageSenderGateway.createTextMessage(graduationRequest);
            //set the destination
            message.setJMSReplyTo(messageReceiverGateway.getReceiver());
            messageSenderGateway.send(message);
            //add msg_id and request to sentMessages
            sentMessages.put(message.getJMSMessageID(), graduationRequest);

        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    public abstract void onGraduationReplyReceived(GraduationRequest request, GraduationReply reply);

    private GraduationRequest getGraduationRequestByMessageId(String messageId) {
        return sentMessages.get(messageId);
    }

    public void closeConnection()
    {
        messageSenderGateway.closeConnection();
        messageReceiverGateway.closeConnection();
    }
}

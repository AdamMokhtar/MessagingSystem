package graduation.gateway;

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

public abstract class ClientApplicationGateway {
    private MessageReceiverGateway messageReceiverGateway;
    private MessageSenderGateway messageSenderGateway;
    private HashMap<GraduationRequest, Message> receivedMessages = new HashMap<>();

    public ClientApplicationGateway()
    {
        messageReceiverGateway = new MessageReceiverGateway("graduationRequestQueue");
        //messageSenderGateway = new MessageSenderGateway("loanReplyQueue");
        MessageListener messageListener = new MessageListener() {
            @Override
            public void onMessage(Message message) {
                System.out.println("received: " + message);
                String msgBody = "";
                Gson gson = new Gson();
                try {
                    //convert to graduationRequest
                    msgBody = ((TextMessage) message).getText();
                    GraduationRequest graduationRequest = gson.fromJson(msgBody, GraduationRequest.class);
                    //trigger displaying it
                    receivedMessages.put(graduationRequest,message);
                    onGraduationRequestReceived(graduationRequest);

                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
        };
        this.messageReceiverGateway.setListener(messageListener);
    }

    public void sendGraduationReply(GraduationRequest request, GraduationReply reply)
    {
        try {
            //get message corresponding to the request
            Message msgRequest = getMessageOfRequest(request);
            //get the destination and remove the extra chars
            //set the queue according to the received request

            if( msgRequest != null)
            {
                String queue = msgRequest.getJMSReplyTo().toString().replace("queue://", ""); //here get an error!!!
                messageSenderGateway = new MessageSenderGateway(queue);
            }else
                {
                    messageSenderGateway = new MessageSenderGateway();
                }

            Message message = messageSenderGateway.createTextMessage(reply);
            //set the correlated id
            message.setJMSCorrelationID(msgRequest.getJMSMessageID());
            messageSenderGateway.send(message);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
    public abstract void onGraduationRequestReceived(GraduationRequest graduationRequest);

    public Message getMessageOfRequest(GraduationRequest graduationRequest)
    {
        return receivedMessages.get(graduationRequest);
    }

    public void closeConnection()
    {
        messageReceiverGateway.closeConnection();
        messageSenderGateway.closeConnection();
    }
}

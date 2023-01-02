package graduation.approval.gateway;

import com.google.gson.Gson;
import shared.gateway.MessageReceiverGateway;
import shared.gateway.MessageSenderGateway;
import shared.model.approval.ApprovalReply;
import shared.model.approval.ApprovalRequest;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.HashMap;

public abstract class GraduationApplicationGateway {
    private MessageReceiverGateway messageReceiverGateway;
    private MessageSenderGateway messageSenderGateway;
    private final String AGGREGATION_ID = "aggregationID";
    private HashMap<ApprovalRequest, Message> receivedMessages = new HashMap<>();

    public GraduationApplicationGateway(String queueName)
    {
        messageReceiverGateway = new MessageReceiverGateway(queueName);
        //messageSenderGateway = new MessageSenderGateway("graduationReplyQueue");
        MessageListener messageListener = new MessageListener() {
            @Override
            public void onMessage(Message message) {
                System.out.println("received: " + message);
                String msgBody = "";
                Gson gson = new Gson();
                try {
                    msgBody = ((TextMessage) message).getText();
                    ApprovalRequest approvalRequest = gson.fromJson(msgBody, ApprovalRequest.class);
                    onApprovalRequestReceived(approvalRequest);
                    //save the message received
                    receivedMessages.put(approvalRequest,message);

                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
        };
        this.messageReceiverGateway.setListener(messageListener);
    }

    public void sendApprovalReply(ApprovalRequest request, ApprovalReply reply)
    {
        try {
            //get message corresponding to the request
            Message msgRequest = getMessageOfRequest(request);

            //get the destination and remove the extra chars
            //set the queue according to the received request
            String queue = msgRequest.getJMSReplyTo().toString().replace("queue://", "");
            messageSenderGateway = new MessageSenderGateway(queue);
            Message message = messageSenderGateway.createTextMessage(reply);
            //set the correlated id
            if(message != null)
            {
                //set the aggregation id & corr id
                int aggregationID = msgRequest.getIntProperty(AGGREGATION_ID);
                message.setIntProperty(AGGREGATION_ID, aggregationID);
                message.setJMSCorrelationID(msgRequest.getJMSMessageID());
                messageSenderGateway.send(message);
            }

        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    public abstract void onApprovalRequestReceived(ApprovalRequest approvalRequest);

    public Message getMessageOfRequest(ApprovalRequest approvalRequest)
    {
        return receivedMessages.get(approvalRequest);
    }

    public void closeConnection()
    {
        messageReceiverGateway.closeConnection();
        messageSenderGateway.closeConnection();
    }
}

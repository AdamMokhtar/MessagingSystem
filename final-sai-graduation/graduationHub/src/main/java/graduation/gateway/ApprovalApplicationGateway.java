package graduation.gateway;

import com.google.gson.Gson;
import graduation.ApprovalFileParser;
import org.mariuszgromada.math.mxparser.Argument;
import org.mariuszgromada.math.mxparser.Expression;
import shared.gateway.MessageReceiverGateway;
import shared.gateway.MessageSenderGateway;
import shared.model.approval.ApprovalReply;
import shared.model.approval.ApprovalRequest;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class ApprovalApplicationGateway {
    private MessageSenderGateway messageSenderGateway;
    private MessageReceiverGateway messageReceiverGateway;
    private ApprovalFileParser approvalFileParser;
    private HashMap<String, ApprovalRequest> sentMessages = new HashMap<>(); //string is the message id
    private HashMap<Integer, List<ApprovalReply>> receivedApprovalReplies = new HashMap<>(); //for each aggregation list of replies
    private HashMap<String, String> rules; //get the map of rules and approval names
    private HashMap<Integer,Integer> aggregation_counter = new HashMap<>(); //for each aggregation to how many banks its sent
    private final String AGGREGATION_ID = "aggregationID";
    private static int aggregationIdValue = 1;

    public ApprovalApplicationGateway()
    {
        messageSenderGateway = new MessageSenderGateway();
        messageReceiverGateway = new MessageReceiverGateway("approvalReplyQueue");
        approvalFileParser = new ApprovalFileParser();
        rules = approvalFileParser.getRules();
        aggregation_counter.put(aggregationIdValue,0);
        //if reply received
        MessageListener messageListener = new MessageListener() {
            @Override
            public void onMessage(Message message) {
                System.out.println("received: " + message);
                String msgBody = "";
                Gson gson = new Gson();
                try {
                    msgBody = ((TextMessage) message).getText();
                    //get the approvalReply
                    ApprovalReply approvalReply = gson.fromJson(msgBody, ApprovalReply.class);
                    String messageId = message.getJMSCorrelationID();
                    //get the corresponding approval request
                    ApprovalRequest approvalRequest = getApprovalRequestByMessageId(messageId);
                    //get the agId of the message
                    Integer aggregationId = message.getIntProperty(AGGREGATION_ID);
                    receivedApprovalReplies.get(aggregationId).add(approvalReply);
                    if(aggregation_counter.get(aggregationId) == receivedApprovalReplies.get(aggregationId).size())
                    {
                        String rejection = "";
                        ApprovalReply approvalReplyToSend = new ApprovalReply(true, "");

                        ///check rejection
                        for (int i = 0; i < receivedApprovalReplies.get(aggregationId).size(); i++) {
                            if (!receivedApprovalReplies.get(aggregationId).get(i).isApproved()){
                                approvalReplyToSend = receivedApprovalReplies.get(aggregationId).get(i);
                            }
                        }
                        onGraduationReplyReceived(approvalRequest,approvalReplyToSend);
                    }
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
        };
        this.messageReceiverGateway.setListener(messageListener);
    }

    public void applyForApproval(ApprovalRequest approvalRequest)
    {
        Argument ecs = new Argument(" ecs = " + approvalRequest.getEcs() + " ");
        try {
            boolean rejected = true;
            Message message = messageSenderGateway.createTextMessage(approvalRequest);
            message.setIntProperty(AGGREGATION_ID, aggregationIdValue);
            //set the destination !!!
            message.setJMSReplyTo(messageReceiverGateway.getReceiver());

            //based on rules send requests
            for (Map.Entry<String, String> entry : rules.entrySet()) {
                Expression expression = new Expression(entry.getValue(), ecs);
                if (expression.calculate() == 1) {
                    //increment counter by 1
                    aggregation_counter.put(aggregationIdValue, aggregation_counter.get(aggregationIdValue) + 1);
                    receivedApprovalReplies.put(aggregationIdValue,new ArrayList<>());
                    messageSenderGateway.send(message, entry.getKey());
                    //add msg_id and request to sentMessages
                    sentMessages.put(message.getJMSMessageID(), approvalRequest);
                    rejected = false;
                }
            }

            if (rejected) {
                ApprovalReply ar = new ApprovalReply(false,"InvalidRequest");
                onGraduationReplyReceived(approvalRequest, ar);
            } else {
                aggregationIdValue++;
                aggregation_counter.put(aggregationIdValue,0);
            }

        } catch (JMSException e) {
            e.printStackTrace();
        }

    }

    public abstract void onGraduationReplyReceived(ApprovalRequest approvalRequest, ApprovalReply approvalReply);

    private ApprovalRequest getApprovalRequestByMessageId(String messageId) {
        return sentMessages.get(messageId);
    }

    public void closeConnection()
    {
        messageSenderGateway.closeConnection();
        messageReceiverGateway.closeConnection();
    }
}

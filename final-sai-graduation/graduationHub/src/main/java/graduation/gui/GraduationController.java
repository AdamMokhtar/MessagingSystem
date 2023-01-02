package graduation.gui;

import graduation.gateway.ApprovalApplicationGateway;
import graduation.gateway.ClientApplicationGateway;
import graduation.gateway.api.AdministrationApplicationGateway;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import org.json.JSONObject;
import shared.gui.ListViewLine;
import shared.model.approval.ApprovalReply;
import shared.model.approval.ApprovalRequest;
import shared.model.client.GraduationReply;
import shared.model.client.GraduationRequest;

import javax.jms.JMSException;
import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;

public class GraduationController implements Initializable {

    @FXML
    private ListView<ListViewLine<ApprovalRequest, ApprovalReply>> lvGraduationRequestReply;
    private HashMap<ApprovalRequest, GraduationRequest> approvalRequest_graduationRequest;
    private ApprovalApplicationGateway approvalApplicationGateway;
    private ClientApplicationGateway clientApplicationGateway;
    private AdministrationApplicationGateway administrationApplicationGateway;

    private void showApprovalRequest(ApprovalRequest approvalRequest){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                lvGraduationRequestReply.getItems().add(new ListViewLine<>(approvalRequest));
            }
        });
    }

    public GraduationController()
    {
        administrationApplicationGateway = new AdministrationApplicationGateway();
        approvalRequest_graduationRequest = new HashMap<>();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        approvalApplicationGateway = new ApprovalApplicationGateway() {
            @Override
            public void onGraduationReplyReceived(ApprovalRequest approvalRequest, ApprovalReply approvalReply) {
                //the received reply from the approval
                //get the same graduation request from the list view and update it with the reply then refresh
                for (ListViewLine<ApprovalRequest, ApprovalReply> lv: lvGraduationRequestReply.getItems())
                {
                    if(lv.getRequest().equals(approvalRequest))
                    {
                        lv.setReply(approvalReply);
                    }
                }

                //get the bank request from the map
                GraduationRequest graduationRequest = approvalRequest_graduationRequest.get(approvalRequest);
                GraduationReply graduationReply = new GraduationReply(approvalReply.isApproved(),"");
                if(!approvalReply.isApproved())
                {
                    //check if is not approved then include the name
                    graduationReply.setRejectedBy(approvalReply.getName());
                }
                lvGraduationRequestReply.refresh();
                //send the reply to the client
                clientApplicationGateway.sendGraduationReply(graduationRequest,graduationReply);
            }
        };
        clientApplicationGateway = new ClientApplicationGateway() {
            @Override
            public void onGraduationRequestReceived(GraduationRequest graduationRequest) {

                //call API to get ecs
                int ecs = administrationApplicationGateway.getEcs(graduationRequest.getStudentNumber());
                //create approval request
                ApprovalRequest approvalRequest = new ApprovalRequest(graduationRequest.getStudentNumber(),graduationRequest.getCompany()
                ,graduationRequest.getProjectTitle(),ecs,graduationRequest.getGroup());
                //save the graduation request associated with the approval request
                approvalRequest_graduationRequest.put(approvalRequest,graduationRequest);
                showApprovalRequest(approvalRequest);
                //send the received approval request to the approval
                approvalApplicationGateway.applyForApproval(approvalRequest);
            }
        };
    }

    void stop() throws JMSException {
        //close JMS connection here
        approvalApplicationGateway.closeConnection();
        clientApplicationGateway.closeConnection();
    }
}

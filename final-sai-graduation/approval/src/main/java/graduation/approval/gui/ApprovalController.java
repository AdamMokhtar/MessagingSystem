package graduation.approval.gui;

import graduation.approval.gateway.GraduationApplicationGateway;
import javafx.application.Platform;
import javafx.fxml.Initializable;
import shared.gui.ListViewLine;
import shared.model.approval.ApprovalReply;
import shared.model.approval.ApprovalRequest;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;

import java.net.URL;
import java.util.ResourceBundle;

@SuppressWarnings("unused")
class ApprovalController implements Initializable {

    private final String approvalName;
    private String queueName; //added
    @FXML
    private ListView<ListViewLine<ApprovalRequest, ApprovalReply>> lvApprovalRequestReply;

    @FXML
    private CheckBox cbApproved;

    private GraduationApplicationGateway graduationApplicationGateway;

    public ApprovalController(String approvalName, String queueName){
        this.approvalName = approvalName;
        this.queueName = queueName;
   }

    @FXML
    private void btnSendApprovalReplyClicked(){
        boolean isAccepted = cbApproved.isSelected();
        System.out.println(isAccepted);
        ApprovalReply approvalReply = new ApprovalReply(isAccepted, approvalName);

        ListViewLine<ApprovalRequest, ApprovalReply> listViewLine = lvApprovalRequestReply.getSelectionModel().getSelectedItem();
        if (listViewLine != null) {
            ApprovalRequest approvalRequest = listViewLine.getRequest();
            listViewLine.setReply(approvalReply);
            Platform.runLater(() -> this.lvApprovalRequestReply.refresh());
            System.out.println("Approval application " + approvalName + " is sending " + approvalReply + " for " + approvalRequest);
            // send a message back with the correlation ID
            graduationApplicationGateway.sendApprovalReply(approvalRequest,approvalReply);
        } else {
            System.err.println("Please select one request in the list!");
        }
    }

    /**
     * Adds a new ListViewLine to lvApprovalRequestReply with graduationApprovalRequest
     */
    private void addApprovalRequest(ApprovalRequest approvalRequest){
        ListViewLine<ApprovalRequest, ApprovalReply> listViewLine = new ListViewLine<>(approvalRequest);
        this.lvApprovalRequestReply.getItems().add(listViewLine);
    }

    /**
     * This method returns the line of lvMessages which contains the given request.
     * @param request GraduationClientRequest for which the line of lvMessages should be found and returned
     * @return The ListViewLine line of lvMessages which contains the given request
     */
    private ListViewLine<ApprovalRequest, ApprovalReply> getRequestReply(ApprovalRequest request) {
        for (ListViewLine<ApprovalRequest, ApprovalReply> listViewLine: lvApprovalRequestReply.getItems()) {
            if (listViewLine.getRequest() == request) {
                return listViewLine;
            }
        }
        return null;
    }

    /**
     * This method is called in ApprovalMain when closing this JavaFX stage, i.e., when closing the application.
     */
    public void stop() {
        graduationApplicationGateway.closeConnection();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        graduationApplicationGateway = new GraduationApplicationGateway(queueName) {
            @Override
            public void onApprovalRequestReceived(ApprovalRequest approvalRequest) {
                addApprovalRequest(approvalRequest);
            }
        };
    }
}

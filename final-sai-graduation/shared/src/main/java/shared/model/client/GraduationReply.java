/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shared.model.client;

/**
 *
 * @author 884294
 */
public class GraduationReply {
    private boolean approved;
    private String rejectedBy;
    
    public GraduationReply(boolean approved, String rejectedBy){
        this.approved = approved;
        this.rejectedBy = rejectedBy;
    }
    
    public GraduationReply(){
        this.approved = false;
        this.rejectedBy = "unknown";
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public String getRejectedBy() {
        return rejectedBy;
    }

    public void setRejectedBy(String rejectedBy) {
        this.rejectedBy = rejectedBy;
    }
    
    @Override
    public String toString(){
        return (approved?"approved":"rejected->") + ((!approved)?rejectedBy:"");
    }
}

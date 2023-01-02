/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shared.model.approval;

/**
 *
 * @author 884294
 */
public class ApprovalReply {
    private boolean approved;
    private String name;

    
    public ApprovalReply(boolean approved, String name){
        this.approved = approved;
        this.name = name;                
    }
    
    public ApprovalReply(){
        this.approved = true;
        this.name = null;                
    }    
    
    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    } 
    
    @Override
    public String toString(){
        return (approved?"approved-":"rejected-") + name;
    }
}

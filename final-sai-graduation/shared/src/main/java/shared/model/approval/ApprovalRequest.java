/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shared.model.approval;

import shared.model.Group;

/**
 *
 * @author 884294
 */
public class ApprovalRequest {
    
    private int studentNumber;       
    private String company;
    private String projectTitle;    
    private int ecs;
    private Group group;
    
    public ApprovalRequest(int studentNumber, String company, String projectTitle, int ecs, Group group){
        this.studentNumber = studentNumber;
        this.company = company;
        this.projectTitle = projectTitle;
        this.ecs = ecs;
        this.group = group;
    }
    
    public ApprovalRequest(){
        this.studentNumber = 0;
        this.company = "unknown";
        this.projectTitle = "unknown";
        this.ecs = 0;
        this.group = Group.SOFTWARE;
    }

    public int getStudentNumber() {
        return studentNumber;
    }

    public void setStudentNumber(int studentNumber) {
        this.studentNumber = studentNumber;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getProjectTitle() {
        return projectTitle;
    }

    public void setProjectTitle(String projectTitle) {
        this.projectTitle = projectTitle;
    }
    
    public int getEcs() {
        return ecs;
    }

    public void setEcs(int ecs) {
        this.ecs = ecs;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    @Override
     public String toString(){
        return "["+ studentNumber + "] [" + group.shortName()  +  "] [" + company + "] [" + projectTitle + "] ["
				+ ecs + " ECs]";
    }
}

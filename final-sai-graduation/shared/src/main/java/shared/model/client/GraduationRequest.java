/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shared.model.client;

import shared.model.Group;

/**
 *
 * @author 884294
 */
public class GraduationRequest {
    private int studentNumber;
       
    private String company;
    private String projectTitle;
    private Group group;
    
    public GraduationRequest(int studentNumber, String company, String projectTitle, Group group){
        this.studentNumber = studentNumber;
        this.company = company;
        this.projectTitle = projectTitle;
        this.group = group;
    }
    
    public GraduationRequest(){
        this.studentNumber = 0;
        this.company = "unknown";
        this.projectTitle = "unknown";
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

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    @Override
     public String toString(){
       // return "stnr:"+ studentNumber + " gr: " + group.name().substring(0,4) + " comp:" + company + " proj:" + projectTitle ;
        return "["+ studentNumber + "]  [" + group.shortName() + "]  [" + company + "]  [" + projectTitle + "]";
    }
    
}

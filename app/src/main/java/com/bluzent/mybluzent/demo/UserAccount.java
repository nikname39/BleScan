/**
 * vo
 */
package com.bluzent.mybluzent.demo;

public class UserAccount
{
    private String idToken;     // Firebase 고유번호
    private String emailId;
    private String password;
    private String name;
    private String work_start;
    private String work_end;
    private String time_work_start;
    private String time_work_end;
    private String android_Id;

    public UserAccount() {}

    public  String getIdToken() { return idToken;}
    public void setIdToken(String idToken) { this.idToken = idToken;}

    public  String getEmailId() { return emailId;}
    public void setEmailId(String emailId) { this.emailId = emailId;}

    public  String getPassword() { return password;}
    public void setPassword(String password) { this.password = password;}

    public  String getName() { return name;}
    public void setName(String name) { this.name = name;}

    public  String getWork_start() { return work_start;}
    public void setWork_start(String work_start) { this.work_start = work_start;}

    public  String getWork_end() { return work_end;}
    public void setWork_end(String work_end) { this.work_end = work_end;}

    public  String getTime_work_start() { return time_work_start;}
    public void setTime_work_start(String time_work_start) { this.time_work_start = time_work_start;}

    public  String getTime_work_end() { return time_work_end;}
    public void setTime_work_end(String time_work_end) { this.time_work_end = time_work_end;}

    public  String getAndroid_Id() { return android_Id;}
    public void setAndroid_Id(String android_Id) { this.android_Id = android_Id;}

}
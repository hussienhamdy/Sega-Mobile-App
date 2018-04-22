package com.example.ussien.sega.Model;

/**
 * Created by ussien on 18/09/2017.
 */

public class Student {
    private String firstName;
    private String lastName;

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    private String nickName;
    private String email;
    private String phone;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    private String profilePicture;
    private int studentID;

    public int  getStudentID() {
        return studentID;
    }

    public void setStudentID(int studentID) {
        this.studentID = studentID;
    }

    public Student(int studentID , String nickName , String email)
    {
        this.studentID=studentID;
        this.email=email;
        this.nickName=nickName;
    }

    public Student(String firstName, String lastName, String nickName, String profilePicture, int studentID, String email) {

        this.firstName = firstName;
        this.lastName = lastName;
        this.nickName = nickName;
        this.profilePicture = profilePicture;
        this.studentID = studentID;
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }
}

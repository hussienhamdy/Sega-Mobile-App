package com.example.ussien.sega.Model;

import java.sql.SQLTransactionRollbackException;

/**
 * Created by ussien on 19/09/2017.
 */

public class LoginResult {
    String valid;
    String error;
    Student student;

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public String getValid() {
        return valid;
    }

    public void setValid(String valid) {
        this.valid = valid;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}

package DataModel;

import java.io.Serializable;

public class Grade implements Serializable {

    private String courseName;
    private double grade;
    private String status;

    public Grade(String courseName, double grade, String status) {
        this.courseName = courseName;
        this.grade = grade;
        this.status = status;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public double getGrade() {
        return grade;
    }

    public void setGrade(double grade) {
        this.grade = grade;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
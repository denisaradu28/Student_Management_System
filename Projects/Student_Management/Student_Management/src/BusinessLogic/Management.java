package BusinessLogic;

import DataAccess.Serialization;
import DataModel.Grade;
import DataModel.Student;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Management {

    private Map<Student, List<Grade>> map = new HashMap<>();
    private List<Grade> grades = new ArrayList<>();

    public Management() {
        Map<Student, List<Grade>> loadData = Serialization.loadStudentData();
        List<Grade> loadGrade = Serialization.loadGradesData();

        this.map = (loadData != null) ? loadData : new HashMap<>();
        this.grades = (loadGrade != null) ? loadGrade : new ArrayList<>();
    }

    public void addStudent(Student student) {
        if (!map.containsKey(student)) {
            map.put(student, new ArrayList<>());
            Serialization.saveStudentData(map);
        }
    }

    public void addGrade(Grade grade) {
        if (grade != null) {
            grades.add(grade);
            Serialization.saveGradesData(grades);
        }
    }

    public void assignGradeToStudent(Student student, Grade grade) {
        if (student == null || grade == null) return;

        map.putIfAbsent(student, new ArrayList<>());

        // Avoid duplicates by course name
        boolean exists = map.get(student).stream()
                .anyMatch(g -> g.getCourseName().equalsIgnoreCase(grade.getCourseName()));
        if (!exists) {
            map.get(student).add(grade);
        }

        Serialization.saveStudentData(map);
    }

    public float calculateStudentAverage(int idStudent) {
        List<Grade> grades = getStudentGrades(idStudent);
        if (grades == null || grades.isEmpty()) return 0;

        float sum = 0;
        for (Grade grade : grades) {
            sum += (float) grade.getGrade();
        }

        return sum / grades.size();
    }

    public boolean modifyStudentGrade(int idStudent, String courseName, double newGrade, String newStatus) {
        for (Student student : map.keySet()) {
            if (student.getId() == idStudent) {
                List<Grade> grades = map.get(student);
                for (Grade grade : grades) {
                    if (grade.getCourseName().equalsIgnoreCase(courseName)) {
                        grade.setGrade(newGrade);
                        grade.setStatus(newStatus);

                        Serialization.saveStudentData(map);
                        Serialization.saveGradesData(this.grades); // save global list
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public List<Grade> getStudentGrades(int idStudent) {
        for (Student student : map.keySet()) {
            if (student.getId() == idStudent) {
                return new ArrayList<>(map.get(student));
            }
        }
        return new ArrayList<>();
    }

    public Student findStudentById(int idStudent) {
        for (Student student : map.keySet()) {
            if (student.getId() == idStudent) {
                return student;
            }
        }
        return null;
    }

    public Grade findCourseByName(String name) {
        for (Grade grade : grades) {
            if (grade.getCourseName().equalsIgnoreCase(name)) {
                return grade;
            }
        }

        for (List<Grade> list : map.values()) {
            for (Grade grade : list) {
                if (grade.getCourseName().equalsIgnoreCase(name)) {
                    return grade;
                }
            }
        }

        return null;
    }

    public List<Grade> getPassedCourses(int idStudent) {
        List<Grade> passedCourses = new ArrayList<>();
        List<Grade> grades = getStudentGrades(idStudent);

        for (Grade grade : grades) {
            if (grade.getStatus().equalsIgnoreCase("Passed")) {
                passedCourses.add(grade);
            }
        }
        return passedCourses;
    }

    public void clearData() {
        map.clear();
        grades.clear();
        Serialization.saveStudentData(map);
        Serialization.saveGradesData(grades);
    }

    public void reset() {
        clearData();
    }

    public Map<Student, List<Grade>> getMap() {
        return map;
    }

    public List<Student> getAllStudents() {
        return new ArrayList<>(map.keySet());
    }

    public List<Grade> getAllGrades() {
        return new ArrayList<>(grades);
    }
}

package DataModel;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class Student implements Serializable {

    private static int idCounter = 1;
    private int id;
    private String name;
    List<Grade> grades;

    public Student(String name) {

        this.id = idCounter++;
        this.name = name;
        this.grades = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }


    public List<Grade> getGrades() {
        return grades;
    }

    private void addGrade(Grade grade) {
        grades.add(grade);
    }

    private double calculateAverage()
    {
        if(grades.isEmpty())
            return 0;

        double sum = 0;
        for (Grade grade : grades) {
            sum += grade.getGrade();
        }
        return sum / grades.size();
    }

    public int countPassed()
    {
        int passed = 0;
        for (Grade grade : grades) {
            if("Passed".equalsIgnoreCase(grade.getStatus()))
                passed++;
        }
        return passed;
    }

    public int countNotPassed()
    {
        int notPassed = 0;
        for (Grade grade : grades) {
            if(!"Passed".equalsIgnoreCase(grade.getStatus()))
                notPassed++;
        }
        return notPassed;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }
        if(!(o instanceof Student)) {
            return false;
        }

        Student student = (Student) o;
        return id == student.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
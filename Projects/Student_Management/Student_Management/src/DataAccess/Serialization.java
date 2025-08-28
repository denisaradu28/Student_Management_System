package DataAccess;

import DataModel.Grade;
import DataModel.Student;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Serialization {

    private static final String STUDENT_FILE = "student_data.ser";
    private static final String GRADES_FILE = "grades_data.ser";

    public static void saveStudentData(Map<Student, List<Grade>> map)
    {
        try(ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(STUDENT_FILE)))
        {
            out.writeObject(map);
            System.out.println("Student data saved.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Map<Student, List<Grade>> loadStudentData(){

        try(ObjectInputStream in = new ObjectInputStream(new FileInputStream(STUDENT_FILE)))
        {
            Object obj = in.readObject();
            Map<Student, List<Grade>> map = (Map<Student, List<Grade>>) obj;
            return map;
        }
        catch(IOException | ClassNotFoundException e)
        {
            System.out.println("Student data could not be loaded.");
        }

        return new HashMap<>();
    }

    public static void saveGradesData(List<Grade> grades)
    {
        try(ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(GRADES_FILE)))
        {
            out.writeObject(grades);
            System.out.println("Grades saved.");
        }
        catch (IOException e)
            {
            e.printStackTrace();
            }
    }

    public static List<Grade> loadGradesData(){
        try(ObjectInputStream in = new ObjectInputStream(new FileInputStream(GRADES_FILE)))
        {
            Object obj = in.readObject();
            List<Grade> grades = (List<Grade>) obj;
            return grades;
        }
        catch(IOException | ClassNotFoundException e)
            {
            System.out.println("Grades data could not be loaded.");
            }
        return new ArrayList<>();
    }

}

package BusinessLogic;

import DataModel.Grade;
import DataModel.Student;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Utility {

    public static void filterStudentByAverage(Map<Student, List<Grade>> map, Management management) {

        for(Student student : map.keySet()) {

            double average = management.calculateStudentAverage(student.getId());

            if(average > 8)
                System.out.println(student.getName() + " has a average of: " + average);
        }
    }

    public static Map<String, Map<String, Integer>> countPassed(Map<Student, List<Grade>> map) {
        Map<String, Map<String, Integer>> passedMap = new HashMap<>();
        for(Student student : map.keySet()) {

            int passed = 0;
            int failed = 0;

            for(Grade grade : map.get(student)) {
                if("Passed".equals(grade.getStatus())) {
                    passed++;
                }
                else if("Failed".equals(grade.getStatus())) {
                    failed++;
                }
            }

            Map<String, Integer> getPassedMap = new HashMap<>();
            getPassedMap.put("Passed", passed);
            getPassedMap.put("Failed", failed);

            passedMap.put(student.getName(), getPassedMap);

        }
        return passedMap;
    }

}

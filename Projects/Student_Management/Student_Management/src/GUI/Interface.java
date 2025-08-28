package GUI;

import BusinessLogic.Management;
import BusinessLogic.Utility;
import DataAccess.Serialization;
import DataModel.Grade;
import DataModel.Student;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Interface extends JPanel {

    private JTable studentTable;
    private JTable gradesTable;
    private DefaultTableModel studentTableModel;
    private DefaultTableModel gradesTableModel;
    private Management management;

    public Interface() {

        this.setLayout(new BorderLayout());

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(9, 1, 10, 10));

        JButton addStudentButton = new JButton("Add Student");
        JButton addGradesButton = new JButton("Add Grades");
        JButton assignGradesButton = new JButton("Assign Grades");
        JButton viewStudentButton = new JButton("View Student");
        JButton modifyGradesButton = new JButton("Modify Grades");
        JButton filterStudentButton = new JButton("Filter Students");
        JButton countPassedButton = new JButton("Count Passed");
        JButton reloadButton = new JButton("Reload");
        JButton resetButton = new JButton("Reset");

        panel.add(addStudentButton);
        panel.add(addGradesButton);
        panel.add(assignGradesButton);
        panel.add(viewStudentButton);
        panel.add(modifyGradesButton);
        panel.add(filterStudentButton);
        panel.add(countPassedButton);
        panel.add(reloadButton);
        panel.add(resetButton);

        studentTableModel = new DefaultTableModel(new String[]{"ID", "Name", "Grades"}, 0);
        studentTable = new JTable(studentTableModel);

        gradesTableModel = new DefaultTableModel(new String[]{"Course", "Grade", "Status"}, 0);
        gradesTable = new JTable(gradesTableModel);

        JScrollPane studentScroll = new JScrollPane(studentTable);
        JScrollPane gradesScroll = new JScrollPane(gradesTable);

        studentScroll.setBorder(new TitledBorder("Student"));
        gradesScroll.setBorder(new TitledBorder("Grade"));

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, studentScroll, gradesScroll);
        splitPane.setResizeWeight(0.5);

        JSplitPane mainPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panel, splitPane);
        mainPane.setDividerLocation(0.25);

        this.add(mainPane, BorderLayout.CENTER);

        management = new Management();

        addStudentButton.addActionListener(e -> {
            String studentName = JOptionPane.showInputDialog("Enter Student Name: ");
            if (studentName != null && !studentName.isEmpty()) {
                Student student = new Student(studentName);
                management.addStudent(student);
                studentTableModel.addRow(new Object[]{student.getId(), student.getName(), "No grades assigned"});
                JOptionPane.showMessageDialog(null, "Student Added Successfully!");
            }
        });

        addGradesButton.addActionListener(e -> {
            String courseName = JOptionPane.showInputDialog("Enter Course Name: ");
            if (courseName == null || courseName.isEmpty()) return;

            String gradeInput = JOptionPane.showInputDialog("Enter Grade: ");
            if (gradeInput == null || gradeInput.isEmpty()) return;

            double grade;
            try {
                grade = Double.parseDouble(gradeInput);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Grade must be a number!");
                return;
            }

            String status = JOptionPane.showInputDialog("Enter Status (Passed/Failed): ");
            if (status == null || status.isEmpty()) return;

            Grade grades = new Grade(courseName, grade, status);
            management.addGrade(grades);
            gradesTableModel.addRow(new Object[]{courseName, grade, status});
            JOptionPane.showMessageDialog(null, "Grade Added Successfully!");
        });

        assignGradesButton.addActionListener(e -> {
            Integer studentId = selectStudentIdFromTable("Select Student by ID:");
            if (studentId == null) return;

            Student student = management.findStudentById(studentId);
            if (student == null) {
                JOptionPane.showMessageDialog(null, "Student Not Found!");
                return;
            }

            List<String> selectedCourses = selectMultipleItemsFromTable(gradesTableModel, "Select Courses:");
            if (selectedCourses == null || selectedCourses.isEmpty()) return;

            for (String courseName : selectedCourses) {
                Grade grade = management.findCourseByName(courseName);
                if (grade != null) {
                    management.assignGradeToStudent(student, grade);
                }
            }

            Serialization.saveStudentData(management.getMap());
            loadDataIntoTable();
            JOptionPane.showMessageDialog(null, "Grades assigned successfully!");
        });


        reloadButton.addActionListener(e -> loadDataIntoTable());
        resetButton.addActionListener(e -> {
            studentTableModel.setRowCount(0);
            gradesTableModel.setRowCount(0);
            management.reset();
        });

        modifyGradesButton.addActionListener(e -> {
            int selectedStudentId = selectIntItemFromTable(studentTableModel, "Select Student:");
            if (selectedStudentId == -1) return;
            Student selectedStudent = management.findStudentById(selectedStudentId);
            if (selectedStudent == null) {
                JOptionPane.showMessageDialog(null, "Student Not Found!");
                return;
            }
            String selectedCourseName = selectItemFromTable(gradesTableModel, "Select Course to modify:");
            if (selectedCourseName == null) return;
            Grade selectedGrade = management.findCourseByName(selectedCourseName);
            if (selectedGrade == null) {
                JOptionPane.showMessageDialog(null, "Grade Not Found!");
                return;
            }

            String newGradeInput = JOptionPane.showInputDialog("Enter new grade:");
            if (newGradeInput == null || newGradeInput.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Invalid grade!");
                return; }
            double newGrade;
            try {
                newGrade = Double.parseDouble(newGradeInput);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Grade must be a number!");
                return;
            }
            String[] statusOptions = {"Passed", "Failed"};
            String newStatus = (String) JOptionPane.showInputDialog( this, "Select new status:", "Modify Course Status", JOptionPane.QUESTION_MESSAGE, null, statusOptions, selectedGrade.getStatus() );

            if (newStatus == null) {
                JOptionPane.showMessageDialog(null, "Status selection cancelled!");
                return;
            }

            boolean updated = management.modifyStudentGrade( selectedStudent.getId(), selectedCourseName, newGrade, newStatus );
            if (updated) {
                JOptionPane.showMessageDialog(null, "Grade updated successfully!");
                loadDataIntoTable();
            }
            else {
                JOptionPane.showMessageDialog(null, "Failed to update grade!");
            }
        });

        viewStudentButton.addActionListener(e -> {

            JFrame viewFrame = new JFrame("View Student");
            viewFrame.setSize(800, 600);

            DefaultTableModel model = new DefaultTableModel(new String[]{"ID", "Name", "Course", "Grade", "Status"}, 0);
            JTable table = new JTable(model);

            Map<Student, List<Grade>> studentGradeMap = Serialization.loadStudentData();
            if(studentGradeMap != null)
            {
                for(Map.Entry<Student, List<Grade>> entry : studentGradeMap.entrySet())
                {
                    Student student = entry.getKey();
                    List<Grade> grades = entry.getValue();

                    if(grades == null || grades.isEmpty())
                    {
                        model.addRow(new Object[]{student.getId(), student.getName(), "-", "No grades", "-"});
                        continue;
                    }

                    for(Grade grade : grades)
                    {
                        model.addRow(new Object[]{
                                student.getId(),
                                student.getName(),
                                grade.getCourseName(),
                                grade.getGrade(),
                                grade.getStatus()
                        });
                    }
                }
            }

            JScrollPane scrollPane = new JScrollPane(table);
            viewFrame.add(scrollPane);
            viewFrame.setVisible(true);
        });

        filterStudentButton.addActionListener(e -> {

            Map<Student, List<Grade>> studentGradeMap = Serialization.loadStudentData();
            List<Grade> allGrades = Serialization.loadGradesData();

            if(studentGradeMap.isEmpty() || allGrades.isEmpty())
            {
                JOptionPane.showMessageDialog(null, "Data Not Found!");
                return;
            }

            JFrame viewFrame = new JFrame("Students with an average grater than 8.0");
            viewFrame.setSize(800, 600);

            DefaultTableModel model = new DefaultTableModel(new String[]{"Student", "Average"}, 0);
            JTable table = new JTable(model);

            Utility.filterStudentByAverage(studentGradeMap, management);

            for(Student student : studentGradeMap.keySet())
            {
                double average = management.calculateStudentAverage(student.getId());

                if(average > 8.0){
                    model.addRow(new Object[]{student.getName(), average});
                }
            }

            if(model.getRowCount() == 0)
            {
                model.addRow(new Object[]{"-", "-"});
            }

            JScrollPane scrollPane = new JScrollPane(table);
            viewFrame.add(scrollPane);
            viewFrame.setVisible(true);

        });

        countPassedButton.addActionListener(e -> {

            Map<Student, List<Grade>> studentGradeMap = Serialization.loadStudentData();
            List<Grade> allGrades = Serialization.loadGradesData();

            if(studentGradeMap.isEmpty() || allGrades.isEmpty())
            {
                JOptionPane.showMessageDialog(null, "Data Not Found!");
                return;
            }

            JFrame viewFrame = new JFrame("Passed count");
            viewFrame.setSize(800, 600);
            DefaultTableModel model = new DefaultTableModel(new String[]{"Student", "Passed", "Faild"}, 0);
            JTable table = new JTable(model);

            Map<String, Map<String, Integer>> passedCountmap = Utility.countPassed(studentGradeMap);

            for(String studentName : passedCountmap.keySet())
            {
                Map<String, Integer> passedCountMap = passedCountmap.get(studentName);
                int passed =  passedCountMap.getOrDefault("Passed", 0);
                int failed = passedCountMap.getOrDefault("Failed", 0);

                model.addRow(new Object[]{studentName, passed, failed});
            }

            if(model.getRowCount() == 0)
            {
                model.addRow(new Object[]{"-", "-"});
            }

            JScrollPane scrollPane = new JScrollPane(table);
            viewFrame.add(scrollPane);
            viewFrame.setVisible(true);

        });

    }

    private String selectItemFromTable(DefaultTableModel tableModel, String message) {
        Object[] items = getColumnValues(tableModel, 0);

        if (items.length == 0) return null;

        return (String) JOptionPane.showInputDialog(this, message, "Select", JOptionPane.QUESTION_MESSAGE, null, items, items[0]);
    }

    private int selectIntItemFromTable(DefaultTableModel tableModel, String message) {
        Object[] items = getColumnValues(tableModel, 0);

        if (items.length == 0) return -1;

        String selected = (String) JOptionPane.showInputDialog(
                this, message, "Select",
                JOptionPane.QUESTION_MESSAGE, null, items, items[0]
        );

        if (selected == null) return -1;

        try {
            return Integer.parseInt(selected);
        } catch (NumberFormatException e) {
            return -1;
        }
    }


    private Integer selectStudentIdFromTable(String message) {
        Object[] items = getColumnValues(studentTableModel, 0); // column 0 = ID
        if (items.length == 0) return null;
        String selected = (String) JOptionPane.showInputDialog(
                this, message, "Select",
                JOptionPane.QUESTION_MESSAGE, null, items, items[0]
        );
        if (selected == null) return null;
        return Integer.parseInt(selected);
    }

    private void loadDataIntoTable() {
        studentTableModel.setRowCount(0);
        gradesTableModel.setRowCount(0);

        for (Student student : management.getAllStudents()) {
            String gradesList = gradeToString(management.getStudentGrades(student.getId()));
            studentTableModel.addRow(new Object[]{student.getId(), student.getName(), gradesList});
        }

        for (Grade grade : management.getAllGrades()) {
            gradesTableModel.addRow(new Object[]{grade.getCourseName(), grade.getGrade(), grade.getStatus()});
        }
    }

    private String gradeToString(List<Grade> grades) {
        if (grades == null || grades.isEmpty()) return "No grades assigned";
        List<String> gradeStrings = new ArrayList<>();
        for (Grade g : grades) {
            gradeStrings.add(g.getCourseName() + " (" + g.getGrade() + ", " + g.getStatus() + ")");
        }
        return String.join(", ", gradeStrings);
    }

    private List<String> selectMultipleItemsFromTable(DefaultTableModel tableModel, String message) {
        JList<Object> list = new JList<>(getColumnValues(tableModel, 0));
        list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        int result = JOptionPane.showConfirmDialog(this, new JScrollPane(list), message,
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
        return (result == JOptionPane.OK_OPTION)
                ? list.getSelectedValuesList().stream().map(String::valueOf).toList()
                : null;
    }

    private Object[] getColumnValues(DefaultTableModel tableModel, int column) {
        int rowCount = tableModel.getRowCount();
        Object[] items = new Object[rowCount];
        for (int i = 0; i < rowCount; i++) {
            items[i] = String.valueOf(tableModel.getValueAt(i, column));
        }
        return items;
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Student Management System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 600);
        frame.add(new Interface());
        frame.setVisible(true);
    }
}

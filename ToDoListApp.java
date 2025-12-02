import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ToDoListApp extends JFrame {
    private DefaultListModel<Task> taskListModel;
    private JList<Task> taskList;
    private JTextField taskInput;
    private JButton addButton, deleteButton, toggleButton, clearButton;
    private ArrayList<Task> tasks;

    public ToDoListApp() {
        tasks = new ArrayList<>();
        initializeUI();
    }

    private void initializeUI() {
        setTitle("To-Do List Application");
        setSize(500, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(new Color(240, 240, 245));

        // Title
        JLabel titleLabel = new JLabel("My To-Do List", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(60, 60, 60));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Task list
        taskListModel = new DefaultListModel<>();
        taskList = new JList<>(taskListModel);
        taskList.setFont(new Font("Arial", Font.PLAIN, 14));
        taskList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        taskList.setCellRenderer(new TaskCellRenderer());

        JScrollPane scrollPane = new JScrollPane(taskList);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Bottom panel
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        bottomPanel.setBackground(new Color(240, 240, 245));

        // Input panel
        JPanel inputPanel = new JPanel(new BorderLayout(5, 5));
        inputPanel.setBackground(new Color(240, 240, 245));

        taskInput = new JTextField();
        taskInput.setFont(new Font("Arial", Font.PLAIN, 14));
        taskInput.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                new EmptyBorder(5, 5, 5, 5)));

        addButton = new JButton("Add Task");
        addButton.setBackground(new Color(76, 175, 80));
        addButton.setForeground(Color.WHITE);
        addButton.setFocusPainted(false);
        addButton.setFont(new Font("Arial", Font.BOLD, 12));

        inputPanel.add(taskInput, BorderLayout.CENTER);
        inputPanel.add(addButton, BorderLayout.EAST);

        // Button panel
        JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        buttonPanel.setBackground(new Color(240, 240, 245));

        toggleButton = new JButton("Mark Complete");
        toggleButton.setBackground(new Color(33, 150, 243));
        toggleButton.setForeground(Color.WHITE);
        toggleButton.setFocusPainted(false);

        deleteButton = new JButton("Delete Task");
        deleteButton.setBackground(new Color(244, 67, 54));
        deleteButton.setForeground(Color.WHITE);
        deleteButton.setFocusPainted(false);

        clearButton = new JButton("Clear All");
        clearButton.setBackground(new Color(150, 150, 150));
        clearButton.setForeground(Color.WHITE);
        clearButton.setFocusPainted(false);

        buttonPanel.add(toggleButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);

        bottomPanel.add(inputPanel, BorderLayout.NORTH);
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);

        // Event listeners
        addButton.addActionListener(e -> addTask());
        taskInput.addActionListener(e -> addTask());
        deleteButton.addActionListener(e -> deleteTask());
        toggleButton.addActionListener(e -> toggleTaskComplete());
        clearButton.addActionListener(e -> clearAllTasks());
    }

    private void addTask() {
        String taskText = taskInput.getText().trim();
        if (!taskText.isEmpty()) {
            Task task = new Task(taskText);
            tasks.add(task);
            taskListModel.addElement(task);
            taskInput.setText("");
            taskInput.requestFocus();
        } else {
            JOptionPane.showMessageDialog(this, "Please enter a task!", "Empty Task", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void deleteTask() {
        int selectedIndex = taskList.getSelectedIndex();
        if (selectedIndex != -1) {
            tasks.remove(selectedIndex);
            taskListModel.remove(selectedIndex);
        } else {
            JOptionPane.showMessageDialog(this, "Please select a task to delete!", "No Selection",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    private void toggleTaskComplete() {
        int selectedIndex = taskList.getSelectedIndex();
        if (selectedIndex != -1) {
            Task task = taskListModel.get(selectedIndex);
            task.toggleComplete();
            taskList.repaint();
        } else {
            JOptionPane.showMessageDialog(this, "Please select a task to toggle!", "No Selection",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    private void clearAllTasks() {
        if (!tasks.isEmpty()) {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to clear all tasks?",
                    "Clear All",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                tasks.clear();
                taskListModel.clear();
            }
        }
    }

    // Task class
    class Task {
        private String description;
        private boolean completed;
        private LocalDateTime createdAt;

        public Task(String description) {
            this.description = description;
            this.completed = false;
            this.createdAt = LocalDateTime.now();
        }

        public String getDescription() {
            return description;
        }

        public boolean isCompleted() {
            return completed;
        }

        public void toggleComplete() {
            this.completed = !this.completed;
        }

        public String getCreatedTime() {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, HH:mm");
            return createdAt.format(formatter);
        }

        @Override
        public String toString() {
            return (completed ? "✓ " : "○ ") + description;
        }
    }

    // Custom cell renderer
    class TaskCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            if (value instanceof Task) {
                Task task = (Task) value;
                label.setText("<html><div style='padding: 5px;'><b>" +
                        (task.isCompleted() ? "<strike>" + task.getDescription() + "</strike>" : task.getDescription())
                        +
                        "</b><br/><small style='color: gray;'>" + task.getCreatedTime() + "</small></div></html>");

                if (task.isCompleted()) {
                    label.setForeground(new Color(150, 150, 150));
                } else {
                    label.setForeground(new Color(60, 60, 60));
                }

                if (isSelected) {
                    label.setBackground(new Color(200, 220, 240));
                }
            }

            label.setBorder(new EmptyBorder(5, 10, 5, 10));
            return label;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }

            ToDoListApp app = new ToDoListApp();
            app.setVisible(true);
        });
    }
}
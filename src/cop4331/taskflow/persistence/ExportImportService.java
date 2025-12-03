package cop4331.taskflow.persistence;

import cop4331.taskflow.model.Task;
import cop4331.taskflow.model.TaskModel;
import cop4331.taskflow.model.TaskPriority;
import cop4331.taskflow.model.TaskStatus;

import javax.swing.*;
import java.awt.Component;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Service for exporting and importing tasks to/from various formats (JSON, CSV, XML).
 * 
 * <p>This service provides functionality to export tasks to JSON, CSV, or XML formats
 * and import tasks from these formats.
 * 
 * @author TaskFlow Team
 * @version 1.0
 */
public class ExportImportService {
    
    private static final DateTimeFormatter DATE_FORMATTER = 
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    
    /**
     * Exports tasks to a JSON file.
     * 
     * @param tasks the list of tasks to export
     * @param filePath the path to save the JSON file
     * @throws IOException if an I/O error occurs
     */
    public void exportToJSON(List<Task> tasks, Path filePath) throws IOException {
        // Export to JSON - because sometimes I need to backup my tasks (or share them)
        JsonPersistenceService jsonService = new JsonPersistenceService();
        jsonService.save(tasks, filePath);
    }
    
    /**
     * Exports tasks to a CSV file.
     * 
     * @param tasks the list of tasks to export
     * @param filePath the path to save the CSV file
     * @throws IOException if an I/O error occurs
     */
    public void exportToCSV(List<Task> tasks, Path filePath) throws IOException {
        // CSV export - for when I want to open my tasks in Excel (because why not?)
        try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
            // Write CSV header - all the columns I care about
            writer.write("ID,Title,Description,Due Date,Due Date String,Priority,Status,Category,Tags,Reminder Time,Created At,Updated At,Dependencies");
            writer.newLine();
            
            // Write each task - dumping all my tasks into CSV format
            for (Task task : tasks) {
                writer.write(escapeCSV(task.getId()));
                writer.write(",");
                writer.write(escapeCSV(task.getTitle()));
                writer.write(",");
                writer.write(escapeCSV(task.getDescription()));
                writer.write(",");
                writer.write(escapeCSV(task.getDueDateTime() != null ? 
                    task.getDueDateTime().format(DATE_FORMATTER) : ""));
                writer.write(",");
                writer.write(escapeCSV(task.getDueDateString()));
                writer.write(",");
                writer.write(escapeCSV(task.getPriority().name()));
                writer.write(",");
                writer.write(escapeCSV(task.getStatus().name()));
                writer.write(",");
                writer.write(escapeCSV(task.getCategory()));
                writer.write(",");
                writer.write(escapeCSV(String.join(";", task.getTags())));
                writer.write(",");
                writer.write(escapeCSV(task.getReminderTime() != null ? 
                    task.getReminderTime().format(DATE_FORMATTER) : ""));
                writer.write(",");
                writer.write(escapeCSV(task.getCreatedAt() != null ? 
                    task.getCreatedAt().format(DATE_FORMATTER) : ""));
                writer.write(",");
                writer.write(escapeCSV(task.getUpdatedAt() != null ? 
                    task.getUpdatedAt().format(DATE_FORMATTER) : ""));
                writer.write(",");
                writer.write(escapeCSV(String.join(";", task.getDependencies())));
                writer.newLine();
            }
        }
    }
    
    /**
     * Exports tasks to an XML file.
     * 
     * @param tasks the list of tasks to export
     * @param filePath the path to save the XML file
     * @throws IOException if an I/O error occurs
     */
    public void exportToXML(List<Task> tasks, Path filePath) throws IOException {
        // XML export - because XML is still a thing, I guess? (Honestly, I prefer JSON)
        try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
            writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            writer.newLine();
            writer.write("<tasks>");
            writer.newLine();
            
            for (Task task : tasks) {
                // Write each task as XML - wrapping everything in tags
                writer.write("  <task>");
                writer.newLine();
                writer.write("    <id>" + escapeXML(task.getId()) + "</id>");
                writer.newLine();
                writer.write("    <title>" + escapeXML(task.getTitle()) + "</title>");
                writer.newLine();
                writer.write("    <description>" + escapeXML(task.getDescription()) + "</description>");
                writer.newLine();
                if (task.getDueDateTime() != null) {
                    writer.write("    <dueDateTime>" + 
                        task.getDueDateTime().format(DATE_FORMATTER) + "</dueDateTime>");
                    writer.newLine();
                }
                if (task.getDueDateString() != null) {
                    writer.write("    <dueDateString>" + escapeXML(task.getDueDateString()) + "</dueDateString>");
                    writer.newLine();
                }
                writer.write("    <priority>" + task.getPriority().name() + "</priority>");
                writer.newLine();
                writer.write("    <status>" + task.getStatus().name() + "</status>");
                writer.newLine();
                if (task.getCategory() != null) {
                    writer.write("    <category>" + escapeXML(task.getCategory()) + "</category>");
                    writer.newLine();
                }
                writer.write("    <tags>");
                writer.newLine();
                for (String tag : task.getTags()) {
                    writer.write("      <tag>" + escapeXML(tag) + "</tag>");
                    writer.newLine();
                }
                writer.write("    </tags>");
                writer.newLine();
                if (task.getReminderTime() != null) {
                    writer.write("    <reminderTime>" + 
                        task.getReminderTime().format(DATE_FORMATTER) + "</reminderTime>");
                    writer.newLine();
                }
                writer.write("    <dependencies>");
                writer.newLine();
                for (String depId : task.getDependencies()) {
                    writer.write("      <dependency>" + escapeXML(depId) + "</dependency>");
                    writer.newLine();
                }
                writer.write("    </dependencies>");
                writer.newLine();
                writer.write("  </task>");
                writer.newLine();
            }
            
            writer.write("</tasks>");
            writer.newLine();
        }
    }
    
    /**
     * Imports tasks from a JSON file.
     * 
     * @param filePath the path to the JSON file
     * @return list of imported tasks
     * @throws IOException if an I/O error occurs
     */
    public List<Task> importFromJSON(Path filePath) throws IOException {
        JsonPersistenceService jsonService = new JsonPersistenceService();
        return jsonService.load(filePath);
    }
    
    /**
     * Imports tasks from a CSV file.
     * 
     * @param filePath the path to the CSV file
     * @return list of imported tasks
     * @throws IOException if an I/O error occurs
     */
    public List<Task> importFromCSV(Path filePath) throws IOException {
        List<Task> tasks = new ArrayList<>();
        List<String> lines = Files.readAllLines(filePath);
        
        if (lines.isEmpty()) {
            return tasks;
        }
        
        // Skip header line
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.trim().isEmpty()) continue;
            
            String[] parts = parseCSVLine(line);
            if (parts.length < 6) continue; // Need at least ID, Title, Description, Due Date, Priority, Status
            
            try {
                String id = unescapeCSV(parts[0]);
                String title = unescapeCSV(parts[1]);
                String description = unescapeCSV(parts.length > 2 ? parts[2] : "");
                LocalDateTime dueDateTime = null;
                if (parts.length > 3 && !parts[3].isEmpty()) {
                    dueDateTime = LocalDateTime.parse(parts[3], DATE_FORMATTER);
                }
                String dueDateString = parts.length > 4 ? unescapeCSV(parts[4]) : null;
                TaskPriority priority = TaskPriority.valueOf(parts.length > 5 ? parts[5] : "LOW");
                TaskStatus status = TaskStatus.valueOf(parts.length > 6 ? parts[6] : "PENDING");
                String category = parts.length > 7 ? unescapeCSV(parts[7]) : null;
                
                List<String> tags = new ArrayList<>();
                if (parts.length > 8 && !parts[8].isEmpty()) {
                    String[] tagArray = parts[8].split(";");
                    for (String tag : tagArray) {
                        if (!tag.trim().isEmpty()) {
                            tags.add(tag.trim());
                        }
                    }
                }
                
                Task task = new Task(id, title, description, dueDateTime, priority, status, tags, null);
                if (dueDateString != null && !dueDateString.isEmpty()) {
                    task.setDueDateString(dueDateString);
                }
                if (category != null && !category.isEmpty()) {
                    task.setCategory(category);
                }
                
                tasks.add(task);
            } catch (Exception e) {
                // Skip invalid lines
                System.err.println("Error parsing CSV line: " + e.getMessage());
            }
        }
        
        return tasks;
    }
    
    /**
     * Shows a file chooser dialog for exporting tasks.
     * 
     * @param parent the parent component
     * @param tasks the tasks to export
     */
    public void showExportDialog(Component parent, List<Task> tasks) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export Tasks");
        
        // Add file filters
        fileChooser.addChoosableFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
            "JSON Files (*.json)", "json"));
        fileChooser.addChoosableFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
            "CSV Files (*.csv)", "csv"));
        fileChooser.addChoosableFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
            "XML Files (*.xml)", "xml"));
        fileChooser.setFileFilter(fileChooser.getChoosableFileFilters()[0]);
        
        int result = fileChooser.showSaveDialog(parent);
        if (result == JFileChooser.APPROVE_OPTION) {
            try {
                Path filePath = fileChooser.getSelectedFile().toPath();
                javax.swing.filechooser.FileFilter filter = fileChooser.getFileFilter();
                
                if (filter.getDescription().contains("JSON")) {
                    exportToJSON(tasks, filePath);
                } else if (filter.getDescription().contains("CSV")) {
                    exportToCSV(tasks, filePath);
                } else if (filter.getDescription().contains("XML")) {
                    exportToXML(tasks, filePath);
                }
                
                JOptionPane.showMessageDialog(parent, 
                    "Tasks exported successfully!", "Export", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(parent, 
                    "Error exporting tasks: " + e.getMessage(), "Export Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Shows a file chooser dialog for importing tasks.
     * 
     * @param parent the parent component
     * @return list of imported tasks, or empty list if cancelled
     */
    public List<Task> showImportDialog(Component parent) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Import Tasks");
        
        // Add file filters
        fileChooser.addChoosableFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
            "JSON Files (*.json)", "json"));
        fileChooser.addChoosableFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
            "CSV Files (*.csv)", "csv"));
        fileChooser.addChoosableFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
            "XML Files (*.xml)", "xml"));
        fileChooser.setFileFilter(fileChooser.getChoosableFileFilters()[0]);
        
        int result = fileChooser.showOpenDialog(parent);
        if (result == JFileChooser.APPROVE_OPTION) {
            try {
                Path filePath = fileChooser.getSelectedFile().toPath();
                javax.swing.filechooser.FileFilter filter = fileChooser.getFileFilter();
                
                List<Task> tasks;
                if (filter.getDescription().contains("JSON")) {
                    tasks = importFromJSON(filePath);
                } else if (filter.getDescription().contains("CSV")) {
                    tasks = importFromCSV(filePath);
                } else {
                    JOptionPane.showMessageDialog(parent, 
                        "XML import not yet implemented", "Import", JOptionPane.INFORMATION_MESSAGE);
                    return new ArrayList<>();
                }
                
                JOptionPane.showMessageDialog(parent, 
                    "Imported " + tasks.size() + " tasks successfully!", "Import", JOptionPane.INFORMATION_MESSAGE);
                return tasks;
            } catch (IOException e) {
                JOptionPane.showMessageDialog(parent, 
                    "Error importing tasks: " + e.getMessage(), "Import Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        
        return new ArrayList<>();
    }
    
    private String escapeCSV(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
    
    private String unescapeCSV(String value) {
        if (value == null || value.isEmpty()) return "";
        if (value.startsWith("\"") && value.endsWith("\"")) {
            return value.substring(1, value.length() - 1).replace("\"\"", "\"");
        }
        return value;
    }
    
    private String[] parseCSVLine(String line) {
        List<String> parts = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder current = new StringBuilder();
        
        for (char c : line.toCharArray()) {
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                parts.add(current.toString());
                current = new StringBuilder();
            } else {
                current.append(c);
            }
        }
        parts.add(current.toString());
        
        return parts.toArray(new String[0]);
    }
    
    private String escapeXML(String value) {
        if (value == null) return "";
        return value.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("'", "&apos;");
    }
}


import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;
import javax.swing.plaf.FontUIResource;

public class EditLibraryContent extends JFrame {
    private JTextField bookCodeField;
    private JTextField bookNameField;
    private JTextField authorNameField;
    private DefaultTableModel tableModel;
    private JTable table;
    private Set<String> bookCodes;

    public EditLibraryContent() {
        setTitle("Edit Library Content");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        UIManager.put("Label.font", new FontUIResource(new Font("Arial", Font.PLAIN, 20)));
        UIManager.put("TextField.font", new FontUIResource(new Font("Arial", Font.PLAIN, 20)));
        UIManager.put("Button.font", new FontUIResource(new Font("Arial", Font.PLAIN, 20)));

        bookCodeField = new JTextField(20);
        bookNameField = new JTextField(20);
        authorNameField = new JTextField(20);

        tableModel = new DefaultTableModel();
        tableModel.addColumn("Book Code");
        tableModel.addColumn("Book Name");
        tableModel.addColumn("Author Name");
        tableModel.addColumn("Book Picture");

        table = new JTable(tableModel);
        table.setFont(new Font("Arial", Font.PLAIN, 20));
        table.getColumnModel().getColumn(3).setCellRenderer(new ImageRenderer());

        JTableHeader header = table.getTableHeader();
        Font headerFont = new Font("Arial", Font.BOLD, 25);
        header.setFont(headerFont);
        header.setBackground(Color.BLUE);
        header.setForeground(Color.WHITE);
        ((DefaultTableCellRenderer) header.getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);

        JScrollPane scrollPane = new JScrollPane(table);
        int topBorderSize = 20;
        int leftBorderSize = 10;
        int bottomBorderSize = 20;
        int rightBorderSize = 10;

        EmptyBorder border = new EmptyBorder(topBorderSize, leftBorderSize, bottomBorderSize, rightBorderSize);
        scrollPane.setBorder(border);

        add(scrollPane, BorderLayout.CENTER);

        JButton saveButton = new JButton("Save Book");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                promptUserForBookInfo();
                displayEnteredContent();
            }
        });

        JButton addPictureButton = new JButton("Add New Picture ");
        addPictureButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                promptUserForPicture();
            }
        });

        JButton saveAndExitButton = new JButton("Save Changes and Exit");
        saveAndExitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveAndExit();
            }
        });

        JButton deleteButton = new JButton("Delete Book");
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                promptUserForDeletion();
            }
        });

        JPanel inputPanel1 = new JPanel();
        inputPanel1.setLayout(new GridLayout(0, 1, 1, 1));

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(0, 2, 1, 1));
        inputPanel.add(new JLabel("Book Code:"));
        inputPanel.add(bookCodeField);
        inputPanel.add(new JLabel("Book Name:"));
        inputPanel.add(bookNameField);
        inputPanel.add(new JLabel("Author Name:"));
        inputPanel.add(authorNameField);
        inputPanel.add(new JLabel("Click to add a picture to new book and save:"));
        inputPanel.add(saveButton);
        inputPanel.add(new JLabel("Select a book to replace a new picture:"));
        inputPanel.add(addPictureButton);
        inputPanel.add(new JLabel("Select a book to delete:"));
        inputPanel.add(deleteButton);
        inputPanel1.add(saveAndExitButton);

        add(inputPanel, BorderLayout.NORTH);
        add(inputPanel1, BorderLayout.SOUTH);

        displayEnteredContent();

        bookCodes = new HashSet<>();
        sortTableById();
        setVisible(true);
    }
        
        private void sortTableById() {
            TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
            sorter.setComparator(0, Comparator.comparingInt(s -> Integer.parseInt(s.toString())));
            table.setRowSorter(sorter);
            sorter.toggleSortOrder(0);
    }
        private void promptUserForBookInfo() {
            String bookCodeStr = bookCodeField.getText();
            String bookName = bookNameField.getText();
            String authorName = authorNameField.getText();
        
            if (!validateBookCode(bookCodeStr)) {
                return;
            }
        
            if (bookCodes.contains(bookCodeStr) || checkIdExists(bookCodeStr, "Book.txt")) {
                JOptionPane.showMessageDialog(this, "Book with ID " + bookCodeStr + " already exists. Registration unsuccessful.");
                return;
            }
        
            if (bookName.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Book name cannot be empty.");
                return;
            }
        
            if (authorName.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Author name cannot be empty.");
                return;
            }
        
            String imagePath = JOptionPane.showInputDialog(this, "Enter Image Path (or leave blank for no image):");
        
            saveToFile(bookCodeStr, bookName, authorName, imagePath);
        
            bookCodeField.setText("");
            bookNameField.setText("");
            authorNameField.setText("");  
        }
        
        private boolean validateBookCode(String bookCodeStr) {
            try {
                int bookCode = Integer.parseInt(bookCodeStr);
        
                if (bookCode < 0) {
                    JOptionPane.showMessageDialog(this, "Book code must be a positive integer.");
                    return false;
                }
        
                if (bookCodes.contains(bookCodeStr) || checkIdExists(bookCodeStr, "Books.txt")) {
                    JOptionPane.showMessageDialog(this, "Book with ID " + bookCodeStr + " already exists. Registration unsuccessful.");
                    return false;
                }
        
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid book code. Please enter a valid integer.");
                return false;
            }
        
            return true;
        }
        
        private boolean checkIdExists(String id, String filename) {
            try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length > 0 && parts[0].trim().equals(id)) {
                        return true;
                    }
                }
            } catch (IOException e) {
                System.out.println("An error occurred while reading the file: " + filename);
                e.printStackTrace();
            }
            return false;
        }
        
    
        private void saveToFile(String bookCode, String bookName, String authorName, String imagePath) {
            try (BufferedWriter writerBook = new BufferedWriter(new FileWriter("Book.txt", true));
                 BufferedWriter writerBooks = new BufferedWriter(new FileWriter("Books.txt", true))) {
        
                File file = new File(imagePath);
                String canonicalImagePath = file.getCanonicalPath();
                String line = bookCode + "," + bookName + "," + authorName + "," + canonicalImagePath + "\n";
        
                writerBook.write(line);
                writerBook.flush();
        
                writerBooks.write(line);
                writerBooks.flush();

                
        
                System.out.println("Successfully wrote to the files.");
            } catch (IOException e) {
                System.out.println("An error occurred while writing to the files.");
                e.printStackTrace();
            }
        }
        
        private void saveAndExit() {
            saveDataToFile("Book.txt"); 
            saveDataToFile("Books.txt");
            SwingUtilities.invokeLater(() -> {
                this.dispose();
                Admin.f.setVisible(true); 
            });
        }
        
        private void saveDataToFile(String filename) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
                List<Vector<Object>> dataRows = new ArrayList<>();
                for (int row = 0; row < tableModel.getRowCount(); row++) {
                    Vector<Object> rowData = new Vector<>();
                    for (int col = 0; col < tableModel.getColumnCount(); col++) {
                        Object cellValue = tableModel.getValueAt(row, col);
                        rowData.add(cellValue != null ? cellValue.toString() : "");
                    }
                    dataRows.add(rowData);
                }
        
                dataRows.sort(Comparator.comparing(row -> Integer.parseInt(row.get(0).toString())));
        
                for (Vector<Object> rowData : dataRows) {
                    StringBuilder line = new StringBuilder();
                    for (int col = 0; col < rowData.size(); col++) {
                        line.append(rowData.get(col));
                        if (col < rowData.size() - 1) {
                            line.append(",");
                        }
                    }
                    line.append("\n");
                    writer.write(line.toString());
                }
        
                writer.flush();
                System.out.println("Successfully wrote changes to the " + filename + " file.");
            } catch (IOException ex) {
                System.out.println("An error occurred while writing changes to the " + filename + " file.");
                ex.printStackTrace();
            }
        }
        
    
      private void promptUserForDeletion() {
            int[] selectedRows = table.getSelectedRows();
        
            if (selectedRows.length > 0) {
                List<String> booksToRemove = new ArrayList<>();
        
                for (int selectedRow : selectedRows) {
                    int modelRow = table.convertRowIndexToModel(selectedRow); // Convert to model index
                    String bookCode = (String) tableModel.getValueAt(modelRow, 0);
                    booksToRemove.add(bookCode);
                }
        
                for (String bookCode : booksToRemove) {
                    int modelRow = findRowByBookCode(bookCode); // Find the model row
                    if (modelRow != -1) {
                        tableModel.removeRow(modelRow);
                    }
                }
        
                bookCodes.removeAll(booksToRemove);
        
                saveDataToFile("Book.txt");
                saveDataToFile("Books.txt");
        
                JOptionPane.showMessageDialog(this, "Books deleted successfully.");
            } else {
                JOptionPane.showMessageDialog(this, "Please select a book to delete.");
            }
        }
        
        private int findRowByBookCode(String bookCode) {
            for (int row = 0; row < tableModel.getRowCount(); row++) {
                if (tableModel.getValueAt(row, 0).equals(bookCode)) {
                    return row;
                }
            }
            return -1;
        }
        
        private void promptUserForPicture() {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileFilter(new FileNameExtensionFilter("Image files", "jpg", "jpeg", "png", "gif"));
            int result = fileChooser.showOpenDialog(this);
        
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                String imagePath = makeImagePathRelative(selectedFile);
        
                int selectedRow = table.getSelectedRow();
        
                if (selectedRow != -1) {
                    tableModel.setValueAt(imagePath, selectedRow, 3);
                } else {
                    JOptionPane.showMessageDialog(this, "Please select a book to add a picture.");
                }
            }
        }

        private String makeImagePathRelative(File selectedFile) {
            if (selectedFile == null) {
                return "";
            }
            String classpath = getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
            String folderPath = new File(classpath).getParent();
            String imagePath = new File(folderPath).toURI().relativize(selectedFile.toURI()).getPath();
            return imagePath;
        }

        private void displayEnteredContent() {
            try (BufferedReader reader = new BufferedReader(new FileReader("Books.txt"))) {
                Vector<Vector<Object>> dataVector = new Vector<>();
                final int[] maxImageHeight = {0};
    
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    Vector<Object> row = new Vector<>();
                    row.add(parts[0].trim());
                    row.add(parts[1].trim());
                    row.add(parts[2].trim());
    
                    if (parts.length > 3) {
                        String imagePath = parts[3].trim();
                        row.add(imagePath);
    
                        ImageIcon imageIcon = loadImageIcon(imagePath);
                        int imageHeight = (imageIcon != null) ? imageIcon.getIconHeight() : 0;
    
                        if (imageHeight > maxImageHeight[0]) {
                            maxImageHeight[0] = imageHeight;
                        }
                    } else {
                        row.add("");
                    }
    
                    dataVector.add(row);
                }
    
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        tableModel.setRowCount(0);
    
                        for (Vector<Object> row : dataVector) {
                            tableModel.addRow(row);
                        }
    
                        int pictureColumnIndex = 3;
                        TableColumn pictureColumn = table.getColumnModel().getColumn(pictureColumnIndex);
                        pictureColumn.setPreferredWidth(maxImageHeight[0]);
                        int newHeight = Math.max(table.getRowHeight(), maxImageHeight[0]);
                        table.setRowHeight(newHeight);
                    }
                });
                
    
            } catch (IOException e) {
                System.out.println("An error occurred while reading the file.");
                e.printStackTrace();
            }
        }
    
        private ImageIcon loadImageIcon(String path) {
            ImageIcon imageIcon = new ImageIcon(path);
            if (imageIcon.getIconWidth() == -1) {
                return null;
            }
            return imageIcon;
        }
    
        class ImageRenderer extends DefaultTableCellRenderer {
            JLabel label = new JLabel();
    
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                label.setIcon(new ImageIcon(value.toString()));
                label.setHorizontalAlignment(SwingConstants.CENTER);
                label.setVerticalAlignment(SwingConstants.CENTER);
                
                return label;
            }
        }

        

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new EditLibraryContent());
    }
}
    
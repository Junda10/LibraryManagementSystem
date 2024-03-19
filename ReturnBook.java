import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Vector;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class ReturnBook extends JFrame {
    private NonEditableTableModel tableModel;
    private JTable table;
    private JButton returnBookButton;
    private JTextField searchField;
    private JButton searchButton;
    private int memberId;

    public ReturnBook(int memberId) {
        this.memberId = memberId;

        setTitle("Return a Book");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        Font largerFont = new Font("Arial", Font.PLAIN, 20);
        UIManager.put("Table.font", new FontUIResource(largerFont));

        tableModel = new NonEditableTableModel();
        tableModel.addColumn("Member ID");
        tableModel.addColumn("Book Code");
        tableModel.addColumn("Book Name");
        tableModel.addColumn("Author Name");
        tableModel.addColumn("Picture");
        tableModel.addColumn("Borrow Book Date");
        tableModel.addColumn("Book Return Due");

        table = new JTable(tableModel);
        table.setFont(new Font("Arial", Font.ITALIC, 20));
        table.setFocusable(false);
        table.setRowSelectionAllowed(true);
        table.setColumnSelectionAllowed(false);

        JScrollPane scrollPane = new JScrollPane(table);
        setLayout(new BorderLayout());

        add(scrollPane, BorderLayout.CENTER);

        returnBookButton = new JButton("Return Book");
        Font largerButtonFont = new Font("Arial", Font.PLAIN, 25);
        returnBookButton.setFont(largerButtonFont);

        Dimension largerButtonSize = new Dimension(100, 40);
        returnBookButton.setPreferredSize(largerButtonSize);
        returnBookButton.addActionListener(e -> promptUserForReturn());

        JButton exitButton = new JButton("Exit");
        Font largerButtonFont1 = new Font("Arial", Font.PLAIN, 25);
        exitButton.setFont(largerButtonFont1);

        Dimension largerButtonSize1 = new Dimension(100, 40);
        exitButton.setPreferredSize(largerButtonSize1);
        exitButton.addActionListener(e -> saveAndExit());

        searchField = new JTextField();
        searchButton = new JButton("Search");
        Font largerButtonFont2 = new Font("Arial", Font.PLAIN, 25);
        searchButton.setFont(largerButtonFont2);

        Dimension largerButtonSize2 = new Dimension(100, 40);
        searchButton.setPreferredSize(largerButtonSize2);
        searchButton.addActionListener(e -> searchBook());

        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 2));
        Font labelFont = new Font("Arial", Font.PLAIN, 25);

        JLabel searchLabel = new JLabel("Search Book Name Here:");
        searchLabel.setFont(labelFont);

        Font buttonFont = new Font("Arial", Font.PLAIN, 25);

        searchField.setFont(buttonFont);

        Dimension searchFieldSize = new Dimension(200, 40);
        searchField.setPreferredSize(searchFieldSize);

        JButton searchButton = new JButton("Search");
        searchButton.setFont(buttonFont);
        searchButton.addActionListener(e -> searchBook());

        inputPanel.add(searchLabel);
        inputPanel.add(searchField);
        inputPanel.add(searchButton);

        JPanel buttonPanel = new JPanel(new GridLayout(0, 2, 10, 2));
        buttonPanel.add(returnBookButton);
        buttonPanel.add(exitButton);

        add(inputPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.SOUTH);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Arial", Font.BOLD, 22));
        header.setBackground(Color.BLUE);
        header.setForeground(Color.WHITE);

        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer();
        cellRenderer.setFont(new Font("Arial", Font.PLAIN, 25));
        table.setDefaultRenderer(Object.class, cellRenderer);

        displayAvailableBooks();

        setVisible(true);
    }

    private void displayAvailableBooks() {
        try (BufferedReader reader = new BufferedReader(new FileReader(memberId + ".txt"))) {
            Vector<Vector<Object>> dataVector = new Vector<>();
            final int[] maxImageHeight = {0};

            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                Vector<Object> row = new Vector<>();
                row.add(memberId);
                row.add(parts[0].trim());
                row.add(parts[1].trim());
                row.add(parts[2].trim());

                if (parts.length > 3) {
                    String imagePath = parts[3].trim();
                    ImageIcon imageIcon = loadImageIcon(imagePath);

                    if (imageIcon != null) {
                        row.add(imageIcon);
                        int imageHeight = imageIcon.getIconHeight();

                        if (imageHeight > maxImageHeight[0]) {
                            maxImageHeight[0] = imageHeight;
                        }
                    } else {
                        row.add(null);
                    }
                } else {
                    row.add(null);
                }

                if (parts.length > 6) {
                    row.add(parts[5].trim());
                    row.add(parts[6].trim());
                } else {
                    row.add(null);
                    row.add(null);
                }

                dataVector.add(row);
            }

            SwingUtilities.invokeLater(() -> {
                tableModel.setRowCount(0);

                for (Vector<Object> row : dataVector) {
                    tableModel.addRow(row);
                }

                int pictureColumnIndex = 4;
                int textColumnIndex = 3;
                TableColumn textColumn = table.getColumnModel().getColumn(textColumnIndex);
                textColumn.setCellRenderer(new TextRenderer());
                TableColumn pictureColumn = table.getColumnModel().getColumn(pictureColumnIndex);
                pictureColumn.setCellRenderer(new ImageRenderer());
                pictureColumn.setPreferredWidth(maxImageHeight[0]);
                int newHeight = Math.max(table.getRowHeight(), maxImageHeight[0]);
                table.setRowHeight(newHeight);
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

    class TextRenderer extends DefaultTableCellRenderer {
        public TextRenderer() {
            setHorizontalAlignment(SwingConstants.CENTER);
            setVerticalAlignment(SwingConstants.CENTER);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            Font largerFont = new Font("Arial", Font.PLAIN, 25);
            setFont(largerFont);
            return this;
        }
    }

    class ImageRenderer extends DefaultTableCellRenderer {
        JLabel label = new JLabel();

        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            if (value instanceof ImageIcon) {
                ImageIcon icon = (ImageIcon) value;
                int cellWidth = table.getColumnModel().getColumn(column).getWidth();
                int cellHeight = table.getRowHeight(row);

                int imageWidth = icon.getIconWidth();
                int imageHeight = icon.getIconHeight();

                double widthRatio = (double) cellWidth / imageWidth;
                double heightRatio = (double) cellHeight / imageHeight;
                double scale = Math.min(widthRatio, heightRatio);

                int newWidth = (int) (scale * imageWidth);
                int newHeight = (int) (scale * imageHeight);

                Image scaledImage = icon.getImage().getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
                label.setIcon(new ImageIcon(scaledImage));
            } else {
                label.setIcon(null);
            }
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setVerticalAlignment(SwingConstants.CENTER);
            return label;
        }
    }

    private void saveAndExit() {
        SwingUtilities.invokeLater(() -> {
            this.dispose();
        });
    }

    private void promptUserForReturn() {
        int selectedRow = table.getSelectedRow();

        if (selectedRow != -1) {
            String bookInfo = fetchBookInfoFromTableModel(selectedRow);

            String bookCode = tableModel.getValueAt(selectedRow, 1).toString();
            String bookName = tableModel.getValueAt(selectedRow, 2).toString();
            String authorName = tableModel.getValueAt(selectedRow, 3).toString();
            String imagePath = (tableModel.getValueAt(selectedRow, 4) != null)
                    ? ((ImageIcon) tableModel.getValueAt(selectedRow, 4)).getDescription()
                    : "";

            updateBookFile(bookCode, bookName, authorName, imagePath);

            updateFilesOnBookReturn(selectedRow);
            JOptionPane.showMessageDialog(this, "Book Returned: " + bookInfo);
            tableModel.removeRow(selectedRow);
        } else {
            JOptionPane.showMessageDialog(this, "Please select a book to return.");
        }
    }

    private void updateFilesOnBookReturn(int selectedRow) {
        try {
            String memberId = tableModel.getValueAt(selectedRow, 0).toString();
            String bookCode = tableModel.getValueAt(selectedRow, 1).toString();
    
            updateMemberFile(selectedRow);
            updateTotalBookBorrowFile(memberId, bookCode);
    
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateMemberFile(int selectedRow) throws IOException {
        String memberFileName = memberId + ".txt";
        File memberFile = new File(memberFileName);

        if (memberFile.exists()) {
            List<String> lines = Files.readAllLines(memberFile.toPath(), StandardCharsets.UTF_8);

            lines.remove(selectedRow);

            Files.write(memberFile.toPath(), lines, StandardCharsets.UTF_8);
        }
    }

    private void updateTotalBookBorrowFile(String memberId, String bookCode) {
        try {
            File totalBookBorrowFile = new File("BorrowedBook.txt");
    
            if (totalBookBorrowFile.exists()) {
                List<String> lines = Files.readAllLines(totalBookBorrowFile.toPath(), StandardCharsets.UTF_8);
    
                List<String> updatedLines = new ArrayList<>();
    
                for (String currentLine : lines) {
                    String[] currentParts = currentLine.split(",");
    
                    String currentMemberId = currentParts[0].trim();
                    String currentBookCode = currentParts[1].trim();
    
                    String currentUniqueIdentifier = currentMemberId + "_" + currentBookCode;
    
                    if (currentUniqueIdentifier.equals(memberId + "_" + bookCode)) {
                        continue;
                    }
    
                    updatedLines.add(currentLine);
                }
    
                Files.write(totalBookBorrowFile.toPath(), updatedLines, StandardCharsets.UTF_8);
            } else {
                System.out.println("BorrowedBook.txt does not exist.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void updateBookFile(String bookCode, String bookName, String authorName, String imagePath) {
        try (FileWriter writer = new FileWriter("Book.txt", true)) {
            writer.write(bookCode + "," + bookName + "," + authorName + "," + imagePath + System.lineSeparator());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void searchBook() {
        String searchTerm = searchField.getText().trim().toLowerCase();
        
        Vector<Vector<Object>> dataVector = new Vector<>();
    
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String bookName = tableModel.getValueAt(i, 2).toString().trim().toLowerCase();
    
            if (bookName.contains(searchTerm)) {
                Vector<Object> row = new Vector<>();
                row.add(tableModel.getValueAt(i, 0));
                row.add(tableModel.getValueAt(i, 1));
                row.add(tableModel.getValueAt(i, 2));
                row.add(tableModel.getValueAt(i, 3));
                row.add(tableModel.getValueAt(i, 4));
                row.add(tableModel.getValueAt(i, 5));
                row.add(tableModel.getValueAt(i, 6));
                dataVector.add(row);
            }
        }
    
        SwingUtilities.invokeLater(() -> {
            tableModel.setRowCount(0);
    
            for (Vector<Object> row : dataVector) {
                tableModel.addRow(row);
            }
        });
    }
    
    private String fetchBookInfoFromTableModel(int row) {
        String memberID = tableModel.getValueAt(row, 0).toString();
        String bookCode = tableModel.getValueAt(row, 1).toString();
        String bookName = tableModel.getValueAt(row, 2).toString();
        String authorName = tableModel.getValueAt(row, 3).toString();
        return "Member ID: " + memberID + ", Book Code: " + bookCode + ", Book Name: " + bookName + " by " + authorName;
    }

    private class NonEditableTableModel extends DefaultTableModel {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            if (columnIndex == 1 || columnIndex == 2) {
                return String.class;
            } else {
                return super.getColumnClass(columnIndex);
            }
        }
    }

    private static boolean validateMemberId(int memberId) {
        try (Scanner scanner = new Scanner(new File("BorrowedBook.txt"))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.trim().split(",");
                if (parts.length > 0 && Integer.parseInt(parts[0].trim()) == memberId) {
                    return true;
                }
            }
        } catch (FileNotFoundException | NumberFormatException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void main(String[] args) {
        String memberIdInput = JOptionPane.showInputDialog("Enter Member ID:");

        try {
            int memberId = Integer.parseInt(memberIdInput);

            if (validateMemberId(memberId)) {
                new ReturnBook(memberId);
            } else {
                JOptionPane.showMessageDialog(null, "Invalid Member ID", "Access Denied", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Invalid Member ID format", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

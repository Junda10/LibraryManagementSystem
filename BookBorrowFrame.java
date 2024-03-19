import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

public class BookBorrowFrame extends JFrame {
    private NonEditableTableModel tableModel;
    private JTable table; 
    private JButton registerBorrowButton;
    private JButton searchButton; 
    private JTextField searchField; 
    private Set<String> borrowedBooks;
    private JLabel titleLabel; 
    private JLabel searchT;
    private JButton sae;
    private int memberId; 

    public BookBorrowFrame(int memberId) {
        this.memberId = memberId;

        setTitle("Borrow a book");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        Font largerFont = new Font("Arial", Font.PLAIN, 23);
        UIManager.put("Table.font", new FontUIResource(largerFont));

        titleLabel = new JLabel("Welcome to Library Management System!!");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        searchT = new JLabel("Search Book Name Here:");
        searchT.setFont(new Font("Arial", Font.PLAIN, 16));

        tableModel = new NonEditableTableModel();
        tableModel.addColumn("Book Code");
        tableModel.addColumn("Book Name");
        tableModel.addColumn("Author Name");
        tableModel.addColumn("Picture");

        table = new JTable(tableModel);
        table.setFocusable(false);
        table.setRowSelectionAllowed(true);
        table.setColumnSelectionAllowed(false);
        JScrollPane scrollPane = new JScrollPane(table);
        setLayout(new BorderLayout());

        add(titleLabel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        displayAvailableBooks();

        registerBorrowButton = new JButton("Register Borrow");
        registerBorrowButton.addActionListener(e -> promptUserForBorrowRegistration());
        registerBorrowButton.setFont(new Font("Arial", Font.PLAIN, 25));
        searchButton = new JButton("Search");
        searchField = new JTextField();
        searchButton.addActionListener(e -> searchBook());

        sae = new JButton("Save and Exit");
        sae.addActionListener(e -> saveAndExit());
        sae.setFont(new Font("Arial", Font.PLAIN, 25));

        JPanel inputPanel = new JPanel(new GridLayout(0, 1, 2, 2));
        inputPanel.add(searchT);
        inputPanel.add(searchField);
        inputPanel.add(searchButton);

        JPanel inputPanel1 = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 2));
        inputPanel1.add(registerBorrowButton);
        inputPanel1.add(sae);

        add(inputPanel, BorderLayout.NORTH);
        add(inputPanel1, BorderLayout.SOUTH);

        borrowedBooks = new HashSet<>();

        JLabel memberIdLabel = new JLabel("Member ID: " + memberId);
        memberIdLabel.setFont(new Font("Arial", Font.BOLD, 30));
        memberIdLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(memberIdLabel, BorderLayout.NORTH);

        for (int i = 0; i < tableModel.getColumnCount(); i++) {
            TableColumn column = table.getColumnModel().getColumn(i);
            column.setCellRenderer(new TextRenderer());
        }

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Arial", Font.BOLD, 30));
        header.setBackground(Color.BLUE);
        header.setForeground(Color.WHITE);
        setVisible(true);
    }

    private void displayAvailableBooks() {
        try (BufferedReader reader = new BufferedReader(new FileReader("Book.txt"))) {
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
                    int ImageColumnIndex = 3;
                    TableColumn textColumn = table.getColumnModel().getColumn(ImageColumnIndex);
                    textColumn.setCellRenderer(new TextRenderer());
                    TableColumn pictureColumn = table.getColumnModel().getColumn(pictureColumnIndex);
                    pictureColumn.setCellRenderer(new ImageRenderer()); 
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

    private void promptUserForBorrowRegistration() {
        int selectedRow = table.getSelectedRow();

        if (selectedRow != -1) {
            String bookCode = tableModel.getValueAt(selectedRow, 0).toString();

            if (!borrowedBooks.contains(bookCode)) {
                String bookInfo = fetchBookInfoFromTableModel(selectedRow);

                if (bookInfo != null) {
                    JPanel imagePanel = createImagePanel(selectedRow);

                    int choice = JOptionPane.showConfirmDialog(this, imagePanel, "Confirm Borrow", JOptionPane.YES_NO_OPTION);

                    if (choice == JOptionPane.YES_OPTION) {
                        borrowedBooks.add(bookCode);
                        String borrowedBookInfo = fetchBookInfoFromTableModel(selectedRow);
                        String imagePath = tableModel.getValueAt(selectedRow, 3) != null
                                ? tableModel.getValueAt(selectedRow, 3).toString() : "";

                        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        Calendar cal = Calendar.getInstance();
                        String startDate = dateFormat.format(cal.getTime());

                        cal.add(Calendar.DAY_OF_YEAR, 7);
                        String endDate = dateFormat.format(cal.getTime());
                        
                        storeTotalBorrowedBookInfo( memberId,  bookInfo, imagePath,startDate,endDate);
                        storeBorrowedBookInfo(borrowedBookInfo, imagePath, startDate, endDate);
                        deleteBookInfo(selectedRow); 
                        JOptionPane.showMessageDialog(this, "Book Borrowed Successfully!");
                    } else {
                        JOptionPane.showMessageDialog(this, "Borrow Canceled.");
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Book not found!");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Book already borrowed!");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a book to borrow.");
        }
    }

    private JPanel createImagePanel(int selectedRow) {
        String imagePath = tableModel.getValueAt(selectedRow, 3) != null
                ? tableModel.getValueAt(selectedRow, 3).toString() : "";

        JPanel imagePanel = new JPanel(new BorderLayout());

        if (!imagePath.isEmpty()) {
            ImageIcon imageIcon = loadImageIcon(imagePath);
            if (imageIcon != null) {
                JLabel imageLabel = new JLabel(imageIcon);
                imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
                imagePanel.add(imageLabel, BorderLayout.CENTER);
            }
        }

        return imagePanel;
    }

    private String fetchBookInfoFromTableModel(int row) {
        String bookCode = tableModel.getValueAt(row, 0).toString();
        String bookName = tableModel.getValueAt(row, 1).toString();
        String authorName = tableModel.getValueAt(row, 2).toString();
        String imagePath = tableModel.getValueAt(row, 3) != null ? tableModel.getValueAt(row, 3).toString() : "";
        
        return bookCode + "," + bookName + "," + authorName + "," + imagePath;
    }

    private void storeBorrowedBookInfo(String bookInfo, String imagePath, String startDate, String endDate) {
        try (FileWriter writer = new FileWriter(memberId +".txt", true)) {
            writer.write(bookInfo + ", Image Path: " + imagePath + ", " + startDate + ", " + endDate + System.lineSeparator());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void storeTotalBorrowedBookInfo(int memberId, String bookInfo, String imagePath, String startDate, String endDate) {
        try (FileWriter writer = new FileWriter("BorrowedBook.txt", true)) {
            writer.write(memberId +", "+ bookInfo + ", Image Path: " + imagePath + ", " + startDate + ", " + endDate + System.lineSeparator());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void deleteBookInfo(int row) {
        tableModel.removeRow(row);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("Book.txt"))) {
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                StringBuilder line = new StringBuilder();
                for (int j = 0; j < tableModel.getColumnCount(); j++) {
                    line.append(tableModel.getValueAt(i, j));
                    if (j < tableModel.getColumnCount() - 1) {
                        line.append(",");
                    }
                }
                writer.write(line.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void saveAndExit() {
        SwingUtilities.invokeLater(() -> {
            this.dispose();
        });
        new User(memberId);
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

    private void searchBook() {
        String searchTerm = searchField.getText().trim().toLowerCase();

        try (BufferedReader reader = new BufferedReader(new FileReader("Book.txt"))) {
            Vector<Vector<Object>> dataVector = new Vector<>();
            final int[] maxImageHeight = {0};

            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                String bookName = parts[1].trim().toLowerCase();

                if (bookName.contains(searchTerm)) {
                    Vector<Object> row = new Vector<>();
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

                    dataVector.add(row);
                }
            }

            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    tableModel.setRowCount(0);
    
                    for (int i = 0; i < tableModel.getColumnCount(); i++) {
                        TableColumn column = table.getColumnModel().getColumn(i);
                        if (i == 1 || i == 2) {
                            column.setCellRenderer(new TextRenderer());
                        } else if (i == 3) {
                            column.setCellRenderer(new ImageRenderer());
                            column.setPreferredWidth(maxImageHeight[0]);
                            int newHeight = Math.max(table.getRowHeight(), maxImageHeight[0]);
                            table.setRowHeight(newHeight);
                        }
                    }
                    
                    for (Vector<Object> row : dataVector) {
                        tableModel.addRow(row);
                    }
                }
            });
    
        } catch (IOException e) {
            System.out.println("An error occurred while reading the file.");
            e.printStackTrace();
        }
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

    public static void main(String[] args) {
        String memberIdString = JOptionPane.showInputDialog("Enter Member ID:");
        
        try {
            int memberId = Integer.parseInt(memberIdString);
            new BookBorrowFrame(memberId);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Invalid Member ID. Please enter a number.");
        }
    }
}

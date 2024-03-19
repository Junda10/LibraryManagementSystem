import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

public class DisplayBorrowedBooks extends JFrame {
    private NonEditableTableModel tableModel;
    private JTable table;
    private JButton sae;
    private int memberId;

    public DisplayBorrowedBooks(int memberId) {
        this.memberId = memberId;
        setTitle("Borrowed Books");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 400);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        tableModel = new NonEditableTableModel();
        tableModel.addColumn("Member ID");
        tableModel.addColumn("Book Code");
        tableModel.addColumn("Book Name");
        tableModel.addColumn("Author Name");
        tableModel.addColumn("Picture");
        tableModel.addColumn("Borrow Book Date");
        tableModel.addColumn("Book Return Due");

        table = new JTable(tableModel);
        table.setFocusable(false);
        table.setRowSelectionAllowed(true);
        table.setColumnSelectionAllowed(false);
        JScrollPane scrollPane = new JScrollPane(table);
        setLayout(new BorderLayout());

        add(scrollPane, BorderLayout.CENTER);

        displayBorrowedBooks();

        JButton totalBorrowsButton = new JButton("Show Total Borrows");
        totalBorrowsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showTotalBorrows();
            }
        });
        totalBorrowsButton.setFont(new Font("Arial", Font.BOLD, 20)); 
        add(totalBorrowsButton, BorderLayout.NORTH);

        sae = new JButton("Save and Exit");
        sae.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveAndExit();
            }
        });
        sae.setFont(new Font("Arial", Font.BOLD, 20));
        add(sae, BorderLayout.SOUTH);
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Arial", Font.BOLD, 25)); 
        header.setBackground(Color.BLUE);
        header.setForeground(Color.WHITE);
        setVisible(true);
    }

    private void showTotalBorrows() {
        int totalBorrows = tableModel.getRowCount();
        JOptionPane.showMessageDialog(this, "Total Book Borrows: " + totalBorrows, "Total Borrows", JOptionPane.INFORMATION_MESSAGE);
    }

    private void displayBorrowedBooks() {
        try (BufferedReader reader = new BufferedReader(new FileReader(memberId + ".txt"))) {
            Vector<Vector<Object>> dataVector = new Vector<>();
            final int[] maxImageHeight = {0};

            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");

                if (parts.length >= 5) {
                    Vector<Object> row = new Vector<>();
                    row.add(memberId); 

                    row.add(parts[0].trim());
                    row.add(parts[1].trim());
                    row.add(parts[2].trim());

                    if (parts.length > 5) {
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

                    row.add(parts[parts.length - 2].trim());
                    row.add(parts[parts.length - 1].trim());

                    dataVector.add(row);
                }
            }

            SwingUtilities.invokeLater(() -> {
                tableModel.setRowCount(0);

                for (Vector<Object> row : dataVector) {
                    tableModel.addRow(row);
                }

                for (int i = 0; i < tableModel.getColumnCount(); i++) {
                    TableColumn column = table.getColumnModel().getColumn(i);
                    if (i == 0 || i == 1 || i == 2 || i == 5 || i == 6) {
                        column.setCellRenderer(new TextRenderer());
                    } else if (i == 4) {
                        column.setCellRenderer(new ImageRenderer());
                        column.setPreferredWidth(maxImageHeight[0]);
                        int newHeight = Math.max(table.getRowHeight(), maxImageHeight[0]);
                        table.setRowHeight(newHeight);
                    }
                }
            });

        } catch (IOException e) {
            System.out.println("An error occurred while reading the file.");
            e.printStackTrace();
        }
    }
    
    private void saveAndExit() {
        SwingUtilities.invokeLater(() -> {
            this.dispose();
            User.f.setVisible(true); 
        });
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

    class TextRenderer extends DefaultTableCellRenderer {
        public TextRenderer() {
            setHorizontalAlignment(SwingConstants.CENTER);
            setVerticalAlignment(SwingConstants.CENTER);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            Font largerFont = new Font("Arial", Font.PLAIN, 20);
            setFont(largerFont);
            return this;
        }
    }

    private class NonEditableTableModel extends DefaultTableModel {
        @Override
        public void addColumn(Object columnName) {
            super.addColumn(columnName);
        }

        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    }
    

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DisplayBorrowedBooks(1)); 
    }
}

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;

import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

public class DisplayBorrowed extends JFrame {
    private JTable table;
    private DefaultTableModel tableModel;

    public DisplayBorrowed() {
        setTitle("Display Borrowed Books");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        tableModel = new DefaultTableModel();
        tableModel.addColumn("Member ID");
        tableModel.addColumn("Book Code");
        tableModel.addColumn("Book Name");
        tableModel.addColumn("Author Name");
        tableModel.addColumn("Image");
        tableModel.addColumn("Borrow Book Date");
        tableModel.addColumn("Book Return Due");

        table = new JTable(tableModel);
        table.setFont(new Font("Arial", Font.ITALIC, 20));
        table.getColumnModel().getColumn(4).setCellRenderer(new ImageRenderer());

        JScrollPane scrollPane = new JScrollPane(table);

        displayBorrowedBooks();

        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);

        JButton exitButton = new JButton("Exit");
        Font largerButtonFont = new Font("Arial", Font.PLAIN, 23);
        exitButton.setFont(largerButtonFont);

        Dimension largerButtonSize = new Dimension(300, 40);
        exitButton.setPreferredSize(largerButtonSize);

        exitButton.addActionListener(e -> dispose());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(exitButton);

        add(buttonPanel, BorderLayout.SOUTH);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Arial", Font.BOLD, 22));

        header.setBackground(Color.BLUE);
        header.setForeground(Color.WHITE); 

        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer();
        cellRenderer.setFont(new Font("Arial", Font.PLAIN, 25));
        table.setDefaultRenderer(Object.class, cellRenderer);

        setVisible(true);
    }

    private void displayBorrowedBooks() {
        try (BufferedReader reader = new BufferedReader(new FileReader("BorrowedBook.txt"))) {
            Vector<Vector<Object>> dataVector = new Vector<>();
            final int[] maxImageHeight = {0};

            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");

                if (parts.length >= 8) {
                    Vector<Object> row = new Vector<>();
                    row.add(parts[0].trim());
                    row.add(parts[1].trim());
                    row.add(parts[2].trim());
                    row.add(parts[3].trim()); 

                    String imagePath = parts[4].trim();
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

            Font largerFont = new Font("Arial", Font.PLAIN, 25);
            setFont(largerFont);
            return this;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(DisplayBorrowed::new);
    }
}

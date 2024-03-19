import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.Vector;

public class Member extends JFrame {
    private JTextField searchField;
    private DefaultTableModel tableModel;
    private JTable table;
    private JButton addMemberButton;
    private JButton deleteMemberButton;
    private JButton sae;

    public Member() {
        setTitle("Member Management");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel searchLabel = new JLabel("Use Name to Search Member:");
        searchLabel.setFont(new Font("Arial", Font.PLAIN, 26));
        searchField = new JTextField(50);
        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        add(searchPanel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tableModel.addColumn("Member ID");
        tableModel.addColumn("Member Name");
        tableModel.addColumn("Contact Number +60");
        tableModel.addColumn("Faculty");

        table = new JTable(tableModel);
        table.setFont(new Font("Arial", Font.ITALIC, 22));
        table.setFocusable(false);
        table.setRowSelectionAllowed(true);
        table.setColumnSelectionAllowed(false);
        JScrollPane scrollPane = new JScrollPane(table);
        setLayout(new BorderLayout());

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Arial", Font.BOLD, 24));
        header.setBackground(Color.BLUE);
        header.setForeground(Color.WHITE);
        DefaultTableCellRenderer renderer = (DefaultTableCellRenderer) header.getDefaultRenderer();
        renderer.setHorizontalAlignment(SwingConstants.CENTER);

        table.setRowHeight(100);
        setColumnWidths();

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < tableModel.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        add(scrollPane, BorderLayout.CENTER);

        displayMembers();

        addMemberButton = new JButton("Add Member");
        addMemberButton.setFont(new Font("Arial", Font.PLAIN, 22));
        addMemberButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addMember();
            }
        });

        deleteMemberButton = new JButton("Delete Member");
        deleteMemberButton.setFont(new Font("Arial", Font.PLAIN, 22));
        deleteMemberButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteMember();
            }
        });

        JButton searchButton = new JButton("Search");
        searchButton.setFont(new Font("Arial", Font.PLAIN, 22));
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchMember();
            }
        });

        sae = new JButton("Save and Exit");
        sae.setFont(new Font("Arial", Font.PLAIN, 22));
        sae.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveAndExit();
            }
        });

        JPanel inputPanel = new JPanel(new BorderLayout());

        JPanel searchButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        searchButtonPanel.add(searchButton);

        JPanel addDeleteButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        addDeleteButtonPanel.add(addMemberButton);
        addDeleteButtonPanel.add(deleteMemberButton);

        inputPanel.add(searchPanel, BorderLayout.WEST);
        inputPanel.add(searchButtonPanel, BorderLayout.CENTER);
        inputPanel.add(addDeleteButtonPanel, BorderLayout.SOUTH);

        Dimension buttonSize = new Dimension(300, 30);
        searchButton.setPreferredSize(buttonSize);
        addMemberButton.setPreferredSize(buttonSize);
        deleteMemberButton.setPreferredSize(buttonSize);
        sae.setPreferredSize(buttonSize);

        Dimension centerPanelSize = new Dimension(800, 100);
        searchButtonPanel.setPreferredSize(centerPanelSize);

        add(inputPanel, BorderLayout.NORTH);

        JPanel inputPanel1 = new JPanel(new GridLayout(0, 1, 2, 2));
        inputPanel1.add(sae);
        add(inputPanel1, BorderLayout.SOUTH);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Member());
    }

    private void setColumnWidths() {
        table.getColumnModel().getColumn(0).setPreferredWidth(150);
        table.getColumnModel().getColumn(1).setPreferredWidth(300);
        table.getColumnModel().getColumn(2).setPreferredWidth(200);
        table.getColumnModel().getColumn(3).setPreferredWidth(200);
    }

    private void addMember() {
        String memberIDStr = JOptionPane.showInputDialog(this, "Enter Your ID:");
        String memberNameStr = JOptionPane.showInputDialog(this, "Enter Your Name:");
        String contactNumberStr = JOptionPane.showInputDialog(this, "Enter Your Contact Number +60:");
        String faculty = JOptionPane.showInputDialog(this, "Enter Your Faculty:");
    
        try {
            int memberID = Integer.parseInt(memberIDStr);
            if (memberID < 0) {
                JOptionPane.showMessageDialog(this, "Member ID must be a positive integer.");
                return;
            }
    
            if (isMemberIDExists(memberID)) {
                JOptionPane.showMessageDialog(this, "Member ID already exists. Please use a different ID.");
                return;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid Member ID. Please enter a valid integer.");
            return;
        }
    
        if (contactNumberStr.length() < 9) {
            JOptionPane.showMessageDialog(this, "Contact number must have at least 9 digits.");
            return;
        }
    
        String memberName = memberNameStr.isEmpty() ? "" : memberNameStr;
    
        try {
            long contactNumber = Long.parseLong(contactNumberStr);
            if (contactNumber < 0) {
                JOptionPane.showMessageDialog(this, "Contact number must be a positive integer.");
                return;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid contact number. Please enter a valid integer.");
            return;
        }
    
        saveMemberToFile(memberIDStr, memberName, contactNumberStr, faculty);
    
        searchField.setText("");
        displayMembers();
    }

    private boolean isMemberIDExists(int memberID) {
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            int existingID = Integer.parseInt((String) tableModel.getValueAt(i, 0));
            if (existingID == memberID) {
                return true;
            }
        }
        return false;
    }

    private void saveMemberToFile(String memberIDStr, String memberName, String contactNumberStr, String faculty) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("Members.txt", true))) {
            StringBuilder line = new StringBuilder();
            line.append(memberIDStr).append(",");
            line.append(memberName).append(",");
            line.append(contactNumberStr).append(",");
            line.append(faculty).append("\n");

            writer.write(line.toString());
            writer.flush();
            System.out.println("Successfully wrote to the Members file.");
        } catch (IOException e) {
            System.out.println("An error occurred while updating the Members file.");
            e.printStackTrace();
        }
    }

    private void searchMember() {
        String searchTerm = searchField.getText().trim().toLowerCase();

        try (BufferedReader reader = new BufferedReader(new FileReader("Members.txt"))) {
            Vector<Vector<Object>> dataVector = new Vector<>();

            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                String memberName = parts[1].trim().toLowerCase();

                if (memberName.contains(searchTerm)) {
                    Vector<Object> row = new Vector<>();
                    row.add(parts[0].trim());
                    row.add(parts[1].trim());
                    row.add(parts[2].trim());
                    row.add(parts[3].trim());
                    dataVector.add(row);
                }
            }

            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    tableModel.setRowCount(0);

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

        private void saveAndExit() {
            saveTableDataToFile();
            SwingUtilities.invokeLater(() -> {
                this.dispose();
                Admin.f.setVisible(true); 
            });
        }

    private void saveTableDataToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("Members.txt"))) {
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                StringBuilder line = new StringBuilder();
                for (int j = 0; j < tableModel.getColumnCount(); j++) {
                    line.append(tableModel.getValueAt(i, j));
                    if (j < tableModel.getColumnCount() - 1) {
                        line.append(",");
                    }
                }
                writer.write(line.toString() + "\n");
            }
            writer.flush();
            System.out.println("Successfully wrote to the Members file.");
        } catch (IOException e) {
            System.out.println("An error occurred while updating the Members file.");
            e.printStackTrace();
        }
    }

    private void deleteMember() {
        int selectedRow = table.getSelectedRow();
    
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a member to delete.");
            return;
        }
    
        int confirmation = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete this member?",
                "Confirmation",
                JOptionPane.YES_NO_OPTION
        );
    
        if (confirmation == JOptionPane.YES_OPTION) {
            tableModel.removeRow(selectedRow);
            Vector<Object> removedRow = dataVector.remove(selectedRow);
            updateMembersFile(removedRow);
            JOptionPane.showMessageDialog(this, "Member deleted successfully.");
        }
    }
    private Vector<Vector<Object>> dataVector = new Vector<>();
    private void displayMembers() {
        try (BufferedReader reader = new BufferedReader(new FileReader("Members.txt"))) {
            dataVector.clear(); 
    
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                Vector<Object> row = new Vector<>();
                row.add(parts[0].trim());
                row.add(parts[1].trim());
                row.add(parts[2].trim());
                row.add(parts[3].trim());
                dataVector.add(row);
            }
    
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    tableModel.setRowCount(0);
    
                    for (Vector<Object> row : dataVector) {
                        tableModel.addRow(row);
                    }
    
                    TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
                    table.setRowSorter(sorter);
                }
            });
    
        } catch (IOException e) {
            System.out.println("An error occurred while reading the file.");
            e.printStackTrace();
        }
    }
    
    private void updateMembersFile(Vector<Object> removedRow) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("Members.txt"))) {
            for (Vector<Object> row : dataVector) {
                StringBuilder line = new StringBuilder();
                for (int j = 0; j < row.size(); j++) {
                    line.append(row.get(j));
                    if (j < row.size() - 1) {
                        line.append(",");
                    }
                }
                writer.write(line.toString() + "\n");
            }
            writer.flush();
            System.out.println("Successfully wrote to the Members file.");
        } catch (IOException e) {
            System.out.println("An error occurred while updating the Members file.");
            e.printStackTrace();
        }
    }
}
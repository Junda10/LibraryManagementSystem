import javax.swing.*;
import java.awt.*;

public class Admin extends JFrame {

    public static JFrame f;

    public Admin() {
        setTitle("Admin Page");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        JPanel customPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(Color.BLACK);              
                 g.fillRect(0, 0, getWidth(), getHeight());
            }
        };

        Font buttonFont = new Font("Arial", Font.PLAIN, 20);

        ImageIcon registerIcon = new ImageIcon("Image\\Admin Page Logo.png");
        Image scaledImage = registerIcon.getImage().getScaledInstance(600, 450, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);
        JLabel registerLabel = new JLabel(scaledIcon);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(5, 0, 5, 0);
        customPanel.add(registerLabel, gbc);

        JLabel titleLabel = new JLabel("Welcome to Admin Page, Please make your choice!");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 25));
        gbc.gridy = 1;
        gbc.insets = new Insets(30, 0, 30, 0);
        customPanel.add(titleLabel, gbc);

        JButton openRegisterBorrowBookFrame = createButton("Book Management", new ImageIcon("Image\\Book Management.png"), buttonFont, 300, 60);
        JButton openMemberButton = createButton("Open Member Management", new ImageIcon("Image\\Open Member Management.png"), buttonFont, 300, 61);
        JButton openDisplayBorrowedButton = createButton("Display All Borrowed Book", new ImageIcon("Image\\Borrowed Book.png"), buttonFont, 300, 65);
        JButton openReturnBookButton = createButton("Return a Book", new ImageIcon("Image\\Return Book.png"), buttonFont, 300, 60);
        JButton exit = createButton("Exit", new ImageIcon("Image\\Exit Admin.png"), buttonFont, 300, 70);
        

        gbc.gridy = 2;
        gbc.insets = new Insets(10, 0, 10, 0);
        customPanel.add(openRegisterBorrowBookFrame, gbc);

        gbc.gridy = 3;
        customPanel.add(openMemberButton, gbc);

        gbc.gridy = 4;
        customPanel.add(openDisplayBorrowedButton, gbc);

        gbc.gridy = 5;
        customPanel.add(openReturnBookButton, gbc);

        gbc.gridy = 6;
        customPanel.add(exit, gbc);

        setContentPane(customPanel);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
    }

    private JButton createButton(String actionCommand, ImageIcon icon, Font font, int width, int height) {
        JButton button = new JButton("", icon);
        button.setHorizontalTextPosition(SwingConstants.CENTER);
        button.setVerticalTextPosition(SwingConstants.BOTTOM);
        button.setActionCommand(actionCommand);
        button.addActionListener(e -> handleButtonClick(e.getActionCommand()));
        button.setFont(font);
        button.setPreferredSize(new Dimension(width, height));
        return button;
    }

    private void handleButtonClick(String actionCommand) {
        switch (actionCommand) {
            case "Open Member Management":
                openMemberManagement();
                break;
            case "Book Management":
                openEditLibraryContent();
                break;
            case "Display All Borrowed Book":
                openDisplayBorrowedBooksFrame();
                break;
            case "Return a Book":
                openReturnBookFrame();
                break;
            case "Exit":
            SwingUtilities.invokeLater(() -> {
                this.dispose();
            });
            new LibraryManagementSystem();
                break;
        }
    }

    private void openEditLibraryContent() {
        EditLibraryContent registerFrame = new EditLibraryContent();
        registerFrame.setVisible(true);
        f.dispose();
    }

    private void openMemberManagement() {
        Member member = new Member();
        member.setVisible(true);
        f.dispose();
    }

    private void openDisplayBorrowedBooksFrame() {
        DisplayBorrowed displayBorrowedBooks = new DisplayBorrowed();
        displayBorrowedBooks.setVisible(true);
        f.dispose();
    }

    private void openReturnBookFrame() {
        SwingUtilities.invokeLater(() -> ReturnBook.main(null));
        f.dispose();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Admin admin = new Admin();
            admin.setVisible(true);
        });
    }
}

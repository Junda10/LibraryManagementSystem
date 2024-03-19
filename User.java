import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class User extends JFrame {
    public static JFrame f;
    private JButton b, b1, b2;
    private JLabel l;
    private static int memberId;

    public User(int memberId) {
        User.memberId = memberId;
        initialize();
    }

    private void initialize() {
        f = new JFrame("User Page");

        l = new JLabel("Welcome To User Page!");
        Font bigFont = new Font(l.getFont().getName(), Font.PLAIN, 40);
        l.setFont(bigFont);
        l.setForeground(Color.WHITE);

        b = createImageButton("Register Borrow Book", "Image\\Register.png", 250, 50);
        b1 = createImageButton("Display Book Borrowed", "Image\\Display.png", 250, 50);
        b2 = createImageButton("Exit", "Image\\Exit.png", 250, 50);

        JPanel p = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 10, 10, 10);

        ImageIcon logoIcon = new ImageIcon("Image\\Admin Page Logo.png");
        JLabel logoLabel = new JLabel(logoIcon);
        logoLabel.setPreferredSize(new Dimension(800, 400));

        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 0, 0);
        logoLabel.setBorder(BorderFactory.createEmptyBorder(-20, 0, 0, 0));
        gbc.anchor = GridBagConstraints.NORTH;
        p.add(logoLabel, gbc);

        gbc.gridy++;
        l.setAlignmentX(Component.TOP_ALIGNMENT);
        p.add(l, gbc);

        gbc.gridy++;
        b.setAlignmentX(Component.TOP_ALIGNMENT);
        b1.setAlignmentX(Component.TOP_ALIGNMENT);
        b2.setAlignmentX(Component.TOP_ALIGNMENT);
        gbc.insets = new Insets(15, 0, 15, 0);

        p.add(b, gbc);

        gbc.gridy++;
        p.add(b1, gbc);

        gbc.gridy++;
        p.add(b2, gbc);

        p.setBackground(Color.BLACK);

        f.add(p);

        f.setSize(600, 600);
        f.setExtendedState(JFrame.MAXIMIZED_BOTH);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setVisible(true);
    }

    private JButton createImageButton(String buttonText, String imagePath, int width, int height) {
        ImageIcon icon = new ImageIcon(imagePath);
        JButton button = new JButton(buttonText, icon);
        button.setHorizontalTextPosition(SwingConstants.CENTER);
        button.setVerticalTextPosition(SwingConstants.BOTTOM);
        button.addActionListener(e -> handleButtonClick(buttonText));
        button.setFont(new Font("Arial", Font.PLAIN, 20));
        button.setPreferredSize(new Dimension(width, height));
        return button;
    }

    public static void main(String[] args) {
        String memberIdInput = JOptionPane.showInputDialog("Enter Member ID:");
        try {
            int memberId = Integer.parseInt(memberIdInput);
            if (validateMemberId(memberId)) {
                SwingUtilities.invokeLater(() -> {
                    new User(memberId);
                });
            } else {
                JOptionPane.showMessageDialog(null, "Invalid Member ID", "Access Denied", JOptionPane.ERROR_MESSAGE);
                new LibraryManagementSystem();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Invalid Member ID format", "Error", JOptionPane.ERROR_MESSAGE);
            new LibraryManagementSystem();
        }
    }

    private static void openBorrowBookFrame(int memberId) {
        new BookBorrowFrame(memberId);
        f.dispose();
    }

    private void openDisplayBorrowedBooksFrame() {
        new DisplayBorrowedBooks(memberId);
        f.dispose();
    }

    private static boolean validateMemberId(int memberId) {
        try (Scanner scanner = new Scanner(new File("Members.txt"))) {
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

    private void handleButtonClick(String buttonText) {
        switch (buttonText) {
            case "Register Borrow Book":
                openBorrowBookFrame(memberId);
                break;
            case "Display Book Borrowed":
                openDisplayBorrowedBooksFrame();
                break;
            case "Exit":
            SwingUtilities.invokeLater(() -> {
                f.dispose();
            });
            new LibraryManagementSystem();
                break;
        }
    }
}

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LibraryManagementSystem extends JFrame {
    private static final String ADMIN_PASSWORD = "123";
    public static JFrame f;

    public LibraryManagementSystem() {
        setTitle("Library Management System");
        setSize(400, 300);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.BLACK);

        ImageIcon imageIcon = new ImageIcon("Image\\AdminLg.gif");
        JLabel imageLabel = new JLabel(imageIcon);
        imageLabel.setPreferredSize(new Dimension(2000, 650));


        JLabel titleLabel = new JLabel("Victoria Story Library Management System");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD | Font.ITALIC, 40));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel buttonPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                ImageIcon gifIcon = new ImageIcon("Image\\bt.gif");
                gifIcon.paintIcon(this, g, 0, 0);
            }
        };
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;

        JButton adminButton = new JButton();

        ImageIcon buttonImageIcon = new ImageIcon("Image\\Button Admin.png");
        adminButton.setIcon(buttonImageIcon);
        adminButton.setPreferredSize(new Dimension(500, 150));
        adminButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String password = JOptionPane.showInputDialog("Enter Admin Password:");
                if (password != null && password.equals(ADMIN_PASSWORD)) {
                    openAdminPage();
                } else {
                    JOptionPane.showMessageDialog(null, "Incorrect Password", "Access Denied", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        gbc.insets = new Insets(10, 0, 10, 0);
        buttonPanel.add(adminButton, gbc);
        gbc.gridy++;

        Font adminButtonFont = adminButton.getFont().deriveFont(Font.PLAIN, 25);
        adminButton.setFont(adminButtonFont);

        JButton userButton = new JButton();

        ImageIcon userButtonImageIcon = new ImageIcon("Image\\Button User.png");
        userButton.setIcon(userButtonImageIcon);
        userButton.setPreferredSize(new Dimension(500, 150));
        
        userButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openUserPage();
            }
        });

        Font userButtonFont = userButton.getFont().deriveFont(Font.PLAIN, 25);
        userButton.setFont(userButtonFont);

        gbc.insets = new Insets(30, 0, 30, 0);
        buttonPanel.add(userButton, gbc);

        Dimension buttonSize = new Dimension(500, 80);
        adminButton.setPreferredSize(buttonSize);
        userButton.setPreferredSize(buttonSize);
        mainPanel.add(imageLabel, BorderLayout.NORTH);
        mainPanel.add(titleLabel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        buttonPanel.add(adminButton, gbc);
        gbc.gridy++;
        buttonPanel.add(userButton, gbc);

        // Add the main panel to the content pane
        setLayout(new BorderLayout());
        add(mainPanel, BorderLayout.CENTER);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void openAdminPage() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Admin admin = new Admin();
                admin.setVisible(true);
            }
        });
        this.dispose();
    }

    private void openUserPage() {
        SwingUtilities.invokeLater(() -> User.main(null));
        this.dispose();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                LibraryManagementSystem mainInterface = new LibraryManagementSystem();
                mainInterface.setVisible(true);
            }
        });
    }
}

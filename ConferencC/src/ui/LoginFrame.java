package ui;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import command.Query;
import command.Server;
import java.awt.Font;
import java.io.IOException;
import javax.swing.JOptionPane;

/**
 * Login frame.
 * <p>Frame for signing in</p>
 * 
 */
public class LoginFrame extends JFrame {
    private JLabel loginLabel;
    private JTextField login; 
    private static final Color OK_COLOR = new Color(0, 128, 0);
    private static final Color ERR_COLOR = new Color(255, 0, 0);
    
    private JLabel passwordLabel;
    private JPasswordField password;
    
    private JButton loginButton;
    
    Query query = Query.QUERY;
    
    public LoginFrame() {
        super("Conferenc");
        
        initServer("localhost");
        
        setSize(300, 400);
      
        // locates frame 
        setLocationRelativeTo(null);
        
        setLayout(new GridLayout(5, 1, 10, 20));
        initLoginElements();
        initPasswordElements();
        initLoginButton();
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    } 

    private void initServer(String IP) {
        try {
            Server server = new Server(IP);
            query.init(server);
            System.out.print(server.receiveMessage());
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(
                this,
                "Can't connect to server!",
                "ERROR",
                JOptionPane.ERROR_MESSAGE
            );
            System.exit(0);
        }
        
    }
    private void initLoginElements() {
        loginLabel = new JLabel("Login:");
        loginLabel.setFont(new Font("Serif", Font.PLAIN, 16));
        add(loginLabel);
        
        login = new JTextField();
        login.setHorizontalAlignment(JTextField.CENTER);
        login.addFocusListener(new FocusListener() {

            @Override
            public void focusGained(FocusEvent e) {
                login.setBackground(Color.WHITE);
            }

            @Override
            public void focusLost(FocusEvent e) {
                checkLoginStatus();
            }
        });
        login.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (password.getPassword().length > 0)
                    tryLogin();
                else
                    password.requestFocus();
            }
        });
        add(login);
    }

    private void initPasswordElements() {
        passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Serif", Font.PLAIN, 16));
        add(passwordLabel);
        
        password = new JPasswordField();
        password.setHorizontalAlignment(JPasswordField.CENTER);
        password.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (!login.getText().isEmpty())
                    tryLogin();
                else
                    login.requestFocus();
            }
        });
        add(password);
    }

    private void initLoginButton() {
        loginButton = new JButton("Sign in!");
        loginButton.addActionListener( new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                tryLogin();
            }
        });
        add(loginButton);
    }
    
    private void checkLoginStatus() {
        if (query.loginExists(login.getText())) {
            login.setBackground(LoginFrame.OK_COLOR);
        }
        else {
           login.setBackground(LoginFrame.ERR_COLOR); 
        }
    }
    
    public void tryLogin() {
        if (query.fastLogin(login.getText(), new String(password.getPassword()))) {
            setVisible(false);
            ChatFrame cf = new ChatFrame(login.getText());
        }
        else {
            JOptionPane.showMessageDialog(
                this,
                "Password incorrect!",
                "WARNING",
                JOptionPane.WARNING_MESSAGE
            );
            
            password.setText("");
        }
    }
}

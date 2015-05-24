package ui;

import command.Query;
import command.QueryException;
import java.awt.PopupMenu;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

/**
 * Chat frame.
 *
 */
public class ChatFrame extends JFrame implements Runnable{
    JTextField message;
    
    JTextPane chat;
    HTMLEditorKit chatHTMLKit;
    
    Query query = Query.QUERY;
    String username;
    
    Thread messageReader;
    
    public ChatFrame(String username) {
        super("Conferenc");
        setSize(700, 550);
        setLocationRelativeTo(null) ;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);
        setResizable(false);
        
        initChatArea();
        initMessageField();
        initMenu();
        
        this.username = username;
        
        setVisible(true);
        
        messageReader = new Thread(this);
        messageReader.start();
    }

    private void initMenu() {
        JMenuBar bar = new JMenuBar();
        
        JMenu optionsMenu = new JMenu("Options");
        JMenuItem clear = new JMenuItem("Clear chat");
        clear.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                chat.setText("");
            }
        });
        optionsMenu.add(clear);
        
        bar.add(optionsMenu);
        JPopupMenu chatMenu = new JPopupMenu();
        clear = new JMenuItem("Clear");
        clear.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                chat.setText("");
            }
        });
        chatMenu.add(clear);
        chat.setComponentPopupMenu(chatMenu);
        
        JMenu helpMenu = new JMenu("Help");
        JMenuItem about = new JMenuItem("About");
        about.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(
                    (JComponent)e.getSource(),
                    "Simple conferenc client.\n\nAuthors: Aleksey Grinchuk & Ekaterina Bobariko",
                    "ABOUT",
                    JOptionPane.INFORMATION_MESSAGE
                );
            }
        });
        
        helpMenu.add(about);
        
        bar.add(helpMenu);
        
        bar.add(Box.createHorizontalGlue());
        
        JMenu logoutMenu = new JMenu("Logout");
        JMenuItem logout = new JMenuItem("Log out");
        logout.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                messageReader.stop();
                query.disconnect();
                LoginFrame loginFrame = new LoginFrame();
            }
        });
        logoutMenu.add(logout);
        
        JMenuItem exit = new JMenuItem("Exit");
        exit.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        logoutMenu.add(exit);
        
        bar.add(logoutMenu);
        
        setJMenuBar(bar);
    }

    private void initChatArea() {
        chat = new JTextPane();
        JScrollPane chatScroll = new JScrollPane(chat);
        chatScroll.setSize(650, 350);
        chatScroll.setLocation(25, 25);
        chat.setContentType("text/html");
        
        chatHTMLKit = new HTMLEditorKit();
        chat.setEditorKit(chatHTMLKit);
        chat.setFocusable(false);
        add(chatScroll);
    }

    private void initMessageField() {
        message = new JTextField();
        message.setSize(650, 50);
        message.setLocation(25, 400);
        message.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    query.sendMessage(message.getText());
                } catch (QueryException ex) {
                    JOptionPane.showMessageDialog(
                    (JTextField)e.getSource(),
                    "Connection error!",
                    "ERROR",
                    JOptionPane.ERROR_MESSAGE
                );
                
                System.exit(0);
                }
                message.setText("");
            }
        });
        add(message);
    }
    
    private void appendMessage(String[] msgData) {
        String color = "'black'";
        if (msgData[0].equals(username))
            color = "'blue'";
        
        String toAppend =   "<br><font color=" + 
                            color +
                            "><u><b>" +
                            msgData[0] +
                            "</b></u>: " +
                            msgData[1] +
                            "</font>";
        
        HTMLDocument doc = (HTMLDocument) chat.getDocument();
        try {
            chatHTMLKit.insertHTML(doc, doc.getLength(), toAppend, 0, 0, null);
        } catch (BadLocationException | IOException ex) {

        }
        
        chat.setCaretPosition(chat.getDocument().getLength());
    }
    
    @SuppressWarnings("empty-statement")
    public void startChat() {
        while (true) {
            try {
                appendMessage(query.receiveMessage());
                
            } catch (QueryException ex) {
                JOptionPane.showMessageDialog(
                    this,
                    "Connection error!1",
                    "ERROR",
                    JOptionPane.ERROR_MESSAGE
                );
                
                System.exit(0);
            }
        }
    }

    @Override
    public void run() {
        startChat();
    }
}

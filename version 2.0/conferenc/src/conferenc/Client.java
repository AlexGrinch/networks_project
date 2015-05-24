package conferenc;


import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Client connected to server.
 * 
 * @since conferenc 0.0.1
 */
public class Client { int cntr = 0;
    /**
     * Client logger.
     */
    static final Logger logger = Logger.getLogger(Client.class.getName());
    
    
    /**
     * Client input.
     */
    private Scanner in;
    
    /**
     * Client output.
     */
    private PrintWriter out;
    
    /**
     * Client connection socket.
     */
    private Socket socket;
    
    /**
     * Conferenc server.
     */
    private final ConferencServer server;
    
    /**
     * Connection wrap.
     */
    private final ClientConnection connection;
    
    /**
     * Database connection interface.
     */
    private final DataBase db = DataBase.DATA_BASE;
    
    
    /**
     * Client login.
     * <strong>WARNING:</strong> might be not initialized.
     */
    private String name;
    
    /**
     * If client signed in.
     */
    private boolean signedIn;
    
    /**
     * Creates new <code>Client</code>.
     * 
     * @param server server client connected to.
     * @param connection connection wrap.
     */
    public Client(ConferencServer server, ClientConnection connection) {
        try {
            
            FileHandler fh = new FileHandler("%tclients_log.log", 1000000, 10, true);
            logger.addHandler(fh);

        } catch (SecurityException ex) {
            
            logger.log(Level.SEVERE, "Can't create log file: forbidden", ex);
            System.exit(0xDEFACED);
            
        } catch (IOException ex) {
            
            logger.log(Level.SEVERE, "Can't create log file: IO error", ex);
            System.exit(0xDEFACED);
            
        }
        
        this.server = server;
        this.connection = connection;
        
        try {
            
            socket = server.getServerSocket().accept();
            socket.setKeepAlive(false);
            socket.setTcpNoDelay(false);
            
        } catch (IOException ex) {
            
            logger.log(Level.SEVERE, "Can't open socket", ex);
            System.exit(0xDEFACED);
            
        }
        
        try {
            
            in  = new Scanner(
                new InputStreamReader(socket.getInputStream(), "UTF-8")
            );
            
        } catch (IOException ex) {
            
            logger.log(Level.SEVERE, "Can't open socket input stream", ex);
            System.exit(0xDEFACED);
            
        }
        
        try {
            
            out = new PrintWriter(socket.getOutputStream(), true);
            
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "Can't open socket output stream", ex);
            System.exit(0xDEFACED);
        }
    
        signedIn = false;
        
        sendMessage("[CONNECTED]");
        
        logger.info("Client connected");
    }
    
    /**
     * Recives message from client.
     * The message is client's query by defaylt, which will be executed  by
     * {@link API}.
     * 
     * @return message <code>String</code>.
     */
    public final String readMessage() {
        logger.finest("Reading message");
        try {
            
            return in.nextLine();
                      
        } catch (NoSuchElementException ex) {
            
            logger.log(Level.WARNING, "Can't read message from socket");
            server.closeConnection(connection);
            
        }
        
        return null;
    }
    
    /**
     * Sends message to client.
     * 
     * @param message message to send.
     */
    public final void sendMessage(String message) {
        logger.fine("Sending message");
        out.println(message);
    }
    
    /**
     * Initialize client authorization.
     */
   public void signIn() {
       logger.info("Client authentication");
       
       String login;
       String password;
       
       sendMessage("[LOGIN?]");
       login = readMessage();
       
       sendMessage("[PASSWORD?]");
       password = readMessage();
       
       while (!db.authorize(login, password)) {
           if (socket.isClosed())
               return;
           
           logger.warning("Bad login");
           
           sendMessage("[BAD LOGIN, RETRY]");
           
           sendMessage("[LOGIN?]");
           login = readMessage();
           
           sendMessage("[PASSWORD?]");
           password = readMessage();
       }
       
       signedIn = true;
       sendMessage("[SIGNED IN SUCCESSFULY]");
       
       name = login;
       
       logger.log(Level.INFO, "Client signed in as {0}", name);
   }
   
   /**
     * Initialize client authorization.
     * 
     * @param login client login
     * @param password client password
     */
   public void signIn(String login, String password) {
       logger.info("Client authentication");
       
       while (!db.authorize(login, password)) {
           logger.warning("Bad login");
           sendMessage("[BAD LOGIN]");
           return;
       }
       
       signedIn = true;
       sendMessage("[SIGNED IN SUCCESSFULY]");
       
       name = login;
       
       logger.log(Level.INFO, "Client signed in as {0}", name);
   }
   
   /**
    * Initialize client registration.
    */
   public void signUp() {
       logger.info("Client registration");
       
       String login;
       String password;
       
       sendMessage("[LOGIN?]");
       login = readMessage();
       
       sendMessage("[PASSWORD?]");
       password = readMessage();
       
       while (db.userExists(login)) {
           if (socket.isClosed())
               return;
           
           sendMessage("[USER ALREADY EXISTS, RETRY]");
           sendMessage("[LOGIN?]");
           login = readMessage();
       }
       
       db.addUser(login, password);
       logger.log(Level.INFO, "User {0} registred", login);
       
       signedIn = true;
       sendMessage("[SIGNED UP SUCCESSFULY]");
       sendMessage("[SIGNED IN SUCCESSFULY]");
       name = login;
       
       logger.log(Level.INFO, "Client signed in as {0}", name);
   }
   
    
   /**
    * Starts executing client's queries.
    */
    public void process() {
        boolean processing = true;
        while (!socket.isClosed() && processing) {
            processing = API.processInput(readMessage(), connection);
        }
        
        server.closeConnection(connection);
    }
    
    /**
     * Disconnects client from the server.
     */
    public void disconnect() {
        try {
            
            socket.close();
            
        } catch (IOException ex) {
            
            logger.log(Level.SEVERE, "Can't close socket", ex);
            
        }
        
        logger.log(Level.INFO, "{0} disconnected", name);
    }
    
    /**
     * Returns {@link ConferencServer} client connected to.
     * 
     * @return server client connected to.
     */
    public ConferencServer getServer() {
        return server;
    }
    
    /**
     * Returns client's username.
     * <strong>WARNING:</strong> returns <code>null</code> if client
     * not authorized.
     * 
     * @return client's userneme or <code>null</code> if client isn't signed in.
     */
    public String getName() {
        return name;
    }
    
    /**
     * Returns if client authorized.
     * 
     * 
     * @return if cilent signed in.
     */
    public boolean isSignedIn() {
        return signedIn;
    }
    
    
    @Override
    public String toString() {
        return  "{\n" +
                "\t\"client\": {\n" +
                "\t\t\"name\": \"" + name + "\"\n" +
                "\t\t\"socket\": " + socket + "\n" +
                "\t}\n" +
                "}"
                ;
    }
    
}
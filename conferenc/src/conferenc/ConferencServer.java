package conferenc;


import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import conferenc.RSA;



/**
 * Main server.
 * <p>Provide information exchange between clients.</p>
 * 
 * <p> Using singelton design pattern (non-lazy initialization).
 * <code>CONFERENC_SERVER</code> — singe object of this class.</p>
 * 
 * @since conferenc 0.0.1
 */
public class ConferencServer {
    /**
     * Server logger.
     */
    static final Logger logger = Logger.getLogger(ConferencServer.class.getName());
    
    
    /**
     * Sigle <code>ConferencServer</code> object.
     */
    public static final ConferencServer CONFERENC_SERVER =
            new ConferencServer();
    
    
    /**
     * Default conferenc protocol port.
     */
    public static final int DEFAULT_PORT = 4444;
    
    /**
     * Conferenc protocol version.
     */
    public static String VERSION = "UNDEFINED";
    
    
    /**
     * Sever socket.
     */
    private ServerSocket serverSocket;
    
    /**
     * Active client connections.
     */
    private CopyOnWriteArrayList<ClientConnection> connections;

    /**
     * Initialize server using default port.
     * 
     * @param version version of protocol.
     *                <strong>Mustn't be <code>null</code>!</strong>
     */
    public void initConferencServer(String version) {
        VERSION = version;
        initServer(DEFAULT_PORT);
        
    }
    
    public static final RSA rsa = new RSA(1024);
    
    /**
     * Initialize server.
     * 
     * @param version version of protocol.
     *                <strong>Mustn't be <code>null</code>!</strong>
     * @param port  listening port.
     */
    public void initConferencServer(String version, int port) {
        VERSION = version;
        initServer(port);
    }
    
    private void initServer(int port) {
    	
        try {
            
            FileHandler fh = new FileHandler(
                "%tserver_log.log",
                1000000,
                10, 
                true
            );
            
            logger.addHandler(fh);
            
        } catch (SecurityException ex) {
            
            logger.log(Level.SEVERE, "Can't create log file: forbidden", ex);
            System.exit(0xBEE);
            
        } catch (IOException ex) {
            
            logger.log(Level.SEVERE, "Can't create log file: IO error", ex);
            System.exit(0xBEE);
            
        }
        
        logger.info("Server starting...");
        
        System.out.println(
            "Welcome to Conferenc!\n" +
            "\tServer version: " + VERSION + "\n" +
            "Server inialization..."
        );
        
        connections = new CopyOnWriteArrayList<>();
        
        try {
            
            serverSocket = new ServerSocket(port);
            
        } catch (IOException ex) {
            
            logger.log(Level.SEVERE, "Can't open socket", ex);
            System.exit(0xBEE);
            
        }
        
        logger.info("Initialization OK");
        
        System.out.println("Initialization OK!"); 
        
        while (true) {
            connections.add(new ClientConnection(this));
            connections.get(connections.size() - 1).process();
            
            logger.info("Open new connection");
        }
    }

    /**
     * Send message to all authorized clients connected to server.
     * 
     * @param message message to send.
     */
    public void sendMessage(String message) {
        logger.finest("Sending message");
        for (ClientConnection connection : connections) {
            if (connection.getClient().isSignedIn())
                connection.sendMessage(message);
        }
    }

    /**
     * Returns the {@link ServerSocket}
     * 
     * @return this server socket.
     */
    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    /**
     * Returns clients list.
     * 
     * <p>Server using Thread-safe {@link CopyOnWriteArrayList}</p>
     * 
     * @return list of connection.
     */
    public CopyOnWriteArrayList<ClientConnection> getConnections() {
        return connections;
    }
    
    /**
     * Returns version of conferenc protocol.
     * 
     * @return protocol version.
     */
    public String getVersion() {
        return VERSION;
    }
    
    /**
     * Closes connection if it exists.
     * 
     * @param connection connection to close. 
     */
    public void closeConnection(ClientConnection connection) {
        if (connections.contains(connection)) {
            logger.info("Close connection");
            connections.remove(connection);
            connection.close();
        }
    }
    
    @Override
    public String toString() {
        return  "{\n" +
                "\t\"ConferencServer\": {\n" +
                "\t\t\"version\": \"" + VERSION + "\"\n" +
                "\t\t\"socket\": \"" + serverSocket + "\"\n" +
                "\t\t\"connections\": " + connections + "\n" +
                "}";
    }
}
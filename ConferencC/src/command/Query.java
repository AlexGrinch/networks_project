package command;

import java.math.BigInteger;

import command.RSA;

/**
 * This is the server queries interface.
 * <p> It provides all types of interactions with server. </p>
 * 
 * <p> Using singleton design pattern (non-lazy initialization).
 * <code>QUERY</code> — single object of this class.</p>
 * 
 */
public class Query {
    /**
     * Single <code>Query</code> object.
     */
    public static Query QUERY = new Query();
    
    /**
     * Server which should execute queries.
     */
    private Server server;
    
    
    /**
     * Initializes single <code>Query</code> object.
     * In fact, "connects" queries with defined server.
     * 
     * @param server server which should exeecute queries. 
     */
    public void init(Server server) {
        setServer(server);
    }
    
    /**
     * "Connects" this queries processor with defined server.
     * 
     * @param server server which should exeecute queries.
     */
    public void setServer(Server server) {
        this.server = server;
    }
    
    /**
     * Sends message.
     * 
     * @param message message to send.
     * @throws command.QueryException if message wasn't received by server.
     */
    
    public static BigInteger server_rsa_key;
    public static BigInteger server_rsa_mod;
    public static RSA server_rsa;
    
    
    public synchronized void sendMessage(String message) throws QueryException {
    	if (server_rsa_key == null) {
    		server.sendMessage("[MESSAGE]".concat(message));
    	} 
    	else {
    		// Message encryption by server
    		RSA server_rsa = new RSA(server_rsa_mod, server_rsa_key);
    		BigInteger plaintext = new BigInteger(message.getBytes());
    		BigInteger ciphertext = server_rsa.encrypt(plaintext);
    		server.sendMessage("[MESSAGE]".concat(ciphertext.toString()));   
    	}   
    }
    
    /**
     * Receives message from server.
     * @return array of <code>String</code>'s — information about message.
     *         First string is message author username, second is message text.
     * @throws command.QueryException if server's answer is not a message.
     */
    public String[] receiveMessage() throws QueryException {
        
        String msg = " ";
        
        while (!msg.startsWith("[MESSAGE BY")) {
        	if (msg.startsWith("[KEY]")) {
        		server_rsa_key = new BigInteger(msg.substring(6));
        	}
        	if (msg.startsWith("[MOD]")) {
        		server_rsa_mod = new BigInteger(msg.substring(6));
        	}
            //throw new QueryException("Server's answer is not a message.");
            msg = server.receiveMessage();
            System.out.println("Message: " + msg);
        }

        msg = msg.replaceFirst("\\[MESSAGE BY ", "");
        String[] dic = msg.split("]", 2);
        
        return dic;
    }
    
    /**
     * Sends sign in query to server.
     * <p>Tries to sign in to server (non-interactive mode).</p>
     * 
     * @param username username.
     * @param password password.
     * @return if signing in successfully.
     */
    public synchronized boolean fastLogin(String username, String password) {
    	   	
        server.sendMessage("[FAST SIGN IN]" + username + "," + password);
        
        boolean flag = server.receiveMessage().equals("[SIGNED IN SUCCESSFULY]");
        
        //String mesg = server.receiveMessage();
        //System.out.println("Message: " + mesg);
        
        //RSA server_rsa = new RSA(server_rsa_mod, server_rsa_key);
        
        return flag;
    }
    
    
    
    /**
     * Answers if login exists.
     * 
     * @param username — username to check
     * @return if username exists
     */
    public synchronized boolean loginExists(String username) {
        server.sendMessage("[LOGIN EXISTS?]" + username);
        return server.receiveMessage().equals("[YES]");
    }
    
    /**
     * Disconnects from server.
     */
    public void disconnect() {
        server.sendMessage("[DISCONNECT]");
        server.closeConnection();
    }
}
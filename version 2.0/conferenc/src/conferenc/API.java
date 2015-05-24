package conferenc;


import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import conferenc.RSA;



/**
 * Conferenc system API.
 * <p>This API using to execute queries given by clients.</p>
 * 
 * @since conferenc 0.0.1
 */
class API {
    
    /**
     * Parses and executes command from client query.
     * 
     * @param input query, sended by client.
     * @param connection {@link ClientConnection} wrap for client.
     * @return if server must wait for the next query or disconnect client (if
     *         false)
     */
    public static boolean processInput(String input, ClientConnection connection) {
        String msg = getMessage(input);
        
        switch (getCommand(input)) {
            case "{}" :
            case "CSEQ" :
            case "CMD" :
            case "COMMANDS" :
            case "SCRIPT" :
            case "COMMAND SEQUENCE": return processInput(msg.split("#"), connection);
            
            case "" :
            case "KEY" : return client_key(connection, msg);
            case "MOD" : return client_mod(connection, msg);
            case "MESSAGE" : return sendMessage(msg, connection);
            case "P" :
            case "ECHO" :
            case "PING" : return ping(msg, connection);
            case "EXIT" :
            case "QUIT" :
            case "DISCONNECT" : return exit(msg, connection);
            case "V?" :
            case "VERSION?" : return version(connection);
            case "DATE?" : return date(connection);
            case "TIME?" : return time(connection);
            case "UL?" :
            case "USERLIST?" :
            case "USER LIST?" : return userList(connection);
            case "LOGIN" :
            case "LOG IN" :
            case "SIGN IN" : return signIn(connection);
            case "SIGN UP" : return signUp(connection);
            case "FAST SIGN IN" : return signIn(msg, connection);
            case "LOGIN FREE?" : return loginFree(msg, connection);
            case "LOGIN EXISTS?" : return loginExists(msg, connection);
                
            case "FAST SIGN UP" :
            case "HEARTBEAT" :
            case "VOTE BAN" :
            case "HELP" :
            case "FILE" :
            case "PROTECT" : return notSupportedYet(connection);
        }
        
        return badCommand(connection,"UNKNOWN COMMAND");
    }
    
    private static boolean processInput(String[] input, ClientConnection connection) {
        boolean goodSeq = true;
        
        for (String s : input) {
            goodSeq &= processInput(s, connection);
        }
        
        return goodSeq;
    }
    
    private static String getCommand(String input) {
        try {
            
            return input.substring(1, input.indexOf(']')).toUpperCase();
            
        } catch (Exception e) {
            
            return "ERR";
            
        }
    }
    
    private static String getMessage(String input) {
        if (input == null)
            input = "";
        return input.substring(input.indexOf(']') + 1);
    }
    
    private static boolean badCommand(ClientConnection connection, String error) {
        answer(connection, "[BAD COMMAND]".concat(error));
        
        return true;
    }
    
    private static boolean sendMessage(String message, ClientConnection connection) {
        if (authorized(connection)) {
        	
        	System.out.println("Server has received the message: " + message);
        	
        	// Message decryption by server
        	/**BigInteger ciphertext = new BigInteger(message);
            BigInteger plaintext = connection.getServer().rsa.decrypt(ciphertext);
            message = new String(plaintext.toByteArray());*/
            
            /**RSA client_rsa = new RSA(client_rsa_mod, client_rsa_key);
            BigInteger plaintext = new BigInteger(message.getBytes());
    		BigInteger ciphertext = client_rsa.encrypt(plaintext);
            message = ciphertext.toString();*/
            
            say(
                connection, 
                "[MESSAGE BY ".concat(
                    connection.getClient().getName()
                ).concat(
                    "]"
                ).concat(
                    message
                )
            );
        }
        else
            forbid(connection, "NOT SIGNED IN");
        
        return true;
    }
    
    private static boolean ping(String msg, ClientConnection connection) {
        if(msg.isEmpty())
            answer(connection, "[PING OK]");
        else
            answer(connection, "[ECHO]".concat(msg));
        
        return true;
    }
    
    private static boolean exit(String message, ClientConnection connection) {
        answer(connection, "[DISCONNECTING...]");
        
        if (authorized(connection))
            if ( message.isEmpty() )
                say(
                    connection,
                    "[DISCONNECT BY " + 
                    connection.getClient().getName() + 
                    "]"
                );
            else
                say(
                    connection, 
                    "[DISCONNECT BY " + 
                    connection.getClient().getName() +
                    " WITH MESSAGE]" +
                    message
            );
        
        connection.getServer().closeConnection(connection);
        
        return false;
    }
    
    private static boolean version(ClientConnection connection) {
        answer(
            connection, 
            "[VERSION]" +
            connection.getServer().getVersion()
        );
        
        return true;
    }
    
    private static boolean date(ClientConnection connection) {
        answer(
            connection,
            "[DATE]" +
            new SimpleDateFormat("dd/MM/yyy").format(
                Calendar.getInstance().getTime()
            )
        );
        
        return true;
    }
    
    private static boolean time(ClientConnection connection) {
        answer(
            connection,
            "[TIME]" +
            new SimpleDateFormat("HH:mm:ss").format(
                Calendar.getInstance().getTime()
            )
        );
        
        return true;
    }
    
    private static boolean userList(ClientConnection connection) {
        if (authorized(connection))
            answer(
                connection, 
                "[USER LIST]".concat(
                    connection.getServer().getConnections().toString()
                )
            );
        else
            forbid(connection, "NOT SIGNED IN");
        
        return true;
    }
    
    private static boolean signIn(ClientConnection connection) {
        connection.getClient().signIn();
        
        
        return true;
    }
    
    private static boolean signIn(String loginData, ClientConnection connection) {
        String[] loginParams = loginData.split(",", 2);
        if (loginParams.length != 2)
            return badCommand(connection, "BAD FORMAT");
        connection.getClient().signIn(loginParams[0], loginParams[1]);
        answer(connection, "[ENABLE]encryption");
        /**BigInteger server_rsa_key = connection.getServer().rsa.getE();
        BigInteger server_rsa_mod = connection.getServer().rsa.getN();
        answer(connection, "[KEY] " + server_rsa_key.toString());
        answer(connection, "[MOD] " + server_rsa_mod.toString());*/
        return true;
    }
    
    private static boolean signUp(ClientConnection connection) {
        connection.getClient().signUp();
        
        return true;
    }
    
    private static boolean loginExists(String login, ClientConnection connection) {
        if(DataBase.DATA_BASE.userExists(login))
            answer(connection, "[YES]");
        else
            answer(connection, "[NO]");
        
        return true;
    }
    
    private static boolean loginFree(String login, ClientConnection connection) {
        if(DataBase.DATA_BASE.userExists(login))
            answer(connection, "[NO]");
        else
            answer(connection, "[YES]");
        
        return true;
    }
    
    private static boolean notSupportedYet(ClientConnection connection) {
        answer(connection, "[NOT SUPPORTED YET]");
        
        return true;
    }
    
    private static void forbid(ClientConnection connection, String reason) {
        answer(connection, "[FORBIDDEN]".concat(reason));
    }
    
    private static void answer(ClientConnection connection, String message) {
        connection.sendMessage(message);
    }
    
    private static void say(ClientConnection connection, String message) {
        connection.getServer().sendMessage(message);
    }
    
    private static boolean authorized(ClientConnection connection) {
        return connection.getClient().isSignedIn();
    }
    
    public static BigInteger client_rsa_key;
    public static BigInteger client_rsa_mod;
    
    private static boolean client_key(ClientConnection connection, String message) {
    	client_rsa_key = new BigInteger(message);
    	//System.out.println("Message: " + client_rsa_key);
    	return true;
    }
    
    private static boolean client_mod(ClientConnection connection, String message) {
    	client_rsa_mod = new BigInteger(message);
    	//System.out.println("Message: " + message);
    	return true;
    }
}
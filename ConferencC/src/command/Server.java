package command;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 * Class for low-level server interactions.
 * <p>Provides sending and receiving socket messages.</p>
 * 
 */
public class Server {
    /**
     * Server socket.
     */
    Socket soc;
    
    /**
     * Server input stream.
     */
    Scanner in;
    
    /**
     * Server output stream.
     */
    PrintWriter out;
    
    public Server(String IP) throws IOException {
        soc = new Socket("93.175.5.10", 4444);
        
        in = new Scanner(soc.getInputStream(), "UTF-8");
        out = new PrintWriter(soc.getOutputStream());
    }
    
    /**
     * Sends string to server.
     * 
     * @param message — string to send.
     */
    public void sendMessage(String message) {
        out.print(message.concat("\n"));
        out.flush();
    }
    
    /**
     * Receives message from server.
     * 
     * @return received message.
     */
    public String receiveMessage() {
        return in.nextLine();
    }
    
//          UNSUPPORTED YET    
//    /**
//     * Receives message from server according to pattern.
//     * 
//     * @param pattern
//     * @return 
//     */
//    public String reciveMessage(String pattern) {
//        while (!in.hasNext(pattern)){};
//        return in.nextLine();
//    }
    

    /**
     * Closes connection.
     */
    void closeConnection() {
        in.close();
        out.close();
        try {
            soc.close();
        } catch (IOException ex) {
            System.exit(1);
        }
    }
}

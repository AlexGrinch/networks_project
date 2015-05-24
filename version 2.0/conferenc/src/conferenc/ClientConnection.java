package conferenc;



/**
 * Wrap for {@link Client}.
 * <p> Gives ability to execute client's queries in it's ont {@link Thread}. </p>
 * 
 * @since conferenc 0.0.1
 */
public class ClientConnection implements Runnable {
    /**
     * {@link Client} wraped by this <code>ClientConnection</code>.
     */
    private final Client client;
    
    /**
     * {@link Conferenc Server} this <code>ClientConnection</code> belonges to.
     */
    private final ConferencServer server;
    
    /**
     * {@link Thread} for this connection queries executing.
     */
    Thread connectionThread;
    
    /**
     * Creates new <code>ClientConnection</code>.
     * 
     * <p><strong>WARNING:</strong> creates new <code>Thread</code></p>
     * 
     * @param server {@link ConferencServer} the {@link Client} belongs to.
     */
    public ClientConnection(ConferencServer server) {
        this.client = new Client(server, this);
        this.server = server;
        
       connectionThread = new Thread(this);
    }
    
    /**
     *  Starts {@link Thread} to execute client's queries.
     */
    public void process() {
        connectionThread.start();
    }
 
    @Override
    public void run() {
        client.process();
    }
    
    /**
     * Sends message to client.
     * 
     * @param message message to send.
     */
    public void sendMessage(String message) {
        client.sendMessage(message);
    }
    
    /**
     * Receives message from client.
     * The message is client's query by default, which will be executed  by
     * {@link API}.
     * 
     * @return message <code>String</code>.
     */
    public String readMessage() {
        return client.readMessage();
    }
    
    /**
     * Close connection.
     */
    public void close() {
        client.disconnect();
    }
    
    /**
     * Returns {@link Client} this <code>ClientConnection</code> is wrap for.
     * 
     * @return wraped {@link Client}.
     */
    public Client getClient() {
        return client;
    }
    
    /**
     * Returns {@link ConferencServer} this <code>ClientConnection</code> belongs to.
     * 
     * @return {@link ConferencServer} this connection belongs to.
     */
    public ConferencServer getServer() {
        return server;
    }
    
    @Override
    public String toString() {
        return  "{\n" +
                "\t\"connection\": {\n" +
                "\t\t\"client\": " + client +"\n" +
                "\t}\n" +
                "}"
                ;
    }
    
}
import conferenc.ConferencServer;


public class Conferenc {
    static String version = "0.0.1";
    
    public static void main(String[] args) throws Exception {
        ConferencServer conferencServer = ConferencServer.CONFERENC_SERVER;
        conferencServer.initConferencServer(version);
    }
    
}
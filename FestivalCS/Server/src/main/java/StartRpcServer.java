import network.AbstractServer;
import network.FestivalRpcConcurrentServer;
import network.ServerException;
import repository.*;
import repository.orm.SellerHbmRepository;
import server.ServiceImplementation;
import service.IService;

import java.io.IOException;
import java.util.Properties;

public class StartRpcServer {
    private static int defaultPort=55555;

    public static void main(String[] args) {
        Properties serverProps=new Properties();
        try {
            serverProps.load(StartRpcServer.class.getResourceAsStream("/server.properties"));
            System.out.println("Server properties set. ");
            serverProps.list(System.out);
        } catch (IOException e) {
            System.err.println("Cannot find server.properties "+e);
            return;
        }

        IArtistDb artistDb = new ArtistDb(serverProps);
        IBuyerDb buyerDb = new BuyerDb(serverProps);
        ISellerDb sellerDb = new SellerHbmRepository();
        IShowDb showDb = new ShowDb(serverProps);
        IService service = new ServiceImplementation(artistDb, buyerDb, sellerDb, showDb);

        int festivalServerPort=defaultPort;
        try {
            festivalServerPort = Integer.parseInt(serverProps.getProperty("festival.server.port"));
        }catch (NumberFormatException nef){
            System.err.println("Wrong Port Number"+nef.getMessage());
            System.err.println("Using default port "+defaultPort);
        }
        System.out.println("Starting server on port: "+festivalServerPort);
        AbstractServer server = new FestivalRpcConcurrentServer(festivalServerPort, service);
        try {
            server.start();
        } catch (ServerException e) {
            System.err.println("Error starting the server" + e.getMessage());
        }finally {
            try {
                server.stop();
            }catch(ServerException e){
                System.err.println("Error stopping server "+e.getMessage());
            }
        }
    }
}

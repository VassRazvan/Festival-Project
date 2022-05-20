package network;

import service.IService;

import java.net.Socket;


public class FestivalRpcConcurrentServer extends AbsConcurrentServer {
    private IService chatServer;
    public FestivalRpcConcurrentServer(int port, IService chatServer) {
        super(port);
        this.chatServer = chatServer;
        System.out.println("Chat- ChatRpcConcurrentServer");
    }

    @Override
    protected Thread createWorker(Socket client) {
        FestivalClientRpcWorker worker=new FestivalClientRpcWorker(chatServer, client);
//        ChatClientRpcReflectionWorker worker=new ChatClientRpcReflectionWorker(chatServer, client);

        Thread tw=new Thread(worker);
        return tw;
    }

    @Override
    public void stop(){
        System.out.println("Stopping services ...");
    }
}

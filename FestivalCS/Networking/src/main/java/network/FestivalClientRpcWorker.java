package network;


import domain.Artist;
import domain.Seller;
import domain.Show;
import service.IObserver;
import service.IService;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.time.LocalDate;
import java.util.List;


public class FestivalClientRpcWorker implements Runnable, IObserver {
    private IService server;
    private Socket connection;

    private ObjectInputStream input;
    private ObjectOutputStream output;
    private volatile boolean connected;
    public FestivalClientRpcWorker(IService server, Socket connection) {
        this.server = server;
        this.connection = connection;
        try{
            output=new ObjectOutputStream(connection.getOutputStream());
            output.flush();
            input=new ObjectInputStream(connection.getInputStream());
            connected=true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        while(connected){
            try {
                Object request=input.readObject();
                Response response=handleRequest((Request)request);
                if (response!=null){
                    sendResponse(response);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        try {
            input.close();
            output.close();
            connection.close();
        } catch (IOException e) {
            System.out.println("Error "+e);
        }
    }



    private static final Response okResponse=new Response.Builder().type(ResponseType.OK).build();

    private Response handleRequest(Request request) throws Exception {
        Response response=null;
        if (request.type() == RequestType.LOGIN)
        {
            System.out.println("Login request...");
            Seller seller = (Seller) request.data();
            try {
                server.login(seller,this);
                return okResponse;
            } catch (Exception e) {
                connected = false;
                return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
            }
        }

        if (request.type() == RequestType.LOGOUT)
        {
            System.out.println("Logout request...");
            try {
                server.logout((Seller) request.data(),this);
                connected = false;
                return okResponse;
            } catch (Exception e) {
                return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
            }
        }

        if (request.type() == RequestType.GET_ALL_ARTISTS){
            System.out.println("Get artists request...");
            Iterable<Artist> artists = server.getAllArtists();
            System.out.println("Worker artists " + artists);
            return new Response.Builder().type(ResponseType.OK).data(artists).build();
        }

        if (request.type() == RequestType.GET_SELLER_BY_EMAIL)
        {
            System.out.println("Get seller by email...2");
            String email = (String) request.data();
            try{
                Seller seller = server.findSellerByEmail(email);
                return new Response.Builder().type(ResponseType.OK).data(seller).build();
            }
            catch(Exception ex){
                return new Response.Builder().type(ResponseType.ERROR).data(ex.getMessage()).build();
            }
        }

        if (request.type() == RequestType.GET_SHOWS_BY_ARTIST_ID)
        {
            System.out.println("Get shows by artist id...");
            Integer id = (Integer) request.data();
            try{
                List<Show> shows = server.findShowsByArtistId(id);
                return new Response.Builder().type(ResponseType.OK).data(shows).build();
            }
            catch(Exception ex){
                return new Response.Builder().type(ResponseType.ERROR).data(ex.getMessage()).build();
            }
        }

        if (request.type() == RequestType.GET_ARTIST_BY_ID)
        {
            System.out.println("Get artist by id...");
            Integer id = (Integer) request.data();
            try{
                Artist artist = server.findArtistById(id);
                return new Response.Builder().type(ResponseType.OK).data(artist).build();
            }
            catch(Exception ex){
                return new Response.Builder().type(ResponseType.ERROR).data(ex.getMessage()).build();
            }
        }


        if (request.type() == RequestType.GET_SHOWS_BY_DAY)
        {
            System.out.println("Get shows by date...");
            LocalDate localDate = (LocalDate) request.data();
            try{
                List<Show> shows = server.findShowsByDay(localDate);
                return new Response.Builder().type(ResponseType.OK).data(shows).build();
            }
            catch(Exception ex){
                return new Response.Builder().type(ResponseType.ERROR).data(ex.getMessage()).build();
            }
        }

        if (request.type() == RequestType.GET_SHOW_BY_DATE_AND_PLACE)
        {
            System.out.println("Get shows by date and place...");
            List<String> stringList = (List<String>) request.data();
            try{
                Show show = server.findShowByDateAndPlace(stringList.get(0), stringList.get(1));
                return new Response.Builder().type(ResponseType.OK).data(show).build();
            }
            catch(Exception ex){
                return new Response.Builder().type(ResponseType.ERROR).data(ex.getMessage()).build();
            }
        }

        if (request.type() == RequestType.UPDATE_SHOW)
        {
            System.out.println("Show updating...");
            Show show = (Show) request.data();
            try{
                server.updateShow(show);
                return okResponse;
            }
            catch(Exception ex){
                return new Response.Builder().type(ResponseType.ERROR).data(ex.getMessage()).build();
            }
        }


        return response;
    }

    private void sendResponse(Response response) throws IOException{
        System.out.println("sending response "+response);
        output.writeObject(response);
        output.flush();
    }

    @Override
    public void ticketBought() throws Exception {
        System.out.println("Notifing all clients...");
        Response resp=new Response.Builder().type(ResponseType.UPDATE).data(null).build();
        try {
            sendResponse(resp);
        } catch (IOException e) {
            throw new Exception("Sending error: "+e);
        }
    }
}

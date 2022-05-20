package network;

import domain.Artist;
import domain.Buyer;
import domain.Seller;
import domain.Show;
import service.IObserver;
import service.IService;

import javax.swing.text.html.ListView;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


public class FestivalServicesRpcProxy implements IService {
    private String host;
    private int port;

    private IObserver client;

    private ObjectInputStream input;
    private ObjectOutputStream output;
    private Socket connection;

    private BlockingQueue<Response> qresponses;
    private volatile boolean finished;
    public FestivalServicesRpcProxy(String host, int port) {
        this.host = host;
        this.port = port;
        qresponses=new LinkedBlockingQueue<Response>();
    }

    private void closeConnection() {
        finished=true;
        try {
            input.close();
            output.close();
            connection.close();
            client=null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendRequest(Request request) throws Exception {
        try {
            output.writeObject(request);
            output.flush();
        } catch (IOException e) {
            throw new Exception("Error sending object "+e);
        }

    }

    private Response readResponse() throws Exception {
        Response response=null;
        try{

            response=qresponses.take();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return response;
    }
    private void initializeConnection() throws Exception {
        try {
            connection=new Socket(host,port);
            output=new ObjectOutputStream(connection.getOutputStream());
            output.flush();
            input=new ObjectInputStream(connection.getInputStream());
            finished=false;
            startReader();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void startReader(){
        Thread tw=new Thread(new ReaderThread());
        tw.start();
    }


    private void handleUpdate(Response response) {
        if (response.type() == ResponseType.UPDATE)
        {
            try {
                client.ticketBought();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private boolean isUpdate(Response response){
        return response.type() == ResponseType.UPDATE ;
    }

    @Override
    public void login(Seller seller, IObserver obs) throws Exception {
        initializeConnection();
        Request req = new Request.Builder().type(RequestType.LOGIN).data(seller).build();
        sendRequest(req);
        Response res = readResponse();
        if (res.type() == ResponseType.OK)
        {
            System.out.println("Am ajuns in RpcService");
            this.client = obs;
            return;
        }
        if (res.type() == ResponseType.ERROR)
        {
            String err = res.data().toString();
            closeConnection();
            throw new Exception(err);
        }
    }

    @Override
    public void logout(Seller seller, IObserver obs) throws Exception {
        Request req = new Request.Builder().type(RequestType.LOGOUT).data(seller).build();
        sendRequest(req);
        Response res = readResponse();
        closeConnection();
        if (res.type() == ResponseType.ERROR){
            String err=res.data().toString();
            throw new Exception(err);
        }
    }

    @Override
    public void addArtist(Artist artist) {

    }

    @Override
    public void removeArtist(Integer id) {

    }

    @Override
    public void updateArtist(Artist artist) {

    }

    @Override
    public Artist findArtistById(Integer id) throws Exception {
        System.out.println("Getting artists...");
        Request req = new Request.Builder().type(RequestType.GET_ARTIST_BY_ID).data(id).build();
        sendRequest(req);
        Response res = readResponse();
        if (res.type() == ResponseType.ERROR)
        {
            String err = res.data().toString();
            throw new Exception(err);
        }
        return (Artist)res.data();
    }

    @Override
    public Iterable<Artist> getAllArtists() throws Exception {
        System.out.println("Getting artists...");
        Request req = new Request.Builder().type(RequestType.GET_ALL_ARTISTS).data(null).build();
        sendRequest(req);
        Response res = readResponse();
        if (res.type() == ResponseType.ERROR)
        {
            String err = res.data().toString();
            throw new Exception(err);
        }
        return (List<Artist>) res.data();
    }

    @Override
    public void addBuyer(Buyer buyer) {

    }

    @Override
    public void removeBuyer(Integer id) {

    }

    @Override
    public void updateBuyer(Buyer buyer) {

    }

    @Override
    public Buyer findBuyerById(Integer id) {
        return null;
    }

    @Override
    public Iterable<Buyer> getAllBuyers() {
        return null;
    }

    @Override
    public void addSeller(Seller seller) {

    }

    @Override
    public void removeSeller(Integer id) {

    }

    @Override
    public void updateSeller(Seller seller) {

    }

    @Override
    public Seller findSellerById(Integer id) {
        return null;
    }

    @Override
    public Iterable<Seller> getAllSellers() {
        return null;
    }

    @Override
    public Seller findSellerByEmail(String email) throws Exception{
        System.out.println("Getting seller by email...");
        Request req = new Request.Builder().type(RequestType.GET_SELLER_BY_EMAIL).data(email).build();
        sendRequest(req);
        Response res = readResponse();
        if (res.type() == ResponseType.ERROR)
        {
            String err = res.data().toString();
            throw new Exception(err);
        }
        return (Seller)res.data();
    }

    @Override
    public void addShow(Show show) {

    }

    @Override
    public void removeShow(Integer id) {

    }

    @Override
    public void updateShow(Show show) throws Exception {
        System.out.println("Show updating...");
        Request req = new Request.Builder().type(RequestType.UPDATE_SHOW).data(show).build();
        sendRequest(req);
        Response res = readResponse();
        if (res.type() == ResponseType.ERROR)
        {
            String err = res.data().toString();
            throw new Exception(err);
        }
    }

    @Override
    public Show findById(Integer id) {
        return null;
    }

    @Override
    public Iterable<Show> getAll() {
        return null;
    }

    @Override
    public List<Show> findShowsByArtistId(Integer idArtist) throws Exception {
        System.out.println("Getting shows...");
        Request req = new Request.Builder().type(RequestType.GET_SHOWS_BY_ARTIST_ID).data(idArtist).build();
        sendRequest(req);
        Response res = readResponse();
        if (res.type() == ResponseType.ERROR)
        {
            String err = res.data().toString();
            throw new Exception(err);
        }
        return (List<Show>) res.data();
    }

    @Override
    public List<Show> findShowsByDay(LocalDate date) throws Exception {
        System.out.println("Getting shows by day...");
        Request req = new Request.Builder().type(RequestType.GET_SHOWS_BY_DAY).data(date).build();
        sendRequest(req);
        Response res = readResponse();
        if (res.type() == ResponseType.ERROR)
        {
            String err = res.data().toString();
            throw new Exception(err);
        }
        return (List<Show>) res.data();
    }

    @Override
    public Show findShowByDateAndPlace(String date, String place) throws Exception {
        System.out.println("Getting show by date and place...");
        List<String> stringList = new ArrayList<>(Arrays.asList(date, place));
        Request req = new Request.Builder().type(RequestType.GET_SHOW_BY_DATE_AND_PLACE).data(stringList).build();
        sendRequest(req);
        Response res = readResponse();
        if (res.type() == ResponseType.ERROR)
        {
            String err = res.data().toString();
            throw new Exception(err);
        }
        return (Show) res.data();
    }


    private class ReaderThread implements Runnable{
        public void run() {
            while(!finished){
                try {
                    Object response=input.readObject();
                    System.out.println("response received "+response);
                    if (isUpdate((Response)response)){
                        handleUpdate((Response)response);
                    }else{

                        try {
                            qresponses.put((Response)response);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (IOException e) {
                    System.out.println("Reading error "+e);
                } catch (ClassNotFoundException e) {
                    System.out.println("Reading error "+e);
                }
            }
        }
    }
}

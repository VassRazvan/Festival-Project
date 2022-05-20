package server;
import domain.Artist;
import domain.Buyer;
import domain.Seller;
import domain.Show;
import repository.*;
import service.*;

import java.nio.charset.StandardCharsets;
import java.security.KeyException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class ServiceImplementation implements IService {

    private final IArtistDb artistRepo;
    private final IBuyerDb buyerRepo;
    private final ISellerDb sellerRepo;
    private final IShowDb showRepo;

    private Map<String, IObserver> loggedClients;
    private final int defaultThreadsNumber = 5;

    public ServiceImplementation(IArtistDb artistRepo, IBuyerDb buyerRepo, ISellerDb sellerRepo, IShowDb showRepo) {
        this.artistRepo = artistRepo;
        this.buyerRepo = buyerRepo;
        this.sellerRepo = sellerRepo;
        this.showRepo = showRepo;
        this.loggedClients = new ConcurrentHashMap<>();
    }

    @Override
    public synchronized void login(Seller sellerEmailAndPassword, IObserver obs) throws Exception {
        Seller seller = this.findSellerByEmail(sellerEmailAndPassword.getEmail());

        if (seller == null)
            throw new KeyException("The email does not exist");
        if (!Objects.equals(seller.getPassword(), Base64.getEncoder().encodeToString(sellerEmailAndPassword.getPassword().getBytes(StandardCharsets.UTF_8))))
            throw new KeyException("Incorrect password");
        if (loggedClients.get(seller.getEmail()) != null)
            throw new Exception("User already logged in");
        loggedClients.put(seller.getEmail(),obs);
    }

    @Override
    public synchronized void logout(Seller seller, IObserver obs) throws Exception {
        IObserver localClient = loggedClients.remove(seller.getEmail());
        if (localClient==null)
            throw new Exception("User "+seller.getEmail()+" is not logged in.");
    }

    @Override
    public synchronized void addArtist(Artist artist) throws Exception {
        artistRepo.add(artist);
    }

    @Override
    public synchronized void removeArtist(Integer id) {
        artistRepo.delete(id);
    }

    @Override
    public synchronized void updateArtist(Artist artist) {
        artistRepo.update(artist);
    }

    @Override
    public synchronized Artist findArtistById(Integer id) {
        return artistRepo.findById(id);
    }

    @Override
    public synchronized Iterable<Artist> getAllArtists() {
        return artistRepo.findAll();
    }

    @Override
    public synchronized void addBuyer(Buyer buyer) throws Exception {
        buyerRepo.add(buyer);
    }

    @Override
    public synchronized void removeBuyer(Integer id) {
        buyerRepo.delete(id);
    }

    @Override
    public synchronized void updateBuyer(Buyer buyer) {
        buyerRepo.update(buyer);
    }

    @Override
    public synchronized Buyer findBuyerById(Integer id) {
        return buyerRepo.findById(id);
    }

    @Override
    public synchronized Iterable<Buyer> getAllBuyers() {
        return buyerRepo.findAll();
    }

    @Override
    public synchronized void addSeller(Seller seller) throws Exception {
        seller.setPassword(Base64.getEncoder().encodeToString(seller.getPassword().getBytes(StandardCharsets.UTF_8)));
        sellerRepo.add(seller);
    }

    @Override
    public synchronized void removeSeller(Integer id) {
        sellerRepo.delete(id);
    }

    @Override
    public synchronized void updateSeller(Seller seller) {
        sellerRepo.update(seller);
    }

    @Override
    public synchronized Seller findSellerById(Integer id) {
        return sellerRepo.findById(id);
    }

    @Override
    public synchronized Iterable<Seller> getAllSellers() {
        return sellerRepo.findAll();
    }

    @Override
    public synchronized Seller findSellerByEmail(String email) {
        return StreamSupport.stream(sellerRepo.findAll().spliterator(), false)
                .filter(x -> Objects.equals(x.getEmail(), email))
                .findFirst().orElse(null);
    }

    @Override
    public synchronized void addShow(Show show) throws Exception {
        showRepo.add(show);
    }

    @Override
    public synchronized void removeShow(Integer id) {
        showRepo.delete(id);
    }

    @Override
    public synchronized void updateShow(Show show) {
        showRepo.update(show);
        notifyTicketBought();
    }

    @Override
    public synchronized Show findById(Integer id) {
        return showRepo.findById(id);
    }

    @Override
    public synchronized Iterable<Show> getAll() {
        return showRepo.findAll();
    }

    @Override
    public synchronized List<Show> findShowsByArtistId(Integer idArtist) {
        return StreamSupport
                .stream(showRepo.findAll().spliterator(), false)
                .filter(x -> (Objects.equals(x.getIdArtist(), idArtist)))
                .collect(Collectors.toList());
    }

    @Override
    public synchronized List<Show> findShowsByDay(LocalDate date) {
        return StreamSupport
                .stream(showRepo.findAll().spliterator(), false)
                .filter(x -> (LocalDateTime.ofInstant(Instant.ofEpochMilli(x.getDate()), ZoneOffset.UTC).getYear() == date.getYear() &&
                        LocalDateTime.ofInstant(Instant.ofEpochMilli(x.getDate()), ZoneOffset.UTC).getDayOfYear() == date.getDayOfYear()))
                .collect(Collectors.toList());
    }

    @Override
    public synchronized Show findShowByDateAndPlace(String date, String place) {
        for (Show show : showRepo.findAll())
            if (LocalDate.ofInstant(Instant.ofEpochMilli(show.getDate()), ZoneOffset.UTC).toString().equals(date) && Objects.equals(show.getPlace(), place))
                return show;
        return null;
    }

    private synchronized void notifyTicketBought() {
        System.out.println("Number of clients: " + loggedClients.values().size());
        ExecutorService executor = Executors.newFixedThreadPool(defaultThreadsNumber);
        for (IObserver observer : loggedClients.values()){
            executor.execute(()->{
                try {
                    System.out.println("Notifing client...");
                    observer.ticketBought();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

}

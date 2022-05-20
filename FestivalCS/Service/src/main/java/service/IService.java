package service;

import domain.Artist;
import domain.Buyer;
import domain.Seller;
import domain.Show;

import java.time.LocalDate;
import java.util.List;

public interface IService {
    public void login(Seller seller, IObserver obs) throws Exception;

    public void logout(Seller seller, IObserver obs) throws Exception;

    public void addArtist(Artist artist) throws Exception;

    public void removeArtist(Integer id);

    public void updateArtist(Artist artist);

    public Artist findArtistById(Integer id) throws Exception;

    public Iterable<Artist> getAllArtists() throws Exception;

    public void addBuyer(Buyer buyer) throws Exception;

    public void removeBuyer(Integer id);

    public void updateBuyer(Buyer buyer);

    public Buyer findBuyerById(Integer id);

    public Iterable<Buyer> getAllBuyers();

    public void addSeller(Seller seller) throws Exception;

    public void removeSeller(Integer id);

    public void updateSeller(Seller seller);

    public Seller findSellerById(Integer id);

    public Iterable<Seller> getAllSellers();

    public Seller findSellerByEmail(String email) throws Exception;

    public void addShow(Show show) throws Exception;

    public void removeShow(Integer id);

    public void updateShow(Show show) throws Exception;

    public Show findById(Integer id);

    public Iterable<Show> getAll();

    public List<Show> findShowsByArtistId(Integer idArtist) throws Exception;

    public List<Show> findShowsByDay(LocalDate date) throws Exception;

    public Show findShowByDateAndPlace(String date, String place) throws Exception;
}

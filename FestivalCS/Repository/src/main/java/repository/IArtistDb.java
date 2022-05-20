package repository;

import domain.Artist;

public interface IArtistDb extends Repository<Artist, Integer>{
    public Artist add2(Artist artist);
}

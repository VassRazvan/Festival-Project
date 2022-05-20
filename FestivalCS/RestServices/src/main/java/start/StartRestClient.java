package start;

import domain.Artist;
import restClient.ArtistsClient;

import java.time.LocalDateTime;

public class StartRestClient {
    private final static ArtistsClient artistsClient =new ArtistsClient();
    public static void main(String[] args) throws Exception {
        Artist artist = new Artist("Razvi");
        Artist artistCreeat = artistsClient.create(artist);
        System.out.println(artistCreeat.toString());
        System.out.println(artistsClient.getById("2"));
        Artist artist2 = new Artist("Zamfir2");
        artistsClient.update(artist2, "1");
        artistsClient.delete("6");
        Artist[] artists = artistsClient.getAll();
        for(Artist currentArtist : artists)
            System.out.println(currentArtist);
    }




}

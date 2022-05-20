package restClient;

import domain.Artist;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.Callable;


public class ArtistsClient {
    public static final String URL = "http://localhost:8080/restServices/artists";

    private RestTemplate restTemplate = new RestTemplate();

    private <T> T execute(Callable<T> callable) throws Exception {
        try {
            return callable.call();
        } catch (ResourceAccessException | HttpClientErrorException e) { // server down, resource exception
            throw new Exception(e);
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    public Artist[] getAll() throws Exception {
        return execute(() -> restTemplate.getForObject(URL, Artist[].class));
    }

    public Artist getById(String id) throws Exception {
        return execute(() -> restTemplate.getForObject(String.format("%s/%s", URL, id), Artist.class));
    }

    public Artist create(Artist artist) throws Exception {
        return execute(() -> restTemplate.postForObject(URL, artist, Artist.class));
    }

    public void update(Artist artist, String id) throws Exception {
        execute(() -> {
            restTemplate.put(String.format("%s/%s", URL, id), artist);
            return null;
        });
    }

    public void delete(String id) throws Exception {
        execute(() -> {
            restTemplate.delete(String.format("%s/%s", URL, id));
            return null;
        });
    }

}

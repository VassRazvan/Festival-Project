package restServices;

import domain.Artist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import repository.ArtistDb;
import start.StartRestServices;

import java.util.Collection;

@RestController
@RequestMapping("restServices/artists")
public class ArtistRestController {
    @Autowired
    private ArtistDb artistDb = new ArtistDb(StartRestServices.getBdProperties());

    @GetMapping("/test")
    public  String test(@RequestParam(value="name", defaultValue="Hello") String name) {
        return name.toUpperCase();
    }

    @PostMapping
    public Artist create(@RequestBody Artist artist) throws Exception {
        System.out.println("Creating artist");
        return artistDb.add2(artist);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public Artist update(@RequestBody Artist artist, @PathVariable int id) {
        System.out.println("Updating artist ...");
        artist.setId(id);
        artistDb.update(artist);
        return artist;

    }

    @RequestMapping(value="/{id}", method= RequestMethod.DELETE)
    public ResponseEntity<?> delete(@PathVariable int id){
        System.out.println("Deleting artist with id: "+ id);
        try {
            artistDb.delete(id);
            return new ResponseEntity<Artist>(HttpStatus.OK);
        }catch (Exception ex){
            System.out.println("Ctrl Delete artist exception");
            return new ResponseEntity<String>(ex.getMessage(),HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(method = RequestMethod.GET)
    public Collection<Artist> getAll(){
        System.out.println("Getting artists");
        return (Collection<Artist>) artistDb.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer id){
        System.out.println("Get by id "+id);
        Artist request= artistDb.findById(id);
        if (request==null)
            return new ResponseEntity<String>("Artist not found", HttpStatus.NOT_FOUND);
        else
            return new ResponseEntity<Artist>(request, HttpStatus.OK);
    }
}

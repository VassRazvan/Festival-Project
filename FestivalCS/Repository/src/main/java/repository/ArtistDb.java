package repository;

import domain.Artist;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Component
public class ArtistDb implements IArtistDb {

    private JdbcUtils dbUtils;
    private static final Logger logger= LogManager.getLogger();

    public ArtistDb(Properties props) {
        logger.info("Initializing ArtistBd with properties: {} ",props);
        dbUtils=new JdbcUtils(props);
    }


    @Override
    public void add(Artist artist){
        logger.traceEntry("saving artist{}", artist);
        Connection con = dbUtils.getConnection();
        try(PreparedStatement preStmt = con.prepareStatement("insert into artists(name) values (?)")){
            preStmt.setString(1, artist.getName());
            int result=preStmt.executeUpdate();
            logger.trace("Saved {} instances", result);
        } catch (SQLException ex) {
            logger.error(ex);
            System.err.println("Error DB"+ex);
        }

    }

    @Override
    public Artist add2(Artist artist){
        logger.traceEntry("saving artist{}", artist);
        Connection con = dbUtils.getConnection();
        try(PreparedStatement preStmt = con.prepareStatement("insert into artists(name) values (?)")){
            preStmt.setString(1, artist.getName());
            int result=preStmt.executeUpdate();
            if(result > 0){
                ResultSet rs = preStmt.getGeneratedKeys();
                if(rs.next()){
                    int id = rs.getInt(1);
                    artist.setId(id);
                    return artist;
                }
            }
            logger.trace("Saved {} instances", result);
        } catch (SQLException ex) {
            logger.error(ex);
            System.err.println("Error DB"+ex);
        }
        logger.traceExit();
        return null;
    }

    @Override
    public void delete(Integer id) {
        logger.traceEntry("deleting artist with id{}", id);
        Connection con = dbUtils.getConnection();
        try(PreparedStatement preStmt = con.prepareStatement("delete from artists where id=?")){
            preStmt.setInt(1, id);
            int result=preStmt.executeUpdate();
            logger.trace("Deleted {} instances", result);
        } catch (SQLException ex) {
            logger.error(ex);
            System.err.println("Error DB"+ex);
        }

    }

    @Override
    public void update(Artist artist) {
        logger.traceEntry("update challenge{}", artist);
        Connection con = dbUtils.getConnection();
        try(PreparedStatement preStmt = con.prepareStatement("update artists set name=? where id = ?")){
            preStmt.setString(1, artist.getName());
            preStmt.setInt(2, artist.getId());
            int result=preStmt.executeUpdate();
            logger.trace("Saved {} instances", result);
        } catch (SQLException ex) {
            logger.error(ex);
            System.err.println("Error DB"+ex);
        }
    }

    @Override
    public Artist findById(Integer id) {
        logger.traceEntry();
        Connection con=dbUtils.getConnection();
        Artist artist = null;
        try(PreparedStatement preStmt = con.prepareStatement("select * from artists where id = ?")){
            preStmt.setInt(1,id);
            try(ResultSet result = preStmt.executeQuery()){
                String name = result.getString("name");
                artist = new Artist(name);
                artist.setId(id);
            }
        } catch (SQLException e) {
            logger.error(e);
            System.err.println("Erorr DB"+e);
        }

        logger.traceExit(artist);
        return artist;

    }

    @Override
    public Iterable<Artist> findAll() {
        logger.traceEntry();
        Connection con=dbUtils.getConnection();
        List<Artist> challenges = new ArrayList<>();
        try(PreparedStatement preStmt = con.prepareStatement("select * from artists")){
            try(ResultSet result = preStmt.executeQuery()){
                while (result.next()){
                    int id = result.getInt("id");
                    String name = result.getString("name");
                    Artist artist = new Artist(name);
                    artist.setId(id);
                    challenges.add(artist);
                }
            }
        } catch (SQLException e) {
            logger.error(e);
            System.err.println("Erorr DB"+e);
        }
        logger.traceExit(challenges);
        return challenges;
    }
}

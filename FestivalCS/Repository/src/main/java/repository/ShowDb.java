package repository;

import domain.Show;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class ShowDb implements IShowDb {

    private JdbcUtils dbUtils;
    private static final Logger logger= LogManager.getLogger();

    public ShowDb(Properties props) {
        logger.info("Initializing ShowDb with properties: {} ",props);
        dbUtils=new JdbcUtils(props);
    }

    public void add(Show show){
        logger.traceEntry("saving show{}", show);
        Connection con = dbUtils.getConnection();
        try(PreparedStatement preStmt = con.prepareStatement("insert into shows(place, date, idArtist, soldSeats, totalSeats) values (?,?,?,?,?)")){
            preStmt.setString(1, show.getPlace());
            preStmt.setLong(2, show.getDate());
            preStmt.setInt(3, show.getIdArtist());
            preStmt.setInt(4, show.getSoldSeats());
            preStmt.setInt(5, show.getTotalSeats());
            int result=preStmt.executeUpdate();
            logger.trace("Saved {} instances", result);
        } catch (SQLException ex) {
            logger.error(ex);
            System.err.println("Error DB"+ex);
        }

    }

    public void delete(Integer id){
        logger.traceEntry("deleting show with id{}", id);
        Connection con = dbUtils.getConnection();
        try(PreparedStatement preStmt = con.prepareStatement("delete from shows where id=?")){
            preStmt.setInt(1, id);
            int result=preStmt.executeUpdate();
            logger.trace("Deleted {} instances", result);
        } catch (SQLException ex) {
            logger.error(ex);
            System.err.println("Error DB"+ex);
        }


    }

    public void update(Show show){
        logger.traceEntry("update show{}", show);
        Connection con = dbUtils.getConnection();
        try(PreparedStatement preStmt = con.prepareStatement("update shows set place=?, date=?, idArtist=?, soldSeats=?, totalSeats=? where id = ?")){
            preStmt.setString(1, show.getPlace());
            preStmt.setLong(2, show.getDate());
            preStmt.setInt(3, show.getIdArtist());
            preStmt.setInt(4, show.getSoldSeats());
            preStmt.setInt(5, show.getTotalSeats());
            preStmt.setInt(6, show.getId());
            int result=preStmt.executeUpdate();
            logger.trace("Saved {} instances", result);
        } catch (SQLException ex) {
            logger.error(ex);
            System.err.println("Error DB"+ex);
        }

    }

    public Show findById(Integer id){
        logger.traceEntry();
        Connection con=dbUtils.getConnection();
        Show show = null;
        try(PreparedStatement preStmt = con.prepareStatement("select * from shows where id = ?")){
            preStmt.setInt(1,id);
            try(ResultSet result = preStmt.executeQuery()){

                String place = result.getString("place");
                long date = result.getLong("date");
                int idArtist = result.getInt("idArtist");
                int soldSeats = result.getInt("soldSeats");
                int totalSeats = result.getInt("totalSeats");
                show = new Show(place, date, idArtist, soldSeats, totalSeats);
                show.setId(id);
            }
        } catch (SQLException e) {
            logger.error(e);
            System.err.println("Erorr DB"+e);
        }

        logger.traceExit(show);
        return show;
    }

    public Iterable<Show> findAll(){
        logger.traceEntry();
        Connection con=dbUtils.getConnection();
        List<Show> shows = new ArrayList<>();
        try(PreparedStatement preStmt = con.prepareStatement("select * from shows")){
            try(ResultSet result = preStmt.executeQuery()){
                while (result.next()){
                    int id = result.getInt("id");
                    String place = result.getString("place");
                    long date = result.getLong("date");
                    int idArtist = result.getInt("idArtist");
                    int soldSeats = result.getInt("soldSeats");
                    int totalSeats = result.getInt("totalSeats");
                    Show show = new Show(place, date, idArtist, soldSeats, totalSeats);
                    show.setId(id);
                    shows.add(show);
                }

            }
        } catch (SQLException e) {
            logger.error(e);
            System.err.println("Erorr DB"+e);
        }

        logger.traceExit(shows);
        return shows;

    }
}

package repository;

import domain.Buyer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class BuyerDb implements IBuyerDb {

    private JdbcUtils dbUtils;
    private static final Logger logger= LogManager.getLogger();

    public BuyerDb(Properties props) {
        logger.info("Initializing BuyerDb with properties: {} ",props);
        dbUtils=new JdbcUtils(props);
    }


    @Override
    public void add(Buyer buyer){
        logger.traceEntry("saving challenge{}", buyer);
        Connection con = dbUtils.getConnection();
        try(PreparedStatement preStmt = con.prepareStatement("insert into buyers(name, numberOfSeats, idShow) values (?,?,?)")){
            preStmt.setString(1, buyer.getName());
            preStmt.setInt(2, buyer.getNumberOfSeats());
            preStmt.setInt(3, buyer.getIdShow());
            int result=preStmt.executeUpdate();
            logger.trace("Saved {} instances", result);
        } catch (SQLException ex) {
            logger.error(ex);
            System.err.println("Error DB"+ex);
        }

    }


    @Override
    public void delete(Integer id) {
        logger.traceEntry("deleting buyer with id{}", id);
        Connection con = dbUtils.getConnection();
        try(PreparedStatement preStmt = con.prepareStatement("delete from buyers where id=?")){
            preStmt.setInt(1, id);
            int result=preStmt.executeUpdate();
            logger.trace("Deleted {} instances", result);
        } catch (SQLException ex) {
            logger.error(ex);
            System.err.println("Error DB"+ex);
        }

    }

    @Override
    public void update(Buyer buyer) {
        logger.traceEntry("update buyer{}", buyer);
        Connection con = dbUtils.getConnection();
        try(PreparedStatement preStmt = con.prepareStatement("update buyers set name=?, numberOfSeats=?, idShow=? where id = ?")){
            preStmt.setString(1, buyer.getName());
            preStmt.setInt(2, buyer.getNumberOfSeats());
            preStmt.setInt(3, buyer.getIdShow());
            preStmt.setInt(4, buyer.getId());
            int result=preStmt.executeUpdate();
            logger.trace("Saved {} instances", result);
        } catch (SQLException ex) {
            logger.error(ex);
            System.err.println("Error DB"+ex);
        }
    }

    @Override
    public Buyer findById(Integer id) {
        logger.traceEntry();
        Connection con=dbUtils.getConnection();
        Buyer buyer = null;
        try(PreparedStatement preStmt = con.prepareStatement("select * from buyers where id = ?")){
            preStmt.setInt(1,id);
            try(ResultSet result = preStmt.executeQuery()){
                String name = result.getString("name");
                int numberOfSeats = result.getInt("numberOfSeats");
                int idShow = result.getInt("idShow");
                buyer = new Buyer(name, numberOfSeats, idShow);
                buyer.setId(id);
            }
        } catch (SQLException e) {
            logger.error(e);
            System.err.println("Erorr DB"+e);
        }

        logger.traceExit(buyer);
        return buyer;

    }

    @Override
    public Iterable<Buyer> findAll() {
        logger.traceEntry();
        Connection con=dbUtils.getConnection();
        List<Buyer> buyers = new ArrayList<>();
        try(PreparedStatement preStmt = con.prepareStatement("select * from buyers")){
            try(ResultSet result = preStmt.executeQuery()){
                while (result.next()){
                    int id = result.getInt("id");
                    String name = result.getString("name");
                    int numberOfSeats = result.getInt("numberOfSeats");
                    int idShow = result.getInt("idShow");
                    Buyer artist = new Buyer(name, numberOfSeats, idShow);
                    artist.setId(id);
                    buyers.add(artist);
                }
            }
        } catch (SQLException e) {
            logger.error(e);
            System.err.println("Erorr DB"+e);
        }
        logger.traceExit(buyers);
        return buyers;
    }
}

package repository;

import domain.Seller;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class SellerDb implements ISellerDb {

    private JdbcUtils dbUtils;
    private static final Logger logger= LogManager.getLogger();

    public SellerDb(Properties props) {
        logger.info("Initializing SellerDb with properties: {} ",props);
        dbUtils=new JdbcUtils(props);
    }

    public void add(Seller seller){
        logger.traceEntry("saving seller{}", seller);
        Connection con = dbUtils.getConnection();
        try(PreparedStatement preStmt = con.prepareStatement("insert into sellers(email, password) values (?,?)")){
            preStmt.setString(1, seller.getEmail());
            preStmt.setString(2, seller.getPassword());
            int result=preStmt.executeUpdate();
            logger.trace("Saved {} instances", result);
        } catch (SQLException ex) {
            logger.error(ex);
            System.err.println("Error DB"+ex);
        }

    }

    public void delete(Integer id){
        logger.traceEntry("deleting seller with id{}", id);
        Connection con = dbUtils.getConnection();
        try(PreparedStatement preStmt = con.prepareStatement("delete from sellers where id=?")){
            preStmt.setInt(1, id);
            int result=preStmt.executeUpdate();
            logger.trace("Deleted {} instances", result);
        } catch (SQLException ex) {
            logger.error(ex);
            System.err.println("Error DB"+ex);
        }


    }

    public void update(Seller seller){
        logger.traceEntry("update seller{}", seller);
        Connection con = dbUtils.getConnection();
        try(PreparedStatement preStmt = con.prepareStatement("update sellers set email=?, password=? where id = ?")){
            preStmt.setString(1, seller.getEmail());
            preStmt.setString(2, seller.getPassword());
            preStmt.setInt(3, seller.getId());
            int result=preStmt.executeUpdate();
            logger.trace("Saved {} instances", result);
        } catch (SQLException ex) {
            logger.error(ex);
            System.err.println("Error DB"+ex);
        }

    }

    public Seller findById(Integer id){
        logger.traceEntry();
        Connection con=dbUtils.getConnection();
        Seller seller = null;
        try(PreparedStatement preStmt = con.prepareStatement("select * from sellers where id = ?")){
            preStmt.setInt(1,id);
            try(ResultSet result = preStmt.executeQuery()){

                String email = result.getString("email");
                String password = result.getString("password");
                seller = new Seller(email, password);
                seller.setId(id);
            }
        } catch (SQLException e) {
            logger.error(e);
            System.err.println("Erorr DB"+e);
        }
        logger.traceExit(seller);
        return seller;
    }

    public Iterable<Seller> findAll(){
        logger.traceEntry();
        Connection con=dbUtils.getConnection();
        List<Seller> sellers = new ArrayList<>();
        try(PreparedStatement preStmt = con.prepareStatement("select * from sellers")){
            try(ResultSet result = preStmt.executeQuery()){
                while (result.next()){
                    int id = result.getInt("id");
                    String email = result.getString("email");
                    String password = result.getString("password");
                    Seller seller = new Seller(email, password);
                    seller.setId(id);
                    sellers.add(seller);
                }
            }
        } catch (SQLException e) {
            logger.error(e);
            System.err.println("Erorr DB"+e);
        }
        logger.traceExit(sellers);
        return sellers;
    }
}

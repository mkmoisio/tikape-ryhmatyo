package tikape.runko.database;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import tikape.runko.domain.*;

public class AlueDao implements Dao<Alue, Integer>{
    private Database database;
    
    public AlueDao(Database database) {
        this.database = database;
    }
    
    @Override
    public Alue create(Alue t) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Alue findOne(Integer key) throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Alue WHERE tunnus = ?");
        stmt.setInt(1, key);

        ResultSet rs = stmt.executeQuery();
        boolean hasOne = rs.next();
        
        if (!hasOne) {
            return null;
        }

        String nimi = rs.getString("nimi");
        String kuvaus = rs.getString("kuvaus");

        Alue alue = new Alue(key, nimi, kuvaus);

        rs.close();
        stmt.close();
        connection.close();

        return alue;
    }

    @Override
    public List<Alue> findAll() throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Alue");
        
        ResultSet rs = stmt.executeQuery();
        
        List<Alue> alueet = new ArrayList();
        
        while (rs.next()) {
            Integer tunnus = rs.getInt("tunnus");
            String nimi = rs.getString("nimi");
            String kuvaus = rs.getString("kuvaus");
            
            alueet.add(new Alue(tunnus, nimi, kuvaus));
        }
        
        rs.close();
        stmt.close();
        connection.close();
        
        return alueet;
    }
    
    public List<Alue> findAllIn(Collection<Integer> keys) throws SQLException {
        if (keys.isEmpty()) {
            return new ArrayList<>();
        }

        // Luodaan IN-kyselyä varten paikat, joihin arvot asetetaan --
        // toistaiseksi IN-parametrille ei voi antaa suoraan kokoelmaa
        StringBuilder arvot = new StringBuilder("?");
        for (int i = 1; i < keys.size(); i++) {
            arvot.append(", ?");
        }

        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Alue WHERE tunnus IN (" + arvot + ")");
        int laskuri = 1;
        
        for (Integer key : keys) {
            stmt.setInt(laskuri, key);
            laskuri++;
        }

        ResultSet rs = stmt.executeQuery();
        List<Alue> alueet = new ArrayList<>();
        
        while (rs.next()) {
            Integer tunnus = rs.getInt("tunnus");
            String nimi = rs.getString("nimi");
            String kuvaus = rs.getString("kuvaus");
            
            alueet.add(new Alue(tunnus, nimi, kuvaus));
        }
        
        rs.close();
        stmt.close();
        connection.close();

        return alueet;
    }
    
    @Override
    public void update(Integer key, Alue t) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}

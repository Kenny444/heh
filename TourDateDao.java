package com.heh.dao;

import com.heh.pojo.TourDate;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import javax.naming.NamingException;

/**
 *
 * @author kennethharris
 */
public class TourDateDao extends Dao {

    public TourDateDao(String jndiName) throws NamingException {
        super(jndiName);
    }

    public List<TourDate> getTourDates() throws SQLException {
        ResultSet results = null;
        PreparedStatement stmt = null;
        Connection conn = null;
        try {
            conn = ds.getConnection();
            stmt = conn.prepareStatement("select * from hemh.tour_dates");
            results = stmt.executeQuery();
            List<TourDate> tourDates = new LinkedList<>();
            while (results.next()) {
                TourDate tourDate = buildTourDate(results);
                tourDates.add(tourDate);
            }
            return tourDates;
        } finally {
            closeResources(results, stmt, conn);
        }
    }

    private TourDate buildTourDate(ResultSet results) throws SQLException {
        TourDate tourDate = new TourDate();
        tourDate.setKey(results.getInt("hemh_key"));
        tourDate.setDate(results.getString("hemh_date"));
        tourDate.setLocation(results.getString("hemh_location"));
        tourDate.setCity(results.getString("hemh_city"));
        tourDate.setState(results.getString("hemh_state"));
        tourDate.setPurchase(results.getString("hemh_purchase"));
        return tourDate;
    }

    public TourDate getTourDate(int key) throws SQLException {
        ResultSet results = null;
        PreparedStatement stmt = null;
        Connection conn = null;
        try {
            conn = ds.getConnection();
            stmt = conn.prepareStatement("SELECT * FROM hemh.tour_dates where hemh_key = ?");
            stmt.setInt(1, key);
            results = stmt.executeQuery();
            if (results.next()) {
                TourDate tourDates = buildTourDate(results);
                return tourDates;
            } else {
                return null;
            }
        } finally {
            closeResources(results, stmt, conn);
        }
    }

    public int insertTourDate(TourDate tourDate) throws SQLException {
        PreparedStatement stmt = null;
        Connection conn = null;
        try {
            conn = ds.getConnection();
            stmt = conn.prepareStatement("insert into hemh.tour_dates (hemh_date, hemh_location, "
                    + "hemh_city, hemh_state, hemh_purchase) values (?, ?, ?, ?, ?)");
            int i = 1;
            stmt.setString(i++, tourDate.getDate());
            stmt.setString(i++, tourDate.getLocation());
            stmt.setString(i++, tourDate.getCity());
            stmt.setString(i++, tourDate.getState());
            stmt.setString(i++, tourDate.getPurchase());
            int count = stmt.executeUpdate();
            return count;
        } finally {
            closeResources(null, stmt, conn);
        }
    }

    public int updateTourDate(TourDate tourDate) throws SQLException {
        PreparedStatement stmt = null;
        Connection conn = null;
        try {
            conn = ds.getConnection();
            stmt = conn.prepareStatement("update hemh.tour_dates set hemh_date = ?, hemh_location = ?, "
                    + "hemh_city = ?, hemh_state = ?, hemh_purchase = ? where hemh_key = ?");
            int i = 1;
            stmt.setString(i++, tourDate.getDate());
            stmt.setString(i++, tourDate.getLocation());
            stmt.setString(i++, tourDate.getCity());
            stmt.setString(i++, tourDate.getState());
            stmt.setString(i++, tourDate.getPurchase());
            stmt.setInt(i++, tourDate.getKey());
            int count = stmt.executeUpdate();
            return count;
        } finally {
            closeResources(null, stmt, conn);
        }
    }

    public int deleteTourDate(int key) throws SQLException {
        PreparedStatement stmt = null;
        Connection conn = null;
        try {
            conn = ds.getConnection();
            stmt = conn.prepareStatement("delete from hemh.tour_dates where hemh_key = ?");
            stmt.setInt(1, key);
            int count = stmt.executeUpdate();
            return count;
        } finally {
            closeResources(null, stmt, conn);
        }
    }
}

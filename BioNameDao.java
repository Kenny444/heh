package com.heh.dao;

import com.heh.pojo.BioName;
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
public class BioNameDao extends Dao {

    public BioNameDao(String jndiName) throws NamingException {
        super(jndiName);
    }

    public List<BioName> getBioNames() throws SQLException {
        ResultSet results = null;
        PreparedStatement stmt = null;
        Connection conn = null;
        try {
            conn = ds.getConnection();
            stmt = conn.prepareStatement("select * from hemh.bio_names");
            results = stmt.executeQuery();
            List<BioName> bioNames = new LinkedList<>();
            while (results.next()) {
                BioName bioName = buildBioName(results);
                bioNames.add(bioName);
            }
            return bioNames;
        } finally {
            closeResources(results, stmt, conn);
        }

    }

    private BioName buildBioName(ResultSet results) throws SQLException {
        BioName bioName = new BioName();
        bioName.setBioKey(results.getInt("hemh_biokey"));
        bioName.setPerson(results.getString("hemh_person"));
        bioName.setDesc(results.getString("hemh_desc"));
        bioName.setStatus(results.getString("hemh_status"));
        bioName.setInstrument(results.getString("hemh_instrument"));
        return bioName;
    }

    public BioName getBioName(int bioKey) throws SQLException {
        ResultSet results = null;
        PreparedStatement stmt = null;
        Connection conn = null;
        try {
            conn = ds.getConnection();
            stmt = conn.prepareStatement("select * from hemh.bio_names where hemh_biokey = ?");
            stmt.setInt(1, bioKey);
            results = stmt.executeQuery();
            if (results.next()) {
                BioName bioNames = buildBioName(results);
                return bioNames;
            } else {
                return null;
            }
        } finally {
            closeResources(results, stmt, conn);

        }
    }

    public int insertBioName(BioName bioName) throws SQLException {
        PreparedStatement stmt = null;
        Connection conn = null;
        try {
            conn = ds.getConnection();
            stmt = conn.prepareStatement("insert into hemh.bio_names (hemh_person, hemh_desc, "
                    + "hemh_status, hemh_instrument) values (?, ?, ?, ?)");
            int i = 1;
            stmt.setString(i++, bioName.getPerson());
            stmt.setString(i++, bioName.getDesc());
            stmt.setString(i++, bioName.getStatus());
            stmt.setString(i++, bioName.getInstrument());
            int count = stmt.executeUpdate();
            return count;
        } finally {
            closeResources(null, stmt, conn);
        }
    }

    public int updateBioName(BioName bioName) throws SQLException {
        PreparedStatement stmt = null;
        Connection conn = null;
        try {
            conn = ds.getConnection();
            stmt = conn.prepareStatement("update hemh.bio_names set hemh_person = ?, hemh_desc = ?, "
                    + "hemh_status = ?, hemh_instrument = ? where hemh_biokey = ?");
            int i = 1;
            stmt.setString(i++, bioName.getPerson());
            stmt.setString(i++, bioName.getDesc());
            stmt.setString(i++, bioName.getStatus());
            stmt.setString(i++, bioName.getInstrument());
            stmt.setInt(i++, bioName.getBioKey());
            int count = stmt.executeUpdate();
            return count;
        } finally {
            closeResources(null, stmt, conn);
        }
    }

    public int deleteBioName(int bioKey) throws SQLException {
        PreparedStatement stmt = null;
        Connection conn = null;
        try {
            conn = ds.getConnection();
            stmt = conn.prepareStatement("delete from hemh.bio_names where hemh_biokey = ?");
            stmt.setInt(1, bioKey);
            int count = stmt.executeUpdate();
            return count;
        } finally {
            closeResources(null, stmt, conn);
        }
    }
}

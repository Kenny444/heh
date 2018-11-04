package com.heh.dao;

import com.heh.pojo.News;
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
public class NewsDao extends Dao {

    public NewsDao(String jndiName) throws NamingException {
        super(jndiName);
    }

    public List<News> getNewsInfo() throws SQLException {
        ResultSet results = null;
        PreparedStatement stmt = null;
        Connection conn = null;
        try {
            conn = ds.getConnection();
            stmt = conn.prepareStatement("select * from hemh.news");
            results = stmt.executeQuery();
            List<News> newsInfos = new LinkedList<>();
            while (results.next()) {
                News newsInfo = buildNewsInfo(results);
                newsInfos.add(newsInfo);
            }
            return newsInfos;
        } finally {
            closeResources(results, stmt, conn);
        }
    }

    private News buildNewsInfo(ResultSet results) throws SQLException {
        News newsInfo = new News();
        newsInfo.setNewsKey(results.getInt("news_key"));
        newsInfo.setNewsInfo(results.getString("news_info"));
        return newsInfo;
    }

    public News getNewsInfo(int newsKey) throws SQLException {
        ResultSet results = null;
        PreparedStatement stmt = null;
        Connection conn = null;
        try {
            conn = ds.getConnection();
            stmt = conn.prepareStatement("select * from hemh.news where news_key = ?");
            stmt.setInt(1, newsKey);
            results = stmt.executeQuery();
            if (results.next()) {
                News newsInfos = buildNewsInfo(results);
                return newsInfos;
            } else {
                return null;
            }
        } finally {
            closeResources(results, stmt, conn);
        }
    }

    public int insertNewsInfo(News newsInfo) throws SQLException {
        PreparedStatement stmt = null;
        Connection conn = null;
        try {
            conn = ds.getConnection();
            stmt = conn.prepareStatement("insert into hemh.news (news_info) values (?)");
            int i = 1;
            stmt.setString(i++, newsInfo.getNewsInfo());
            int count = stmt.executeUpdate();
            return count;
        } finally {
            closeResources(null, stmt, conn);
        }
    }

    public int updateNewsInfo(News newsInfo) throws SQLException {
        PreparedStatement stmt = null;
        Connection conn = null;
        try {
            conn = ds.getConnection();
            stmt = conn.prepareStatement("update hemh.news set news_info = ? where news_key = ?");
            int i = 1;
            stmt.setString(i++, newsInfo.getNewsInfo());
            stmt.setInt(i++, newsInfo.getNewsKey());
            int count = stmt.executeUpdate();
            return count;
        } finally {
            closeResources(null, stmt, conn);
        }
    }

    public int deleteNewsInfo(int newsKey) throws SQLException {
        PreparedStatement stmt = null;
        Connection conn = null;
        try {
            conn = ds.getConnection();
            stmt = conn.prepareStatement("delete from hemh.news where news_key = ?");
            stmt.setInt(1, newsKey);
            int count = stmt. executeUpdate();
            return count;
        } finally {
            closeResources(null, stmt, conn);
        }
    }
}

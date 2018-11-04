package com.heh.controller.servlet;

import com.heh.dao.BioNameDao;
import com.heh.dao.NewsDao;
import com.heh.dao.TourDateDao;
import com.heh.pojo.BioName;
import com.heh.pojo.News;
import com.heh.pojo.TourDate;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author kennethharris
 */
@WebServlet(name = "TourDatesServlet", urlPatterns = {"/tourdates"})
public class TourDateServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        String action = request.getParameter("action");
        try {
            if ("getTourDates".equals(action)) {
                getTourDates(request, response);
            } else if ("insertTourDate".equals(action)) {
                insertTourDate(request, response);
            } else if ("updateTourDate".equals(action)) {
                updateTourDate(request, response);
            } else if ("deleteTourDate".equals(action)) {
                deleteTourDate(request, response);
            } else if ("getBioNames".equals(action)) {
                getBioNames(request, response);
            } else if ("insertBioName".equals(action)) {
                insertBioName(request, response);
            } else if ("updateBioName".equals(action)) {
                updateBioName(request, response);
            } else if ("deleteBioName".equals(action)) {
                deleteBioName(request, response);
            } else if ("getNewsInfo".equals(action)) {
                getNewsInfo(request, response);
            } else if ("insertNewsInfo".equals(action)) {
                insertNewsInfo(request, response);
            } else if ("updateNewsInfo".equals(action)) {
                updateNewsInfo(request, response);
            } else if ("deleteNewsInfo".equals(action)) {
                deleteNewsInfo(request, response);
            } else {
                sendErrorJson(response, "Uknown Action");
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendErrorJson(response, "Could not process your request.");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

    private void sendDefaultOkJson(HttpServletResponse response) throws IOException {
        JSONObject respJson = new JSONObject();
        respJson.put("status", true);
        response.getWriter().write(respJson.toString());
    }

    private void sendErrorJson(HttpServletResponse response, String msg) throws IOException {
        JSONObject respJson = new JSONObject();
        respJson.put("status", false);
        respJson.put("message", msg);
        response.getWriter().write(respJson.toString());
    }

    private void insertTourDate(HttpServletRequest request, HttpServletResponse response) throws IOException, NamingException, SQLException {
        String tourDateDate = request.getParameter("hemh_date");
        String tourDateLocation = request.getParameter("hemh_location");
        String tourDateCity = request.getParameter("hemh_city");
        String tourDateState = request.getParameter("hemh_state");
        String tourDatePurchase = request.getParameter("hemh_purchase");
        TourDateDao dao = new TourDateDao("jdbc/tourDate");
        TourDate tourDate = new TourDate();
        tourDate.setDate(tourDateDate);
        tourDate.setLocation(tourDateLocation);
        tourDate.setCity(tourDateCity);
        tourDate.setState(tourDateState);
        tourDate.setPurchase(tourDatePurchase);
        int count = dao.insertTourDate(tourDate);
        if (count > 0) {
            sendDefaultOkJson(response);
        } else {
            sendErrorJson(response, "Could not add new tour date");
        }
    }

    private void updateTourDate(HttpServletRequest request, HttpServletResponse response) throws IOException, NamingException, SQLException {
        Integer tourDateKey = Integer.parseInt(request.getParameter("hemh_key"));
        String tourDateDate = request.getParameter("hemh_date");
        String tourDateLocation = request.getParameter("hemh_location");
        String tourDateCity = request.getParameter("hemh_city");
        String tourDateState = request.getParameter("hemh_state");
        String tourDatePurchase = request.getParameter("hemh_purchase");
        TourDateDao dao = new TourDateDao("jdbc/tourDate");
        TourDate tourDate = new TourDate();
        tourDate.setKey(tourDateKey);
        tourDate.setDate(tourDateDate);
        tourDate.setLocation(tourDateLocation);
        tourDate.setCity(tourDateCity);
        tourDate.setState(tourDateState);
        tourDate.setPurchase(tourDatePurchase);
        int count = dao.updateTourDate(tourDate);
        if (count > 0) {
            sendDefaultOkJson(response);
        } else {
            sendErrorJson(response, "Could not update tour date");
        }
    }

    private void getTourDates(HttpServletRequest request, HttpServletResponse response) throws NamingException, IOException, SQLException {
        TourDateDao dao = new TourDateDao("jdbc/tourDate");
        List<TourDate> tourDates = dao.getTourDates();
        JSONObject respJson = new JSONObject();
        respJson.put("status", true);
        JSONArray tourInfoJson = new JSONArray();
        respJson.put("tourInfo", tourInfoJson);
        for (TourDate tourDate : tourDates) {
            JSONObject tourDateJson = new JSONObject();
            tourInfoJson.put(tourDateJson);
            tourDateJson.put("tourDateKey", tourDate.getKey());
            tourDateJson.put("tourDateDate", tourDate.getDate());
            tourDateJson.put("tourDateLocation", tourDate.getLocation());
            tourDateJson.put("tourDateCity", tourDate.getCity());
            tourDateJson.put("tourDateState", tourDate.getState());
            tourDateJson.put("tourDatePurchase", tourDate.getPurchase());
        }
        response.getWriter().write(respJson.toString());
    }

    private void deleteTourDate(HttpServletRequest request, HttpServletResponse response) throws NamingException, IOException, SQLException {
        String key = request.getParameter("hemh_key");
        int tourDateKey = Integer.parseInt(key);
        TourDateDao dao = new TourDateDao("jdbc/tourDate");
        int count = dao.deleteTourDate(tourDateKey);
        if (count > 0) {
            sendDefaultOkJson(response);
        } else {
            sendErrorJson(response, "Could not delete date");
        }
    }

    private void getBioNames(HttpServletRequest request, HttpServletResponse response) throws NamingException, SQLException, IOException {
        BioNameDao dao = new BioNameDao("jdbc/tourDate");
        List<BioName> bioNames = dao.getBioNames();
        JSONObject respJson = new JSONObject();
        respJson.put("status", true);
        JSONArray bioInfoJson = new JSONArray();
        respJson.put("bioInfo", bioInfoJson);
        for (BioName bioName : bioNames) {
            JSONObject bioNameJson = new JSONObject();
            bioInfoJson.put(bioNameJson);
            bioNameJson.put("bioNameKey", bioName.getBioKey());
            bioNameJson.put("bioNamePerson", bioName.getPerson());
            bioNameJson.put("bioNameDesc", bioName.getDesc());
            bioNameJson.put("bioNameStatus", bioName.getStatus());
            bioNameJson.put("bioNameInstrument", bioName.getInstrument());
        }
        response.getWriter().write(respJson.toString());
    }

    private void insertBioName(HttpServletRequest request, HttpServletResponse response) throws NamingException, SQLException, IOException {
        String bioNamePerson = request.getParameter("hemh_person");
        String bioNameDesc = request.getParameter("hemh_desc");
        String bioNameStatus = request.getParameter("hemh_status");
        String bioNameInstrument = request.getParameter("hemh_instrument");
        BioNameDao dao = new BioNameDao("jdbc/tourDate");
        BioName bioName = new BioName();
        bioName.setPerson(bioNamePerson);
        bioName.setDesc(bioNameDesc);
        bioName.setStatus(bioNameStatus);
        bioName.setInstrument(bioNameInstrument);
        int count = dao.insertBioName(bioName);
        if (count > 0) {
            sendDefaultOkJson(response);
        } else {
            sendErrorJson(response, "Could not add new member.");
        }
    }

    private void updateBioName(HttpServletRequest request, HttpServletResponse response) throws NamingException, SQLException, IOException {
        Integer bioNameKey = Integer.parseInt(request.getParameter("hemh_biokey"));
        String bioNamePerson = request.getParameter("hemh_person");
        String bioNameDesc = request.getParameter("hemh_desc");
        String bioNameStatus = request.getParameter("hemh_status");
        String bioNameInstrument = request.getParameter("hemh_instrument");
        BioNameDao dao = new BioNameDao("jdbc/tourDate");
        BioName bioName = new BioName();
        bioName.setBioKey(bioNameKey);
        bioName.setPerson(bioNamePerson);
        bioName.setDesc(bioNameDesc);
        bioName.setStatus(bioNameStatus);
        bioName.setInstrument(bioNameInstrument);
        int count = dao.updateBioName(bioName);
        if (count > 0) {
            sendDefaultOkJson(response);
        } else {
            sendErrorJson(response, "Could not update member information.");
        }
    }

    private void deleteBioName(HttpServletRequest request, HttpServletResponse response) throws NamingException, SQLException, IOException {
        String bioKey = request.getParameter("hemh_biokey");
        int bioNameKey = Integer.parseInt(bioKey);
        BioNameDao dao = new BioNameDao("jdbc/tourDate");
        int count = dao.deleteBioName(bioNameKey);
        if (count > 0) {
            sendDefaultOkJson(response);
        } else {
            sendErrorJson(response, "Could not delete member");
        }
    }

    private void getNewsInfo(HttpServletRequest request, HttpServletResponse response) throws NamingException, SQLException, IOException {
        NewsDao dao = new NewsDao("jdbc/tourDate");
        List<News> newsInfos = dao.getNewsInfo();
        JSONObject respJson = new JSONObject();
        respJson.put("status", true);
        JSONArray newsInfoJson = new JSONArray();
        respJson.put("newsInfo", newsInfoJson);
        for (News newsInfo : newsInfos) {
            JSONObject newsJson = new JSONObject();
            newsInfoJson.put(newsJson);
            newsJson.put("newsKey", newsInfo.getNewsKey());
            newsJson.put("newsInfo", newsInfo.getNewsInfo());
        }
        response.getWriter().write(respJson.toString());
    }

    private void insertNewsInfo(HttpServletRequest request, HttpServletResponse response) throws NamingException, SQLException, IOException {
        String newsInfo = request.getParameter("news_info");
        NewsDao dao = new NewsDao("jdbc/tourDate");
        News news = new News();
        news.setNewsInfo(newsInfo);
        int count = dao.insertNewsInfo(news);
        if (count > 0) {
            sendDefaultOkJson(response);
        } else {
            sendErrorJson(response, "Could not add news information.");
        }
    }

    private void updateNewsInfo(HttpServletRequest request, HttpServletResponse response) throws NamingException, IOException, SQLException {
        Integer newsKey = Integer.parseInt(request.getParameter("news_key"));
        String newsInfo = request.getParameter("news_info");
        NewsDao dao = new NewsDao("jdbc/tourDate");
        News news = new News();
        news.setNewsKey(newsKey);
        news.setNewsInfo(newsInfo);
        int count = dao.updateNewsInfo(news);
        if (count > 0) {
            sendDefaultOkJson(response);
        } else {
            sendErrorJson(response, "Could not add news information.");
        }
    }

    private void deleteNewsInfo(HttpServletRequest request, HttpServletResponse response) throws NamingException, SQLException, IOException {
        String newsKey = request.getParameter("news_key");
        int newsInfoKey = Integer.parseInt(newsKey);
        NewsDao dao = new NewsDao("jdbc/tourDate");
        int count = dao.deleteNewsInfo(newsInfoKey);
        if (count > 0) {
            sendDefaultOkJson(response);
        } else {
            sendErrorJson(response, "Could not delete news information.");
        }
    }
}

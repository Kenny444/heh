/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.heh.pojo;

/**
 *
 * @author kennethharris
 */
public class News {
    private int newsKey;
    private String newsInfo;

    /**
     * @return the newsKey
     */
    public int getNewsKey() {
        return newsKey;
    }

    /**
     * @param newsKey the newsKey to set
     */
    public void setNewsKey(int newsKey) {
        this.newsKey = newsKey;
    }

    /**
     * @return the newsInfo
     */
    public String getNewsInfo() {
        return newsInfo;
    }

    /**
     * @param newsInfo the newsInfo to set
     */
    public void setNewsInfo(String newsInfo) {
        this.newsInfo = newsInfo;
    }
            
}

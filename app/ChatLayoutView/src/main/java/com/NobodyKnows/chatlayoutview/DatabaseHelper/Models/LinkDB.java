package com.NobodyKnows.chatlayoutview.DatabaseHelper.Models;

public class LinkDB {
    public static final String TABLE_NAME = "LinksDB";

    private int id;
    private String url = "";
    private String imageurl = "";
    private String title = "";
    private String description = "";
    private String sitename = "";
    private String mediatype = "";
    private String favicon = "";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_URL = "url";
    public static final String COLUMN_IMAGEURL = "imageurl";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_SITENAME = "sitename";
    public static final String COLUMN_MEDIATYPE = "mediatype";
    public static final String COLUMN_FAVICON = "favicon";


    public static final String getTableName() {
        return TABLE_NAME;
    }

    public static final String getCreateTableQuery() {
        String CREATE_TABLE="CREATE TABLE IF NOT EXISTS "+getTableName()+"("
                + COLUMN_ID +" INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_URL +" TEXT,"
                + COLUMN_IMAGEURL +" TEXT,"
                + COLUMN_TITLE +" TEXT,"
                + COLUMN_DESCRIPTION +" TEXT,"
                + COLUMN_SITENAME +" TEXT,"
                + COLUMN_MEDIATYPE +" TEXT,"
                + COLUMN_FAVICON +" TEXT"
                +")";
        return CREATE_TABLE;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSitename() {
        return sitename;
    }

    public void setSitename(String sitename) {
        this.sitename = sitename;
    }

    public String getMediatype() {
        return mediatype;
    }

    public void setMediatype(String mediatype) {
        this.mediatype = mediatype;
    }

    public String getFavicon() {
        return favicon;
    }

    public void setFavicon(String favicon) {
        this.favicon = favicon;
    }
}

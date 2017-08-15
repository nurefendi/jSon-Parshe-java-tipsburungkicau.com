package com.tipsburungkicau.tipsburungkicau.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by mas on 12/08/2017.
 */

public class BlogModel {
    private String published;
    private String title;
    private String content;
    private String author;
    private String media$thumbnail;
    @SerializedName("category")
    private List<Categori> categori;
    private String link;

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getPublished() {
        return published;
    }

    public void setPublished(String published) {
        this.published = published;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getMedia$thumbnail() {
        return media$thumbnail;
    }

    public void setMedia$thumbnail(String media$thumbnail) {
        this.media$thumbnail = media$thumbnail;
    }

    public List<Categori> getCategori() {
        return categori;
    }

    public void setCategori(List<Categori> categori) {
        this.categori = categori;
    }

    public static class Categori {
        private String term;

        public String getTerm() {
            return term;
        }

        public void setTerm(String term) {
            this.term = term;
        }
    }

}

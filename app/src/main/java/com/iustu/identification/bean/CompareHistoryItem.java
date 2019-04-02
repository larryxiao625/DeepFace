package com.iustu.identification.bean;

/**
 * Created by Liu Yuchuan on 2017/11/22.
 */

public class CompareHistoryItem {
    private String time;
    private int similarity;
    private String name;
    private String age;
    private String nationality;
    private String library;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getSimilarity() {
        return similarity;
    }

    public void setSimilarity(int similarity) {
        this.similarity = similarity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getLibrary() {
        return library;
    }

    public void setLibrary(String library) {
        this.library = library;
    }
}

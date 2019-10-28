package com.example.memorialpoint.Models;

public class Comments {

    int no;
    int postNo;
    String url;
    String id;
    String comments;
    int groupNo;
    String date;
    public int cnt;

    public Comments(int no, int postNo, String url, String id, String comments, int groupNo, String date) {
        this.no = no;
        this.postNo = postNo;
        this.url = url;
        this.id = id;
        this.comments = comments;
        this.groupNo = groupNo;
        this.date = date;
        cnt = 0;
    }

    public int getNo() {
        return no;
    }

    public void setNo(int no) {
        this.no = no;
    }

    public int getPostNo() {
        return postNo;
    }

    public void setPostNo(int postNo) {
        this.postNo = postNo;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public int getGroupNo() {
        return groupNo;
    }

    public void setGroupNo(int groupNo) {
        this.groupNo = groupNo;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}

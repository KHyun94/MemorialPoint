package com.example.memorialpoint.Models;

public class Like {

    int no;
    int user_no;
    int post_no;

    public Like(int no, int user_no, int post_no) {
        this.no = no;
        this.user_no = user_no;
        this.post_no = post_no;
    }

    public int getNo() {
        return no;
    }

    public void setNo(int no) {
        this.no = no;
    }

    public int getUser_no() {
        return user_no;
    }

    public void setUser_no(int user_no) {
        this.user_no = user_no;
    }

    public int getPost_no() {
        return post_no;
    }

    public void setPost_no(int post_no) {
        this.post_no = post_no;
    }
}

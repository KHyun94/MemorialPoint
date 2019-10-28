package com.example.memorialpoint.Models;

import java.util.ArrayList;

public class GroupComments{

    Comments comments;
    public ArrayList<Comments> childList;

    public GroupComments(Comments comments) {
        this.comments = comments;
        childList = new ArrayList<>();
    }

    public Comments getComments() {
        return comments;
    }

    public void setComments(Comments comments) {
        this.comments = comments;
    }

}

package com.example.memorialpoint.Models;

class Meta {

    String totalCount;
    String count;

    public Meta(String totalCount, String count) {
        this.totalCount = totalCount;
        this.count = count;
    }

    public String getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(String totalCount) {
        this.totalCount = totalCount;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }
}

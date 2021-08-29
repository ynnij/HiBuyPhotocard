package com.example.hibuyphotocard;

import java.io.Serializable;

public class SearchItemList implements Serializable {
    private String price;
    private String userName;
    private String imageURI;
    private String groupTag;
    private String albumTag;
    private String memberTag;
    private String sellID;






    public String getSellID() {
        return sellID;
    }

    public void setSellID(String sellID) {
        this.sellID = sellID;
    }

    public SearchItemList(){}
    public String getGroupTag() {
        return groupTag;
    }

    public void setGroupTag(String groupTag) {
        this.groupTag = groupTag;
    }

    public String getAlbumTag() {
        return albumTag;
    }

    public void setAlbumTag(String albumTag) {
        this.albumTag = albumTag;
    }
    public String getMemberTag() {
        return memberTag;
    }

    public void setMemberTag(String memberTag) {
        this.memberTag = memberTag;
    }
    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getImageURI() {
        return imageURI;
    }

    public void setImageURI(String imageURI) {
        this.imageURI = imageURI;
    }

}
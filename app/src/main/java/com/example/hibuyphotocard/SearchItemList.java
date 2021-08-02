package com.example.hibuyphotocard;

public class SearchItemList {

  //  private String price;

    private long price;

    private String userName;
    private String imageURI;

    public SearchItemList(){}

//    public String getPrice() {
//        return price;
//    }

//    public void setPrice(String price) {
//        this.price = price.toString();
//    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
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

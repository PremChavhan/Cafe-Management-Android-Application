package com.android.eshoppingmart;

public class Post {
    public String title;
    public String body;
    public double price;
    public String imageUrl;
    public String imgname;

    public Post(){}

    public Post(String title, String body,double price,String imageUrl,String imgname) {
        this.title = title;
        this.body = body;
        this.price = price;
        if(imgname.trim().equals(""))
        {
            imgname="No name";
        }
        this.imageUrl= imageUrl;
        this.imgname= imgname;
    }
}
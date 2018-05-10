package com.example.abc.demoapplication.model;

/**
 * Created by abc on 2018/2/12.
 */

public class NewsBean {

    /**
     * title : Beavers
     * description : Beavers are second only to humans in their ability to manipulate and change their environment. They can measure up to 1.3 metres long. A group of beavers is called a colony
     * imageHref : http://upload.wikimedia.org/wikipedia/commons/thumb/6/6b/American_Beaver.jpg/220px-American_Beaver.jpg
     */

    private String title;

    private String description;

    private String imageHref;

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


    public String getImageHref() {
        return imageHref;
    }

    public void setImageHref(String imageHref) {
        this.imageHref = imageHref;
    }

    public NewsBean(){}

    public NewsBean(String title, String description) {

        this.title = title;
        this.description = description;
    }
}


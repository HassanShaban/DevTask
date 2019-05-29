package com.example.devtask.models;

public class TaskModel {

    String name ,description ,html_url ;
    boolean fork;

    public Owner owner;

    public Owner getOwner() {
        return owner;
    }

    public String getHtml_url() {
        return html_url;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public boolean isFork() {
        return fork;
    }

}

package com.fabrikam;

public class ToDoItem {
    public String id;
    public ToDoItem(String id, String title, String description) {
        this.id = id;
        this.title = title;
        this.description = description;
    }
    public String title;
    public String description;
}

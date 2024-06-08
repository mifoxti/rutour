package com.example.rutour;

public class Place {
    private int id;
    private String name;
    private String city;
    private String photoSrc;
    private String description;
    private String address;

    // Конструктор, геттеры и сеттеры

    public Place(int id, String name, String city, String photoSrc, String description, String address) {
        this.id = id;
        this.name = name;
        this.city = city;
        this.photoSrc = photoSrc;
        this.description = description;
        this.address = address;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCity() {
        return city;
    }

    public String getPhotoSrc() {
        return photoSrc;
    }

    public String getDescription() {
        return description;
    }

    public String getAddress() {
        return address;
    }
}


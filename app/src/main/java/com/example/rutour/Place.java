package com.example.rutour;

import android.os.Parcel;
import android.os.Parcelable;

public class Place implements Parcelable {
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

    public Place(String name, String city, String description, String address) {
        this.name = name;
        this.city = city;
        this.description = description;
        this.address = address;
    }

    public Place(String name, String city) {
        this.name = name;
        this.city = city;
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

    // Реализация Parcelable

    protected Place(Parcel in) {
        id = in.readInt();
        name = in.readString();
        city = in.readString();
        photoSrc = in.readString();
        description = in.readString();
        address = in.readString();
    }

    public static final Creator<Place> CREATOR = new Creator<Place>() {
        @Override
        public Place createFromParcel(Parcel in) {
            return new Place(in);
        }

        @Override
        public Place[] newArray(int size) {
            return new Place[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(city);
        dest.writeString(photoSrc);
        dest.writeString(description);
        dest.writeString(address);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPhotoSrc(String imagePath) {
        this.photoSrc = imagePath;
    }
}

package com.example.unigate;

import java.io.Serializable;
import java.util.ArrayList;


public class PackageData implements Serializable {
    private String operationType;
    private User user;
    private Door door;
    private ArrayList<User> usersArray;
    private ArrayList<Door> doorArray;

    public PackageData() {
    }

    public PackageData(String operationType, User user, Door door, ArrayList<User> usersArray, ArrayList<Door> doorArray) {
        this.operationType = operationType;
        this.user = user;
        this.door = door;
        this.usersArray = usersArray;
        this.doorArray = doorArray;
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Door getDoor() {
        return door;
    }

    public void setDoor(Door door) {
        this.door = door;
    }

    public ArrayList<User> getUsersArray() {
        return usersArray;
    }

    public void setUsersArray(ArrayList<User> usersArray) {
        this.usersArray = usersArray;
    }

    public ArrayList<Door> getDoorArray() {
        return doorArray;
    }

    public void setDoorArray(ArrayList<Door> doorArray) {
        this.doorArray = doorArray;
    }
}

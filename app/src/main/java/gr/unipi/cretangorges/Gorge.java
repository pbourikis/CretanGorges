package gr.unipi.cretangorges;

import java.io.Serializable;

public class Gorge implements Serializable {

    public String id;
    public String name;
    public String text;
    public float price;
    public float time;
    public float length;
    public int difficulty;

    public Gorge() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Gorge(String id, String name, String text, float price, float time, float length, int difficulty) {
        this.id = id;
        this.name = name;
        this.text = text;
        this.price = price;
        this.time = time;
        this.length = length;
        this.difficulty = difficulty;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public float getTime() {
        return time;
    }

    public void setTime(float time) {
        this.time = time;
    }

    public float getLength() {
        return length;
    }

    public void setLength(float length) {
        this.length = length;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }
}

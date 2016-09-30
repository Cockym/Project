package com.example.cockym.phonebook.bean;

public class Contact {

    public final int id;
    public final String name;
    public final String email;
    public final String number;
    public final String numbers;

    public Contact(int id, String name, String email, String number, String numbers) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.number = number;
        this.numbers = numbers;
    }
}

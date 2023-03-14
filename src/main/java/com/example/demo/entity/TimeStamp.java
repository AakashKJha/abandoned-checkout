package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Table
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TimeStamp implements Comparable<TimeStamp> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String cartId;
    private Long remainder;
    private String message;
    private Date date;
    @Override
    public int compareTo(TimeStamp other) {
        return this.date.compareTo(other.date);
    }
}

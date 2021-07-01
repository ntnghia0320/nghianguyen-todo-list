package com.ntnghia.todo.models;

import lombok.Data;
import javax.persistence.*;

@Data
@Entity
@Table(name = "todo")
public class Todo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(name = "content")
    private String content;
}

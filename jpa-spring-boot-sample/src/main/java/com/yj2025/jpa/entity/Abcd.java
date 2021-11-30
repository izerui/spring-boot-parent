package com.yj2025.jpa.entity;

import javax.persistence.*;

@Entity
@Table(name = "abcd")
public class Abcd {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String a;
    private String b;
    private String c;
    private String d;
}

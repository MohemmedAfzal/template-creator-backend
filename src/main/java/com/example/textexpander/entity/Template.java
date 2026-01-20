package com.example.textexpander.entity;
import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;

@Entity
@Getter
@Setter
public class Template {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String groupId;
    private String title;
    @Lob
    private String content;
    private boolean published;
    private String signatureName;
}
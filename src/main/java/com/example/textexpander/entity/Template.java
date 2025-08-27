package com.example.textexpander.entity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class Template {
    @Id @GeneratedValue
    private Long id;
    private String groupId;
    private String title;
    @Lob
    private String content;
    private boolean published;
    private String signatureName;
}
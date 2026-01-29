package com.example.textexpander.dto;

import lombok.Data;

@Data
public class Stats {
    private Long totalSignatures;
    private Long totalTemplates;
    private Long totalPublishedTemplates;
    private Long totalDraftTemplates;
}

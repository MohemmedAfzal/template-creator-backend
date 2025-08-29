package com.example.textexpander.controller;

import com.example.textexpander.service.validation.FileNameValidationProcessor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FileNameValidationController {
    private final FileNameValidationProcessor fileNameValidationProcessor;

    public FileNameValidationController(FileNameValidationProcessor fileNameValidationProcessor) {
        this.fileNameValidationProcessor = fileNameValidationProcessor;
    }

    @GetMapping("validate/{fileName}")
    public String validate(@PathVariable String fileName) {
        return fileNameValidationProcessor.process(fileName);
    }
}

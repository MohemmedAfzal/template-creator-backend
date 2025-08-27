package com.example.textexpander.service;

import com.example.textexpander.entity.Signature;
import com.example.textexpander.entity.Template;
import com.example.textexpander.repository.SignatureRepository;
import com.example.textexpander.repository.TemplateRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TemplateService {
    private final TemplateRepository templateRepo;
    private final SignatureRepository signatureRepo;

    public TemplateService(TemplateRepository tRepo, SignatureRepository sRepo) {
        this.templateRepo = tRepo;
        this.signatureRepo = sRepo;
    }

    public Template saveTemplate(Template template) {
        return templateRepo.save(template);
    }

    public Signature saveSignature(Signature signature) {
        return signatureRepo.save(signature);
    }

}


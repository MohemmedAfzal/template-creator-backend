package com.example.textexpander.service;
import java.util.List;
import org.springframework.stereotype.Service;
import com.example.textexpander.entity.Signature;
import com.example.textexpander.entity.Template;
import com.example.textexpander.repository.SignatureRepository;
import com.example.textexpander.repository.TemplateRepository;
@Service
public class AdminService {
    private final SignatureRepository sigRepo;
    private final TemplateRepository tplRepo;
    public AdminService(SignatureRepository sigRepo, TemplateRepository tplRepo) {
        this.sigRepo = sigRepo;
        this.tplRepo = tplRepo;
    }
    public Signature addSignature(Signature s){ return sigRepo.save(s); }
    public Template addTemplate(Template t){ return tplRepo.save(t); }
    public List<Signature> allSignatures(){ return sigRepo.findAll(); }
    public List<Template> allTemplates(){ return tplRepo.findAll(); }
}
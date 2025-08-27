package com.example.textexpander.controller;

import com.example.textexpander.repository.SignatureRepository;
import com.example.textexpander.repository.TemplateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import com.example.textexpander.entity.Signature;
import com.example.textexpander.entity.Template;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    SignatureRepository signatureRepo;

    @Autowired
    TemplateRepository templateRepo;

    // -------- SIGNATURE CRUD --------

    @PostMapping("/signatures")
    public Signature addSignature(@RequestBody Signature sig) {
        return signatureRepo.save(sig);
    }

    @GetMapping("/signatures")
    public List<Signature> listSignatures() {
        return signatureRepo.findAll();
    }

    @PutMapping("/signatures/{id}")
    public Signature updateSignature(@PathVariable Long id, @RequestBody Signature sig) {
        Signature existing = signatureRepo.findById(id).orElseThrow();
        existing.setName(sig.getName());
        existing.setBody(sig.getBody());
        return signatureRepo.save(existing);
    }

    @DeleteMapping("/signatures/{id}")
    public void deleteSignature(@PathVariable Long id) {
        signatureRepo.deleteById(id);
    }

    // -------- TEMPLATE CRUD --------

    @PostMapping("/templates")
    public Template addTemplate(@RequestBody Template tpl) {
        return templateRepo.save(tpl);
    }

    @GetMapping("/templates")
    public List<Template> listTemplates(@RequestParam(required = false) String groupId) {
        return groupId == null
                ? templateRepo.findAll()
                : templateRepo.findByGroupIdAndPublishedTrue(groupId);
    }

    @PutMapping("/templates/{id}")
    public Template updateTemplate(@PathVariable Long id, @RequestBody Template tpl) {
        Template existing = templateRepo.findById(id).orElseThrow();
        existing.setGroupId(tpl.getGroupId());
        existing.setTitle(tpl.getTitle());
        existing.setContent(tpl.getContent());
        existing.setSignatureName(tpl.getSignatureName());
        existing.setPublished(tpl.isPublished());
        return templateRepo.save(existing);
    }

    @DeleteMapping("/templates/{id}")
    public void deleteTemplate(@PathVariable Long id) {
        templateRepo.deleteById(id);
    }

    @PatchMapping("/templates/{id}/publish")
    public Template publish(@PathVariable Long id, @RequestParam boolean published) {
        Template tpl = templateRepo.findById(id).orElseThrow();
        tpl.setPublished(published);
        return templateRepo.save(tpl);
    }
}

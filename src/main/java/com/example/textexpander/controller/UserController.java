package com.example.textexpander.controller;

import com.example.textexpander.entity.Signature;
import com.example.textexpander.entity.Template;
import com.example.textexpander.repository.SignatureRepository;
import com.example.textexpander.repository.TemplateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    TemplateRepository templateRepo;
    @Autowired
    SignatureRepository signatureRepo;


    @GetMapping("/templates/{groupId}")
    public List<Template> getTemplates(@PathVariable String groupId) {
        return templateRepo.findByGroupIdAndPublishedTrue(groupId);
    }


    @PostMapping("/templates/{id}/render")
    public String render(@PathVariable Long id, @RequestBody Map<String, String> vars) {
        Template tpl = templateRepo.findById(id).orElseThrow();
        String content = tpl.getContent();
        Signature sig = signatureRepo.findByName(tpl.getSignatureName()).orElse(null);
        if (sig != null) content += "\n\n" + sig.getBody();
        for (var e : vars.entrySet()) content = content.replace("{{" + e.getKey() + "}}", e.getValue());
        content = content.replace("{{today}}", LocalDate.now().toString());
        return content;
    }
}
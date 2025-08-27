package com.example.textexpander.controller;

import com.example.textexpander.entity.Template;
import com.example.textexpander.repository.TemplateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/templates")
public class TemplateController {

    @Autowired
    private TemplateRepository tplRepo;

    @GetMapping("/user/{groupId}")
    public List<Template> getUserTemplates(@PathVariable String groupId) {
        return tplRepo.findByGroupId(groupId);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/admin")
    public Template addTemplate(@RequestBody Template tpl) {
        return tplRepo.save(tpl);
    }
}


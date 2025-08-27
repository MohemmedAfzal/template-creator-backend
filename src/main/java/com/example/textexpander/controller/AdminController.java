package com.example.textexpander.controller;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import com.example.textexpander.entity.Signature;
import com.example.textexpander.entity.Template;
import com.example.textexpander.service.AdminService;
@RestController
@RequestMapping("/api/admin")
@CrossOrigin
public class AdminController {
    private final AdminService adminService;
    public AdminController(AdminService adminService){ this.adminService = adminService; }
    @PostMapping("/signatures")
    public Signature addSignature(@RequestBody Signature s){ return adminService.addSignature(s); }
    @PostMapping("/templates")
    public Template addTemplate(@RequestBody Template t){ return adminService.addTemplate(t); }
    @GetMapping("/signatures")
    public List<Signature> allSignatures(){ return adminService.allSignatures(); }
    @GetMapping("/templates")
    public List<Template> allTemplates(){ return adminService.allTemplates(); }
}
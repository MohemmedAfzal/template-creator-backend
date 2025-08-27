package com.example.textexpander.controller;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import com.example.textexpander.entity.Template;
import com.example.textexpander.service.UserService;
@RestController
@RequestMapping("/api/user")
@CrossOrigin
public class UserController {
    private final UserService userService;
    public UserController(UserService userService){ this.userService = userService; }
    @GetMapping("/templates/{groupId}")
    public List<Template> getTemplates(@PathVariable String groupId){ return userService.getTemplatesByGroup(groupId); }
}
package com.example.textexpander.service;
import java.util.List;
import org.springframework.stereotype.Service;
import com.example.textexpander.entity.Template;
import com.example.textexpander.repository.TemplateRepository;
@Service
public class UserService {
    private final TemplateRepository tplRepo;
    public UserService(TemplateRepository tplRepo) { this.tplRepo = tplRepo; }
    public List<Template> getTemplatesByGroup(String groupId){ return tplRepo.findByGroupId(groupId); }
}
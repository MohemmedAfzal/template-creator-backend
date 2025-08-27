package com.example.textexpander.repository;

import com.example.textexpander.entity.Template;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TemplateRepository extends JpaRepository<Template, Long> {
    List<Template> findByGroupIdAndPublishedTrue(String groupId);

    List<Template> findByGroupId(String groupId);
}
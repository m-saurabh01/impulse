package com.wipro.iaf.email.attachment.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wipro.iaf.email.attachment.entity.Attachment;

public interface AttachmentRepository
        extends JpaRepository<Attachment, Long> {

    List<Attachment> findByEmailId(Long emailId);

    void deleteByEmailId(Long emailId);
}

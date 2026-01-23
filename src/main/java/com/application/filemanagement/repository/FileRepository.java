package com.application.filemanagement.repository;

import com.application.filemanagement.entity.FileEntity;
import com.application.filemanagement.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FileRepository extends JpaRepository<FileEntity, Long> {
    List<FileEntity> findByUser(User user);
    FileEntity findByIdAndUser(long id, User user);
    void deleteAllByUser(User user);
}

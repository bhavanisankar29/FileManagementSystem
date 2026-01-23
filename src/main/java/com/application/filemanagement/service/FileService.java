package com.application.filemanagement.service;

import com.application.filemanagement.dto.FileDownloadDTO;
import com.application.filemanagement.dto.FileResponse;
import com.application.filemanagement.entity.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileService {
    List<FileResponse>  getUserFiles(User user);
    void uploadFile(MultipartFile file, User user);
    FileDownloadDTO downloadFile(Long fileId, User user);
    void deleteFile(Long fileId, User user);
    void deleteAllFiles(User user);
}

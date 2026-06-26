package com.application.filemanagement.service;

import com.application.filemanagement.dto.FileDownloadDTO;
import com.application.filemanagement.dto.FileResponse;
import com.application.filemanagement.entity.FileEntity;
import com.application.filemanagement.entity.User;
import com.application.filemanagement.exceptions.FileNotFoundException;
import com.application.filemanagement.exceptions.InvalidFilePathExtension;
import com.application.filemanagement.repository.FileRepository;
import jakarta.transaction.Transactional;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class FileServiceImpl implements FileService {

    private static final String uploadDir = "upload";

    private final FileRepository fileRepository;
    public FileServiceImpl(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    @Override
    public List<FileResponse> getUserFiles(User user){
        return fileRepository.findByUser(user)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // Method to convert FileEntity into FileResponse
    public FileResponse convertToDto(FileEntity fileEntity){
        FileResponse fileResponse = new FileResponse();
        fileResponse.setId(fileEntity.getId());
        fileResponse.setOriginalFilename(fileEntity.getOriginalFilename());
        fileResponse.setStoredFilename(fileEntity.getStoredFilename());
        fileResponse.setDisplayFiletype(fileEntity.getDisplayFiletype());
        fileResponse.setFilesize(fileEntity.getFilesize());
        return fileResponse;
    }

    @Transactional
    @Override
    public void uploadFile(MultipartFile file, User user) {
        try {
            if (file.isEmpty()) {throw new IllegalArgumentException("File is empty");}

            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {Files.createDirectories(uploadPath);}

            String actualName = Paths.get(file.getOriginalFilename()).getFileName().toString();

            // Get the file name without the extension
            int dotIndex = actualName.lastIndexOf('.');
            String originalName = actualName.substring(0, dotIndex);

            String ext = actualName.contains(".")
                    ? actualName.substring(actualName.lastIndexOf('.') + 1)
                    : "";
            // Generate UUID + extension of the file
            String storedFilename = UUID.randomUUID() + (ext.isEmpty() ? "" : "." + ext);

            Path filePath = uploadPath.resolve(storedFilename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Save the fileEntity
            FileEntity fileEntity = new FileEntity();
            fileEntity.setOriginalFilename(originalName);
            fileEntity.setStoredFilename(storedFilename);
            fileEntity.setFilepath(filePath.toString());
            fileEntity.setFiletype(file.getContentType());
            fileEntity.setDisplayFiletype(ext);
            fileEntity.setFilesize(file.getSize());
            fileEntity.setUser(user);

            fileRepository.save(fileEntity);

        } catch (IOException e) {
            throw new RuntimeException("File upload failed", e);
        }
    }

    @Override
    public FileDownloadDTO downloadFile(Long fileId, User user) {
        FileEntity file = fileRepository.findByIdAndUser(fileId, user);
        if (file == null) {
            throw new RuntimeException("File not found or access denied");
        }

        Path filePath = Paths.get(file.getFilepath()).normalize();
        if (!Files.exists(filePath)) {
            throw new FileNotFoundException("File not found on disk");
        }

        Resource resource;
        try {
            resource = new UrlResource(filePath.toUri());
        } catch (MalformedURLException e) {
            throw new InvalidFilePathExtension("Invalid File path");
        }
        return new FileDownloadDTO(
                resource,
                file.getOriginalFilename(),
                file.getFiletype(),
                file.getFilesize()
        );
    }

    @Transactional
    @Override
    public void deleteFile(Long fileId, User user) {
        FileEntity file = fileRepository.findByIdAndUser(fileId, user);
        if (file == null) {throw new RuntimeException("File not found or access denied");}

        Path filePath = Paths.get(file.getFilepath()).normalize();
        try {
            if(Files.exists(filePath)) {Files.delete(filePath);}
        } catch (IOException e) {
            throw new FileNotFoundException("Failed to delete file from disk");
        }
        fileRepository.delete(file);
    }

    @Transactional
    @Override
    public void deleteAllFiles(User user) {
        List<FileEntity> files = fileRepository.findByUser(user);
        if (files == null) {throw new RuntimeException("Files not found or access denied");}
        for (FileEntity file : files) {
            Path filePath = Paths.get(file.getFilepath()).normalize();
            try {
                if(Files.exists(filePath)) {Files.delete(filePath);}
            } catch (IOException e) {
                throw new FileNotFoundException("Failed to delete file from disk" +  file.getOriginalFilename());
            }
        }
        fileRepository.deleteAllByUser(user);
    }

    @Transactional
    @Override
    public String shareFile(Long fileId, User user) {
        FileEntity file = fileRepository.findByIdAndUser(fileId, user);
        if (file == null) {throw new RuntimeException("File not found or access denied");}

        if(file.getShareToken() == null || file.getShareToken().isEmpty()) {
            file.setShareToken(UUID.randomUUID().toString());
        }
        file.setShared(true);
        fileRepository.save(file);
        return file.getShareToken();
    }

    @Transactional
    @Override
    public void unshareFile(Long fileId, User user) {
        FileEntity file = fileRepository.findByIdAndUser(fileId, user);
        if(file == null) {
            throw new RuntimeException("File not found or access denied");
        }
        file.setShared(false);
        file.setShareToken(null);
        fileRepository.save(file);
    }

    @Override
    public FileDownloadDTO downloadSharedFile(String shareToken) {
        FileEntity file = fileRepository.findByShareToken(shareToken);
        if (file == null) {
            throw new RuntimeException("File not found or access denied");
        }

        Path filePath = Paths.get(file.getFilepath()).normalize();
        if(!Files.exists(filePath)) {
            throw new FileNotFoundException("File not found on disk");
        }
        Resource resource;
        try {
            resource = new UrlResource(filePath.toUri());
        } catch (MalformedURLException e) {
            throw new InvalidFilePathExtension("Invalid File path");
        }
        return new FileDownloadDTO(
                resource,
                file.getOriginalFilename(),
                file.getFiletype(),
                file.getFilesize()
        );
    }
}

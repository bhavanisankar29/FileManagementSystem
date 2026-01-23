package com.application.filemanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.core.io.Resource;

@Getter
@Setter
@AllArgsConstructor
public class FileDownloadDTO {

    private Resource resource;
    private String originalFilename;
    private String contentType;
    private Long filesize;
}

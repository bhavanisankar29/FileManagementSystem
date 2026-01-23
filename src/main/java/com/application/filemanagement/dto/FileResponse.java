package com.application.filemanagement.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FileResponse {

    private Long id;
    private String originalFilename;
    private String storedFilename;
    private String displayFiletype;
    private Long filesize;

}

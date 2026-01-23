package com.application.filemanagement.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "files")
@Getter @Setter
public class FileEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "original_filename")
    private String originalFilename;

    @Column(name = "stored_filename", unique = true)
    private String storedFilename;

    @Column(name = "filepath")
    private String filepath;

    @Column(name = "filetype")
    private String filetype;

    @Column(name = "display_filetype")
    private String displayFiletype;

    @Column(name = "filesize")
    private Long filesize;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}

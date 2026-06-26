package com.application.filemanagement.controller;

import com.application.filemanagement.dto.FileDownloadDTO;
import com.application.filemanagement.entity.User;
import com.application.filemanagement.exceptions.FileNotFoundException;
import com.application.filemanagement.security.CustomUserDetails;
import com.application.filemanagement.service.FileService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Controller
public class FileController {

    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @GetMapping("/dashboard")
    public String dashboardPage(@AuthenticationPrincipal CustomUserDetails userDetails,
                                Model model) {
        User user = userDetails.getUser();
        model.addAttribute("userName", capitalizeWords(user.getFullname()));
        model.addAttribute("files", fileService.getUserFiles(user));
        return "dashboard";
    }

    @PostMapping("/upload")
    public String uploadFile(@RequestParam MultipartFile file,
                             @AuthenticationPrincipal CustomUserDetails userDetails,
                             RedirectAttributes redirectAttributes) {
        try {
            fileService.uploadFile(file, userDetails.getUser());
            redirectAttributes.addFlashAttribute("fileUploadSuccess", "File successfully uploaded");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("fileError", "File upload failed");
        }
        return "redirect:/dashboard";
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long id,
                                                 @AuthenticationPrincipal CustomUserDetails userDetails) {
        FileDownloadDTO file = fileService.downloadFile(id, userDetails.getUser());

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(file.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + file.getOriginalFilename() + "\"")
                .contentLength(file.getFilesize())
                .body(file.getResource());
    }

    @PostMapping("/delete/{id}")
    public String deleteFile(@PathVariable Long id,
                             @AuthenticationPrincipal CustomUserDetails userDetails,
                             RedirectAttributes redirectAttributes) {
        try {
            fileService.deleteFile(id, userDetails.getUser());
            redirectAttributes.addFlashAttribute("deleteSuccess", "File successfully deleted");
        } catch (FileNotFoundException e) {
            redirectAttributes.addFlashAttribute("deleteError", e.getMessage());
        }
        return  "redirect:/dashboard";
    }

    @PostMapping("/delete-all")
    public String deleteAllFiles(@AuthenticationPrincipal CustomUserDetails userDetails,
                                 RedirectAttributes redirectAttributes) {
        try{
            fileService.deleteAllFiles(userDetails.getUser());
            redirectAttributes.addFlashAttribute("deleteAllSuccess", "Files successfully deleted");
        } catch (FileNotFoundException e) {
            redirectAttributes.addFlashAttribute("deleteError", e.getMessage());
        }
        return "redirect:/dashboard";
    }

    @PostMapping("/share/{id}")
    public String shareFile(@PathVariable Long id,
                            @AuthenticationPrincipal CustomUserDetails userDetails,
                            RedirectAttributes redirectAttributes) {
        try {
            String shareToken = fileService.shareFile(id, userDetails.getUser());
            String shareLink = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/shared/{token}")
                    .buildAndExpand(shareToken)
                    .toUriString();
            redirectAttributes.addFlashAttribute("shareSuccess", "Share link successfully created.");
            redirectAttributes.addFlashAttribute("shareLink", shareLink);
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("shareError", "Failed to create share link.");
        }
        return "redirect:/dashboard";
    }

    @PostMapping("/unshare/{id}")
    public String unshareFile(@PathVariable Long id,
                              @AuthenticationPrincipal CustomUserDetails userDetails,
                              RedirectAttributes redirectAttributes) {
        try {
            fileService.unshareFile(id, userDetails.getUser());
            redirectAttributes.addFlashAttribute("shareSuccess", "Unshare link successfully created.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("shareError", "Failed to unshare link.");
        }
        return "redirect:/dashboard";
    }

    @GetMapping("/shared/{token}")
    public ResponseEntity<Resource> downloadSharedFile(@PathVariable String token) {
        FileDownloadDTO file = fileService.downloadSharedFile(token);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(file.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + file.getOriginalFilename() + "\"")
                .contentLength(file.getFilesize())
                .body(file.getResource());
    }

    // Method to capitalize the first letter of a word in the given string
    public static String capitalizeWords(String input) {
        if (input == null || input.isBlank()) {
            return input;
        }

        String[] words = input.toLowerCase().split("\\s+");
        StringBuilder result = new StringBuilder();

        for (String word : words) {
            if (!word.isEmpty()) {
                result.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1))
                        .append(" ");
            }
        }

        return result.toString().trim();
    }

}

package com.navaantrix.vyakhyanLite.util;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Service
@Slf4j
public class FileName {

    public String generateUniqueFileName(
            String originalFileName,
            List<String> existingFiles
    ) {

        if (existingFiles == null) {
            existingFiles = new ArrayList<>();
        }

        String extension = "";
        String baseName = originalFileName;

        int dotIndex = originalFileName.lastIndexOf(".");

        if (dotIndex > 0) {
            extension = originalFileName.substring(dotIndex);
            baseName = originalFileName.substring(0, dotIndex);
        }

        String candidate = originalFileName;

        int counter = 1;

        while (existingFiles.contains(candidate)) {
            candidate = baseName + "(" + counter + ")" + extension;
            counter++;
        }

        return candidate;
    }

    public String buildConversationFileName(
            Long conversationId,
            String fileName
    ) {
        return conversationId + "_" + fileName;
    }

    public MultipartFile renameMultipartFile(
            MultipartFile file,
            String newFileName
    ) {
        return new RenameMultipartFile(
                file,
                newFileName
        );
    }

}
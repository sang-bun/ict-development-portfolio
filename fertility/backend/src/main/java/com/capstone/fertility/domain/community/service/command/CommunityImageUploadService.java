package com.capstone.fertility.domain.community.service.command;

import com.capstone.fertility.domain.community.config.CommunityUploadProperties;
import com.capstone.fertility.domain.community.dto.res.CommunityResDTO;
import com.capstone.fertility.domain.community.exception.CommunityException;
import com.capstone.fertility.domain.community.exception.code.CommunityErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommunityImageUploadService {

    private static final long MAX_BYTES = 5L * 1024 * 1024;
    private static final Set<String> ALLOWED = Set.of("image/jpeg", "image/png", "image/webp");

    private final CommunityUploadProperties properties;

    public CommunityResDTO.ImageUpload upload(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new CommunityException(CommunityErrorCode.IMAGE_UPLOAD_FAILED);
        }
        if (file.getSize() > MAX_BYTES) {
            throw new CommunityException(CommunityErrorCode.IMAGE_TOO_LARGE);
        }
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED.contains(contentType.toLowerCase(Locale.ROOT))) {
            throw new CommunityException(CommunityErrorCode.IMAGE_TYPE_NOT_ALLOWED);
        }

        String extension = switch (contentType.toLowerCase(Locale.ROOT)) {
            case "image/png" -> "png";
            case "image/webp" -> "webp";
            default -> "jpg";
        };

        try {
            Path baseDir = Path.of(properties.getDirectory()).toAbsolutePath().normalize();
            Files.createDirectories(baseDir);
            String filename = UUID.randomUUID() + "." + extension;
            Path target = baseDir.resolve(filename);
            file.transferTo(target);

            String base = properties.getPublicBaseUrl();
            if (base == null || base.isBlank()) {
                base = "/uploads/community";
            }
            if (base.endsWith("/")) {
                base = base.substring(0, base.length() - 1);
            }
            String imageUrl = base + "/" + filename;
            return CommunityResDTO.ImageUpload.builder().imageUrl(imageUrl).build();
        } catch (IOException e) {
            throw new CommunityException(CommunityErrorCode.IMAGE_UPLOAD_FAILED);
        }
    }
}

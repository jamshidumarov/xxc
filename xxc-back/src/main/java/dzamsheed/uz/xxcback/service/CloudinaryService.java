package dzamsheed.uz.xxcback.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public record UploadResult(String url, String publicId) {}

    public UploadResult upload(MultipartFile file, String folder) {
        try {
            Map<?, ?> result = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                    "folder", "xxceramics/" + folder,
                    "resource_type", "image",
                    "quality", "auto:good",
                    "fetch_format", "webp"
            ));
            return new UploadResult(
                    (String) result.get("secure_url"),
                    (String) result.get("public_id")
            );
        } catch (IOException e) {
            throw new IllegalStateException("Failed to upload file to Cloudinary", e);
        }
    }

    public void delete(String publicId) {
        if (publicId == null || publicId.isBlank()) return;
        try {
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        } catch (IOException e) {
            throw new IllegalStateException("Failed to delete file from Cloudinary", e);
        }
    }
}

package dzamsheed.uz.xxcadminbot.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.net.URI;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageUploadService {

    private final Cloudinary cloudinary;

    @Value("${cloudinary.cloud-name}")
    private String cloudName;

    @Value("${bot.token}")
    private String botToken;

    public record UploadResult(String url, String publicId) {}

    public UploadResult uploadFromTelegram(String filePath, String folder) {
        String fileUrl = "https://api.telegram.org/file/bot" + botToken + "/" + filePath;
        try (InputStream in = URI.create(fileUrl).toURL().openStream()) {
            byte[] bytes = in.readAllBytes();
            return uploadBytes(bytes, folder);
        } catch (Exception e) {
            log.error("Failed to download file from Telegram: {}", filePath, e);
            throw new RuntimeException("Telegram faylini yuklab bo'lmadi", e);
        }
    }

    public UploadResult uploadBytes(byte[] bytes, String folder) {
        try {
            Map<?, ?> result = cloudinary.uploader().upload(bytes, ObjectUtils.asMap(
                    "folder", "xxceramics/" + folder,
                    "resource_type", "image",
                    "quality", "auto:good",
                    "fetch_format", "webp"
            ));
            return new UploadResult(
                    (String) result.get("secure_url"),
                    (String) result.get("public_id")
            );
        } catch (Exception e) {
            log.error("Cloudinary upload failed", e);
            throw new RuntimeException("Cloudinary ga yuklashda xatolik", e);
        }
    }

    public boolean isConfigured() {
        return !"your-cloud-name".equals(cloudName);
    }
}

package com.bibbidi.domain.chat;

import com.bibbidi.domain.chat.dto.WeddingGuideResponse;
import com.bibbidi.domain.vendor.VendorCategory;
import com.bibbidi.support.exception.InternalServerException;
import com.bibbidi.support.exception.NotFoundException;
import com.bibbidi.support.exception.errors.CommonErrors;
import com.bibbidi.support.exception.errors.WeddingGuideErrors;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

@Service
public class WeddingGuideService {

    private static final Map<VendorCategory, String> GUIDE_PATHS = Map.of(
        VendorCategory.WEDDING_HALL, "wedding-guides/wedding-hall.md",
        VendorCategory.STUDIO, "wedding-guides/studio.md",
        VendorCategory.DRESS, "wedding-guides/dress.md",
        VendorCategory.MAKEUP, "wedding-guides/makeup.md"
    );

    public WeddingGuideResponse getGuide(String categoryValue) {
        VendorCategory category = category(categoryValue);
        String content = readGuide(GUIDE_PATHS.get(category));
        return new WeddingGuideResponse(category.apiValue(), content);
    }

    private VendorCategory category(String categoryValue) {
        return VendorCategory.from(categoryValue)
            .orElseThrow(() -> new NotFoundException(WeddingGuideErrors.GUIDE_NOT_FOUND));
    }

    private String readGuide(String path) {
        ClassPathResource resource = new ClassPathResource(path);
        try {
            return resource.getContentAsString(StandardCharsets.UTF_8);
        } catch (IOException exception) {
            throw new InternalServerException(CommonErrors.INTERNAL_SERVER_ERROR);
        }
    }
}

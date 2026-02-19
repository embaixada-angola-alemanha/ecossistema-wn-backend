package ao.gov.embaixada.wn.service;

import ao.gov.embaixada.commons.storage.StorageService;
import ao.gov.embaixada.wn.dto.MediaFileResponse;
import ao.gov.embaixada.wn.entity.MediaFile;
import ao.gov.embaixada.wn.exception.ResourceNotFoundException;
import ao.gov.embaixada.wn.repository.MediaFileRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
@Transactional
public class MediaService {

    private static final Logger log = LoggerFactory.getLogger(MediaService.class);
    private static final String BUCKET = "wn-media";

    private final MediaFileRepository repo;
    private final StorageService storageService;

    public MediaService(MediaFileRepository repo, StorageService storageService) {
        this.repo = repo;
        this.storageService = storageService;
    }

    public MediaFileResponse upload(MultipartFile file, String altPt, String altEn, String altDe) throws IOException {
        storageService.ensureBucket(BUCKET);

        String originalName = file.getOriginalFilename();
        String extension = originalName != null && originalName.contains(".")
                ? originalName.substring(originalName.lastIndexOf('.'))
                : "";
        String fileName = UUID.randomUUID() + extension;
        String objectKey = "articles/" + fileName;

        storageService.upload(BUCKET, objectKey, file.getInputStream(),
                file.getSize(), file.getContentType());

        MediaFile mf = new MediaFile();
        mf.setFileName(fileName);
        mf.setOriginalName(originalName != null ? originalName : fileName);
        mf.setMimeType(file.getContentType() != null ? file.getContentType() : "application/octet-stream");
        mf.setSize(file.getSize());
        mf.setBucket(BUCKET);
        mf.setObjectKey(objectKey);
        mf.setAltPt(altPt);
        mf.setAltEn(altEn);
        mf.setAltDe(altDe);

        if (isImage(mf.getMimeType())) {
            try {
                BufferedImage img = ImageIO.read(file.getInputStream());
                if (img != null) {
                    mf.setWidth(img.getWidth());
                    mf.setHeight(img.getHeight());
                }
            } catch (Exception e) {
                log.warn("Could not read image dimensions: {}", e.getMessage());
            }
        }

        return toResponse(repo.save(mf));
    }

    public MediaFileResponse uploadResized(MultipartFile file, int maxWidth, int maxHeight,
                                            String altPt, String altEn, String altDe) throws IOException {
        if (!isImage(file.getContentType())) {
            return upload(file, altPt, altEn, altDe);
        }

        storageService.ensureBucket(BUCKET);

        BufferedImage original = ImageIO.read(file.getInputStream());
        if (original == null) {
            return upload(file, altPt, altEn, altDe);
        }

        BufferedImage resized = resize(original, maxWidth, maxHeight);

        String originalName = file.getOriginalFilename();
        String fileName = UUID.randomUUID() + ".jpg";
        String objectKey = "articles/" + fileName;

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(resized, "jpg", baos);
        byte[] resizedBytes = baos.toByteArray();

        storageService.upload(BUCKET, objectKey, new ByteArrayInputStream(resizedBytes),
                resizedBytes.length, "image/jpeg");

        MediaFile mf = new MediaFile();
        mf.setFileName(fileName);
        mf.setOriginalName(originalName != null ? originalName : fileName);
        mf.setMimeType("image/jpeg");
        mf.setSize(resizedBytes.length);
        mf.setBucket(BUCKET);
        mf.setObjectKey(objectKey);
        mf.setAltPt(altPt);
        mf.setAltEn(altEn);
        mf.setAltDe(altDe);
        mf.setWidth(resized.getWidth());
        mf.setHeight(resized.getHeight());

        return toResponse(repo.save(mf));
    }

    @Transactional(readOnly = true)
    public MediaFileResponse findById(UUID id) {
        return toResponse(repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("MediaFile", id)));
    }

    @Transactional(readOnly = true)
    public Page<MediaFileResponse> findAll(Pageable pageable) {
        return repo.findAll(pageable).map(this::toResponse);
    }

    public InputStream download(UUID id) {
        MediaFile mf = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("MediaFile", id));
        return storageService.download(mf.getBucket(), mf.getObjectKey());
    }

    public void delete(UUID id) {
        MediaFile mf = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("MediaFile", id));
        storageService.delete(mf.getBucket(), mf.getObjectKey());
        repo.delete(mf);
    }

    private BufferedImage resize(BufferedImage original, int maxWidth, int maxHeight) {
        int origWidth = original.getWidth();
        int origHeight = original.getHeight();

        if (origWidth <= maxWidth && origHeight <= maxHeight) {
            return original;
        }

        double ratio = Math.min((double) maxWidth / origWidth, (double) maxHeight / origHeight);
        int newWidth = (int) (origWidth * ratio);
        int newHeight = (int) (origHeight * ratio);

        BufferedImage resized = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = resized.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.drawImage(original, 0, 0, newWidth, newHeight, null);
        g2d.dispose();
        return resized;
    }

    private boolean isImage(String mimeType) {
        return mimeType != null && mimeType.startsWith("image/");
    }

    private MediaFileResponse toResponse(MediaFile mf) {
        return new MediaFileResponse(
                mf.getId(), mf.getFileName(), mf.getOriginalName(),
                mf.getMimeType(), mf.getSize(), mf.getObjectKey(),
                mf.getAltPt(), mf.getAltEn(), mf.getAltDe(),
                mf.getWidth(), mf.getHeight(), mf.getCreatedAt()
        );
    }
}

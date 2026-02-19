package ao.gov.embaixada.wn.entity;

import ao.gov.embaixada.commons.dto.BaseEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "media_files")
public class MediaFile extends BaseEntity {

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "original_name", nullable = false)
    private String originalName;

    @Column(name = "mime_type", nullable = false, length = 100)
    private String mimeType;

    @Column(nullable = false)
    private long size;

    @Column(nullable = false, length = 100)
    private String bucket;

    @Column(name = "object_key", nullable = false)
    private String objectKey;

    @Column(name = "alt_pt", length = 300)
    private String altPt;

    @Column(name = "alt_en", length = 300)
    private String altEn;

    @Column(name = "alt_de", length = 300)
    private String altDe;

    @Column
    private Integer width;

    @Column
    private Integer height;

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    public String getOriginalName() { return originalName; }
    public void setOriginalName(String originalName) { this.originalName = originalName; }
    public String getMimeType() { return mimeType; }
    public void setMimeType(String mimeType) { this.mimeType = mimeType; }
    public long getSize() { return size; }
    public void setSize(long size) { this.size = size; }
    public String getBucket() { return bucket; }
    public void setBucket(String bucket) { this.bucket = bucket; }
    public String getObjectKey() { return objectKey; }
    public void setObjectKey(String objectKey) { this.objectKey = objectKey; }
    public String getAltPt() { return altPt; }
    public void setAltPt(String altPt) { this.altPt = altPt; }
    public String getAltEn() { return altEn; }
    public void setAltEn(String altEn) { this.altEn = altEn; }
    public String getAltDe() { return altDe; }
    public void setAltDe(String altDe) { this.altDe = altDe; }
    public Integer getWidth() { return width; }
    public void setWidth(Integer width) { this.width = width; }
    public Integer getHeight() { return height; }
    public void setHeight(Integer height) { this.height = height; }
}

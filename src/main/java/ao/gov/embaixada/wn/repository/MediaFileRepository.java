package ao.gov.embaixada.wn.repository;

import ao.gov.embaixada.wn.entity.MediaFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MediaFileRepository extends JpaRepository<MediaFile, UUID> {
}

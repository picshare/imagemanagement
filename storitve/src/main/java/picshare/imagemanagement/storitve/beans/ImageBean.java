package picshare.imagemanagement.storitve.beans;

import com.kumuluz.ee.fault.tolerance.annotations.CommandKey;
import com.kumuluz.ee.fault.tolerance.annotations.GroupKey;
import com.kumuluz.ee.rest.beans.QueryParameters;
import com.kumuluz.ee.rest.utils.JPAUtils;
import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Timeout;
import picshare.imagemanagement.entitete.business.newImage;
import picshare.imagemanagement.entitete.business.updateImage;
import picshare.imagemanagement.entitete.jpa.Album;
import picshare.imagemanagement.entitete.jpa.Image;
import picshare.imagemanagement.storitve.config.MicroserviceMappingConfig;
import picshare.imagemanagement.storitve.faulttolerance.AddImageFallback;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import javax.ws.rs.POST;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.logging.Logger;

@ApplicationScoped
public class ImageBean {
    private final Logger log = Logger.getLogger(this.getClass().getName());

    @PostConstruct
    private void init() {
        log.info("ImageBean initialized");
    }

    @PersistenceContext(unitName = "picshare-imagemanagement-jpa")
    private EntityManager em;

    @Inject
    private AlbumBean aB;

    @Inject
    private HttpClientBean hcB;

    @Inject
    private MicroserviceMappingConfig mmc;

    public List<Image> getAllImages(QueryParameters query) {
        try {
            List<Image> allImages = JPAUtils.queryEntities(em, Image.class, query);
            return allImages;
        } catch (Exception e) {
            log.warning(e.toString());
            return null;
        }
    }

    public Image getImageData(int idImage) {
        try {
            return em.find(Image.class, idImage);
        } catch (Exception e) {
            log.warning(e.toString());
            return null;
        }
    }

    public byte[] getImageRaw(int idImage) {
        try {
            Image i = getImageData(idImage);
            if(i != null) {
                return hcB.getImageRaw(mmc.getStorage()+"/image", i.getAlbum().getUserId(), i.getAlbum().getAlbumId(), i.getImageId());
            } else {
                throw new Exception("Given image doesn't exist");
            }
        } catch (Exception e) {
            log.warning(e.toString());
            return null;
        }
    }

    public byte[] getImageQR(int idImage) {
        try {
            Image i = getImageData(idImage);
            if(i != null) {
                return hcB.getImageQR(mmc.getQrApi(), mmc.getRapidApiKey(), i.getImageId());
            } else {
                throw new Exception("Given image doesn't exist");
            }
        } catch (Exception e) {
            log.warning(e.toString());
            return null;
        }
    }

    @CircuitBreaker(requestVolumeThreshold = 10, delay = 10, delayUnit = ChronoUnit.SECONDS)
    @Fallback(AddImageFallback.class)
    @Transactional
    public Image addImage(newImage newImage) {
            Image i = new Image();
            Album a = aB.getAlbum(newImage.getAlbumId());

            if(a != null && newImage.getUserId() == a.getUserId() && hcB.checkUser(mmc.getUserservice()+"/user", newImage.getUserId())) {
                i.setAlbum(a);
                i.setName(newImage.getName());
                em.persist(i);
                em.flush();

                //image is saved on storage microservice
                hcB.sendImage(mmc.getStorage()+"/image", newImage.getUserId(), newImage.getAlbumId(), i.getImageId(), newImage.getEncodedImage());

                log.info(String.format("Added Image(name: %s, albumId: %s, imageId: %s)", i.getName(), i.getAlbum().getAlbumId(), i.getImageId()));
                return i;
            } else {
                return null;
            }
    }

    @Transactional
    public Image updateImage(int idImage, updateImage image) {
        try {
            Image imageFound = em.find(Image.class, idImage);

            if(imageFound != null) {
                Album a = aB.getAlbum(image.getAlbumId());
                hcB.checkUser(mmc.getUserservice()+"/user", image.getUserId());
                if(a != null) {
                    if(imageFound.getAlbum().getUserId() == a.getUserId()) {

                        hcB.updateImage(mmc.getStorage()+"/image", image.getUserId(), image.getAlbumId(), image.getOldAlbumId(), imageFound.getImageId());

                        imageFound.setAlbum(a);
                        imageFound.setName(image.getName());
                        em.merge(imageFound);
                        return imageFound;
                    } else {
                        throw new Exception("User '"+ imageFound.getAlbum().getUserId() +"' does not own album '"+ a.getAlbumId() +"'");
                    }
                } else {
                    throw new Exception("Given album doesn't exist");
                }
            } else {
                throw new Exception("Given image doesn't exist");
            }
        } catch (Exception e) {
            log.warning(e.toString());
            return null;
        }
    }

    @Transactional
    public boolean deleteImage(int idImage) {
        try {
            Image imageFound = em.find(Image.class, idImage);
            if(imageFound != null) {
                hcB.removeImage(mmc.getStorage()+"/image", imageFound.getAlbum().getUserId(), imageFound.getAlbum().getAlbumId(), imageFound.getImageId());
                log.info(imageFound.getName() +" deleted");
                em.remove(imageFound);
            } else {
                throw new Exception("Given image doesn't exist");
            }
            return true;
        } catch (Exception e) {
            log.warning(e.toString());
            return false;
        }
    }
}

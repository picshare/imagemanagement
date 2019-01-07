package picshare.imagemanagement.storitve.beans;

import com.kumuluz.ee.rest.beans.QueryParameters;
import com.kumuluz.ee.rest.utils.JPAUtils;
import picshare.imagemanagement.entitete.business.newImage;
import picshare.imagemanagement.entitete.business.updateImage;
import picshare.imagemanagement.entitete.jpa.Album;
import picshare.imagemanagement.entitete.jpa.Image;
import picshare.imagemanagement.storitve.config.MicroserviceMappingConfig;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
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
            return hcB.getImageRaw(mmc.getStorage()+"/image", i.getAlbum().getUserId(), i.getAlbum().getAlbumId(), i.getImageId());
        } catch (Exception e) {
            log.warning(e.toString());
            return null;
        }
    }

    @Transactional
    public Image addImage(newImage newImage) {
        try {
            Image i = new Image();
            Album a = aB.getAlbum(newImage.getAlbumId());
            hcB.checkUser(mmc.getUserservice()+"/user", newImage.getUserId());

            if(a != null) {
                if(newImage.getUserId() == a.getUserId()) {
                    i.setAlbum(a);
                    i.setName(newImage.getName());
                }
            } else {
                throw new Exception("Given album doesn't exist");
            }
            em.persist(i);
            em.flush();

            //image is saved on storage microservice
            hcB.sendImage(mmc.getStorage()+"/image", newImage.getUserId(), newImage.getAlbumId(), i.getImageId(), newImage.getEncodedImage());


            log.info(String.format("Added Image(name: %s, albumId: %s, imageId: %s)", i.getName(), i.getAlbum().getAlbumId(), i.getImageId()));

            return i;
        } catch (Exception e) {
            log.warning(e.toString());
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

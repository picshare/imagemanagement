package picshare.imagemanagement.storitve.beans;

import com.kumuluz.ee.rest.beans.QueryParameters;
import com.kumuluz.ee.rest.utils.JPAUtils;
import picshare.imagemanagement.entitete.jpa.Album;
import picshare.imagemanagement.entitete.jpa.Image;
import picshare.imagemanagement.storitve.config.MicroserviceMappingConfig;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.List;
import java.util.logging.Logger;

@ApplicationScoped
public class AlbumBean {
    private final Logger log = Logger.getLogger(this.getClass().getName());

    @PostConstruct
    private void init() {
        log.info("AlbumBean initialized");
    }

    @PersistenceContext(unitName = "picshare-imagemanagement-jpa")
    private EntityManager em;

    @Inject
    private ImageBean iB;

    @Inject
    private HttpClientBean hcB;

    @Inject
    private MicroserviceMappingConfig mmc;

    public List<Album> getAllAlbums(QueryParameters query) {
        try {
            List<Album> allAlbums = JPAUtils.queryEntities(em, Album.class, query);
            return allAlbums;
        } catch (Exception e) {
            log.warning(e.toString());
            return null;
        }
    }

    public Album getAlbum(int idAlbum) {
        try {
            return em.find(Album.class, idAlbum);
        } catch (Exception e) {
            log.warning(e.toString());
            return null;
        }
    }

    @Transactional
    public Album addAlbum(Album album) {
        try {
            if(album.getName() == null) {
                throw new Exception("Name must be specified");
            }

            if(album.getUserId() == null) {
                album.setUserId(1);
            }

            hcB.checkUser(mmc.getUserservice()+"/user", album.getUserId());

            em.persist(album);
            em.flush();

            log.info(String.format("Added Album(name: %s, albumId: %s, userId: %s)", album.getName(), album.getAlbumId(), album.getUserId()));

            return album;
        } catch (Exception e) {
            log.warning(e.toString());
            return null;
        }
    }

    @Transactional
    public Album updateAlbum(int idAlbum, Album album) {
        try {
            Album albumFound = em.find(Album.class, idAlbum);


            if(albumFound.getUserId() != album.getUserId())  {
                throw new Exception("Invalid user");
            }

            albumFound.setUserId(album.getUserId());
            albumFound.setName(album.getName());
            em.merge(albumFound);
            return  albumFound;
        } catch (Exception e) {
            log.warning(e.toString());
            return null;
        }
    }

    @Transactional
    public boolean deleteAlbum(int idAlbum) {
        try {
            Album album = em.find(Album.class, idAlbum);

            if(album == null) {
                throw new Exception("Album with id: '"+ idAlbum +"' doesn't exist");
            }

            TypedQuery<Image> q = em.createQuery("SELECT i FROM picshare_image i WHERE i.album = :albumid", Image.class);
            q.setParameter("albumid", album);

            List<Image> images = q.getResultList();

            for (int i = 0; i < images.size(); i++) {
                if(!iB.deleteImage(images.get(i).getImageId())) {
                    throw new Exception("Album with id: '"+ idAlbum +"' doesn't exist");
                }
            }

            log.info("Deleted album: "+ album.getName());
            em.remove(album);
            return true;
        } catch (Exception e) {
            log.warning(e.toString());
            return false;
        }
    }
}

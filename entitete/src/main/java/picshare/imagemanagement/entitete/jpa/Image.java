package picshare.imagemanagement.entitete.jpa;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@XmlRootElement
@Entity(name = "picshare_image")
@NamedQueries(value =
        {
                @NamedQuery(name = "User.getAll", query = "SELECT i FROM picshare_image i"),
        })
public class Image implements Serializable {

    @XmlElement
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idImage;
    @XmlElement
    @Column(nullable = false)
    private String path;
    @XmlElement
    @Column(nullable = false)
    private String name;
    @XmlElement
    @Column(nullable = false)
    private Integer albumId;


    public Integer getIdImage() {
        return idImage;
    }

    public void setIdImage(Integer idImage) {
        this.idImage = idImage;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAlbumId() {
        return albumId;
    }

    public void setAlbumId(Integer albumId) {
        this.albumId = albumId;
    }
}

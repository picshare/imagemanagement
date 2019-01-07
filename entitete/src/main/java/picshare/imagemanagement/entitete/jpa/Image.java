package picshare.imagemanagement.entitete.jpa;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@XmlRootElement
@Entity(name = "picshare_image")
@NamedQueries(value =
        {
                @NamedQuery(name = "Image.deleteAllByAlbum", query = "DELETE FROM picshare_image i WHERE i.album = :albumid")
        })
public class Image implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer imageId;
    @XmlElement
    @ManyToOne
    @JoinColumn(name="albumid", nullable = false)
    private Album album;

    @XmlElement
    @Column(nullable = false)
    private String name;

    public Integer getImageId() {
        return imageId;
    }

    public void setImageId(Integer imageId) {
        this.imageId = imageId;
    }

    public Album getAlbum() {
        return album;
    }

    public void setAlbum(Album album) {
        this.album = album;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

package picshare.imagemanagement.entitete.business;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class updateImage {
    @XmlElement
    private Integer userId;
    @XmlElement
    private Integer albumId;
    @XmlElement
    private Integer oldAlbumId;
    @XmlElement
    private String name;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getAlbumId() {
        return albumId;
    }

    public void setAlbumId(Integer albumId) {
        this.albumId = albumId;
    }

    public Integer getOldAlbumId() {
        return oldAlbumId;
    }

    public void setOldAlbumId(Integer oldAlbumId) {
        this.oldAlbumId = oldAlbumId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

package picshare.imagemanagement.storitve.config;

import com.kumuluz.ee.configuration.cdi.ConfigBundle;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
@ConfigBundle("microservice-mapping")
public class MicroserviceMappingConfig {

    private String storage;
    private String userservice;
    private String commentsmanagement;

    public String getStorage() {
        return storage;
    }

    public void setStorage(String storage) {
        this.storage = storage;
    }

    public String getUserservice() {
        return userservice;
    }

    public void setUserservice(String userservice) {
        this.userservice = userservice;
    }

    public String getCommentsmanagement() {
        return commentsmanagement;
    }

    public void setCommentsmanagement(String commentsmanagement) {
        this.commentsmanagement = commentsmanagement;
    }
}
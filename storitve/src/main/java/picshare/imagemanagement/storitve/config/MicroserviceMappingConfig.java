package picshare.imagemanagement.storitve.config;

import com.kumuluz.ee.configuration.cdi.ConfigBundle;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
@ConfigBundle("microservice-mapping")
public class MicroserviceMappingConfig {

    private String storage;
    private String userservice;
    private String qrApi;
    private String rapidApiKey;

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

    public String getQrApi() {
        return qrApi;
    }

    public void setQrApi(String qrApi) {
        this.qrApi = qrApi;
    }

    public String getRapidApiKey() {
        return rapidApiKey;
    }

    public void setRapidApiKey(String rapidApiKey) {
        this.rapidApiKey = rapidApiKey;
    }
}
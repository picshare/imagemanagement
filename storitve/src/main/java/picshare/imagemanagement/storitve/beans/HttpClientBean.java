package picshare.imagemanagement.storitve.beans;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;

import javax.enterprise.context.ApplicationScoped;
import java.io.ByteArrayOutputStream;
import java.util.logging.Logger;

@ApplicationScoped
public class HttpClientBean {
    private final Logger log = Logger.getLogger(this.getClass().getName());

    public void sendImage(String toURL, Integer userId, Integer albumId, Integer imageId, String encodedImage) throws Exception {
        HttpClient httpclient = HttpClients.createDefault();
        HttpPost httppost = new HttpPost(toURL);

        String json = "{\"userId\": "+ userId +", \"albumId\": "+ albumId +", \"imageId\": "+ imageId +", \"encodedImage\": \""+ encodedImage +"\"}";
        StringEntity requestEntity = new StringEntity(json);
        httppost.setEntity(requestEntity);
        httppost.setHeader("Accept", "application/json");
        httppost.setHeader("Content-type", "application/json");

        HttpResponse response = httpclient.execute(httppost);
        if (response.getStatusLine().getStatusCode() != 200) {
            throw new Exception("Failed to send the image to storage microservice");
        }
    }

    public void checkUser(String toURL, Integer userId) throws Exception {
        HttpClient httpclient = HttpClients.createDefault();
        HttpGet httpget = new HttpGet(toURL+"/"+userId);

        HttpResponse response = httpclient.execute(httpget);
        if (response.getStatusLine().getStatusCode() != 200) {
            throw new Exception("User with id '"+ userId +"' does't exist");
        }
    }

    public byte[] getImageRaw(String toURL, Integer userId, Integer albumId, Integer imageId) throws Exception {
        HttpClient httpclient = HttpClients.createDefault();
        HttpGet httpget = new HttpGet(toURL+"/"+userId+"/"+albumId+"/"+imageId);

        HttpResponse response = httpclient.execute(httpget);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        if (response.getStatusLine().getStatusCode() != 200) {
            throw new Exception("Failed to send the image to storage microservice");
        } else {
            response.getEntity().writeTo(baos);
            return baos.toByteArray();
        }
    }

    public void updateImage(String toURL, Integer userId, Integer albumId, Integer oldAlbumId, Integer imageId) throws Exception {
        HttpClient httpclient = HttpClients.createDefault();
        HttpPut httpput = new HttpPut(toURL);

        String json = "{\"userId\": "+ userId +", \"albumId\": "+ albumId +", \"oldAlbumId\": "+ oldAlbumId +", \"imageId\": "+ imageId +"}";
        StringEntity requestEntity = new StringEntity(json);
        httpput.setEntity(requestEntity);
        httpput.setHeader("Accept", "application/json");
        httpput.setHeader("Content-type", "application/json");

        HttpResponse response = httpclient.execute(httpput);

        if (response.getStatusLine().getStatusCode() != 200) {
            throw new Exception("Failed to update the image in storage microservice");
        }
    }

    public void removeImage(String toURL, Integer userId, Integer albumId, Integer imageId) throws Exception {
        HttpClient httpclient = HttpClients.createDefault();
        HttpDelete httpdelete = new HttpDelete(toURL+"/"+userId+"/"+albumId+"/"+imageId);

        HttpResponse response = httpclient.execute(httpdelete);

        if (response.getStatusLine().getStatusCode() != 204) {
            throw new Exception("Failed to remove the image in storage microservice");
        }
    }
}

package picshare.imagemanagement.storitve.beans;

import com.kumuluz.ee.configuration.utils.ConfigurationUtil;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;

import javax.enterprise.context.ApplicationScoped;
import java.io.*;
import java.util.logging.Logger;

@ApplicationScoped
public class HttpClientBean {
    private final Logger log = Logger.getLogger(this.getClass().getName());

    public void sendImage(String toURL, Integer userId, Integer albumId, Integer imageId, String encodedImage) throws RuntimeException {
        HttpClient httpclient = HttpClients.createDefault();
        HttpPost httppost = new HttpPost(toURL);
        String json = "{\"userId\": "+ userId +", \"albumId\": "+ albumId +", \"imageId\": "+ imageId +", \"encodedImage\": \""+ encodedImage +"\"}";
        try {
            StringEntity requestEntity = new StringEntity(json);
            httppost.setEntity(requestEntity);
            httppost.setHeader("Accept", "application/json");
            httppost.setHeader("Content-type", "application/json");

            HttpResponse response = httpclient.execute(httppost);
            if (response.getStatusLine().getStatusCode() != 200) {
                throw new RuntimeException("Failed to send the image to storage microservice");
            }
        } catch (Exception e) {
                throw new RuntimeException(e.toString());

        }

    }

    public boolean checkUser(String toURL, Integer userId) throws RuntimeException {
        HttpClient httpclient = HttpClients.createDefault();
        HttpGet httpget = new HttpGet(toURL+"/"+userId);

        try {
            HttpResponse response = httpclient.execute(httpget);
            if (response.getStatusLine().getStatusCode() != 200) {
                return false;
            } else {
                return true;
            }
        } catch (Exception e) {
            throw new RuntimeException(e.toString());
        }

    }

    public byte[] getImageRaw(String toURL, Integer userId, Integer albumId, Integer imageId) throws Exception {
        HttpClient httpclient = HttpClients.createDefault();
        HttpGet httpget = new HttpGet(toURL+"/"+userId+"/"+albumId+"/"+imageId);

        HttpResponse response = httpclient.execute(httpget);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        if (response.getStatusLine().getStatusCode() != 200) {
            throw new Exception("Failed to get the image from storage microservice");
        } else {
            response.getEntity().writeTo(baos);
            return baos.toByteArray();
        }
    }

    public byte[] getImageQR(String toURL, String rapidAPIKey, Integer imageId) throws Exception {
        HttpClient httpclient = HttpClients.createDefault();
        HttpPost httppost = new HttpPost(toURL);

        String bgColor = "#ffffff";
        Integer width = 128;
        String fgColor = "#000000";
        Integer height = 128;
        String content = ConfigurationUtil.getInstance().get("kumuluzee.server.base-url").get()+ "/image/raw/"+imageId;

        String json = "{\"bg-color\": \""+ bgColor +
                "\", \"width\": "+ width +
                ", \"fg-color\": \""+ fgColor +
                "\", \"height\": "+ height +
                ", \"content\": \""+ content +"\"}";



        StringEntity requestEntity = new StringEntity(json);
        httppost.setEntity(requestEntity);
        httppost.setHeader("X-RapidAPI-Key", rapidAPIKey);
        httppost.setHeader("Content-type", "application/json");

        HttpResponse response = httpclient.execute(httppost);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        if (response.getStatusLine().getStatusCode() != 200) {
            throw new Exception("Failed to send request to qrAPI");
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

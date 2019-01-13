package picshare.imagemanagement.rest.v1;

import com.kumuluz.ee.cors.annotations.CrossOrigin;

import javax.ws.rs.ApplicationPath;

@ApplicationPath("v1")
@CrossOrigin(name = "my-resource")
public class RestService extends javax.ws.rs.core.Application {

}

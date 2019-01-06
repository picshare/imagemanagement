package picshare.imagemanagement.rest.v1;

import com.kumuluz.ee.cors.annotations.CrossOrigin;
import io.swagger.oas.annotations.OpenAPIDefinition;
import io.swagger.oas.annotations.info.Info;

import javax.ws.rs.ApplicationPath;

@OpenAPIDefinition(info = @Info(title = "Rest API for user management in picshare system", version = "v1"))
@ApplicationPath("v1")
@CrossOrigin(name = "my-resource")
public class RestService extends javax.ws.rs.core.Application {

}

package picshare.imagemanagement.rest.v1;

import picshare.imagemanagement.entitete.business.Error;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class NotFoundExceptionMapper implements ExceptionMapper<NotFoundException>{

    @Override
    public Response toResponse(NotFoundException e) {
        Error error = new Error();
        error.setMessage("Not Found");
        error.setStatus(404);
        return Response.status(Response.Status.NOT_FOUND).entity(error).build();
    }
}

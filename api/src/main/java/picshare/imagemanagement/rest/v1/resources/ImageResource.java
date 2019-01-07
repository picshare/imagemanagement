package picshare.imagemanagement.rest.v1.resources;

import com.kumuluz.ee.rest.beans.QueryParameters;
import org.apache.commons.io.IOUtils;
import picshare.imagemanagement.entitete.business.newImage;
import picshare.imagemanagement.entitete.business.updateImage;
import picshare.imagemanagement.entitete.jpa.Image;
import picshare.imagemanagement.storitve.beans.ImageBean;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Base64;
import java.util.List;
import java.util.logging.Logger;

@Path("image")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class ImageResource {
    private final Logger log = Logger.getLogger(this.getClass().getName());

    @Context
    protected UriInfo uriInfo;

    @Inject
    ImageBean iB;

    @GET
    public Response returnImages(){
        QueryParameters query = QueryParameters.query(uriInfo.getRequestUri().getQuery()).build();
        List<Image> images = iB.getAllImages(query);
        if(images != null && images.size() > 0) {
            return Response.status(Response.Status.OK).entity(images).build();
        }
        else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @Path("/data/{id}")
    @GET
    public Response returnImageData(@PathParam("id") Integer id){
        Image image = iB.getImageData(id);
        if(image != null) {
            return Response.status(Response.Status.OK).entity(image).build();
        }
        else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @Path("/raw/{id}")
    @Produces("image/png")
    @GET
    public Response returnImageRaw(@PathParam("id") Integer id){
        byte[] bImage = iB.getImageRaw(id);
        if(bImage != null) {
            return Response.ok().entity(new StreamingOutput(){
                @Override
                public void write(OutputStream output)
                        throws IOException, WebApplicationException {
                    output.write(bImage);
                    output.flush();
                }
            }).build();
        }
        else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @POST
    public Response addImage(newImage image){
        Image i = iB.addImage(image);
        if(i != null) {
            return Response.status(Response.Status.OK).entity(i).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @Path("{id}")
    @PUT
    public Response updateImage(@PathParam("id") Integer id, updateImage image) {
        Image i = iB.updateImage(id, image);
        if(i != null) {
            return Response.status(Response.Status.OK).entity(i).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @Path("{id}")
    @DELETE
    public Response deleteImage(@PathParam("id") Integer id) {
        if(iB.deleteImage(id)) {
            return Response.status(Response.Status.NO_CONTENT).build();
        } else {
            return Response.status(Response.Status.CONFLICT).build();
        }
    }

    @POST
    @Path("encode")
    @Consumes("*/*")
    @Produces("text/html")
    public Response EncodeImageToBASE64(InputStream stream) {
        try {
            byte[] image = IOUtils.toByteArray(stream);
            String encodedString = Base64.getEncoder().encodeToString(image);
            return Response.ok().entity(encodedString).build();
        } catch (IOException e) {
            e.printStackTrace();
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }
}

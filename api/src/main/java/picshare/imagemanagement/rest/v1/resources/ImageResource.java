package picshare.imagemanagement.rest.v1.resources;

import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;
import com.kumuluz.ee.logs.cdi.Log;
import com.kumuluz.ee.rest.beans.QueryParameters;
import org.apache.commons.io.IOUtils;
import org.eclipse.microprofile.metrics.annotation.Metered;
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

@Path("image")
@Log
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class ImageResource {
    private static final Logger LOG = LogManager.getLogger(ImageResource.class.getName());

    @Context
    protected UriInfo uriInfo;

    @Inject
    ImageBean iB;

    @GET
    @Metered(name = "requests.get.images")
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
    @Metered(name = "requests.get.image.data")
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
    @Metered(name = "requests.get.image.raw")
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
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }


    @Path("/QR/{id}")
    @Produces("image/png")
    @GET
    @Metered(name = "requests.get.image,QR")
    public Response returnImageQR(@PathParam("id") Integer id){
        byte[] bImage = iB.getImageQR(id);
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
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }


    @POST
    @Metered(name = "requests.add.image")
    public Response addImage(newImage image){
        Image i = iB.addImage(image);
        if(i != null) {
            if(i.getImageId() == 0) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("FAULT TOLEARNCE").build();
            }
            return Response.status(Response.Status.OK).entity(i).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @Path("{id}")
    @PUT
    @Metered(name = "requests.update.image")
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
    @Metered(name = "requests.delete.image")
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

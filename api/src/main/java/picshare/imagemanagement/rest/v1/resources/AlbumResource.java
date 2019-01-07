package picshare.imagemanagement.rest.v1.resources;

import com.kumuluz.ee.rest.beans.QueryParameters;
import picshare.imagemanagement.entitete.jpa.Album;
import picshare.imagemanagement.storitve.beans.AlbumBean;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.List;
import java.util.logging.Logger;

@Path("album")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class AlbumResource {
    private final Logger log = Logger.getLogger(this.getClass().getName());

    @Context
    protected UriInfo uriInfo;

    @Inject
    AlbumBean aB;

    @GET
    public Response returnAlbums(){
        QueryParameters query = QueryParameters.query(uriInfo.getRequestUri().getQuery()).build();
        List<Album> albums = aB.getAllAlbums(query);
        if(albums != null && albums.size() > 0) {
            return Response.status(Response.Status.OK).entity(albums).build();
        }
        else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @Path("/{id}")
    @GET
    public Response returnAlbum(@PathParam("id") Integer id){
        Album album = aB.getAlbum(id);
        if(album != null) {
            return Response.status(Response.Status.OK).entity(album).build();
        }
        else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @POST
    public Response addAlbum(Album album){
        Album a = aB.addAlbum(album);
        if(a != null) {
            return Response.status(Response.Status.OK).entity(a).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @Path("{id}")
    @PUT
    public Response updateAlbum(@PathParam("id") Integer id, Album album) {
        Album a = aB.updateAlbum(id, album);
        if(a != null) {
            return Response.status(Response.Status.OK).entity(a).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @Path("{id}")
    @DELETE
    public Response deleteAlbum(@PathParam("id") Integer id) {
        if(aB.deleteAlbum(id)) {
            return Response.status(Response.Status.NO_CONTENT).build();
        } else {
            return Response.status(Response.Status.CONFLICT).build();
        }
    }
}

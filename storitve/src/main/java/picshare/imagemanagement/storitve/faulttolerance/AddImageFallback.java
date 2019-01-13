package picshare.imagemanagement.storitve.faulttolerance;

import org.eclipse.microprofile.faulttolerance.ExecutionContext;
import org.eclipse.microprofile.faulttolerance.FallbackHandler;
import picshare.imagemanagement.entitete.jpa.Image;

import javax.enterprise.context.RequestScoped;

@RequestScoped
public class AddImageFallback implements FallbackHandler<Image> {

    @Override
    public Image handle(ExecutionContext executionContext) {
        Image i = new Image();
        i.setImageId(0);
        return i;
    }
}
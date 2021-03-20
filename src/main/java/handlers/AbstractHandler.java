package handlers;

import payloads.EmptyPayload;
import payloads.Validable;
import spark.Request;
import spark.Response;
import spark.Route;
import utils.Answer;

import javax.servlet.MultipartConfigElement;
import java.util.Collection;
import java.util.Map;

public abstract class AbstractHandler<V extends Validable> implements RequestHandler<V>, Route {
    private final Class<V> payloadClass;

    protected AbstractHandler(Class<V> payloadClass) {
        this.payloadClass = payloadClass;
    }

    @Override
    public final Answer process(V payload, Map<String, String> requestParams) {
        if (payload != null && !payload.isValid()) {
            return Answer.badRequest("Cuerpo de la petición no válido");
        } else {
            return processRequest(payload, requestParams);
        }
    }

    protected abstract Answer processRequest(V payload, Map<String, String> requestParams);

    @Override
    public Object handle(Request request, Response response) throws Exception {
        request.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("/temp"));
        V payload = null;
        if (payloadClass != EmptyPayload.class) {
            payload = payloadClass.getConstructor(Collection.class).newInstance(request.raw().getParts());
        }

        Answer answer = process(payload, request.params());
        response.status(answer.getCode());
        response.type("application/json");
        return answer.getBody();
    }
}

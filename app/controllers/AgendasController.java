package controllers;

import acl.AuthAction;
import acl.types.Attrs;
import acl.types.User;
import com.google.inject.Inject;
import play.libs.Json;
import play.mvc.*;
import service.AgendasService;

import java.util.HashMap;

@With(AuthAction.class)
public class AgendasController extends Controller {

    private AgendasService agendasService;

    @Inject
    public AgendasController(AgendasService agendasService) {
        this.agendasService = agendasService;
    }

    public Result find(String date, Http.Request request) {
        return request.attrs().getOptional(Attrs.USER).map(user ->
                ok(Json.toJson(getAuthorizedResponse(user, agendasService.findByFecha(date))))
        ).orElse(unauthorized());
    }

    private HashMap getAuthorizedResponse(User user, Object data) {
        HashMap response = new HashMap();
        response.put("data", data);
        response.put("user", user);
        return response;
    }

}

package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;
import acl.types.Attrs;
import acl.AuthAction;
import acl.types.User;
import domain.Agenda;
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

    public Result list(Http.Request request) {
        return  request.attrs().getOptional(Attrs.USER)
                .map(user ->
                        ok(Json.toJson(getAuthorizedResponse(user, agendasService.findAll())))
                ).orElse(unauthorized());
    }

    public Result find(Integer id, Http.Request request) {
        return  request.attrs().getOptional(Attrs.USER)
                .map(user -> agendasService.find(id)
                        .map(agenda ->
                                ok(Json.toJson(getAuthorizedResponse(user, agenda)))
                        ).orElse(notFound())
                ).orElse(unauthorized());
    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result save(Http.Request request) {
        return request.attrs().getOptional(Attrs.USER)
                .map(user -> {
                    JsonNode json = request.body().asJson();
                    Agenda agenda = Json.fromJson(json, Agenda.class);
                    Agenda data = agendasService.save(agenda);
                    return ok(Json.toJson(getAuthorizedResponse(user, data)));
                }).orElse(unauthorized());
    }

    private HashMap getAuthorizedResponse(User user, Object data) {
        HashMap response = new HashMap();
        response.put("data", data);
        response.put("user", user);
        return response;
    }

}

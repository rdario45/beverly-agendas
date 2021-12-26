package controllers;

import acl.AuthAction;
import acl.types.Attrs;
import acl.types.User;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;
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
        return request.attrs().getOptional(Attrs.USER).map(user ->
                ok(Json.toJson(getAuthorizedResponse(user, agendasService.findAll())))
        ).orElse(unauthorized());
    }

    public Result find(String date, Http.Request request) {
        return request.attrs().getOptional(Attrs.USER).map(user ->
                ok(Json.toJson(getAuthorizedResponse(user, agendasService.findAllByFecha(date))))
        ).orElse(unauthorized());
    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result save(Http.Request request) {
        return request.attrs().getOptional(Attrs.USER).map(user -> {
            JsonNode json = request.body().asJson();
            Agenda agenda = Json.fromJson(json, Agenda.class);
            Agenda data = agendasService.save(agenda);
            return ok(Json.toJson(getAuthorizedResponse(user, data)));
        }).orElse(unauthorized());
    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result update(String id, Http.Request request) {
        return request.attrs().getOptional(Attrs.USER).map(user -> {
            JsonNode json = request.body().asJson();
            Agenda agenda = Json.fromJson(json, Agenda.class);
            return agendasService.update(agenda, id).map(data ->
                    ok(Json.toJson(getAuthorizedResponse(user, data)))
            ).orElse(notFound());
        }).orElse(unauthorized());
    }

    private HashMap getAuthorizedResponse(User user, Object data) {
        HashMap response = new HashMap();
        response.put("data", data);
        response.put("user", user);
        return response;
    }

}

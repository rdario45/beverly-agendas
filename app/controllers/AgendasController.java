package controllers;

import acl.BeverlyAuthAction;
import acl.types.BeverlyHttpAuthObject;
import acl.types.BeverlyHttpReqAttrib;
import com.google.inject.Inject;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.With;
import service.AgendasService;

import java.util.HashMap;

@With(BeverlyAuthAction.class)
public class AgendasController extends Controller {

    private AgendasService agendasService;

    @Inject
    public AgendasController(AgendasService agendasService) {
        this.agendasService = agendasService;
    }

    public Result getWeeklyAgenda(String startDate, String finalDate, Http.Request request) {
        return request.attrs().getOptional(BeverlyHttpReqAttrib.USER).map(user ->
                ok(Json.toJson(getAuthorizedResponse(user, agendasService.findByRange(startDate, finalDate))))
        ).orElse(unauthorized());
    }

    public Result getBalancePieChart(String startDate, String finalDate, Http.Request request) {
        return request.attrs().getOptional(BeverlyHttpReqAttrib.USER).map(user ->
                ok(Json.toJson(getAuthorizedResponse(user, agendasService.getBalancePieChart(startDate, finalDate))))
        ).orElse(unauthorized());
    }

    public Result getBalanceBarChart(String startDate, String finalDate, Http.Request request) {
        return request.attrs().getOptional(BeverlyHttpReqAttrib.USER).map(user ->
                ok(Json.toJson(getAuthorizedResponse(user, agendasService.getBalanceBarChart(startDate, finalDate))))
        ).orElse(unauthorized());
    }

    private HashMap getAuthorizedResponse(BeverlyHttpAuthObject user, Object data) {
        HashMap response = new HashMap();
        response.put("data", data);
        response.put("user", user.phone);
        return response;
    }

}

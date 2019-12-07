package com.gwidgets.resources;

import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.keycloak.events.EventBuilder;
import org.keycloak.events.EventType;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.jboss.logging.Logger;

public class ApiKeyResource {

    private static final Logger logger = Logger.getLogger(ApiKeyResource.class);

    private KeycloakSession session;

    public ApiKeyResource(KeycloakSession session) {
        this.session = session;
    }

    @GET
    @Produces("application/json")
    public Response checkApiKey(@QueryParam("apiKey") String apiKey) {
        RealmModel realm = session.getContext().getRealm();

        logger.info("Trying to validate apiKey: " + apiKey + " at realm " + realm.getName());
        List<UserModel> result = session.userStorageManager().searchForUserByUserAttribute("api-key", apiKey, realm);

        EventBuilder event = new EventBuilder(realm, session, session.getContext().getConnection());
        event.detail("api-key", apiKey);
        Response response;

        if (result.isEmpty()) {
            event.event(EventType.LOGIN_ERROR);
            event.error("Invalid attempt for login using api-key");
            response = Response.status(401).build();
        } else {
            event.event(EventType.LOGIN);
            event.user(result.get(0).getId());
            event.success();
            response = Response.ok().build();
        }

        return response;
    }
}

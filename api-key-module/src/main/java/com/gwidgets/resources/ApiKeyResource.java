package com.gwidgets.resources;

import java.util.List;
import java.util.stream.Stream;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.MediaType;

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
        Stream<UserModel> result = session.users().searchForUserByUserAttributeStream(realm, "apiKey", apiKey);
        UserModel user = result.findFirst().orElse(null);

        EventBuilder event = new EventBuilder(realm, session, session.getContext().getConnection());
        event.detail("api-key", apiKey);
        Response response;

        if (user == null) {
            event.event(EventType.LOGIN_ERROR);
            event.error("Invalid attempt for login using api-key (no user found for this apiKey)");
            response = Response.status(401).build();
        } else {
            event.user(user.getId());
            if (user.isEnabled()) {
                event.event(EventType.LOGIN);
                event.success();
                String message = "{\"userID\": \"" + user.getId() + "\"}";
                response = Response.ok().entity(message).type(MediaType.APPLICATION_JSON).build();
            } else {
                event.event(EventType.LOGIN_ERROR);
                event.error("Invalid attempt for login using api-key (user corresponding to this apiKey is disabled)");
                response = Response.status(401).build();
            }
        }

        return response;
    }
}

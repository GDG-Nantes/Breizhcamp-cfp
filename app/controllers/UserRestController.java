package controllers;

import static play.libs.Json.toJson;
import models.User;
import play.mvc.Controller;
import play.mvc.Result;
import securesocial.core.Identity;
import securesocial.core.java.SecureSocial;

import java.util.ArrayList;
import java.util.List;

/**
 * Controlleur pour gérer les requêtes Ajax liées aux utilisateurs
 * 
 * @author lhuet
 */
@SecureSocial.SecuredAction(ajaxCall = true)
public class UserRestController extends Controller {

    private static User getLoggedUser() {
        Identity socialUser = (Identity) ctx().args.get(SecureSocial.USER_KEY);
        User user = User.findByExternalId(socialUser.id().id(), socialUser.id().providerId());
        return user;
    }

    /**
     * Récupère l'ensemble des utilisateurs
     *
     * @return Liste des utilisateurs en JSON
     */
    public static Result get() {
        User user = getLoggedUser();
        // Requête réservée aux admins
        if (!user.admin) {
            return forbidden();
        }
        return ok(toJson(User.findAll()));
    }

    public static Result getCoSpeakers() {
        User user = getLoggedUser();

        List<User> coSpeakers = new ArrayList<User>();
        for (User coSpeaker : User.findAll()) {
            if (!coSpeaker.id.equals(user.id)) {
                coSpeaker.adresseMac = null;
                coSpeaker.authenticationMethod = null;
                coSpeaker.admin = null;
                coSpeaker.dateCreation = null;
                coSpeaker.email = null;
                coSpeaker.description = null;
                coSpeaker.setNotifAdminOnAllTalk(null);
                coSpeaker.setNotifAdminOnTalkWithComment(null);
                coSpeaker.setNotifOnMyTalk(null);
                coSpeakers.add(coSpeaker);
            }
        }
        return ok(toJson(coSpeakers));
    }

    /**
     * Récupère un utilisateur par son id
     *
     * @param id
     * @return Objet utilisateur en JSON
     */
    public static Result getUser(Long id) {
        // TODO: Vérifier getUser(id) n'est pas réservé à un admin => Pb de secu !
        User user = User.findById(id);
        if (user == null) {
            return notFound();
        }

        return ok(toJson(user));
    }

    /**
     * Récupère les données de l'utilisateur loggué
     * 
     * @return Objet Utilisateur en JSON
     */
    public static Result getUserLogged() {
        User user = getLoggedUser();
        return ok(toJson(user));
    }
}

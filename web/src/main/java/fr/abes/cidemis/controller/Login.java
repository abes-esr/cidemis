package fr.abes.cidemis.controller;

import fr.abes.cidemis.exception.DaoException;
import fr.abes.cidemis.model.cidemis.RegistryUser;
import fr.abes.cidemis.web.MyDispatcher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.net.MalformedURLException;
import java.net.URL;

@Slf4j
@MultipartConfig
@Controller
public class Login extends AbstractServlet {
    @Value("${wsAuthSudoc.url}")
    private String urlAuth;

    @Override
    protected boolean checkSession() {
        return false;
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String login(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws MalformedURLException {
        String forward = this.catchProcessRequest(request, response);
        if (!forward.equals("")) {
            return forward;
        }
        param.setRequest(request);

        String userKey = request.getParameter("j_username");
        String password = request.getParameter("j_password");

        URL url = new URL(urlAuth);
        RegistryUser user = param.readUserWsProfile(url, userKey, password);
        if (user != null) {
            HttpSession newSession = request.getSession(true);
            String forwardedUri = (String) request.getAttribute("javax.servlet.forward.request_uri");

            if (forwardedUri != null && forwardedUri.contains("afficher-demande")) {
                newSession.setAttribute("forwarded_uri", "afficher-demande");
                newSession.setAttribute("id_demande", Integer.parseInt(request.getParameter("id")));
            }
            if (newSession.getAttribute("connexion") == null) {
                try {
                    if (param.login(newSession, user)) {
                        // Sert à vider la mémoire de la datatable lors de la première connexion
                        newSession.setAttribute("login", true);
                    }
                } catch (DaoException e) {
                    e.printStackTrace();
                    request.setAttribute("tier_exception", e.getTierOfException());
                    request.setAttribute("table_exception", e.getTableOfException());
                    request.setAttribute("message_exception", e.getMessage());
                    request.setAttribute("link_redirection", "/");
                    return MyDispatcher.ERROREXCEPTION;
                }
            }
            request.getParameter("ajax");
            if ("true".equals(param.getParameter("ajax")))
                return MyDispatcher.LOGOUTAJAX;
            if (request.getCookies() == null) {
                return "redirect:" + MyDispatcher.LISTE_DEMANDES;
            }
            for (Cookie cookie : request.getCookies()) {
                if (cookie.getName().equals("urlCourante")) {
                    Object url_redirect = cookie.getValue();
                    //après récupération de l'url de redirection on supprime le cookie pour les connexions ultérieures
                    cookie.setValue("");
                    cookie.setPath("/");
                    cookie.setMaxAge(0);
                    response.addCookie(cookie);
                    return "redirect:" + url_redirect;
                }
            }
            return "redirect:" + MyDispatcher.LISTE_DEMANDES;
        } else {
            return MyDispatcher.ERREUR_LOGINJSP;
        }
    }

    @Override
    protected String getServletInfo() {
        return "Controller login";
    }
}

package fr.abes.cidemis.controller;

import fr.abes.cidemis.web.MyDispatcher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Slf4j
@Controller
public class LogoutServlet extends AbstractServlet {
	@Value("${DATATABLE_COOKIE_PREFIX}")
	private String cookiePrefix;

	@Override
    protected boolean checkSession() {
        return false;
    }

    @RequestMapping(value = "/logout")
    protected String logout(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ServletException, IOException {
        response.setHeader("Cache-Control", "no-cache, no-store");
        response.setHeader("Pragma", "no-cache");
        
        if (session != null) {
            deleteCookie(request, response, cookiePrefix);
            session.removeAttribute("connection");
            session.invalidate();
        }

        return MyDispatcher.LOGINJSP;
    }
    
    public static void deleteCookie(HttpServletRequest request, HttpServletResponse response, String cookieName){
        Cookie[] serverCookies = request.getCookies();
        if  (serverCookies != null){
            for (Cookie cookie : serverCookies){
                //Patch prod : suppression de l'ensemble des cookies
                if( cookie != null){
                    //Supprime le cookie
                    cookie.setMaxAge(0);
                    response.addCookie(cookie);
                }
                //Supprime les cookies pour la persistance des cases à cocher sur le tableau de bord si on se reconnecte
                if( cookie != null && cookie.getName().startsWith("jsSession")){
                    //Supprime le cookie
                    response.setContentType("text/html"); //obligatoire sinon persistance
                    cookie.setMaxAge(0);
                    response.addCookie(cookie);
                }
                if (cookie != null && cookie.getName().startsWith(cookieName)){
                    cookie.setMaxAge(0);//delete
                    response.addCookie(cookie);
                }
            }
        }
    }

    @Override
    public String getServletInfo() {
        return "Déconnecte l'utilisateur";
    }

}

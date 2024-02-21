package fr.abes.cidemis.controller;

import fr.abes.cidemis.constant.Constant;
import fr.abes.cidemis.service.CidemisManageService;
import fr.abes.cidemis.web.MyDispatcher;
import fr.abes.cidemis.web.ParamHelper;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Enumeration;

@Slf4j
@Getter
public abstract class AbstractServlet {
    private static final long serialVersionUID = -7728100545184244608L;
    @Autowired
    protected ParamHelper param;
    @Autowired
    private CidemisManageService service;

    /**
     * Retourne si, pour la servlet, il faut redirriger vers la servlet de déconnexion
     *
     * @return
     */
    protected boolean checkSession() {
        return true;
    }

    /**
     * Launch servlet and catch Exception if needed
     *
     * @param request
     * @param response
     */
    protected String catchProcessRequest(HttpServletRequest request, HttpServletResponse response) {
        try {
            request.setCharacterEncoding(Constant.ENCODE);
            response.setContentType("text/html;charset=UTF-8");
            response.setHeader("Expires", "Sat, 6 May 1995 12:00:00 GMT");
            response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
            response.addHeader("Cache-Control", "post-check=0, pre-check=0");
            response.setHeader("Pragma", "no-cache");
            //Voir doc API Oracle https://docs.oracle.com/javaee/6/api/javax/servlet/http/HttpServletRequest.html
            HttpSession session = request.getSession(false);

            //Loggue la requête pour debugger
            //debug_httpServletRequestParamLogger(request);

            if (this.checkSession() && session.getAttribute("connexion") == null) {
                String url = request.getRequestURL() + ((request.getQueryString() != null) ? "?" + request.getQueryString() : "");
                response.addCookie(new Cookie("urlCourante", url));
                return "redirect:" + MyDispatcher.LOGOUT;
            }
            if (session.getAttribute("urlCourante") != null) {
                return "redirect:" + session.getAttribute("urlCourante");
            }

        } catch (Exception e) {
            log.error("Error JSP", e);
            return MyDispatcher.ERREUR_500JSP;
        }
        return "";
    }

    static void debug_httpServletRequestParamLogger(HttpServletRequest request) {
        log.debug("");
        log.debug("HttpServletRequest Method Type: " + request.getMethod());
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            if (headerName.matches("origin") || headerName.matches("sec-ch-ua") || headerName.matches("cookie")) {
                log.debug("HttpServletRequest Header Name: " + headerName + "Value: " + request.getHeader(headerName));
            }
        }
        Enumeration<String> params = request.getParameterNames();
        while (params.hasMoreElements()) {
            String paramName = params.nextElement();
            log.debug("HttpServletRequest Parameter: " + paramName + ", Value: " + request.getParameter(paramName));
        }
        Enumeration<String> attributes = request.getSession().getAttributeNames();
        while (attributes.hasMoreElements()) {
            String attribute = attributes.nextElement();
            log.debug("HttpSession Attribute Name: " + attribute);
        }
        log.debug("");
    }

    static void debug_resultMethodLogguer(String nameOfMethod, Object object) {
        log.warn("METHOD CALLED OR ATTRIBUTE:" + nameOfMethod + "-> RESULT: " + object);
    }

    protected abstract String getServletInfo();
}

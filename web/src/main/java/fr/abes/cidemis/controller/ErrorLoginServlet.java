package fr.abes.cidemis.controller;

import fr.abes.cidemis.web.MyDispatcher;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ErrorLoginServlet {

    @GetMapping(value = MyDispatcher.ERREUR)
    public String error() {
        return MyDispatcher.ERREUR_LOGINJSP;
    }

    /*@Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ServletException, IOException {
        this.getServletContext().getRequestDispatcher( MyDispatcher.ERREUR_LOGINJSP ).forward( request, response );
    }

    @Override
    public String getServletInfo() {
        return "erreur d'authentification";
    }*/
}

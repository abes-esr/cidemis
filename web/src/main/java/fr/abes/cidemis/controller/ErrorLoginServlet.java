package fr.abes.cidemis.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import fr.abes.cidemis.web.MyDispatcher;

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
    */
}

package fr.abes.cidemis.controller;

import fr.abes.cidemis.service.CidemisManageService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Getter
@Controller
public class LastPPNSynchronized extends AbstractServlet {
    @Autowired
    private CidemisManageService service;

    @Override
    protected String getServletInfo() {
        return "Récupération dernière notice de la base XML";
    }

    @Override
    protected boolean checkSession() { return false;}

    @GetMapping(value = "/LastPPNSynchronized")
    public void lastPPNSynchronized( HttpServletResponse response) throws IOException {
        response.getWriter().append(getService().getTools().getLastDateSynchronized());
    }

}

package fr.abes.cidemis.controller;

import java.io.IOException;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import fr.abes.cidemis.service.IToolsService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;

@Getter
@Controller
public class LastPPNSynchronized extends AbstractServlet {
    private final IToolsService tools;


    public LastPPNSynchronized(IToolsService tools) {
        this.tools = tools;
    }


    @Override
    protected boolean checkSession() { return false;}

    @GetMapping(value = "/LastPPNSynchronized")
    public void lastPPNSynchronized( HttpServletResponse response) throws IOException {
        response.getWriter().append(this.tools.getLastDateSynchronized());
    }

}

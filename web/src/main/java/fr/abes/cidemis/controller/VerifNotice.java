package fr.abes.cidemis.controller;

import fr.abes.cbs.exception.CBSException;
import fr.abes.cbs.process.ProcessCBS;
import fr.abes.cidemis.components.NoticeHelper;
import fr.abes.cidemis.model.cidemis.Connexion;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

@Slf4j
@Controller
public class VerifNotice extends AbstractServlet {
    @Value("${cbs.url}")
    private String cbsUrl;
    @Value("${cbs.port}")
    private String cbsPort;
    @Value("${cbs.password}")
    private String cbsPassword;

    @Override
    protected boolean checkSession() {
        return true;
    }

    @RequestMapping(value = "/verifier-notice")
    public void verifierNotice(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws IOException {
        debug_httpServletRequestParamLogger(request);
        debug_resultMethodLogguer("session", session);
        debug_resultMethodLogguer("session.getAttribute(\"connexion\")", session.getAttribute("connexion"));

        Connexion connexion = (Connexion) session.getAttribute("connexion");
        param.setRequest(request);
        String ppn = param.getParameter("ppn");
        PrintWriter out = response.getWriter();
        
        if ((ppn.length() == 9) &&  ppn.matches("(\\d{9})|((?i)\\d{8}X{1})")) {
            ProcessCBS cbs = new ProcessCBS();
            try {
            	cbs.authenticate(cbsUrl, cbsPort, "M"+connexion.getRegistryuser().getLibrary(), cbsPassword);
            	
            	NoticeHelper noticehelper = new NoticeHelper(cbs);
            	noticehelper.canModifyNotice(ppn);
            	out.print("OK");
            } catch (CBSException ex) {
            	out.print("NOK");
            }
        }
    }

    @Override
    public String getServletInfo() {
        return "Vérifie que l'utilisateur peut modifier une notice";
    }
}

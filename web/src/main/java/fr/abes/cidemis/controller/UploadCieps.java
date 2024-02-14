package fr.abes.cidemis.controller;

import fr.abes.cidemis.constant.Constant;
import fr.abes.cidemis.process.UploadCiepsProcess;
import fr.abes.cidemis.web.MyDispatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Controller
public class UploadCieps extends AbstractServlet {
    @Autowired
    private UploadCiepsProcess uploadCiepsProcess;
    @Override
    protected boolean checkSession() {
        return true;
    }

    @RequestMapping(value = "/upload-cieps", method = RequestMethod.POST)
	protected String processRequest(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        String forward = this.catchProcessRequest(request, response);
        if (!forward.equals("")) {
            return forward;
        }

        response.setContentType("text/html;charset=" + Constant.ENCODE);
        
        uploadCiepsProcess.processRequest(session, request);
        return MyDispatcher.UPLOADCIEPS;
    }

    @Override
    public String getServletInfo() {
        return "Permet au CIEP d'envoyer les fichiers remplits de demande (cette servlet traite le fichier reçu)";
    }

}

package fr.abes.cidemis.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import fr.abes.cidemis.constant.Constant;
import fr.abes.cidemis.process.UploadCiepsProcess;
import fr.abes.cidemis.web.MyDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
public class UploadCieps extends AbstractServlet {
    private final UploadCiepsProcess uploadCiepsProcess;

    public UploadCieps(UploadCiepsProcess uploadCiepsProcess) {
        this.uploadCiepsProcess = uploadCiepsProcess;
    }
    
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
}

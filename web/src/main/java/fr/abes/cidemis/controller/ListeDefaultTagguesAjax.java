package fr.abes.cidemis.controller;

import fr.abes.cidemis.constant.Constant;
import fr.abes.cidemis.model.cidemis.DefaultTaggues;
import fr.abes.cidemis.service.CidemisManageService;
import org.json.JSONArray;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/liste-taggues-ajax")
public class ListeDefaultTagguesAjax extends AbstractServlet{
	private static final long serialVersionUID = -2917772721708462881L;

	public void processRequest(HttpServletRequest request, HttpServletResponse response, HttpSession session)
			throws IOException {
		response.setContentType("application/javascript;charset=" + Constant.ENCODE);
		CidemisManageService service = new CidemisManageService();
		
		DefaultTaggues defaultTaggues = service.getTaggues().findDefaultTagguesByLibelle(request.getParameter("term"));
		JSONArray tagguesDataJson = new JSONArray();
		tagguesDataJson.put(defaultTaggues.getLibelleTaggue());

		tagguesDataJson.write(response.getWriter());
	}
	
	@Override
	public String getServletInfo() {
		return "Retourne les taggues par défaut";
	}
}

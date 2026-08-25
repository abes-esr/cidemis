package fr.abes.cidemis.controller;

import java.io.IOException;

import org.json.JSONArray;

import fr.abes.cidemis.constant.Constant;
import fr.abes.cidemis.model.cidemis.DefaultTaggues;
import fr.abes.cidemis.service.ITagguesService;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/liste-taggues-ajax")
public class ListeDefaultTagguesAjax extends AbstractServlet{
	private final ITagguesService taggues;

	public ListeDefaultTagguesAjax(ITagguesService taggues) {
		this.taggues = taggues;
	}


	public void processRequest(HttpServletRequest request, HttpServletResponse response, HttpSession session)
			throws IOException {
		response.setContentType("application/javascript;charset=" + Constant.ENCODE);
		
		DefaultTaggues defaultTaggues = this.taggues.findDefaultTagguesByLibelle(request.getParameter("term"));
		JSONArray tagguesDataJson = new JSONArray();
		tagguesDataJson.put(defaultTaggues.getLibelleTaggue());

		tagguesDataJson.write(response.getWriter());
	}
}

package fr.abes.cidemis.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import fr.abes.cidemis.constant.Constant;
import fr.abes.cidemis.exception.DaoException;
import fr.abes.cidemis.localisation.LocalProvider;
import fr.abes.cidemis.model.cidemis.Connexion;
import fr.abes.cidemis.model.cidemis.Demandes;
import fr.abes.cidemis.model.cidemis.Options;
import fr.abes.cidemis.model.cidemis.Taggues;
import fr.abes.cidemis.service.IDemandesService;
import fr.abes.cidemis.service.IOptionsService;
import fr.abes.cidemis.web.ParamHelper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class ListeDemandesAjax extends AbstractServlet {
    private final ParamHelper param;
    private final IDemandesService demandesService;
    private final IOptionsService options;

    public ListeDemandesAjax(ParamHelper param, IOptionsService options, IDemandesService demandes) {
        this.param = param;
        this.demandesService = demandes;
        this.options = options;
    }

	@Override
	protected boolean checkSession() { return true; }

	@RequestMapping(value = "/liste-demandes-ajax", method = RequestMethod.GET)
	public void processRequest(HttpServletRequest request, HttpServletResponse response, HttpSession session)
			throws IOException {
		this.catchProcessRequest(request, response);

		Connexion connexion = (Connexion) session.getAttribute("connexion");
		param.setRequest(request);
		String profil = (session.getAttribute("profil") != null) ? session.getAttribute("profil").toString():"";
		Integer profilId = Constant.getProfiles().get(profil);

		//Booléens basés sur le statut des cases à cocher (si l'on veut les demandes archivées et terminées)
		boolean demandesDoneChecked = Boolean.parseBoolean(param.getParameter("achieved"));
		boolean demandesArchivedChecked = Boolean.parseBoolean(param.getParameter("archived"));

		// Si l'utilisateur a été affecté à un profil et Si aucun profil n'a été
		// sélectionné par l'utilisateur (dans la liste du menu)
		if (connexion.getUser().getIdProfil() != null && profilId == 0)
			profilId = connexion.getUser().getIdProfil();

		/**
		 * On récupère les demandes de l'utilisateur
		 */
		List<Demandes> demandes = this.demandesService.findDemandesByCbsUsers(connexion.getUser(), demandesDoneChecked, demandesArchivedChecked);
		if (!Objects.equals(profilId, Constant.PROFIL_PAS_PROFIL) && !Objects.equals(profilId, Constant.PROFIL_TOUTES_DEMANDES)) {
			Iterator<Demandes> it = demandes.iterator();

			while (it.hasNext()) {
				Demandes d = it.next();
				if (!d.getIdProfil().equals(profilId))
					it.remove();
			}
		}

		if (demandes == null)
			demandes = new ArrayList<>();

		/**
		 * Liste pour le menu "Afficher/Masquer des colonne"
		 */
		List<Options> optionsColonnesListe = null;
		try {
			optionsColonnesListe = this.options.findOptionsColonnesByCbsUsers(connexion.getUser());
		} catch (DaoException e) {
			log.error("ERREUR: " + e.getTierOfException() + " : " + e.getTypeOfException() + " : TABLE " + e.getTableOfException());
		}

		List<Options> optionsColonnesListeOrdered = new ArrayList<>();
		LocalProvider lang = new LocalProvider(request.getLocale());

		for (Options o : optionsColonnesListe) {
			o.setName(lang.getMsg(o.getLibOption()));
			o.setTypeInput(Constant.getColonnes().get(o.getLibOption()));
			optionsColonnesListeOrdered.add(new Options(o));
		}

		request.setAttribute("optionscolonnes_liste", optionsColonnesListe);
		Options optionsClass = new Options();
		Collections.sort(optionsColonnesListeOrdered, optionsClass.new OptionNameComparator());

		/**
		 * Et on traite et on prépare le JSON
		 */
		JSONObject demandesJson = new JSONObject();
		JSONArray demandesDataJson = new JSONArray();
		JSONObject demandeJson;
		JSONObject action;
		
		
		String value;

		for (Demandes de : demandes) {
			demandeJson = new JSONObject();
			for (Options option : optionsColonnesListe) {
				value = this.demandesService.getDemandemap(de).get(option.getLibOption());
				demandeJson.put(option.getLibOption(), (value == null) ? "" : value);
			}

			//ajout des tag
			JSONObject coltaggue = new JSONObject();
			if (de.getTaggues() != null) {
				coltaggue.put("id_demande", de.getIdDemande());
				JSONArray taggues = new JSONArray();
				Taggues tag = de.getTaggues();
				if (tag != null) {
					JSONObject tagJSON = new JSONObject();
					tagJSON.put("libelle",tag.getLibelleTaggue());
					tagJSON.put("couleur",tag.getCouleurTaggue());
					taggues.put(tagJSON);
				}
				coltaggue.put("taggues", taggues);
			}
			demandeJson.put("col_taggue", coltaggue);
			
			action = new JSONObject();
			action.put("id_demande", de.getIdDemande());
			action.put("pieces_justificatives_nb", de.getNbPiecesJustificatives());
			action.put("can_delete", this.demandesService.canUserDeleteDemande(connexion.getUser(), de));
			action.put("can_archive", this.demandesService.canUserArchiveDemande(connexion.getUser(), de));

			if (connexion.getUser().isISSNOrCIEPS())
				action.put("commentaires_nb", de.getNbCommentairesISSN());
			else
				action.put("commentaires_nb", de.getNbCommentaires());

			demandeJson.put("col_action", action);
			demandeJson.put("col_id_etat", de.getEtatsDemandes().getIdEtatDemande());
			demandesDataJson.put(demandeJson);
		}

		demandesJson.put("data", demandesDataJson);
		demandesJson.write(response.getWriter());
	}
}

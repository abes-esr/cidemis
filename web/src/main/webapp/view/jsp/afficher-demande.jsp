<%@ page import="fr.abes.cidemis.constant.Constant"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="fr">
	<head>
		<title>Cidemis - Tableau de bord</title>
		<jsp:include page="templates/meta.jsp" />
	</head>
	<body>
		<div class="page-container row-fluid sidebar-closed">
			<jsp:include page="templates/banner.jsp" />

			<section id='container'>
				<span class="titre_form">Formulaire de demande de
					${demande.typesDemandes.libelleTypeDemande}</span>
				<div class="fields">
					<fieldset>
						<legend>Informations sur la demande
							n°${demande.idDemande}</legend>
						<label>Date de création : </label><span>${demande.dateDemandeFormatee}</span><br />
						<label>Date de modification : </label><span>${demande.dateModifFormatee}</span><br />
						<label>RCR demandeur : </label><span>${demande.rcrDemandeur}</span><br />
						<label>Créateur de la demande : </label><span>${demande.cbsUsers.shortName}</span><br />
						<label>Rôle du créateur de la demande : </label><span>${demande.cbsUsers.roles.libRole}</span><br />
						<c:if
							test="${connexion.user.roles.idRole eq Constant.ROLE_RESPONSABLE_CR && connexion.user.userNum ne demande.cbsUsers.userNum }">
							<label>Email du créateur de la demande : </label>
							<span>${demande.cbsUsers.userEmail}</span>
							<br />
						</c:if>
					</fieldset>
					<fieldset>
						<legend>Informations sur la notice</legend>
						<c:choose>
							<c:when
								test="${demande.typesDemandes.idTypeDemande eq Constant.TYPE_DEMANDE_CREATION && empty demande.notice.ppn}">
								<label>Titre : </label>
								<span>${demande.titre}</span>
								<br />
							</c:when>
							<c:otherwise>
								<c:choose>
									<c:when
										test="${connexion.user.roles.idRole eq Constant.ROLE_CIEPS || connexion.user.roles.idRole eq Constant.ROLE_ISSN}">
										<label>Ppn de la notice concernée : </label>
										<span><a target="_BLANK"
											href="${ Constant.PROPERTIES_PSI_LIEN_PERENNE_ISSN }?format=marc_read&ppn=${demande.notice.ppn}">${demande.notice.ppn}</a></span>
										<br />
									</c:when>
									<c:otherwise>
										<label>Ppn de la notice concernée : </label>
										<span><a target="_BLANK"
											href="${ Constant.PROPERTIES_PSI_LIEN_PERENNE }${demande.notice.ppn}">${demande.notice.ppn}</a></span>
										<br />
									</c:otherwise>
								</c:choose>
								<label>Type de document : </label>
								<span>${demande.notice.typeDocumentLibelle}</span>
								<br />
								<label>Type de ressource continue : </label>
								<span>${demande.notice.typeRessource}</span>
								<br />
								<label>Titre : </label>
								<span>${demande.titre}</span>
								<br />

								<c:if test="${not empty demande.conditionnalIssn}">
									<label>ISSN : </label>
									<span>${demande.conditionnalIssn}</span>
									<br />
								</c:if>

								<label>Pays de publication : </label>
								<span>${demande.notice.pays}</span>
								<br />
								<label>Date de publication : </label>
								<span>${demande.notice.datePublication}</span>
								<br />
							</c:otherwise>
						</c:choose>
						<label>Etat de la demande : </label><span><c:out
								value="${demande.etatsDemandes.libelleEtatDemande}" /></span><br />
					</fieldset>
					<c:if
						test="${not empty demandes_with_same_ppn.demandesList && (connexion.user.roles.idRole eq Constant.ROLE_CATALOGUEUR || connexion.user.roles.idRole eq Constant.ROLE_RESPONSABLE_CR || connexion.user.roles.idRole eq Constant.ROLE_ABES)}">
						<fieldset>
							<legend>Demandes antérieures sur la notice</legend>
							<div id="lierdemandediv">
								<table>
									<thead>
										<tr>
											<th>Numéro demande</th>
											<th>Date de création</th>
											<th>RCR demandeur</th>
											<th>Etat de la demande</th>
											<th>Nature de la demande</th>
											<th>Zone concernée</th>
											<th></th>
										</tr>
									</thead>
									<tbody>
										<c:forEach var="d"
											items="${ demandes_with_same_ppn.demandesList }">
											<c:if test="${ not (d.idDemande eq demande.idDemande) }">
												<tr>
													<td>${ d.idDemande }</td>
													<td>${ d.dateDemandeFormatee }</td>
													<td>${ d.rcrDemandeur }</td>
													<td>${ d.etatsDemandes.libelleEtatDemande }</td>
													<td>${ d.typesDemandes.libelleTypeDemande }</td>
													<td>${ d.zones }</td>
													<td><a target="_blank"
														href="afficher-demande?id=${ d.idDemande }">Consulter</a></td>
												</tr>
											</c:if>
										</c:forEach>
									</tbody>
								</table>
							</div>
						</fieldset>
						<br />
					</c:if>
					<c:if
						test="${demande.typesDemandes.idTypeDemande == Constant.TYPE_DEMANDE_CORRECTION}">
						<fieldset>
							<legend>Zone concernée</legend>
							<span>${demande.zones}</span>
						</fieldset>
					</c:if>
					<fieldset style="width:80%;">
						<legend>Commentaires</legend>
						<c:choose>
							<c:when test="${empty commentaires}">
								<span>Aucun commentaire</span>
							</c:when>
							<c:otherwise>
								<c:forEach var="c" items="${commentaires}">
									<c:if
										test="${c.visibleISSN || !connexion.user.isISSNOrCIEPS()}">
										<p>
											<span title="${ c.visibleISSN ? " Visible par ISSN/CIEPS" : "InvisiblepourISSN/CIEPS" }">
											<i class="fa fa-eye${ c.visibleISSN ? "" : "-slash" }"></i>
											</span>

											<c:choose>
												<c:when
													test="${connexion.user.roles.idRole == Constant.ROLE_ISSN || connexion.user.roles.idRole == Constant.ROLE_CIEPS}">
															Le ${c.dateCommentaireFormatee} par le ${c.cbsUsers.roles.libRole} :<br />
												</c:when>
												<c:otherwise>
															Le ${c.dateCommentaireFormatee} par ${c.cbsUsers.shortName} :<br />
												</c:otherwise>
											</c:choose>
											<i>${c.libCommentaireHTML}</i>
										</p>
									</c:if>
								</c:forEach>
							</c:otherwise>
						</c:choose>
					</fieldset>
					<c:if test="${ connexion.user.roles.idRole == Constant.ROLE_ABES || connexion.user.roles.idRole == Constant.ROLE_ISSN}">
							<fieldset id="tag" style="width:17%;">
								<legend>Définir le tag</legend>
								<select name="taggue">
									<option value="">Aucun tag</option>
									<c:forEach var="t" items="${Constant.getAllDefaultTaggues()}">
										<c:choose>
											<c:when
												test="${demande.taggues.libelleTaggue == t.libelleTaggue }">
												<option value="${t.libelleTaggue }" selected="selected">${t.libelleTaggue}</option>
											</c:when>
											<c:otherwise>
												<option value="${t.libelleTaggue }">${t.libelleTaggue}</option>
											</c:otherwise>
										</c:choose>
									</c:forEach>
								</select>
							</fieldset>
						</c:if>
					<fieldset>
						<legend>Justificatif(s)</legend>
						<div id="filestodelete"></div>
						<div id="filesup">
							<c:choose>
								<c:when test="${empty piecesJustificatives}">
									<span>Aucun justificatif</span>
								</c:when>
								<c:otherwise>
									<c:forEach var="p"
										items="${piecesJustificatives}"
										varStatus="status">
										<div id="fileup${p.idPiece}">
											<p>
												Envoyé par ${p.cbsUsers.shortName} (
												${p.cbsUsers.roles.libRole} ) : <a href="${p.urlfichier}">${p.publicname}</a>
											</p>
										</div>
									</c:forEach>
								</c:otherwise>
							</c:choose>
						</div>
					</fieldset>
				</div>
				<button
					class="formbutton goto_listedemande tbutton tbackground-color-green"
					title="Revenir au tableau de bord" type="button">
					<span>Revenir au tableau de bord</span>
				</button>
			</section>
		</div>
		<jsp:include page="templates/footer.jsp" />
	</body>
</html>

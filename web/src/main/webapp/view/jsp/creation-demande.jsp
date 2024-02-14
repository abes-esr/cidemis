<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="fr.abes.cidemis.constant.Constant" %>

<!DOCTYPE html>
<html lang="fr">
<head>
    <title>Cidemis - Création de demande</title>
    <jsp:include page="templates/meta.jsp"/>
</head>
<body>
<div class="page-container row-fluid sidebar-closed">
    <jsp:include page="templates/banner.jsp"/>

    <section id='container'>
        <form class="AJAXCreationForm" popupid="popup" id="creation-demande" name="creation-demande"
              action="creer-demande" accept-charset="UTF-8"
              method="POST">
            <span class="titre_form">Formulaire de demande de ${demande.typesDemandes.libelleTypeDemande}</span>
            <input type="hidden" id="ppn" name="ppn" value="${demande.notice.ppn}"/>
            <input type="hidden" id="type_demande" name="type_demande" value="${demande.typesDemandes.idTypeDemande}"/>
            <input type="hidden" id="filecount" name="filecount"
                   value="${fn:length(demande.allpiecesjustificativeslist)}"/>
            <input type="hidden" id="id_demande" name="id_demande" value="${demande.idDemande}"/>
            <input type="hidden" id="etat_demande" name="etat_demande" value="${demande.etatsDemandes.idEtatDemande}"/>
            <input type="hidden" id="actiondemande" name="actiondemande" value=""/>
            <input type="hidden" id="frbnf" name="frbnf" value="${demande.notice.frbnf}"/>
            <input type="hidden" id="titre" name="titre" value="${fn:escapeXml(demande.titre)}"/>
            <input type="hidden" id="user_role" name="user_role" value="${connexion.user.roles.idRole}"/>
            <div class="fields">
                <fieldset>
                    <c:choose>
                        <c:when test="${demande.idDemande eq -1}">
                            <legend>Informations sur la demande</legend>
                        </c:when>
                        <c:otherwise>
                            <legend>Informations sur la demande
                                n°${demande.idDemande}</legend>
                        </c:otherwise>
                    </c:choose>
                    <label>Etat de la demande : </label><span><c:out
                        value="${demande.etatsDemandes.libelleEtatDemande}"/></span><br/> <label>Date
                    de création : </label><span>${demande.dateDemandeFormatee}</span><br/>
                    <label>Date de modification : </label><span>${demande.dateModifFormatee}</span><br/>
                    <label>RCR demandeur : </label><span>${demande.rcrDemandeur}</span><br/>
                    <label>Créateur de la demande : </label><span>${demande.cbsUsers.shortName}</span><br/>
                    <label>Rôle du créateur de la demande : </label><span>${demande.cbsUsers.roles.libRole}</span><br/>
                    <c:if
                            test="${connexion.user.roles.idRole eq Constant.ROLE_RESPONSABLE_CR && connexion.user.userNum ne demande.cbsUsers.userNum }">
                        <label>Email du créateur de la demande : </label>
                        <span>${demande.cbsUsers.userEmail}</span>
                        <br/>
                    </c:if>
                </fieldset>
                <fieldset>
                    <legend>Informations sur la notice</legend>
                    <c:choose>
                        <c:when
                                test="${demande.typesDemandes.idTypeDemande eq Constant.TYPE_DEMANDE_CREATION && empty demande.notice.ppn}">
                            <label>Titre : </label>
                            <span>${demande.titre}</span>
                            <br/>
                        </c:when>
                        <c:otherwise>
                            <c:choose>
                                <c:when
                                        test="${connexion.user.roles.idRole eq Constant.ROLE_CIEPS || connexion.user.roles.idRole eq Constant.ROLE_ISSN}">
                                    <label>Ppn de la notice concernée : </label>
                                    <span><a target="_BLANK"
                                             href="${Constant.PROPERTIES_PSI_LIEN_PERENNE_ISSN}?format=marc_read&ppn=${demande.notice.ppn}">${demande.notice.ppn}</a></span>
                                    <br/>
                                </c:when>
                                <c:otherwise>
                                    <label>Ppn de la notice concernée : </label>
                                    <span><a target="_BLANK"
                                             href="${Constant.PROPERTIES_PSI_LIEN_PERENNE}${demande.notice.ppn}">${demande.notice.ppn}</a></span>
                                    <br/>
                                </c:otherwise>
                            </c:choose>
                            <label>Type de document : </label>
                            <span>${demande.notice.typeDocumentLibelle}</span>
                            <br/>
                            <label>Type de ressource continue : </label>
                            <span>${demande.notice.typeRessource}</span>
                            <br/>
                            <label>Titre : </label>
                            <span>${demande.titre}</span>
                            <br/>

                            <c:if test="${not empty demande.conditionnalIssn}">
                                <label>ISSN : </label>
                                <span>${demande.conditionnalIssn}</span>
                                <br/>
                            </c:if>

                            <label>Pays de publication : </label>
                            <span>${demande.notice.pays}</span>
                            <br/>
                            <label>Date de publication : </label>
                            <span>${demande.notice.datePublication}</span>
                            <br/>
                        </c:otherwise>
                    </c:choose>
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
                    <br/>
                </c:if>
                <c:if
                        test="${(connexion.user.roles.idRole == Constant.ROLE_RESPONSABLE_CR) && demande.cbsUsers.roles.idRole == Constant.ROLE_RESPONSABLE_CR}">
                    <fieldset>
                        <legend>RCR demandeur</legend>
                        <input type="text" name="num_RCR" id="num_RCR" maxlength="10"
                               value="${demande.rcrDemandeur}"/>
                        <div id="num_RCR_error"></div>
                    </fieldset>
                </c:if>
                <c:if
                        test="${connexion.user.roles.idRole eq Constant.ROLE_CIEPS || connexion.user.roles.idRole eq Constant.ROLE_ISSN}">
                    <fieldset>
                        <legend>Redirection de la demande</legend>
                        <input id="code_pays" name="code_pays" size="2" readonly
                               value="${demande.notice.pays}"/> <select id="code_pays_select">
                        <option selected disabled>Sélectionner un pays dans la
                            liste
                        </option>
                        <c:forEach var="code"
                                   items="${Constant.getListeLibellePaysCodePays()}">
                            <option value="${code.value}">${code.key}
                                (${code.value})
                            </option>
                        </c:forEach>
                    </select>
                    </fieldset>
                </c:if>
                <c:if test="${connexion.user.userKey == Constant.ADMIN_ISSN}">
                    <fieldset>
                        <legend>Profil de la demande</legend>
                        <select name="id_profil">
                            <option value="${Constant.PROFIL_COLLECTION}"
                                ${(demande.idProfil eq Constant.PROFIL_COLLECTION)?'selected="selected"':''}>Profil
                                collection
                            </option>
                            <option value="${Constant.PROFIL_PERIODIQUE_ELECTRONIQUE}"
                                ${(demande.idProfil eq Constant.PROFIL_PERIODIQUE_ELECTRONIQUE)?'selected="selected"':''}>
                                Profil
                                PER électronique
                            </option>
                            <option value="${Constant.PROFIL_PERIODIQUE_IMPRIME}"
                                ${(demande.idProfil eq Constant.PROFIL_PERIODIQUE_IMPRIME)?'selected="selected"':''}>
                                Profil
                                inventaire
                            </option>
                            <option value="${Constant.PROFIL_PERIODIQUE_IMPRIME_VIVANT}"
                                ${(demande.idProfil eq Constant.PROFIL_PERIODIQUE_IMPRIME_VIVANT)?'selected="selected"':''}>
                                Profil
                                PER vivants
                            </option>
                            <option value="${Constant.PROFIL_PERIODIQUE_IMPRIME_MORT}"
                                ${(demande.idProfil eq Constant.PROFIL_PERIODIQUE_IMPRIME_MORT)?'selected="selected"':''}>
                                Profil
                                PER morts
                            </option>
                            <option value="${Constant.PROFIL_INDETERMINE}"
                                ${(demande.idProfil eq Constant.PROFIL_INDETERMINE)?'selected="selected"':''}>Profil
                                indéterminé
                            </option>
                        </select>
                    </fieldset>
                </c:if>
                <c:if
                        test="${(connexion.user.roles.idRole == Constant.ROLE_ISSN || connexion.user.roles.idRole == Constant.ROLE_CIEPS) && demande.typesDemandes.idTypeDemande==Constant.TYPE_DEMANDE_NUMEROTATION}">
                    <fieldset>
                        <legend>Numéro ISSN</legend>
                        <input type="text" name="num_ISSN" id="num_ISSN"
                               value="${demande.issn}"/>
                        <div id="num_ISSN_error"></div>
                    </fieldset>
                </c:if>
                <c:if
                        test="${demande.typesDemandes.idTypeDemande eq Constant.TYPE_DEMANDE_CREATION && connexion.user.roles.idRole == Constant.ROLE_RESPONSABLE_CR}">
                    <fieldset>
                        <legend>PPN</legend>
                        <input type="text" name="num_ppn" id="num_ppn"
                               value="${demande.notice.ppn}"/>
                        <div id="num_ppn_error"></div>
                    </fieldset>
                </c:if>

                <c:if
                        test="${demande.typesDemandes.idTypeDemande == Constant.TYPE_DEMANDE_CORRECTION}">
                    <fieldset>
                        <legend>Zone concernée</legend>
                        <c:choose>
                            <c:when
                                    test="${connexion.user.roles.idRole != Constant.ROLE_ISSN && connexion.user.roles.idRole != Constant.ROLE_CIEPS}">
                                <select id="zones" name="zones">
                                    <c:forEach var="z" items="${Constant.getAllZoneCorrection()}">
                                        <c:choose>
                                            <c:when test="${z.zone == demande.zones}">
                                                <option value="${z.zone}" selected="selected">${z.zone}
                                                    - ${z.libelle}</option>
                                            </c:when>
                                            <c:otherwise>
                                                <option value="${z.zone}">${z.zone}-${z.libelle}</option>
                                            </c:otherwise>
                                        </c:choose>
                                    </c:forEach>
                                </select>
                                <div id="zones_error"></div>
                            </c:when>
                            <c:otherwise>
                                <p>${demande.zones}</p>
                            </c:otherwise>
                        </c:choose>
                    </fieldset>
                </c:if>
                <fieldset id="comment" style="width:80%;">
                    <legend>Commentaires</legend>
                    <span>Merci de ne pas entrer de données à caractère
							personnel dans le champ "commentaires"<br />
                          Si votre demande concerne un titre dont le pays de publication N'est PAS la France, veuillez écrire votre commentaire en ANGLAIS
                    </span><br/>
                    <c:forEach var="c" items="${ demande.commentairesList }">
                        <c:if
                                test="${ c.visibleISSN || !connexion.user.isISSNOrCIEPS() }">
                            <p class="commentaire">
									<span title="${ c.visibleISSN ? " Visible parISSN/CIEPS" : "InvisiblepourISSN/CIEPS" }">
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
                    <input type="hidden" value="${ lastCommentaire.idCommentaire }"
                           name="last_id_commentaire" id="last_id_commentaire"/>
                    <textarea rows="5" name="commentaire" id="commentaire"
                              placeholder="Tapez votre commentaire ici">${ lastCommentaire.libCommentaire }</textarea>
                    <c:if test="${ !connexion.user.isISSNOrCIEPS() }">
                        <br/>
                        <label for="visible_issn"> <c:choose>
                            <c:when test="${ lastCommentaire.visibleISSN }">
                                <input type="checkbox" checked="checked" name="visible_issn"
                                       id="visible_issn"/>
                                <span>Visible par ISSN/CIEPS</span>
                            </c:when>
                            <c:otherwise>
                                <input type="checkbox" name="visible_issn" id="visible_issn"/>
                                <span>Visible par ISSN/CIEPS</span>
                            </c:otherwise>
                        </c:choose>

                        </label>
                    </c:if>
                    <div id="commentaire_error"></div>
                </fieldset>
                <c:if
                        test="${ connexion.user.roles.idRole == Constant.ROLE_ABES || connexion.user.roles.idRole == Constant.ROLE_ISSN}">
                    <fieldset id="tag" style="width:17%;">
                        <legend>Définir le tag</legend>
                        <select name="taggue">
                            <option value="">Aucun tag</option>
                            <c:forEach var="t" items="${Constant.getAllDefaultTaggues()}">
                                <c:set var="libelleTaggue" value="${demande.taggues.libelleTaggue}"/>
                                <c:choose>
                                    <c:when
                                            test="${libelleTaggue == t.libelleTaggue }">
                                        <option value="${t.libelleTaggue }"
                                                selected="selected">${t.libelleTaggue}</option>
                                    </c:when>
                                    <c:otherwise>
                                        <option value="${t.libelleTaggue }">${t.libelleTaggue}</option>
                                    </c:otherwise>
                                </c:choose>
                            </c:forEach>
                        </select>
                    </fieldset>
                </c:if>
                <p class="separ"/>
                <fieldset>
                    <legend>Justificatif(s)</legend>
                    <span>Attention : La taille des  justificatifs ne doit pas dépasser 20Mo</span><br/>
                    <c:choose>
                        <c:when
                                test="${(((connexion.user.roles.idRole == Constant.ROLE_ISSN || connexion.user.roles.idRole == Constant.ROLE_CIEPS) ) && empty demande.allpiecesjustificativeslist)}">
                            <span>Aucun justificatif</span>
                        </c:when>
                        <c:otherwise>
                            <div id="filestodelete"></div>
                            <div id="filesup">
                                <c:forEach var="p"
                                           items="${demande.allpiecesjustificativeslist}"
                                           varStatus="status">
                                    <div id="fileup${p.idPiece}">
                                        <p>
                                            Envoyé par ${p.cbsUsers.shortName} (
                                                ${p.cbsUsers.roles.libRole} ) : <a
                                                href="${p.urlfichier}">${p.publicname}</a>
                                            <c:if
                                                    test="${((p.cbsUsers.userNum eq connexion.user.userNum) || (connexion.user.roles.idRole eq Constant.ROLE_RESPONSABLE_CR)) && p.demande.idDemande eq demande.idDemande}">
                                                <button fileid="${p.idPiece}"
                                                        class="formbutton supprimerfichierup "
                                                        title="Supprimer le fichier" type="button">
                                                    <span>Supprimer</span>
                                                </button>
                                            </c:if>
                                        </p>
                                    </div>
                                </c:forEach>
                            </div>
                        </c:otherwise>
                    </c:choose>
                    <c:if
                            test="${connexion.user.roles.idRole != Constant.ROLE_ISSN && connexion.user.roles.idRole != Constant.ROLE_CIEPS}">
                        <div id="files">
                            <div id="divfile0" class="file inputline">
                                <input type="file" id="file0" name="file0" multiple/>
                                <button class="formbutton supprimerfichier "
                                        title="Supprimer le fichier" type="button" delete="#divfile0">
                                    <span>Supprimer</span>
                                </button>
                            </div>
                        </div>
                        <div id="files_error"></div>
                        <button id="ajoutfichier" class="formbutton ajoutjustificatif "
                                title="Ajouter justificatif" type="button">
                            <span>Ajouter justificatif</span>
                        </button>
                    </c:if>
                </fieldset>
                <p id="myDIV" style="font-size: 1.5em"></p>
            </div>
            <c:choose>
                <c:when
                        test="${demande.typesDemandes.idTypeDemande eq Constant.TYPE_DEMANDE_CREATION}">
                    <c:choose>
                        <c:when
                                test="${demande.etatsDemandes.idEtatDemande eq Constant.ETAT_EN_ATTENTE_PRECISION_CATALOGUEUR}">
                            <button action="enregistrer" class="formbutton actiondemande "
                                    title="Enregistrer" type="button" id="enregistrementBouton">
                                <span>Enregistrer</span>
                            </button>
                            <button action="valider" class="formbutton actiondemande green"
                                    confirm="Attention vous ne pourrez plus modifier la demande."
                                    title="Valider" type="button" id="validationBouton" style="background: #7cb81c">
                                <span>Valider</span>
                            </button>
                            <button class="formbutton annuler"
                                    title="Revenir au tableau de bord" type="button" id="tableauDeBordBouton">
                                <span>Revenir au tableau de bord</span>
                            </button>
                        </c:when>
                        <c:when
                                test="${(demande.etatsDemandes.idEtatDemande eq Constant.ETAT_EN_ATTENTE_PRECISION_RESPONSABLE_CR) && (demande.cbsUsers.roles.idRole eq Constant.ROLE_CATALOGUEUR)}">
                            <button action="enregistrer" class="formbutton actiondemande "
                                    title="Enregistrer" type="button" id="enregistrementBouton">
                                <span>Enregistrer</span>
                            </button>
                            <button action="precisions"
                                    class="formbutton actiondemande green"
                                    confirm="Attention vous ne pourrez plus modifier la demande."
                                    title="Demander des précisions" type="button" id="precisionsBouton"
                                    style="background: #7cb81c">
                                <span>Demander des précisions</span>
                            </button>
                            <button action="valider" class="formbutton actiondemande green"
                                    confirm="Voulez vous vraiment transmettre cette demande et ses justificatifs au centre ISSN concerné ? Attention vous ne pourrez plus modifier la demande."
                                    title="Valider" type="button" id="validationBouton" style="background: #7cb81c">
                                <span>Valider</span>
                            </button>
                            <button action="rejeter" class="formbutton actiondemande green"
                                    title="Rejeter" type="button" id="rejeterBouton" style="background: #7cb81c">
                                <span>Rejeter</span>
                            </button>
                            <button class="formbutton annuler "
                                    title="Revenir au tableau de bord" type="button" id="tableauDeBordBouton">
                                <span>Revenir au tableau de bord</span>
                            </button>
                        </c:when>
                        <c:when
                                test="${(demande.etatsDemandes.idEtatDemande eq Constant.ETAT_EN_ATTENTE_PRECISION_RESPONSABLE_CR) && (demande.cbsUsers.roles.idRole eq Constant.ROLE_RESPONSABLE_CR)}">
                            <button action="enregistrer" class="formbutton actiondemande"
                                    title="Enregistrer" type="button" id="enregistrementBouton">
                                <span>Enregistrer</span>
                            </button>
                            <button action="valider" class="formbutton actiondemande green"
                                    confirm="Voulez vous vraiment transmettre cette demande et ses justificatifs au centre ISSN concerné ? Attention vous ne pourrez plus modifier la demande."
                                    title="Valider" type="button" id="validationBouton" style="background: #7cb81c">
                                <span>Valider</span>
                            </button>
                            <button class="formbutton annuler "
                                    title="Revenir au tableau de bord" type="button" id="tableauDeBordBouton">
                                <span>Revenir au tableau de bord</span>
                            </button>
                        </c:when>
                        <c:when
                                test="${demande.etatsDemandes.idEtatDemande eq Constant.ETAT_EN_ATTENTE_VALIDATION_CATALOGUEUR}">
                            <button action="enregistrer" class="formbutton actiondemande"
                                    title="Enregistrer" type="button" id="enregistrementBouton">
                                <span>Enregistrer</span>
                            </button>
                            <button action="valider" class="formbutton actiondemande green"
                                    confirm="Attention vous ne pourrez plus modifier la demande."
                                    title="Valider" type="button" id="validationBouton" style="background: #7cb81c">
                                <span>Valider</span>
                            </button>
                            <button class="formbutton annuler "
                                    title="Revenir au tableau de bord" type="button" id="tableauDeBordBouton">
                                <span>Revenir au tableau de bord</span>
                            </button>
                        </c:when>
                        <c:when
                                test="${demande.etatsDemandes.idEtatDemande eq Constant.ETAT_VALIDEE_PAR_CATALOGUEUR || demande.etatsDemandes.idEtatDemande eq Constant.ETAT_PRECISION_PAR_CATALOGUEUR}">
                            <button action="enregistrer" class="formbutton actiondemande "
                                    title="Enregistrer" type="button" id="enregistrementBouton">
                                <span>Enregistrer</span>
                            </button>
                            <button action="precisions"
                                    class="formbutton actiondemande green"
                                    confirm="Attention vous ne pourrez plus modifier la demande."
                                    title="Demander des précisions" type="button" id="precisionsBouton"
                                    style="background: #7cb81c">
                                <span>Demander des précisions</span>
                            </button>
                            <button action="creernotice"
                                    class="formbutton actiondemande green" title="Valider"
                                    type="button" id="validationBouton" style="background: #7cb81c">
                                <span>Valider</span>
                            </button>
                            <button action="rejeter" class="formbutton actiondemande green"
                                    title="Rejeter" type="button" id="rejeterBouton" style="background: #7cb81c">
                                <span>Rejeter</span>
                            </button>
                            <button class="formbutton annuler "
                                    title="Revenir au tableau de bord" type="button" id="tableauDeBordBouton">
                                <span>Revenir au tableau de bord</span>
                            </button>
                        </c:when>
                        <c:when
                                test="${(demande.etatsDemandes.idEtatDemande eq Constant.ETAT_EN_ATTENTE_VALIDATION_RESPONSABLE_CR) && (demande.cbsUsers.roles.idRole eq Constant.ROLE_RESPONSABLE_CR)}">
                            <button action="enregistrer" class="formbutton actiondemande "
                                    title="Enregistrer" type="button" id="enregistrementBouton">
                                <span>Enregistrer</span>
                            </button>
                            <button action="creernotice"
                                    class="formbutton actiondemande green" title="Valider"
                                    type="button" id="validationBouton" style="background: #7cb81c">
                                <span>Valider</span>
                            </button>
                            <button class="formbutton annuler "
                                    title="Revenir au tableau de bord" type="button" id="tableauDeBordBouton">
                                <span>Revenir au tableau de bord</span>
                            </button>
                        </c:when>
                        <c:when
                                test="${(demande.etatsDemandes.idEtatDemande eq Constant.ETAT_EN_ATTENTE_VALIDATION_RESPONSABLE_CR) && (demande.cbsUsers.roles.idRole eq Constant.ROLE_CATALOGUEUR)}">
                            <button action="enregistrer" class="formbutton actiondemande "
                                    title="Enregistrer" type="button" id="enregistrementBouton">
                                <span>Enregistrer</span>
                            </button>
                            <button action="precisions"
                                    class="formbutton actiondemande green"
                                    confirm="Attention vous ne pourrez plus modifier la demande."
                                    title="Demander des précisions" type="button" id="precisionsBouton"
                                    style="background: #7cb81c">
                                <span>Demander des précisions</span>
                            </button>
                            <button action="creernotice"
                                    class="formbutton actiondemande green" title="Valider"
                                    type="button" id="validationBouton" style="background: #7cb81c">
                                <span>Valider</span>
                            </button>
                            <button action="rejeter" class="formbutton actiondemande green"
                                    title="Rejeter" type="button" id="rejeterBouton" style="background: #7cb81c">
                                <span>Rejeter</span>
                            </button>
                            <button class="formbutton annuler "
                                    title="Revenir au tableau de bord" type="button" id="tableauDeBordBouton">
                                <span>Revenir au tableau de bord</span>
                            </button>
                        </c:when>
                    </c:choose>
                </c:when>
                <c:otherwise>
                    <c:choose>
                        <c:when
                                test="${demande.etatsDemandes.idEtatDemande eq Constant.ETAT_EN_ATTENTE_PRECISION_CATALOGUEUR}">
                            <button action="enregistrer" class="formbutton actiondemande "
                                    title="Enregistrer" type="button" id="enregistrementBouton">
                                <span>Enregistrer</span>
                            </button>
                            <button action="valider" class="formbutton actiondemande green"
                                    confirm="Attention vous ne pourrez plus modifier la demande."
                                    title="Valider" type="button" id="validationBouton" style="background: #7cb81c">
                                <span>Valider</span>
                            </button>
                            <button class="formbutton annuler"
                                    title="Revenir au tableau de bord" type="button" id="tableauDeBordBouton">
                                <span>Revenir au tableau de bord</span>
                            </button>
                        </c:when>
                        <c:when
                                test="${(demande.etatsDemandes.idEtatDemande eq Constant.ETAT_EN_ATTENTE_PRECISION_RESPONSABLE_CR) && (demande.cbsUsers.roles.idRole eq Constant.ROLE_CATALOGUEUR)}">
                            <button action="enregistrer" class="formbutton actiondemande "
                                    title="Enregistrer" type="button" id="enregistrementBouton">
                                <span>Enregistrer</span>
                            </button>
                            <button action="precisions"
                                    class="formbutton actiondemande green"
                                    confirm="Attention vous ne pourrez plus modifier la demande."
                                    title="Demander des précisions" type="button" id="precisionsBouton"
                                    style="background: #7cb81c">
                                <span>Demander des précisions</span>
                            </button>
                            <button action="valider" class="formbutton actiondemande green"
                                    confirm="Voulez vous vraiment transmettre cette demande et ses justificatifs au centre ISSN concerné ? Attention vous ne pourrez plus modifier la demande."
                                    title="Valider" type="button" id="validationBouton" style="background: #7cb81c">
                                <span>Valider</span>
                            </button>
                            <button action="rejeter" class="formbutton actiondemande green"
                                    title="Rejeter" type="button" id="rejeterBouton" style="background: #7cb81c">
                                <span>Rejeter</span>
                            </button>
                            <button class="formbutton annuler "
                                    title="Revenir au tableau de bord" type="button" id="tableauDeBordBouton">
                                <span>Revenir au tableau de bord</span>
                            </button>
                        </c:when>
                        <c:when
                                test="${(demande.etatsDemandes.idEtatDemande eq Constant.ETAT_EN_ATTENTE_PRECISION_RESPONSABLE_CR) && (demande.cbsUsers.roles.idRole eq Constant.ROLE_RESPONSABLE_CR)}">
                            <button action="enregistrer" class="formbutton actiondemande "
                                    title="Enregistrer" type="button" id="enregistrementBouton">
                                <span>Enregistrer</span>
                            </button>
                            <button action="valider" class="formbutton actiondemande green"
                                    confirm="Voulez vous vraiment transmettre cette demande et ses justificatifs au centre ISSN concerné ? Attention vous ne pourrez plus modifier la demande."
                                    title="Valider" type="button" id="validationBouton" style="background: #7cb81c">
                                <span>Valider</span>
                            </button>
                            <button class="formbutton annuler "
                                    title="Revenir au tableau de bord" type="button" id="tableauDeBordBouton">
                                <span>Revenir au tableau de bord</span>
                            </button>
                        </c:when>
                        <c:when
                                test="${demande.etatsDemandes.idEtatDemande eq Constant.ETAT_EN_ATTENTE_VALIDATION_CATALOGUEUR}">
                            <button action="enregistrer" class="formbutton actiondemande"
                                    title="Enregistrer" type="button" id="enregistrementBouton">
                                <span>Enregistrer</span>
                            </button>
                            <button action="valider" class="formbutton actiondemande green"
                                    confirm="Attention vous ne pourrez plus modifier la demande."
                                    title="Valider" type="button" id="validationBouton" style="background: #7cb81c">
                                <span>Valider</span>
                            </button>
                            <button class="formbutton annuler "
                                    title="Revenir au tableau de bord" type="button" id="tableauDeBordBouton">
                                <span>Revenir au tableau de bord</span>
                            </button>
                        </c:when>
                        <c:when
                                test="${demande.etatsDemandes.idEtatDemande eq Constant.ETAT_VALIDEE_PAR_CATALOGUEUR || demande.etatsDemandes.idEtatDemande eq Constant.ETAT_PRECISION_PAR_CATALOGUEUR}">
                            <button action="enregistrer" class="formbutton actiondemande "
                                    title="Enregistrer" type="button" id="enregistrementBouton">
                                <span>Enregistrer</span>
                            </button>
                            <button action="precisions"
                                    class="formbutton actiondemande green"
                                    confirm="Attention vous ne pourrez plus modifier la demande."
                                    title="Demander des précisions" type="button" id="precisionsBouton"
                                    style="background: #7cb81c">
                                <span>Demander des précisions</span>
                            </button>
                            <button action="valider" class="formbutton actiondemande green"
                                    confirm="Voulez vous vraiment transmettre cette demande et ses justificatifs au centre ISSN concerné ?"
                                    title="Valider" type="button" id="validationBouton" style="background: #7cb81c">
                                <span>Valider</span>
                            </button>
                            <button action="rejeter" class="formbutton actiondemande green"
                                    title="Rejeter" type="button" id="rejeterBouton" style="background: #7cb81c">
                                <span>Rejeter</span>
                            </button>
                            <button class="formbutton annuler "
                                    title="Revenir au tableau de bord" type="button" id="tableauDeBordBouton">
                                <span>Revenir au tableau de bord</span>
                            </button>
                        </c:when>
                        <c:when
                                test="${(demande.etatsDemandes.idEtatDemande eq Constant.ETAT_EN_ATTENTE_VALIDATION_RESPONSABLE_CR) && (demande.cbsUsers.roles.idRole eq Constant.ROLE_RESPONSABLE_CR)}">
                            <button action="enregistrer" class="formbutton actiondemande "
                                    title="Enregistrer" type="button" id="enregistrementBouton">
                                <span>Enregistrer</span>
                            </button>
                            <button action="valider" class="formbutton actiondemande green"
                                    confirm="Voulez vous vraiment transmettre cette demande et ses justificatifs au centre ISSN concerné ?"
                                    title="Valider" type="button" id="validationBouton" style="background: #7cb81c">
                                <span>Valider</span>
                            </button>
                            <button class="formbutton annuler "
                                    title="Revenir au tableau de bord" type="button" id="tableauDeBordBouton">
                                <span>Revenir au tableau de bord</span>
                            </button>
                        </c:when>
                        <c:when
                                test="${(demande.etatsDemandes.idEtatDemande eq Constant.ETAT_EN_ATTENTE_VALIDATION_RESPONSABLE_CR) && (demande.cbsUsers.roles.idRole eq Constant.ROLE_CATALOGUEUR)}">
                            <button action="enregistrer" class="formbutton actiondemande "
                                    title="Enregistrer" type="button" id="enregistrementBouton">
                                <span>Enregistrer</span>
                            </button>
                            <button action="precisions"
                                    class="formbutton actiondemande green"
                                    confirm="Attention vous ne pourrez plus modifier la demande."
                                    title="Demander des précisions" type="button" id="precisionsBouton"
                                    style="background: #7cb81c">
                                <span>Demander des précisions</span>
                            </button>
                            <button action="valider" class="formbutton actiondemande green"
                                    confirm="Voulez vous vraiment transmettre cette demande et ses justificatifs au centre ISSN concerné?"
                                    title="Valider" type="button" id="validationBouton" style="background: #7cb81c">
                                <span>Valider</span>
                            </button>
                            <button action="rejeter" class="formbutton actiondemande green"
                                    title="Rejeter" type="button" id="rejeterBouton" style="background: #7cb81c">
                                <span>Rejeter</span>
                            </button>
                            <button class="formbutton annuler "
                                    title="Revenir au tableau de bord" type="button" id="tableauDeBordBouton">
                                <span>Revenir au tableau de bord</span>
                            </button>
                        </c:when>
                        <c:when
                                test="${(demande.etatsDemandes.idEtatDemande eq Constant.ETAT_VALIDEE_PAR_RESPONSABLE_CR) || (demande.etatsDemandes.idEtatDemande eq Constant.ETAT_PRECISION_PAR_RESPONSABLE_CR)}">
                            <button action="enregistrer" class="formbutton actiondemande "
                                    title="Enregistrer" type="button" id="enregistrementBouton">
                                <span>Enregistrer</span>
                            </button>
                            <button action="precisions"
                                    class="formbutton actiondemande green"
                                    confirm="Attention vous ne pourrez plus modifier la demande."
                                    title="Demander des précisions" type="button" id="precisionsBouton"
                                    style="background: #7cb81c">
                                <span>Demander des précisions</span>
                            </button>
                            <button action="accepter" class="formbutton actiondemande green"
                                    title="Accepter" type="button" id="validationBouton" style="background: #7cb81c">
                                <span>Accepter</span>
                            </button>
                            <button action="refuser" class="formbutton actiondemande green"
                                    title="Refuser" type="button" id="rejeterBouton" style="background: #7cb81c">
                                <span>Refuser</span>
                            </button>
                            <button class="formbutton annuler "
                                    title="Revenir au tableau de bord" type="button" id="tableauDeBordBouton">
                                <span>Revenir au tableau de bord</span>
                            </button>
                        </c:when>
                        <c:when
                                test="${(demande.etatsDemandes.idEtatDemande eq Constant.ETAT_VALIDEE_PAR_RESPONSABLE_CR_VERS_INTERNATIONAL) || (demande.etatsDemandes.idEtatDemande eq Constant.ETAT_VERS_INTERNATIONAL) || (demande.etatsDemandes.idEtatDemande eq Constant.ETAT_PRECISION_PAR_RESPONSABLE_CR)}">
                            <button action="enregistrer" class="formbutton actiondemande "
                                    title="Enregistrer" type="button" id="enregistrementBouton">
                                <span>Enregistrer</span>
                            </button>
                            <button action="precisions"
                                    class="formbutton actiondemande green"
                                    confirm="Attention vous ne pourrez plus modifier la demande."
                                    title="Demander des précisions" type="button" id="precisionsBouton"
                                    style="background: #7cb81c">
                                <span>Demander des précisions</span>
                            </button>
                            <button action="accepter" class="formbutton actiondemande green"
                                    title="Accepter" type="button" id="validationBouton" style="background: #7cb81c">
                                <span>Accepter</span>
                            </button>
                            <button action="refuser" class="formbutton actiondemande green"
                                    title="Refuser" type="button" id="rejeterBouton" style="background: #7cb81c">
                                <span>Refuser</span>
                            </button>
                            <button class="formbutton annuler "
                                    title="Revenir au tableau de bord" type="button" id="tableauDeBordBouton">
                                <span>Revenir au tableau de bord</span>
                            </button>
                        </c:when>
                        <c:otherwise>
                            <button class="formbutton annuler "
                                    title="Revenir au tableau de bord" type="button">
                                <span>Revenir au tableau de bord</span>
                            </button>
                        </c:otherwise>
                    </c:choose>
                </c:otherwise>
            </c:choose>
        </form>
    </section>
</div>

<jsp:include page="templates/footer.jsp"/>
</body>
</html>
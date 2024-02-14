<%@ page import="fr.abes.cidemis.constant.Constant" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<c:choose>
    <c:when test="${type_demande.idTypeDemande == Constant.TYPE_DEMANDE_CORRECTION}">
        <c:choose>
            <c:when test="${fn:length(listedemandes.demandesList) gt 1}">
                <span>Plusieurs demandes de correction portent sur le ppn ${ppn}.</span>
                <p>
                    <span>Avant de créer une nouvelle demande de correction, assurez vous de vérifier si elle porte sur une autre zone/sous-zone de la notice.</span>
                <p>
                <table id="liste_choix_demande">
                    <thead>
                        <tr>
                            <th>Date de la demande</th>
                            <th>Numéro de demande</th>
                            <th>Zone</th>
                            <th></th>
                        </tr>
                    </thead>
                    <tfoot>
                        <tr>
                            <th>Date de la demande</th>
                            <th>Numéro de demande</th>
                            <th>Zone</th>
                            <th></th>
                        </tr>
                    </tfoot>
                    <tbody>
                        <c:forEach var="demande" items="${listedemandes.demandesList}" varStatus="loop">
                        <tr class="${loop.index % 2 == 0 ? 'even' : 'odd'}">
                            <td>${demande.dateDemandeFormatee}</td>
                            <td>${demande.idDemande}</td>
                            <td>${demande.zones}</td>
                            <td><a target="_BLANK" href="afficher-demande?id=${demande.idDemande}">Consulter</a></td>
                        </tr>
                        </c:forEach>
                    </tbody>
                </table>
                <br/>
                <form id="choix-action" name="choix-action" action="creation-demande" method="POST">
                    <input type="hidden" name="ppn" value="${ppn}"/>
                    <input type="hidden" name="type_demande" value="${type_demande.idTypeDemande}"/>
                    <input type="hidden" name="action" value="creer"/>
                    <button class="formbutton Annuler " title="Annuler" type="button"><span>Annuler</span></button>
                    <button class="formbutton Creer " title="Créer" type="button"><span>Créer une nouvelle demande</span></button>
                </form>
            </c:when>
            <c:otherwise>
                <form id="choix-action" name="choix-action" action="" method="POST">
                    <input type="hidden" name="ppn" value="${ppn}"/>
                    <input type="hidden" name="action" value="creer"/>
                    <input type="hidden" name="type_demande" value="${type_demande.idTypeDemande}"/>
                    <div id="choix1">
                        <p>
                            <span>Une demande de correction existe déjà pour le ppn ${ppn}, que voulez-vous faire ?</span>
                        </p>
                        <p>
                            <span>Avant de créer une nouvelle demande de correction, assurez vous de vérifier si elle porte sur une autre zone/sous-zone de la notice.</span>
                        <p>
                        <c:forEach var="demande" items="${listedemandes.demandesList}" varStatus="loop">
                            <span class="label">Date demande : </span><span class="valeur">${demande.dateDemandeFormatee}</span><br/>
                            <span class="label">Numéro demande : </span><span class="valeur">${demande.idDemande}</span><br/>
                            <span class="label">Zones : </span><span class="valeur">${demande.zones}</span><br/>
                            <c:set var="id_demande" value="${demande.idDemande}"/>
                         </c:forEach>  
                        <br/>
                        <button class="formbutton Annuler " title="Annuler" type="button"><span>Annuler</span></button>
                        <button class="formbutton Consulter " demande="${id_demande}" title="Consulter" type="button"><span>Consulter</span></button>
                        <button class="formbutton Creer " title="Creer" type="button"><span>Créer une nouvelle demande</span></button>
                    </div>
                </form>
            </c:otherwise>
        </c:choose>
    </c:when>
    <c:otherwise>
        <form id="choix-action" name="choix-action" action="" method="POST">
            <input type="hidden" name="ppn" value="${ppn}"/>
            <div id="choix">
                <p>
                    <span>Une demande de numérotation existe déjà pour le ppn ${ppn}, que voulez-vous faire ?</span>
                </p>
                <c:forEach var="demande" items="${listedemandes.demandesList}" varStatus="loop">
                    <span class="label">Date demande : </span><span class="valeur">${demande.dateDemandeFormatee}</span><br/>
                    <span class="label">Numéro demande : </span><span class="valeur">${demande.idDemande}</span><br/>
                    <c:set var="id_demande" value="${demande.idDemande}"/>
                </c:forEach>
                <button class="formbutton Annuler " title="Annuler" type="button"><span>Annuler</span></button>
                <button class="formbutton Consulter " demande="${id_demande}" title="Consulter" type="button"><span>Consulter</span></button>
            </div>
        </form>
    </c:otherwise>
</c:choose>
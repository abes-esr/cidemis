<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="fr.abes.cidemis.constant.Constant" %>

<form class="AJAXForm" popupid="popup" id="choix_notice_form" name="choix_notice" action="creation-demande" method="POST">
    <input type="hidden" name="ppn" value="${notice.ppn}"/>
    <input type="hidden" name="type_demande" value="${type_demande}"/>
    <input type="hidden" name="search" value="${search}"/>
    <input type="hidden" name="action" value="verifier"/>
    <div id="choix_notice">
        <div class="notice">
            <span class="label">Ppn : </span><span class="valeur">${notice.ppn}</span><br/>
            <span class="label">Titre : </span><span class="valeur">${notice.titre}</span><br/>
            <span class="label">Pays de publication : </span><span class="valeur">${notice.pays}</span><br/>
            <span class="label">Date de publication : </span><span class="valeur">${notice.datePublication}</span><br/>
            <span class="label">Type de ressource continue : </span><span class="valeur">${notice.typeRessource}</span><br/>
            <span class="label">Type de document : </span><span class="valeur">${notice.typeDocumentLibelle}</span>
        </div>
        
        <c:choose>
            <c:when test="${type_demande == Constant.TYPE_DEMANDE_CORRECTION && fn:length(zones_manquantes) gt 0}">
                <br/>
                <span>Cette notice ne peut pas faire l’objet d’une demande de correction.<br/>Les zones suivantes sont obligatoires:</span>
                <ul>
                <c:forEach var="zone" items="${zones_manquantes}">
                    <li>${zone}</li>
                </c:forEach>
                </ul>
                <span>Merci de faire une demande d'import ISSN sur le guichet <a target="_BLANK" href="https://stp.abes.fr/node/3?origine=sudocpro" >AbesStp</a></span>
                <button class="formbutton closepopup" title="Non" type="button"><span>OK</span></button>
            </c:when>
            <c:when test="${type_demande == Constant.TYPE_DEMANDE_NUMEROTATION && (fn:length(zones_manquantes) gt 0 || fn:length(zones_presentes) gt 0)}">
                <br/>
                <span>Cette notice ne peut pas faire l’objet d’une demande de numérotation.</span>
                <br/>
                <c:if test="${fn:length(zones_manquantes) gt 0}">
                    <span>Les zones suivantes sont obligatoires</span>
                    <ul>
                    <c:forEach var="zone" items="${zones_manquantes}">
                        <li>${zone}</li>
                    </c:forEach>
                    </ul>
                    <span>Merci de faire une demande d'import ISSN sur le guichet <a target="_BLANK" href="https://stp.abes.fr/node/3?origine=sudocpro" >AbesStp</a></span>
                </c:if>
                <c:if test="${fn:length(zones_presentes) gt 0}">
                    <span>Les zones suivantes ne doivent pas être présentes dans la notice</span>
                    <ul>
                    <c:forEach var="zone" items="${zones_presentes}">
                        <li>${zone}</li>
                    </c:forEach>
                    </ul>
                    <span>Merci de faire une demande sur le guichet <a target="_BLANK" href="https://stp.abes.fr/node/3?origine=sudocpro" >AbesStp</a></span>
                </c:if>
                <button class="formbutton closepopup" title="Non" type="button"><span>OK</span></button>
            </c:when>
            <c:otherwise>
                <span>Cette notice correspond-elle à votre recherche ?</span>
                <button class="formbutton oui tbutton tbackground-color-green" title="Oui" type="button"><span>Oui</span></button>
                <button class="formbutton non tbutton tbackground-color-green" title="Non" type="button"><span>Non</span></button>
            </c:otherwise>
         </c:choose>
        
    </div>
</form>

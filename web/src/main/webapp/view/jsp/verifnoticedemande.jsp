<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>

<h4>Impossible de valider cette demande</h4>
<br/>
<div id="popupverifnoticedemande">
    <span>Il n'est pas possible de transférer cette demande à ISSN.</span>
    <br/>
    <c:if test="${error_code eq 'ERROR_TYPE_DOC'}">
        <span>La notice de cette demande n'est pas une notice de publication en série.</span>        
    </c:if>
    <c:if test="${error_code eq 'ERROR_DEMANDE_INEXISTANTE'}">
        <span>La demande n'existe plus.</span>        
    </c:if>
    <c:if test="${fn:length(zones_manquantes) gt 0}">
        <span>Les zones suivantes sont obligatoires</span>
        <ul>
        <c:forEach var="zone" items="${zones_manquantes}">
                <li>${zone}</li>
        </c:forEach>
        </ul>
        <span>Merci de faire une demande d'import ISSN sur le guichet <a target="_BLANK" href="https://stp.abes.fr/node/3?origine=sudocpro" >AbesStp</a></span>
        <br/><br/>
    </c:if>
    <c:if test="${fn:length(zones_presentes) gt 0}">
        <span>Les zones suivantes ne doivent pas être présentes dans la notice</span>
        <ul>
        <c:forEach var="zone" items="${zones_presentes}">
                <li>${zone}</li>
        </c:forEach>
        </ul>
        <span>Merci de faire une demande sur le guichet <a target="_BLANK" href="https://stp.abes.fr/node/3?origine=sudocpro" >AbesStp</a></span>
        <br/><br/>
    </c:if>
    <button class="formbutton closepopup" title="Non" type="button"><span>OK</span></button>
</div>
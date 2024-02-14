<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>

<c:if test="${error_code eq 'ERROR_SUPPRESSION'}">
    <h4>Impossible de supprimer cette demande, la notice n'est pas conforme</h4>
    <br/>
</c:if>
<c:if test="${error_code eq 'ERROR_CREATION'}">
    <h4>Impossible de valider cette demande, la notice n'est pas conforme</h4>
    <br/>
</c:if>
<c:if test="${error_code eq 'ERROR_WSMAIL'}">
    <h4>Demande enregistrée mais courriel non envoyé</h4>
</c:if>
<c:if test="${error_code eq 'ERROR_WSMAIL_INDISPONIBLE'}">
    <h4>Demande enregistrée mais courriel non envoyé</h4>
</c:if>

<div id="popuperreurnotice">
    <c:if test="${error_code eq 'ERROR_SUPPRESSION'}">
        <span>Erreur de suppression de la demande</span>
        <br />
        <span>${texte_erreur}</span>
    </c:if>
    <c:if test="${error_code eq 'ERROR_CREATION'}">
        <span>Erreur de création de la demande</span>
        <br />
        <span>${texte_erreur}</span>
    </c:if>
    <c:if test="${error_code eq 'ERROR_WSMAIL'}">
        <span>Votre demande a été enregistrée mais aucun courriel n’a pu être envoyé au créateur de la demande suite à un problème technique non lié à l’application Cidemis.
        Merci éventuellement de vérifier l’adresse courriel du créateur de la demande.</span>
        <br />
        <br />
        <a class="login_link" href="${link_redirection}">Cliquer ici pour revenir sur le tableau de bord</a>
    </c:if>
    <c:if test="${error_code eq 'ERROR_WSMAIL_INDISPONIBLE'}">
        <c:if test="${code_erreur_premier_chiffre} eq '4'">
            <span>Problème technique ou applicatif client : votre courriel n'a pas pu être envoyé</span>
        </c:if>
        <c:if test="${code_erreur_premier_chiffre} eq '5'">
            <span>Problème technique serveur : votre courriel n'a pas pu être envoyé</span>
        </c:if>
        <br />
        <span>Code technique de l'erreur : ${code_erreur_http}</span>
        <br />
        <a class="login_link" href="https://fr.wikipedia.org/wiki/Liste_des_codes_HTTP">Liste des codes HTTP et signification</a>
        <br />
        <a class="login_link" href="${link_redirection}">Cliquer ici pour revenir sur le tableau de bord</a>
    </c:if>
<br/>
</div>
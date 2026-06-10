<%@ page import="fr.abes.cidemis.constant.Constant" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.functions" prefix="fn" %>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<h4>Historique des commentaires de la demande numéro ${demande.idDemande} :</h4>
<br/>
<div id="popuppiecejointe">
    <c:forEach var="c" items="${commentaires}">
        <c:if test="${ c.visibleISSN || !connexion.user.isISSNOrCIEPS() }">
            <p class="commentaire">
			<span title="${ c.visibleISSN ? "Visible par ISSN/CIEPS" : "Invisible pour ISSN/CIEPS" }">
				<i class="fa fa-eye${ c.visibleISSN ? "" : "-slash" }"></i>
			</span>

                <c:choose>
                    <c:when test="${connexion.user.roles.idRole == Constant.ROLE_ISSN || connexion.user.roles.idRole == Constant.ROLE_CIEPS}">
                        Le ${c.dateCommentaireFormatee} par le ${c.cbsUsers.roles.libRole} :<br/>
                    </c:when>
                    <c:otherwise>
                        Le ${c.dateCommentaireFormatee} par ${c.cbsUsers.shortName} :<br/>
                    </c:otherwise>
                </c:choose>
                <i>${c.libCommentaireHTML}</i>
            </p>
        </c:if>
    </c:forEach>
</div>

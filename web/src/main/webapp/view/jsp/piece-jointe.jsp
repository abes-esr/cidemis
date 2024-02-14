<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<h4>Liste des pièces justificatives de la demande numéro ${demande.idDemande} :</h4>
<br/>
<div id="popuppiecejointe">
    <c:forEach var="p" items="${piecesJustificatives}" varStatus="status">
        <div id="fileup${p.idPiece}">
            <p>
                Envoyé par ${p.cbsUsers.shortName} ( ${p.cbsUsers.roles.libRole} ) : <a
                    href="${p.urlfichier}">${p.publicname}</a>
            </p>
        </div>
    </c:forEach>
</div>
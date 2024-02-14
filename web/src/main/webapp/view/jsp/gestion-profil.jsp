<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="fr.abes.cidemis.constant.Constant" %>

<!DOCTYPE html>
<html lang="fr">
    <head>
        <title>Cidemis - Création de demande</title>
        <jsp:include page="templates/meta.jsp" />
    </head>
    <body>
        <div class="page-container row-fluid sidebar-closed">
            <jsp:include page="templates/banner.jsp" />
            <section id="container">
                <span class="titre_form">Gestion des profils</span>
                <div id="profil_panel">
                    <div class="profil" profil="${Constant.PROFIL_PAS_PROFIL}">
                        <h4>Pas de profil</h4>
                        <ul>
                            <c:forEach var="user" items="${users}">
                                <c:if test="${user.idProfil eq Constant.PROFIL_PAS_PROFIL}">
                                    <li user="${user.userNum}" draggable="true">${user.shortName}</li>
                                </c:if>
                            </c:forEach>
                        </ul>
                    </div>
                    <div class="profil" profil="${Constant.PROFIL_COLLECTION}">
                        <h4>Profil collection</h4>
                        <ul>
                            <c:forEach var="user" items="${users}">
                                <c:if test="${user.idProfil eq Constant.PROFIL_COLLECTION}">
                                    <li user="${user.userNum}" draggable="true">${user.shortName}</li>
                                </c:if>
                            </c:forEach>
                        </ul>
                    </div>
                    <div class="profil" profil="${Constant.PROFIL_PERIODIQUE_ELECTRONIQUE}">
                        <h4>Profil PER électronique</h4>
                        <ul>
                            <c:forEach var="user" items="${users}">
                                <c:if test="${user.idProfil eq Constant.PROFIL_PERIODIQUE_ELECTRONIQUE}">
                                    <li user="${user.userNum}" draggable="true">${user.shortName}</li>
                                </c:if>
                            </c:forEach>
                        </ul>
                    </div>
                    <div class="profil" profil="${Constant.PROFIL_PERIODIQUE_IMPRIME}">
                        <h4>Profil PER imprimés</h4>
                        <ul>
                            <c:forEach var="user" items="${users}">
                                <c:if test="${user.idProfil eq Constant.PROFIL_PERIODIQUE_IMPRIME}">
                                    <li user="${user.userNum}" draggable="true">${user.shortName}</li>
                                </c:if>
                            </c:forEach>
                        </ul>
                    </div>
                    <div class="profil" profil="${Constant.PROFIL_PERIODIQUE_IMPRIME_VIVANT}">
                        <h4>Profil PER vivants</h4>
                        <ul>
                            <c:forEach var="user" items="${users}">
                                <c:if test="${user.idProfil eq Constant.PROFIL_PERIODIQUE_IMPRIME_VIVANT}">
                                    <li user="${user.userNum}" draggable="true">${user.shortName}</li>
                                </c:if>
                            </c:forEach>
                        </ul>
                    </div>
                    <div class="profil" profil="${Constant.PROFIL_PERIODIQUE_IMPRIME_MORT}">
                        <h4>Profil PER morts</h4>
                        <ul>
                            <c:forEach var="user" items="${users}">
                                <c:if test="${user.idProfil eq Constant.PROFIL_PERIODIQUE_IMPRIME_MORT}">
                                    <li user="${user.userNum}" draggable="true">${user.shortName}</li>
                                </c:if>
                            </c:forEach>
                        </ul>
                    </div>
                    <div class="profil" profil="${Constant.PROFIL_INDETERMINE}">
                        <h4>Profil Indéterminé</h4>
                        <ul>
                            <c:forEach var="user" items="${users}">
                                <c:if test="${user.idProfil eq Constant.PROFIL_INDETERMINE}">
                                    <li user="${user.userNum}" draggable="true">${user.shortName}</li>
                                </c:if>
                            </c:forEach>
                        </ul>
                    </div>
                    <div class="clear"></div>
                </div>
            </section>
        </div>
        <jsp:include page="templates/footer.jsp" />
    </body>
</html>

<%@ page import="fr.abes.cidemis.constant.Constant" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>

<!DOCTYPE html>
<html lang="fr">
	<head>
		<title>Cidemis - Tableau de bord</title>
		<jsp:include page="templates/meta.jsp" />
	</head>
	<body>
        <div class="page-container row-fluid sidebar-closed">
            <jsp:include page="templates/banner.jsp" />

            <section id="demandes_list" class="page-content">
                <input type="hidden" id="positions" value="<c:out value="${positions}"/>" />
                <input type="hidden" id="login" value="<c:out value="${login}"/>" />
                <input type="hidden" id="show_demandes_done" value="<c:out value="${checkbox_show_demandes_done}"/>" />
                <input type="hidden" id="show_demandes_archived" value="<c:out value="${checkbox_show_demandes_archived}"/>" />
                <div id="loading"><img src="<c:url value="/resources/img/loading.gif"/>" alt="Chargement, veuillez patienter"/></div>
                <table id="liste_demande" width="100%">
                    <thead>
                        <tr>    
                            <c:forEach var="optioncolonne" items="${optionscolonnes_liste_ordered}" >
                                <c:set var="values" value="${fn:split(optioncolonne.value, ',')}" />
                                <th type_input="${optioncolonne.typeInput}" col_name="${optioncolonne.libOption}" visible="${values[1] eq 'visible'?'true':'false'}" col_width="${values[2]}">${optioncolonne.name}<div></div></th>
                            </c:forEach>
                            <th col_name="col_action" visible="true">Action<div></div></th>
                            <th col_name="col_id_etat" visible="false">Id Etat</th>
                        </tr>
                        <tr>
                            <c:forEach var="optioncolonne" items="${optionscolonnes_liste_ordered}" >
                                <td>
                                    <c:choose>
                                        <c:when test="${optioncolonne.typeInput eq Constant.COLTYPE_INPUT}">
                                            <input name="filter${optioncolonne.libOption}" col_name="${optioncolonne.libOption}" type="text" placeholder="${optioncolonne.name}"/>
                                        </c:when>
                                        <c:when test="${optioncolonne.typeInput eq Constant.COLTYPE_SELECT}">
                                            <select name="filter${optioncolonne.libOption}" col_name="${optioncolonne.libOption}">
                                            <option value="">Tous</option>
                                        <c:forEach var="FILTRE" items="${constant.getListeFiltre()[optioncolonne.libOption]}" >
                                            <option value="${FILTRE}">${FILTRE}</option>
                                        </c:forEach>
                                            </select>
                                        </c:when>
                                    </c:choose>
                                </td>
                            </c:forEach>
                            <td></td>
                            <td></td>
                        </tr>
                    </thead>
		
                    <tbody></tbody>
                </table>

            </section>
        </div>
		
        <jsp:include page="templates/footer.jsp" />
    </body>
</html>
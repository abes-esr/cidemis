<%@ page import="fr.abes.cidemis.localisation.LocalProvider" %>
<%@ page import="fr.abes.cidemis.constant.Constant" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>

<!DOCTYPE html>
<html lang="fr">
    <head>
        <title>Cidemis - Upload Result</title>
        <jsp:include page="templates/meta.jsp" />
    </head>
    <body>
        <div class="page-container row-fluid sidebar-closed">
            <jsp:include page="templates/banner.jsp" />
            <section id='container'>
                <fieldset>
                    <legend>Upload result</legend>
                    <label>Number of lines submited : </label><span>${lines_count}</span><br />
                    <label>Number of valid lines : </label><span>${lines_valid}</span><br />
                    <label>Number of empty lines : </label><span>${lines_empty}</span><br />
                    <label>Results : </label><br />
                    <ul>
                        <c:forEach var="result" items="${results}">
                            <li>${result}</li>
                        </c:forEach>
                    </ul>
                </fieldset>
            </section>
        </div>
    
        <jsp:include page="templates/footer.jsp" />
    </body>
</html>
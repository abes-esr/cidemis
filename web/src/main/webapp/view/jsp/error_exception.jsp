<%@ page import="fr.abes.cidemis.localisation.LocalProvider" %>
<% LocalProvider lang = new LocalProvider(request.getLocale()); %>

<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.functions" prefix="fn" %>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>

<html>
<head>
    <meta content="NOINDEX, NOFOLLOW" name="ROBOTS">
    <title><%=lang.getMsg("LABEL_HTML_TITLE")%></title>
    <jsp:include page="templates/meta.jsp" />
</head>
<body>
<div class="error_page">
    <h2>Erreur : ${tier_exception}</h2>
    <span class="error_msg">Table ${table_exception}</span><br />
    <span>${message_exception}</span>

    <br /><br />

    <a class="login_link" href="${link_redirection}">Cliquer ici pour revenir sur la page précédente</a>
</div>

<jsp:include page="templates/footer.jsp" />
</body>
</html>

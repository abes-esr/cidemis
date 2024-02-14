<%@ page import="fr.abes.cidemis.localisation.LocalProvider" %>
<% LocalProvider lang = new LocalProvider(request.getLocale()); %>

<html>
    <head>
        <meta content="NOINDEX, NOFOLLOW" name="ROBOTS">
        <title><%=lang.getMsg("LABEL_HTML_TITLE")%></title>
        <jsp:include page="templates/meta.jsp" />
    </head>
    <body>
        <div class="error_page">
            <h2><%=lang.getMsg("LABEL_ERREUR_LOGIN_1")%></h2>
            <span class="error_msg"><%=lang.getMsg("LABEL_ERREUR_LOGIN_2")%></span><br />
            <span><%=lang.getMsg("LABEL_ERREUR_LOGIN_4")%></span>
            
            <br /><br />
            
            <a class="login_link" href="/"><%=lang.getMsg("LABEL_ERREUR_LOGIN_3")%></a>
        </div>
    
        <jsp:include page="templates/footer.jsp" />
    </body>
</html>

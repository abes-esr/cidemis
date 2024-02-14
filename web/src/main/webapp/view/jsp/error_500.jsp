<%@ page import="fr.abes.cidemis.constant.Constant" %>
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
            <h2>Erreur interne</h2>
            <span class="error_msg">Cidemis n'a pas pu fournir la page (erreur interne).</span><br />
            <span>
            	Merci de contacter nos techniciens via :
                <br />
                <a href="https://stp.abes.fr/node/3?origine=cidemis">ABESStp</a>
                <br />
                en précisant :
                <br />
                - le PPN concerné
                <br />
                - votre identifiant utilisateur
                <br />
                - l'heure exacte du problème
                <br />
                <br />
                et, dans la mesure du possible, les étapes successives,
                <br />
                qui ont abouti à ce dysfonctionnement,
                <br />
                ainsi que des copies d'écran illustrant le bug.
                <br />
            	Merci.
            </span>
            
            <br /><br />
            
            <a class="login_link" href="<%=Constant.PROPERTIES_URL_SITE%>"><%=lang.getMsg("LABEL_ERREUR_LOGIN_3")%></a>
        </div>
    </body>
</html>

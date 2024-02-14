<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="fr.abes.cidemis.localisation.LocalProvider" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<% LocalProvider lang = new LocalProvider(request.getLocale()); %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
    <head>
        <meta content="NOINDEX, NOFOLLOW" name="ROBOTS">
        <title><%=lang.getMsg("LABEL_HTML_TITLE")%></title>
        <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css"
              integrity="sha384-JcKb8q3iqJ61gNV9KGb8thSsNjpSL0n8PARn9HuZOnIxN0hoP+VmmDGMN5t9UJ0Z"
              crossorigin="anonymous">
        <jsp:include page="templates/meta.jsp" />
    </head>
    <body>

    <!-- Barre de navigation -->
    <nav class="navbar" style="background-color: #166dae">
        <div><a href='/logout'><img src="<c:url value="/resources/img/cidemis-vectoriel-blanc.png" />" alt="logo de cidemis" width="180" height="45" style="margin-left: 1em"></a></div>
        <div style='position: absolute;right: 150px;top: 10px;width: 200px;'><a href="https://stp.abes.fr/node/3?origine=cidemis" title='Assistance'><img src="<c:url value="/resources/img/item_assistance.svg" />" alt="guichet d'assistance d'assitance" width="40"></a> <span style='color: white'>Assistance</span></div>
        <div style='position: absolute;right: 0px;top: 10px;width: 200px;'><a href="https://documentation.abes.fr/aidecidemis/index.html" title='Documentation'><img src="<c:url value="/resources/img/item_documentation.svg" />" alt="documentation" width="40"></a> <span style='color: white'>Documentation</span></div>
    </nav>

    <!-- Formulaire de connexion -->
    <form method="POST" action="login" style="padding-left: 34%; padding-right: 34%; padding-top: 2%">
        <nav aria-label="breadcrumb">
            <ol class="breadcrumb" style="background-color: #166dae">
                <li class="breadcrumb-item active" aria-current="page" style="color: white; font-weight: bolder">Connexion</li>
            </ol>
        </nav>

        <div class="form-group">
            <label for="j_username"><%=lang.getMsg("LABEL_LOGIN")%></label>
            <input name="j_username" type="text" class="form-control" id="j_username">
        </div>
        <div class="form-group" style='margin-bottom: 2em'>
            <label for="j_password"><%=lang.getMsg("LABEL_MOT_DE_PASSE")%></label>
            <input name="j_password" type="password" class="form-control" id="j_password">
        </div>
        <div>
            <button type="submit" class="btn btn-primary" style="display: block; background-color: #166dae; color: white;font-weight: bolder; float: right"><%=lang.getMsg("LABEL_CONNECTER")%></button>
        </div>
    </form>

    <!-- Pied de page -->
    <nav class="navbar fixed-bottom" style="margin-bottom: 2em; background-color: #166dae">
        <div style="margin-left: 1em"></div>
        <div style="margin-right: 1em">
            <span style='color: white; margin-right: 1em'>Partenaires </span>
            <a href="http://www.bnf.fr/fr/professionnels/s_informer_obtenir_issn.html" style="padding-right: 1em"><img alt="BNF" src="<c:url value="/resources/img/bnfLogo.svg" />" width="80" height="30"></a>
            <a href="http://www.issn.org/fr/"><img alt="ISSN" src="<c:url value="/resources/img/issnLogo.jpg" />" width="80" height="30"></a>
            <a href="https://abes.fr/"><img alt="ABES" src="<c:url value="/resources/img/logo_abes_white.png" />" width="80" height="50" style="margin-left: 1em"></a>
        </div>
    </nav>
    <nav class="navbar fixed-bottom" style="margin-bottom: 0em; text-align: center; background-color: #252c61">
        <div style="margin-left: 1em"></div>
        <div style="margin-right: 1em">
            <a target="_blank" href="https://abes.fr/pages-donnees-personnelles/cidemis.html" style="padding-right: 1em; color:white">Données personnelles</a>
            <a target="_blank" href="https://abes.fr/pages-cgu/conditions-generales-utilisation-sites-abes.html" style="padding-right: 1em; color:white">CGU</a>
            <a target="_blank" href="https://abes.fr/pages-mentions-legales/cidemis.html" style="padding-right: 1em; color:white">Mentions Légales</a>
            <a target="_blank" href="https://abes.fr/pages-accessibilite/cidemis.html" style="padding-right: 1em; color:white">Accessibilité numérique</a>
        </div>
    </nav>
    </body>
</html>
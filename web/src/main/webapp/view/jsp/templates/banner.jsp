<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="fr.abes.cidemis.web.MyDispatcher" %>
<%@ page import="fr.abes.cidemis.constant.Constant" %>

<header id="header">
    <section id="banner">
        <div class="header-pusher"></div>
        <a href='/liste-demandes'><h1>Cidémis</h1></a>
        <div id="utilisateur">
            <button id="deconnecter" class="deconnecter"
                title="Déconnecter" type="button">
                <span>Déconnecter</span>
            </button>
            <span id="bienvenue">Bienvenue ${connexion.user.shortName}</span> <span class="role">${connexion.user.roles.libRole}</span>
            <span class="email">${connexion.user.userEmail} - <a href="https://stp.abes.fr/node/3?origine=cidemis" style='color: white' target="_blank">Assistance</a></span>
            <div class="right">
                <div id="modifemail">
                    <input validateOnEnter="modifemail" type="text" mail="${connexion.user.userEmail}" value="${connexion.user.userEmail}" />
                    <button class="formbutton" id="modifemailbutton" title="Modifier l'adresse e-mail" type="button">
                        <span>Modifier l'adresse e-mail</span>
                    </button>
                    <br /> <span class="errortxt"></span>
                </div>
                <button id="emailbutton"
                    title="Modifier votre adresse e-mail" type="button">
                    <span>Modifier votre adresse e-mail</span>
                </button>
            </div>
        </div>
        <div class="clear"></div>
    </section>
</header>

<div class="header-filler"></div>
<c:if test="${connexion.user.userEmail == null}">
    <section id="covermail">
        <div id="mailpopup">
            <span>Merci de renseigner votre adresse e-mail</span> <input
                validateOnEnter="createmailbutton" type="text"
                value="${connexion.user.userEmail}" /><br /> <span
                class="errortxt"></span><br />
            <button
                class="formbutton createmail tbutton tbackground-color-green"
                title="Enregistrer l'adresse e-mail" type="button">
                <span>Enregistrer l'adresse e-mail</span>
            </button>
            <button
                class="formbutton deconnecter tbackground-color-green"
                title="Déconnecter" type="button">
                <span>Déconnecter</span>
            </button>
        </div>
        <div class="shadow"></div>
    </section>
</c:if>

<section id="popupsection">
    <div class="popup" id="popup">
        <div class="popupdiv">
            <span class="popup-close" title="Fermer cette pop-up">
                <span>fermer</span> <img src="<c:url value="/resources/img/popup-close.png" />" />
            </span>
            <div class="popupcontent"></div>
        </div>
        <div class="shadow"></div>
    </div>
</section>

<div class="page-sidebar nav-collapse collapse">
    <ul>
        <li>
            <div title="Tableau de bord" class="sidebar-toggler"></div>
        </li>
        <!-- Création d'une nouvelle demande -->
        <c:if test="${connexion.user.roles.idRole eq Constant.ROLE_CATALOGUEUR || connexion.user.roles.idRole eq Constant.ROLE_RESPONSABLE_CR || connexion.user.roles.idRole eq Constant.ROLE_ABES}">
            <li class="has-sub demande">
                <a href="javascript:;">
                    <i class="icon-document"></i> <span class="title">Nouvelle demande</span> <span class="arrow"></span>
                </a>
                <ul class="sub">
                    <form class="AJAXForm sidebar-search" popupid="popup" id="chercher-notice" name="chercher-notice" action="chercher-notice" method="POST">
                        <select name="type_demande" id="type_demande_select">
                            <c:choose>
                                <c:when
                                    test="${!(connexion.user.isdeploye)}">
                                    <option value="23">correction</option>
<%--                                    <option value="24">création de notice</option>--%>
                                </c:when>
                                <c:otherwise>
                                    <option value="22">numérotation</option>
                                    <option value="23">correction</option>
                                </c:otherwise>
                            </c:choose>
                        </select>
                        <label for="searched_value">ppn</label> <input id="search_type" name="search" type="hidden" />
                        <input type="text" name="searched_value" autocomplete="off" id="searched_value" />
                        <button id="search" type="button" class="tbutton tbackground-color-green">Rechercher</button>
                        <span class="info"></span>
                    </form>
                    <p class="small">
                        Date de la dernière synchronisation avec le SUDOC: <span id="lastDateSynchronisezSudoc">Calcul en cours</span>
                    </p>
                </ul>
            </li>
        </c:if>
        
        <!-- Liste des profils -->
        <c:if test="${fn:contains(pageContext.request.requestURL, MyDispatcher.LISTE_DEMANDES)}">
            <c:if test="${connexion.user.roles.idRole == Constant.ROLE_ISSN}">
                <li class='has-sub'>
                    <a href="javascript:;">
                        <i class="icon-profils"></i> 
                        <span class="title">Liste des profils</span>
                        <span class="arrow"></span>
                    </a>
                    <ul class="sub">
                        <li><a class="${(profilid eq Constant.PROFIL_TOUTES_DEMANDES)?"profil_selected":""}" href="?profil=${Constant.PROFIL_TOUTES_DEMANDES_TXT}">Aucun profil sélectionné</a></li>
                        <c:if test="${connexion.user.userKey == Constant.ADMIN_ISSN}">
                            <li><a class="${(profilid eq Constant.PROFIL_COLLECTION)?"profil_selected":""}" href="?profil=${Constant.PROFIL_COLLECTION_TXT}">Profil collection</a></li>
                            <li><a class="${(profilid eq Constant.PROFIL_PERIODIQUE_ELECTRONIQUE)?"profil_selected":""}" href="?profil=${Constant.PROFIL_PERIODIQUE_ELECTRONIQUE_TXT}">Profil PER électronique</a></li>
                            <li><a class="${(profilid eq Constant.PROFIL_PERIODIQUE_IMPRIME)?"profil_selected":""}" href="?profil=${Constant.PROFIL_PERIODIQUE_IMPRIME_TXT}">Profil inventaire</a></li>
                            <li><a class="${(profilid eq Constant.PROFIL_PERIODIQUE_IMPRIME_VIVANT)?"profil_selected":""}" href="?profil=${Constant.PROFIL_PERIODIQUE_IMPRIME_VIVANT_TXT}">Profil PER vivants</a></li>
                            <li><a class="${(profilid eq Constant.PROFIL_PERIODIQUE_IMPRIME_MORT)?"profil_selected":""}" href="?profil=${Constant.PROFIL_PERIODIQUE_IMPRIME_MORT_TXT}">Profil PER morts</a></li>
                            <li><a class="${(profilid eq Constant.PROFIL_INDETERMINE)?"profil_selected":""}" href="?profil=${Constant.PROFIL_INDETERMINE_TXT}">Profil indéterminé</a></li>
                        </c:if>
                        <c:choose>
                            <c:when test="${(connexion.user.idProfil eq Constant.PROFIL_COLLECTION)}">
                                <li><a class="${(profilid eq Constant.PROFIL_COLLECTION)?"profil_selected":""}" href="?profil=${Constant.PROFIL_COLLECTION_TXT}">Profil collection</a></li>
                            </c:when>
                            <c:when test="${(connexion.user.idProfil eq Constant.PROFIL_PERIODIQUE_ELECTRONIQUE)}">
                                <li><a class="${(profilid eq Constant.PROFIL_PERIODIQUE_ELECTRONIQUE)?"profil_selected":""}" href="?profil=${Constant.PROFIL_PERIODIQUE_ELECTRONIQUE_TXT}">Profil PER électronique</a></li>
                            </c:when>
                            <c:when test="${(connexion.user.idProfil eq Constant.PROFIL_PERIODIQUE_IMPRIME)}">
                                <li><a class="${(profilid eq Constant.PROFIL_PERIODIQUE_IMPRIME)?"profil_selected":""}" href="?profil=${Constant.PROFIL_PERIODIQUE_IMPRIME_TXT}">Profil inventaire</a></li>
                            </c:when>
                            <c:when test="${(connexion.user.idProfil eq Constant.PROFIL_PERIODIQUE_IMPRIME_VIVANT)}">
                                <li><a class="${(profilid eq Constant.PROFIL_PERIODIQUE_IMPRIME_VIVANT)?"profil_selected":""}" href="?profil=${Constant.PROFIL_PERIODIQUE_IMPRIME_VIVANT_TXT}">Profil PER vivants</a></li>
                            </c:when>
                            <c:when test="${(connexion.user.idProfil eq Constant.PROFIL_PERIODIQUE_IMPRIME_MORT)}">
                                <li><a class="${(profilid eq Constant.PROFIL_PERIODIQUE_IMPRIME_MORT)?"profil_selected":""}" href="?profil=${Constant.PROFIL_PERIODIQUE_IMPRIME_MORT_TXT}">Profil PER morts</a></li>
                            </c:when>
                            <c:when test="${(connexion.user.idProfil eq Constant.PROFIL_INDETERMINE)}">
                                <li><a class="${(profilid eq Constant.PROFIL_INDETERMINE)?"profil_selected":""}" href="?profil=${Constant.PROFIL_INDETERMINE_TXT}">Profil indéterminé</a></li>
                            </c:when>
                        </c:choose>
                    </ul>
                </li>
            </c:if>
        </c:if>
        
        <!-- Gérer les colonnes -->
        <c:if test="${fn:contains(pageContext.request.requestURL, MyDispatcher.LISTE_DEMANDES)}">
            <li class="has-sub colonnes">
                <a href="javascript:;">
                    <i class="icon-colonnes"></i>
                    <span class="title">Afficher/Masquer les colonnes</span>
                    <span class="arrow"></span>
                </a>
                <ul class="sub" id="columns_control">
                    <c:forEach var="option" items="${optionscolonnes_liste_ordered}">
                       <c:set var="optioncolonne" value="${fn:split(option.value, ',')}" />
                        <li>
                                <a href="#" column_name="${option.libOption}" class="toggle-vis ${optioncolonne[1] eq 'visible'?'selected':''}">${option.name}</a>
                            </li>
                    </c:forEach>
                </ul>
            </li>
        </c:if>
        
        <!-- Liste des exports -->
        <c:if test="${fn:contains(pageContext.request.requestURL, MyDispatcher.LISTE_DEMANDES)}">
            <li class='has-sub'>
                <a href="javascript:;">
                <i class="icon-exporter"></i> 
                <span class="title">Exporter</span>
                <span class="arrow"></span>
                </a>
                <ul class="sub">
                    <li><a title="Télécharger la liste au format csv" href="#" class="export_menu" id="exportall">Exporter toutes les demandes</a></li>
                    <li><a title="Télécharger la liste au format csv" href="#" class="export_menu" id="exportfiltered">Exporter les demandes filtrées</a></li>
                    <li><a title="Télécharger la liste au format csv" href="#" class="export_menu" id="exportselected">Exporter les demandes sélectionnées</a></li>
                </ul>
            </li>
        </c:if>
        
        <!-- Gestion des profils -->
        <c:if test="${connexion.user.userKey == Constant.ADMIN_ISSN}">
            <li>
                <a href="/gestion-profil"> <i class="icon-gestionprofil"></i> <span class="title">Gestion des profils</span></a>
            </li>
        </c:if>
        
        <!-- Upload CSV CIEPS -->
        <c:if test="${fn:contains(pageContext.request.requestURL, MyDispatcher.LISTE_DEMANDES)}">
            <c:if test="${connexion.user.roles.idRole == Constant.ROLE_CIEPS}">
                <li class='has-sub upload-cieps'>
                    <a href="#">
                        <i class="icon-upload-cieps"></i> <span class="title">Upload CSV requests file</span>
                    </a>
                    <ul class="sub">
                        <form name="upload-cieps" id="upload-cieps" action="upload-cieps" accept-charset="UTF-8" enctype="multipart/form-data" method="POST">
                            <input type="file" id="fichier_cieps" name="fichier_cieps" />
                        </form>
                    </ul>
                </li>
            </c:if>
        </c:if>
        
        <!-- Lien de l'assistance -->
        <li>
            <a target="_BLANK" href="https://documentation.abes.fr/aidecidemis/accueil/index.html">
                <i class="icon-assistance"></i> <span class="title">Aide en ligne</span>
            </a>
        </li>
    </ul>
</div>
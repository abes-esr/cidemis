/**
 * Documentation de l'api
 * @link https://datatables.net/reference/
 *
 * Manipulation du dom
 * @link dossier documentation du projet -> fichier ManipulationDuDom
 */

var table = null;
var tab_couleur = [];
var checkbox_already_checked = false;

/**
 * Fonction de construction générale
 */
function initDataTables() {
   // Si l'utilisateur vient de se connecter alors on vide la mémoire du
   // localstorage
   if ($('#login').val() === 'true') localStorage.removeItem('DataTables_liste_demande_' + window.location.pathname);

   if ($('#demandes_list').length) {
      var colonnes = [];
      var col;
      $('#liste_demande thead th').each(function () {
         col = {};
         if ($(this).attr('col_name') === 'col_action') {
            col.orderable = false;
            col.name = 'col_action';
            col.data = $(this).attr('col_name');
         } else {
            if ($(this).attr('col_name') === 'col_date' || $(this).attr('col_name') === 'col_date_modif') col.type = 'french-date';
            if ($(this).attr('col_name') === 'col_publication_date_debut') col.type = 'publication-date';
            if ($(this).attr('col_name') === 'col_titre') col.type = 'titre';
            if ($(this).attr('col_name') === 'col_taggue') {
               col.type = 'taggue';
               col.name = 'col_taggue';
            }
            col.search = $(this).attr('type_input');
            col.name = $(this).attr('col_name');
            col.visible = $(this).attr('visible') === 'true';
            col.data = $(this).attr('col_name');
            col.width = $(this).attr('col_width');
         }
         colonnes.push(col);
      });
      var positions = eval($('#positions').val());
      positions.push(17);
      positions.push(18);

      //Si l'utilisateur n'a pas encore coché de cases, on place les valeurs des cookies pour les cases à cocher par defaut à false
      if (getCookieValue('jsSessionCookieKeepDisplayedDemandesDone') === undefined) {
         setCookie('jsSessionCookieKeepDisplayedDemandesDone', false, 1);
      }
      if (getCookieValue('jsSessionCookieKeepDisplayedDemandesArchived') === undefined) {
         setCookie('jsSessionCookieKeepDisplayedDemandesArchived', false, 1);
      }

      var appelAjax = 'liste-demandes-ajax?achieved=' + getCookieValue('jsSessionCookieKeepDisplayedDemandesDone') + '&archived=' + getCookieValue('jsSessionCookieKeepDisplayedDemandesArchived');

      var initValDataTable = {
         ajax: appelAjax,
         deferRender: true,
         orderCellsTop: true,
         order: [[0, 'desc']],
         pageLength: 25,
         lengthMenu: [
            [10, 25, 50, -1],
            ['Afficher 10 demandes', 'Afficher 25 demandes', 'Afficher 50 demandes', 'Toutes les demandes'],
         ],
         stateSave: true,
         pagingType: 'full_numbers',
         dom: 'R<"filtres"<"toolbar">lf><"clear"><pi>rt<"clear">',
         columns: colonnes,
         columnDefs: [
            {
               name: 'col_taggue',
               targets: 12,
               render: function (data) {
                  var chaine = '';
                  if (data.id_demande != null) {
                     chaine = "<ul class='readOnlyTags'>";
                     for (var i = 0; i < data.taggues.length; i++) {
                        chaine += '<li>' + data.taggues[i].libelle + '</li>';
                        tab_couleur[data.taggues[i].libelle] = data.taggues[i].couleur;
                     }
                     chaine += '</ul>';
                  }
                  return chaine;
               },
            },
            {
               name: 'col_action',
               targets: 17,
               render: function (data) {
                  var button = "<a class='afficherdemande' href='afficher-demande?id=" + data.id_demande + "' title='Modifier la demande'></a>";

                  if (data.commentaires_nb !== 0) button += "<a class='voircommentaires' href='#' demandenum='" + data.id_demande + "' title='Voir les commentaires'></a>";
                  if (data.pieces_justificatives_nb !== 0) button += "<a class='voirpiecejointe' href='#' demandenum='" + data.id_demande + "' title='Voir les pièces jointes'></a>";
                  if (data.can_delete === true) button += "<a class='supprimerdemande' href='#' demandenum='" + data.id_demande + "' title='Supprimer la demande'></a>";
                  if (data.can_archive === true) button += "<a class='archiverdemande' href='#' demandenum='" + data.id_demande + "' title='Archiver la demande'></a>";
                  return button;
               },
            },
         ],
         fnInitComplete: function () {
            $('#loading').remove();
            $('#liste_demande').show();
            // mise en forme des tags après chargement de la datatable
            styletags();
         },
         search: {
            caseInsensitive: true,
         },
         language: {
            processing: 'Chargement...',
            lengthMenu: '_MENU_',
            zeroRecords: 'Aucun résultat trouvé',
            emptyTable: 'Aucune demande à afficher',
            info: 'Affiche _START_ à _END_ sur un total de _TOTAL_ demandes',
            infoEmpty: 'Affiche 0 demandes sur un total de 0 demandes',
            infoFiltered: '(filtrées sur un total de _MAX_ demandes)',
            infoPostFix: '',
            search: '',
            searchPlaceholder: 'Chercher',
            url: '',
            paginate: {
               first: 'Première',
               previous: 'Précédente',
               next: 'Suivante',
               last: 'Dernière',
            },
            fnInfoCallback: null,
         },
         oColReorder: {
            fnReorderCallback: function () {
               updateColonnesOptions();
            },
            aiOrder: positions,
         },
      };

      var oControls = $('#liste_demande').find('input,select');

      initValDataTable.fnStateSaveParams = function (oSettings, sValue) {
         oControls.each(function () {
            if ($(this).attr('name')) sValue[$(this).attr('name')] = $(this).val().replace('"', '"');
         });

         return sValue;
      };

      initValDataTable.fnStateLoadParams = function (oSettings, oData) {
         oControls.each(function (index_control) {
            var oControl = $(this);
            $.each(oData.columns, function (index, value) {
               if (index === index_control) {
                  if (oControl.is('select')) oControl.find('option[value="' + value.search.search + '"]').prop('selected', true);
                  else oControl.val(value.search.search);
               }
            });
         });

         return true;
      };

      $.fn.dataTableExt.ofnSearch['titre'] = function (sData) {
         return removeDiacritics(sData);
      };

      $.fn.dataTableExt.ofnSearch['taggue'] = function (sData) {
         return removeDiacritics(sData);
      };
      table = $('#liste_demande').DataTable(initValDataTable);
      $('.toolbar').html('<a title="Tout déselectionner" href="#" class="deselect_all"></a>');

      /**
       * Statut TERMINE : Ajout de la checkbox permettant de filtrer les demandes à l'état terminé
       * @type filtres du dom
       */
      var checkedShowDemandesDone = $('#show_demandes_done').val() === 'true' || getCookieValue('jsSessionCookieKeepDisplayedDemandesDone') === 'true' ? 'checked="checked" ' : '';
      $('.filtres').append('<span style="display: block;margin-bottom: 2em;"></span><span id="divaffichertermine" style="margin-left: 12px">' + '<input ' + checkedShowDemandesDone + ' type="checkbox" name="affichertermine" id="affichertermine" style="position: relative; vertical-align: middle"/>' + '<label style="position: relative;display: inline-block;vertical-align: middle;" for="affichertermine" id="loaderTermine">Demandes terminées</label>' + '</span>');

      /**
       * Au cochage de la case, change le statut de la variable
       */
      $('#divaffichertermine').change(function () {
         checkbox_already_checked = true; //uniquement utilisé pour la variation de couleur au clic du bouton le temps du chargement
         if (getCookieValue('jsSessionCookieKeepDisplayedDemandesDone') === 'true') {
            setCookie('jsSessionCookieKeepDisplayedDemandesDone', false, 1);
         } else if (getCookieValue('jsSessionCookieKeepDisplayedDemandesDone') === 'false') {
            setCookie('jsSessionCookieKeepDisplayedDemandesDone', true, 1);
         }
         afficherdemandeajax();
      });

      /**
       * Au cochage de la case, agit sur le dom en remplaçant la phrase initiale du bouton
       */
      $('#divaffichertermine').change(function () {
         document.getElementById('loaderTermine').innerHTML = '<span class="blink">Demandes terminées &#128472;</span>';
         document.getElementById('loaderTermine').style.color = 'red';
         //Désactivation de la possibilité de recocher les cases pendant le chargement
         document.getElementById('affichertermine').disabled = true;
         document.getElementById('afficherarchive').disabled = true;
      });

      filtrertermine();
      $('#liste_demande_filter [type="search"]').addClass('search');

      /**
       * Statut ARCHIVE : Ajout de la checkbox permettant de filtrer les demandes à l'état terminé
       * @type filtres du dom
       */
      var checkedShowDemandesArchived = $('#show_demandes_archived').val() === 'true' || getCookieValue('jsSessionCookieKeepDisplayedDemandesArchived') === 'true' ? 'checked="checked" ' : '';
      $('.filtres').append('<span id="divafficherarchive">' + '<input ' + checkedShowDemandesArchived + ' type="checkbox" name="afficherarchive" id="afficherarchive" style="position: relative; vertical-align: middle" />' + '<label style="position: relative;display: inline-block;vertical-align: middle;" for="afficherarchive" id="loaderArchive">Demandes archivées</label>' + '</span>');

      $('#divafficherarchive').change(function () {
         checkbox_already_checked = true;
         if (getCookieValue('jsSessionCookieKeepDisplayedDemandesArchived') === 'true') {
            setCookie('jsSessionCookieKeepDisplayedDemandesArchived', false, 1);
         } else if (getCookieValue('jsSessionCookieKeepDisplayedDemandesArchived') === 'false') {
            setCookie('jsSessionCookieKeepDisplayedDemandesArchived', true, 1);
         }
         afficherdemandeajax();
      });

      /**
       * Au cochage de la case, agit sur le dom en remplaçant la phrase initiale du bouton
       */
      $('#divafficherarchive').change(function () {
         document.getElementById('loaderArchive').innerHTML = '<span class="blink">Demandes archivées &#128472;</span>';
         document.getElementById('loaderArchive').style.color = 'red';
         //Désactivation de la possibilité de recocher les cases pendant le chargement
         document.getElementById('affichertermine').disabled = true;
         document.getElementById('afficherarchive').disabled = true;
      });

      filtrerarchive();
      $('#liste_demande_filter [type="search"]').addClass('search');
   }

   $('#liste_demande').delegate('input,select', 'keyup change', searchtable);
   function searchtable(event) {
      var regex = false;
      var el = $(event.target);
      var search_value = el.attr('col_name') === 'col_titre' || el.attr('col_name') === 'col_taggue' ? removeDiacritics(el.val()) : el.val();

      if (/%/.test(search_value)) {
         search_value = '^' + search_value.replace(/%/, '(.*)') + '$';
         regex = true;
      }
      table
         .column(el.attr('col_name') + ':name')
         .search(search_value, regex, true)
         .draw();
   }

   function getDate(a) {
      var time = a.split('-');
      return time[2] + time[1] + time[0];
   }

   jQuery.fn.dataTableExt.oSort['titre-asc'] = function (a, b) {
      var x = removeDiacritics(a.toLowerCase());
      var y = removeDiacritics(b.toLowerCase());

      if (x === y) return a < b;
      else return x > y;
   };

   jQuery.fn.dataTableExt.oSort['titre-desc'] = function (a, b) {
      var x = removeDiacritics(a.toLowerCase());
      var y = removeDiacritics(b.toLowerCase());
      if (x === y) return a > b;
      else return x < y;
   };

   jQuery.fn.dataTableExt.oSort['taggue-asc'] = function (a, b) {
      var x = removeDiacritics(a.toLowerCase());
      var y = removeDiacritics(b.toLowerCase());

      if (x === y) return a < b;
      else {
         return x > y;
      }
   };

   jQuery.fn.dataTableExt.oSort['taggue-desc'] = function (a, b) {
      var x = removeDiacritics(a.toLowerCase());
      var y = removeDiacritics(b.toLowerCase());
      if (x === y) return a > b;
      else return x < y;
   };

   jQuery.fn.dataTableExt.oSort['publication-date-asc'] = function (a, b) {
      var x = a.replace(/X/g, '0');
      var y = b.replace(/X/g, '0');
      // Permet de différencier 18XX de 1800
      if (x === y) return a < b;
      else return x > y;
   };

   jQuery.fn.dataTableExt.oSort['publication-date-desc'] = function (a, b) {
      var x = a.replace(/X/g, '0');
      var y = b.replace(/X/g, '0');
      // Permet de différencier 18XX de 1800
      if (x === y) return a > b;
      else return x < y;
   };

   jQuery.fn.dataTableExt.oSort['french-date-asc'] = function (a, b) {
      var x = getDate(a);
      var y = getDate(b);
      var z = x < y ? -1 : x > y ? 1 : 0;
      return z;
   };

   jQuery.fn.dataTableExt.oSort['french-date-desc'] = function (a, b) {
      var x = getDate(a);
      var y = getDate(b);
      var z = x < y ? 1 : x > y ? -1 : 0;
      return z;
   };

   tableAlreadyLoadedOnce = true;
}

/**
 * Fonctions annexes
 */
function reloadTable() {
   if (table != null) table.ajax.reload();
}

function filtrertermine() {
   var regex = '.*';

   if ($('#divaffichertermine').prop('checked')) regex = '^((?!(36|37|38)).)*$';

   table.column('col_id_etat:name').search(regex, true, true).draw();
}

function filtrerarchive() {
   var regex = '.*'; //Ancienne regex "^((?!(39)).)*$" non fonctionnelle

   if ($('#divafficherarchive').prop('checked')) regex = '^((?!(39)).)*$';

   table.column('col_id_etat:name').search(regex, true, true).draw();
}

function ajustcolumnsize() {
   $('#liste_demande thead tr:first th').each(function (i) {
      var width = $(this).outerWidth() - 4;
      $('#liste_demande tbody td:nth-child(' + (i + 1) + ')').css({
         width: width + 'px',
         'min-width': width + 'px',
         'max-width': width + 'px',
      });
   });
}

function styletags() {
   $('.readOnlyTags').tagit({
      readOnly: true,
      afterTagAdded: function (evt, ui) {
         ui.tag.css('background-color', '#' + tab_couleur[ui.tag[0].textContent]);
      },
   });
}
// Récupère l'ordre des colonnes
function updateColonnesOptions() {
   var colonnes = {};
   $.each($('#liste_demande').DataTable().context[0].aoColumns, function (i) {
      colonnes[this.name] = pad(i, 2) + ',' + (this.bVisible ? 'visible' : 'hide') + ',' + (this.bVisible ? $('table thead tr:first-child th[col_name="' + this.name + '"]').width() : '0');
   });

   return updateColonnesOptionsAjax(jQuery.param(colonnes));
}

/**
 * Déclenchement des évènements
 */
$('#liste_demande').on('draw.dt', function () {
   ajustcolumnsize();
   styletags();
});

$('#liste_demande').on('fnReorderCallback', function () {
   console.info('reorder');
   styletags();
});

$('#liste_demande').on('order.dt', function () {
   styletags();
});

$('#liste_demande').delegate('.supprimerdemande', 'click', suppressiondemande);
function suppressiondemande(event) {
   var el = $(event.target);
   var demandenum = el.attr('demandenum');

   if (confirm('Êtes-vous sûr de vouloir supprimer définitivement la demande n°' + demandenum)) {
      supprimerdemandeajax(demandenum);
   }
}

$('#liste_demande').delegate('.archiverdemande', 'click', popuparchive);
function popuparchive(event) {
   var el = $(event.target);
   var demandenum = el.attr('demandenum');

   if (confirm('Êtes-vous sûr de vouloir archiver la demande n°' + demandenum + ' ?')) {
      archiverdemandeajax(demandenum);
   }
}

function archiverdemandeajax(demandenum) {
   $.ajax({
      type: 'POST',
      url: 'ArchivageDemande',
      data: {
         demandenum: demandenum,
      },
      success: function () {
         reloadTable();
      },
      error: function () {},
   });
}

/**
 * Functions utilisées pour les exports
 */
function getAllDemandes() {
   return $('#liste_demande').DataTable().columns('col_demande_num:name').data()[0];
}

function getFilteredDemandes() {
   return $('#liste_demande')
      .DataTable()
      .columns('col_demande_num:name', {
         search: 'applied',
      })
      .data()[0];
}

function getSelectedDemandes() {
   return $.map($('#liste_demande').DataTable().cells('.selected', 'col_demande_num:name').data(), function (value) {
      if (!Array.isArray(value)) return [value];
   });
}

/**
 * Fonctions AJAX
 */
function supprimerdemandeajax(demandenum) {
   $.ajax({
      type: 'POST',
      url: 'SuppressionDemande',
      data: {
         demandenum: demandenum,
      },
      success: function (data) {
         if (data !== '') {
            $('#popup .popupcontent').html(data);
            $('#popup').show();
         } else {
            reloadTable();
         }
      },
      error: function () {},
   });
}

/**
 * Fonctions d'affichage des demandes selon état des cases à cocher
 * Analyse les membres booléens du fichier et renvoie une requête ajax différente selon leur statut
 * Ne s'execute que lors du cochage d'une des deux cases, pas au chargement initial de la page, c'est initdatatable dans ce cas
 */
function afficherdemandeajax() {
   table.ajax.url('liste-demandes-ajax?achieved=' + getCookieValue('jsSessionCookieKeepDisplayedDemandesDone') + '&archived=' + getCookieValue('jsSessionCookieKeepDisplayedDemandesArchived')).load();
}

/**
 * Fonctions permettant de manipuler le dom ou de déclencher des évenements
 * en cours de vie de chargement du tableau -> voir https://datatables.net/reference/event/
 */

/**
Permet de lancer des évenements sur le dom à chaque fois que le tableau a fini d'être chargé
 */
$('#liste_demande').on('xhr.dt', function () {
   if (checkbox_already_checked) {
      document.getElementById('loaderTermine').innerHTML = 'Demandes terminées</span>';
      document.getElementById('loaderTermine').style.color = 'black';
      document.getElementById('affichertermine').disabled = false;
      document.getElementById('afficherarchive').disabled = false;
   }

   if (checkbox_already_checked) {
      document.getElementById('loaderArchive').innerHTML = 'Demandes archivées</span>';
      document.getElementById('loaderArchive').style.color = 'black';
      document.getElementById('affichertermine').disabled = false;
      document.getElementById('afficherarchive').disabled = false;
   }
});

/**
 * Permet de récupérer la valeur des cookies stockés ou en client (js), ou serveur (java) avec la classe Cookie
 * @param cookieName le nom du cookie (en java -> new Cookie(cookieName, value))
 * @returns {string} la valeur du cookie (value)
 */
function getCookieValue(cookieName) {
   const value = `; ${document.cookie}`;
   const parts = value.split(`; ${cookieName}=`);
   if (parts.length === 2) return parts.pop().split(';').shift();
}

/**
 * Permet de modifier la valeur des cookies
 * @param cookieName nom du cookie
 * @param cookieValue valeur du cookie
 * @param expirationInDays expiration en durée
 */
function setCookie(cookieName, cookieValue, expirationInDays) {
   var d = new Date();
   d.setTime(d.getTime() + expirationInDays * 24 * 60 * 60 * 1000);
   var expires = 'expires=' + d.toUTCString();
   document.cookie = cookieName + '=' + cookieValue + ';' + expires + ';path=/';
}

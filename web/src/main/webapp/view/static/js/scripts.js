/**
 * jQuery Coding Standards & Best Practices test 201803140934
 * http://lab.abhinayrathore.com/jquery-standards/
 */

/**********************************************************************

Functions

**********************************************************************/

jQuery.fn.switchClass = function (removeClassName, addClassName) {
   this.removeClass(removeClassName).addClass(addClassName);
   return this;
};

jQuery.fn.toggleClass = function (className) {
   if (this.hasClass(className)) this.removeClass(className);
   else this.addClass(className);
   return this;
};

jQuery.download = function (url, data) {
   if (url && data) {
      data = typeof data === 'string' ? data : jQuery.param(data);
      var inputs = '';

      jQuery.each(data.split('&'), function () {
         var pair = this.split('=');
         inputs += '<input type="hidden" name="' + pair[0] + '" value="' + pair[1] + '" />';
      });

      //send request

      jQuery('<form action="' + url + '" method="post">' + inputs + '</form>')
         .appendTo('body')
         .submit()
         .remove();
   }
};

/**
 * CHECK DIGIT
 * To confirm the check digit, calculate the sum of all eight digits of the ISSN multiplied by its position in the number, counting from
 * the right (if the check digit is X, then add 10 to the sum). The modulus 11 of the sum must be 0.
 * @param issn
 * @returns {Boolean}
 */
function isValidISSN(issn) {
   var valid = true;
   var sum = 0;
   issn = issn.replace('-', '');
   for (var i = 0; i < issn.length; i++) sum += parseInt(issn.charAt(i) === 'X' ? 10 : issn.charAt(i)) * (8 - i);

   var modulus = sum % 11;
   if (modulus !== 0) {
      $('#num_ISSN').addClass('inputerror');
      $('#num_ISSN_error').html("<span class='errortxt'>Numéro ISSN invalide. Caractère de contrôle incorrect.</span>");
      valid = false;
   }

   if (errorISSNAjax($('#num_ISSN').val(), $('#ppn').val())) {
      valid = false;
      $('#num_ISSN').addClass('inputerror');
      $('#num_ISSN_error').html("<span class='errortxt'>Numéro ISSN déjà attribué</span>");
   }

   return valid;
}

function pad(str, max) {
   str = str.toString();
   return str.length < max ? pad('0' + str, max) : str;
}

function testEmail(email) {
   var reg_email = /[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?/;
   return reg_email.test(email);
}

function initValidateOnEnter() {
   $('[validateOnEnter]').each(function () {
      validateOnEnter(this);
   });
}

function validateOnEnter(element) {
   $(element).keyup(function (event) {
      if (event.which === 13) {
         window[$(element).attr('validateOnEnter')]();
      }
   });
}

function initblurTextElement() {
   $('input[blurText],textarea[blurText]').each(function () {
      $(this).val($(this).attr('blurText'));
   });
}

function clearBlurText() {
   $('input[blurText],textarea[blurText]').each(function () {
      if ($(this).attr('blurText') === $(this).val()) $(this).val('');
   });
}

var defaultDiacriticsRemovalap = [
   {base: 'A', letters: '\u0041\u24B6\uFF21\u00C0\u00C1\u00C2\u1EA6\u1EA4\u1EAA\u1EA8\u00C3\u0100\u0102\u1EB0\u1EAE\u1EB4\u1EB2\u0226\u01E0\u00C4\u01DE\u1EA2\u00C5\u01FA\u01CD\u0200\u0202\u1EA0\u1EAC\u1EB6\u1E00\u0104\u023A\u2C6F'},
   {base: 'AA', letters: '\uA732'},
   {base: 'AE', letters: '\u00C6\u01FC\u01E2'},
   {base: 'AO', letters: '\uA734'},
   {base: 'AU', letters: '\uA736'},
   {base: 'AV', letters: '\uA738\uA73A'},
   {base: 'AY', letters: '\uA73C'},
   {base: 'B', letters: '\u0042\u24B7\uFF22\u1E02\u1E04\u1E06\u0243\u0182\u0181'},
   {base: 'C', letters: '\u0043\u24B8\uFF23\u0106\u0108\u010A\u010C\u00C7\u1E08\u0187\u023B\uA73E'},
   {base: 'D', letters: '\u0044\u24B9\uFF24\u1E0A\u010E\u1E0C\u1E10\u1E12\u1E0E\u0110\u018B\u018A\u0189\uA779'},
   {base: 'DZ', letters: '\u01F1\u01C4'},
   {base: 'Dz', letters: '\u01F2\u01C5'},
   {base: 'E', letters: '\u0045\u24BA\uFF25\u00C8\u00C9\u00CA\u1EC0\u1EBE\u1EC4\u1EC2\u1EBC\u0112\u1E14\u1E16\u0114\u0116\u00CB\u1EBA\u011A\u0204\u0206\u1EB8\u1EC6\u0228\u1E1C\u0118\u1E18\u1E1A\u0190\u018E'},
   {base: 'F', letters: '\u0046\u24BB\uFF26\u1E1E\u0191\uA77B'},
   {base: 'G', letters: '\u0047\u24BC\uFF27\u01F4\u011C\u1E20\u011E\u0120\u01E6\u0122\u01E4\u0193\uA7A0\uA77D\uA77E'},
   {base: 'H', letters: '\u0048\u24BD\uFF28\u0124\u1E22\u1E26\u021E\u1E24\u1E28\u1E2A\u0126\u2C67\u2C75\uA78D'},
   {base: 'I', letters: '\u0049\u24BE\uFF29\u00CC\u00CD\u00CE\u0128\u012A\u012C\u0130\u00CF\u1E2E\u1EC8\u01CF\u0208\u020A\u1ECA\u012E\u1E2C\u0197'},
   {base: 'J', letters: '\u004A\u24BF\uFF2A\u0134\u0248'},
   {base: 'K', letters: '\u004B\u24C0\uFF2B\u1E30\u01E8\u1E32\u0136\u1E34\u0198\u2C69\uA740\uA742\uA744\uA7A2'},
   {base: 'L', letters: '\u004C\u24C1\uFF2C\u013F\u0139\u013D\u1E36\u1E38\u013B\u1E3C\u1E3A\u0141\u023D\u2C62\u2C60\uA748\uA746\uA780'},
   {base: 'LJ', letters: '\u01C7'},
   {base: 'Lj', letters: '\u01C8'},
   {base: 'M', letters: '\u004D\u24C2\uFF2D\u1E3E\u1E40\u1E42\u2C6E\u019C'},
   {base: 'N', letters: '\u004E\u24C3\uFF2E\u01F8\u0143\u00D1\u1E44\u0147\u1E46\u0145\u1E4A\u1E48\u0220\u019D\uA790\uA7A4'},
   {base: 'NJ', letters: '\u01CA'},
   {base: 'Nj', letters: '\u01CB'},
   {base: 'O', letters: '\u004F\u24C4\uFF2F\u00D2\u00D3\u00D4\u1ED2\u1ED0\u1ED6\u1ED4\u00D5\u1E4C\u022C\u1E4E\u014C\u1E50\u1E52\u014E\u022E\u0230\u00D6\u022A\u1ECE\u0150\u01D1\u020C\u020E\u01A0\u1EDC\u1EDA\u1EE0\u1EDE\u1EE2\u1ECC\u1ED8\u01EA\u01EC\u00D8\u01FE\u0186\u019F\uA74A\uA74C'},
   {base: 'OI', letters: '\u01A2'},
   {base: 'OO', letters: '\uA74E'},
   {base: 'OU', letters: '\u0222'},
   {base: 'OE', letters: '\u008C\u0152'},
   {base: 'oe', letters: '\u009C\u0153'},
   {base: 'P', letters: '\u0050\u24C5\uFF30\u1E54\u1E56\u01A4\u2C63\uA750\uA752\uA754'},
   {base: 'Q', letters: '\u0051\u24C6\uFF31\uA756\uA758\u024A'},
   {base: 'R', letters: '\u0052\u24C7\uFF32\u0154\u1E58\u0158\u0210\u0212\u1E5A\u1E5C\u0156\u1E5E\u024C\u2C64\uA75A\uA7A6\uA782'},
   {base: 'S', letters: '\u0053\u24C8\uFF33\u1E9E\u015A\u1E64\u015C\u1E60\u0160\u1E66\u1E62\u1E68\u0218\u015E\u2C7E\uA7A8\uA784'},
   {base: 'T', letters: '\u0054\u24C9\uFF34\u1E6A\u0164\u1E6C\u021A\u0162\u1E70\u1E6E\u0166\u01AC\u01AE\u023E\uA786'},
   {base: 'TZ', letters: '\uA728'},
   {base: 'U', letters: '\u0055\u24CA\uFF35\u00D9\u00DA\u00DB\u0168\u1E78\u016A\u1E7A\u016C\u00DC\u01DB\u01D7\u01D5\u01D9\u1EE6\u016E\u0170\u01D3\u0214\u0216\u01AF\u1EEA\u1EE8\u1EEE\u1EEC\u1EF0\u1EE4\u1E72\u0172\u1E76\u1E74\u0244'},
   {base: 'V', letters: '\u0056\u24CB\uFF36\u1E7C\u1E7E\u01B2\uA75E\u0245'},
   {base: 'VY', letters: '\uA760'},
   {base: 'W', letters: '\u0057\u24CC\uFF37\u1E80\u1E82\u0174\u1E86\u1E84\u1E88\u2C72'},
   {base: 'X', letters: '\u0058\u24CD\uFF38\u1E8A\u1E8C'},
   {base: 'Y', letters: '\u0059\u24CE\uFF39\u1EF2\u00DD\u0176\u1EF8\u0232\u1E8E\u0178\u1EF6\u1EF4\u01B3\u024E\u1EFE'},
   {base: 'Z', letters: '\u005A\u24CF\uFF3A\u0179\u1E90\u017B\u017D\u1E92\u1E94\u01B5\u0224\u2C7F\u2C6B\uA762'},
   {base: 'a', letters: '\u0061\u24D0\uFF41\u1E9A\u00E0\u00E1\u00E2\u1EA7\u1EA5\u1EAB\u1EA9\u00E3\u0101\u0103\u1EB1\u1EAF\u1EB5\u1EB3\u0227\u01E1\u00E4\u01DF\u1EA3\u00E5\u01FB\u01CE\u0201\u0203\u1EA1\u1EAD\u1EB7\u1E01\u0105\u2C65\u0250'},
   {base: 'aa', letters: '\uA733'},
   {base: 'ae', letters: '\u00E6\u01FD\u01E3'},
   {base: 'ao', letters: '\uA735'},
   {base: 'au', letters: '\uA737'},
   {base: 'av', letters: '\uA739\uA73B'},
   {base: 'ay', letters: '\uA73D'},
   {base: 'b', letters: '\u0062\u24D1\uFF42\u1E03\u1E05\u1E07\u0180\u0183\u0253'},
   {base: 'c', letters: '\u0063\u24D2\uFF43\u0107\u0109\u010B\u010D\u00E7\u1E09\u0188\u023C\uA73F\u2184'},
   {base: 'd', letters: '\u0064\u24D3\uFF44\u1E0B\u010F\u1E0D\u1E11\u1E13\u1E0F\u0111\u018C\u0256\u0257\uA77A'},
   {base: 'dz', letters: '\u01F3\u01C6'},
   {base: 'e', letters: '\u0065\u24D4\uFF45\u00E8\u00E9\u00EA\u1EC1\u1EBF\u1EC5\u1EC3\u1EBD\u0113\u1E15\u1E17\u0115\u0117\u00EB\u1EBB\u011B\u0205\u0207\u1EB9\u1EC7\u0229\u1E1D\u0119\u1E19\u1E1B\u0247\u025B\u01DD'},
   {base: 'f', letters: '\u0066\u24D5\uFF46\u1E1F\u0192\uA77C'},
   {base: 'g', letters: '\u0067\u24D6\uFF47\u01F5\u011D\u1E21\u011F\u0121\u01E7\u0123\u01E5\u0260\uA7A1\u1D79\uA77F'},
   {base: 'h', letters: '\u0068\u24D7\uFF48\u0125\u1E23\u1E27\u021F\u1E25\u1E29\u1E2B\u1E96\u0127\u2C68\u2C76\u0265'},
   {base: 'hv', letters: '\u0195'},
   {base: 'i', letters: '\u0069\u24D8\uFF49\u00EC\u00ED\u00EE\u0129\u012B\u012D\u00EF\u1E2F\u1EC9\u01D0\u0209\u020B\u1ECB\u012F\u1E2D\u0268\u0131'},
   {base: 'j', letters: '\u006A\u24D9\uFF4A\u0135\u01F0\u0249'},
   {base: 'k', letters: '\u006B\u24DA\uFF4B\u1E31\u01E9\u1E33\u0137\u1E35\u0199\u2C6A\uA741\uA743\uA745\uA7A3'},
   {base: 'l', letters: '\u006C\u24DB\uFF4C\u0140\u013A\u013E\u1E37\u1E39\u013C\u1E3D\u1E3B\u017F\u0142\u019A\u026B\u2C61\uA749\uA781\uA747'},
   {base: 'lj', letters: '\u01C9'},
   {base: 'm', letters: '\u006D\u24DC\uFF4D\u1E3F\u1E41\u1E43\u0271\u026F'},
   {base: 'n', letters: '\u006E\u24DD\uFF4E\u01F9\u0144\u00F1\u1E45\u0148\u1E47\u0146\u1E4B\u1E49\u019E\u0272\u0149\uA791\uA7A5'},
   {base: 'nj', letters: '\u01CC'},
   {base: 'o', letters: '\u006F\u24DE\uFF4F\u00F2\u00F3\u00F4\u1ED3\u1ED1\u1ED7\u1ED5\u00F5\u1E4D\u022D\u1E4F\u014D\u1E51\u1E53\u014F\u022F\u0231\u00F6\u022B\u1ECF\u0151\u01D2\u020D\u020F\u01A1\u1EDD\u1EDB\u1EE1\u1EDF\u1EE3\u1ECD\u1ED9\u01EB\u01ED\u00F8\u01FF\u0254\uA74B\uA74D\u0275'},
   {base: 'oi', letters: '\u01A3'},
   {base: 'ou', letters: '\u0223'},
   {base: 'oo', letters: '\uA74F'},
   {base: 'p', letters: '\u0070\u24DF\uFF50\u1E55\u1E57\u01A5\u1D7D\uA751\uA753\uA755'},
   {base: 'q', letters: '\u0071\u24E0\uFF51\u024B\uA757\uA759'},
   {base: 'r', letters: '\u0072\u24E1\uFF52\u0155\u1E59\u0159\u0211\u0213\u1E5B\u1E5D\u0157\u1E5F\u024D\u027D\uA75B\uA7A7\uA783'},
   {base: 's', letters: '\u0073\u24E2\uFF53\u00DF\u015B\u1E65\u015D\u1E61\u0161\u1E67\u1E63\u1E69\u0219\u015F\u023F\uA7A9\uA785\u1E9B'},
   {base: 't', letters: '\u0074\u24E3\uFF54\u1E6B\u1E97\u0165\u1E6D\u021B\u0163\u1E71\u1E6F\u0167\u01AD\u0288\u2C66\uA787'},
   {base: 'tz', letters: '\uA729'},
   {base: 'u', letters: '\u0075\u24E4\uFF55\u00F9\u00FA\u00FB\u0169\u1E79\u016B\u1E7B\u016D\u00FC\u01DC\u01D8\u01D6\u01DA\u1EE7\u016F\u0171\u01D4\u0215\u0217\u01B0\u1EEB\u1EE9\u1EEF\u1EED\u1EF1\u1EE5\u1E73\u0173\u1E77\u1E75\u0289'},
   {base: 'v', letters: '\u0076\u24E5\uFF56\u1E7D\u1E7F\u028B\uA75F\u028C'},
   {base: 'vy', letters: '\uA761'},
   {base: 'w', letters: '\u0077\u24E6\uFF57\u1E81\u1E83\u0175\u1E87\u1E85\u1E98\u1E89\u2C73'},
   {base: 'x', letters: '\u0078\u24E7\uFF58\u1E8B\u1E8D'},
   {base: 'y', letters: '\u0079\u24E8\uFF59\u1EF3\u00FD\u0177\u1EF9\u0233\u1E8F\u00FF\u1EF7\u1E99\u1EF5\u01B4\u024F\u1EFF'},
   {base: 'z', letters: '\u007A\u24E9\uFF5A\u017A\u1E91\u017C\u017E\u1E93\u1E95\u01B6\u0225\u0240\u2C6C\uA763'},
];

var diacriticsMap = {};
for (var i = 0; i < defaultDiacriticsRemovalap.length; i++) {
   var letters = defaultDiacriticsRemovalap[i].letters;
   for (var j = 0; j < letters.length; j++) {
      diacriticsMap[letters[j]] = defaultDiacriticsRemovalap[i].base;
   }
}

// "what?" version ... http://jsperf.com/diacritics/12
function removeDiacritics(str) {
   return str.replace(/[^\u0000-\u007E]/g, function (a) {
      return diacriticsMap[a] || a;
   });
}

/**********************************************************************

On document ready event

**********************************************************************/

function initPage() {
   initDataTables(false, false);
   initValidateOnEnter();
   initblurTextElement();
   initDragAnDrop();
   initTagDisplaying();
}

/**********************************************************************

Drag & Drop events

**********************************************************************/

function initDragAnDrop() {
   // Ajoute la propriété pour le drop et le transfert de données
   $.event.props.push('dataTransfer');

   $('#profil_panel .profil').on({
      // on commence le drag
      dragstart: function (e) {
         var el = $(e.target);
         el.css('opacity', '0.5');
         // on garde le num de l'user en mémoire
         e.dataTransfer.setData('text', el.attr('user'));
      },
      // on passe sur un élément draggable
      dragenter: function (e) {
         var el = $(e.target);
         var profil = el.closest('div#profil_panel .profil');
         profil.css({border: 'solid 2px #166dae', 'background-color': 'lightgrey'});
         e.preventDefault();
      },
      // on quitte un élément draggable
      dragleave: function (e) {
         var el = $(e.target);
         var profil = el.closest('div#profil_panel .profil');
         profil.css({border: 'solid 1px #555', 'background-color': 'white'});
      },
      // déclenché tant qu on a pas lâché l élément
      dragover: function (e) {
         e.preventDefault();
      },
      // on lâche l élément
      drop: function (e) {
         var el = $(e.target);
         var user = e.dataTransfer.getData('text');
         var profil = el.closest('div#profil_panel .profil');
         profil.find('ul').append($('#profil_panel li[user="' + user + '"]'));
         profil.css({border: 'solid 1px #555', 'background-color': 'white'});
         updateUserProfil(user, profil.attr('profil'));
         e.preventDefault();
      },
      // fin du drag (même sans drop)
      dragend: function (e) {
         var el = $(e.target);
         el.css('opacity', '1');
      },
      // au clic sur un élément
      click: function () {},
   });
}

/**********************************************************************

Tags handling
 
**********************************************************************/
var defaultTaggues = [];

function initTagDisplaying() {
   $('#formTags').tagit({
      autocomplete: {
         source: function (request, response) {
            $.ajax({
               type: 'GET',
               url: 'liste-taggues-ajax',
               datatype: 'json',
               data: {
                  term: request.term,
               },
               success: function (data) {
                  response(
                     $.map(data, function (item) {
                        return {
                           label: item,
                           value: item,
                        };
                     })
                  );
               },
               error: function (request, status, error) {
                  alert(error);
               },
            });
         },
         minLength: 2,
      },
      singleField: true,
      fieldName: 'tags',
   });
}
/**********************************************************************

GESTION DU REDIMENSIONNEMENT DES COLONNES

**********************************************************************/
var x = 0;
var mouse_down = false;
var resizer;
$('table thead tr:first-child div').mousedown(function (e) {
   mouse_down = true;
   x = e.pageX;
   resizer = $(this);
   e.preventDefault();
   e.stopPropagation();
});

$('body').mouseup(function (e) {
   if (mouse_down) {
      resizer.parent().width(resizer.parent().width() + (e.pageX - x));
      mouse_down = false;
      updateColonnesOptions();
   }
});

/**********************************************************************

OnClick events

**********************************************************************/

//Déconnexion
$('.deconnecter').click(deconnecterClick);
function deconnecterClick() {
   setCookie('jsSessionCookieKeepDisplayedDemandesDone', false, 1);
   setCookie('jsSessionCookieKeepDisplayedDemandesArchived', false, 1);
   window.location.replace('logout');
}

// Sélection des lignes
$('#liste_demande tbody').delegate('tr', 'click', selectDemande);
function selectDemande() {
   $(this).toggleClass('selected');
}

$('#demandes_list').delegate('.deselect_all', 'click', deselectAll);
function deselectAll() {
   $('#liste_demande tr').removeClass('selected');
}

$('#liste_demande tbody').delegate('tr', 'dblclick', openDemande);
function openDemande(event) {
   var el = $(event.target);
   window.location.replace(el.parent('tr').find('.afficherdemande').attr('href'));
}

$('a.toggle-vis').on('click', function (e) {
   e.preventDefault();

   // Get the column API object
   var column = $('#liste_demande')
      .DataTable()
      .column($(this).attr('column_name') + ':name');

   // Toggle the visibility
   column.visible(!column.visible());

   $(e.target).toggleClass('selected');
   ajustcolumnsize();
   styletags();
   updateColonnesOptions();
});

// Fonctions de modifications de l'adresse e-mail
$('#mailpopup .createmail').click(creatEmailButton);
function creatEmailButton() {
   var email = $('#mailpopup input').val().trim();
   if (testEmail(email)) {
      updateemail(email);
      $('#covermail').fadeOut();
      $('#covermail .errortxt').text('');
      $('#modifemail input').attr('mail', email);
      $('.email').text(email);
   } else {
      $('#covermail .errortxt').text('Veuillez saisir une adresse e-mail correcte');
   }
}

$('#liste_demande').delegate('.voirpiecejointe', 'click', popuppiecejointe);
function popuppiecejointe(event) {
   var el = $(event.target);
   var demandenum = el.attr('demandenum');
   ajaxPopup('piece-jointe', {demandenum: demandenum});
   event.preventDefault();
}

$('#liste_demande').delegate('.voircommentaires', 'click', popucommentaire);
function popucommentaire(event) {
   var el = $(event.target);
   var demandenum = el.attr('demandenum');
   ajaxPopup('commentaire', {demandenum: demandenum});
   event.preventDefault();
}

$('#emailbutton').click(showmodifemail);
function showmodifemail() {
   $(this).hide();
   $('#modifemail input').val($('#modifemail input').attr('mail'));
   $('#modifemail').css('display', 'inline');
}

$('#modifemail button').click(modifemail);
function modifemail() {
   var email = $('#modifemail input').val().trim();
   if (testEmail(email)) {
      updateemail(email);
      $('#modifemail .errortxt').text('');
      $('#emailbutton').show('slow');
      $('#modifemail').hide('slow');
      $('#modifemail input').attr('mail', email);
      $('.email').text(email);
   } else {
      $('#modifemail .errortxt').text('Veuillez saisir une adresse e-mail correcte');
   }
}

function updateemail(email) {
   $.ajax({
      type: 'POST',
      url: 'updateemail',
      data: {email: email},
   });
}

// Export Popup
function exportPopup(type) {
   $('#popup .popupcontent').html(
      "<h4>Options d'export :</h4>" +
         "<input type='hidden' class='exporttype' value='" +
         type +
         "' />" +
         "<input type='checkbox' id='allcolumn' value='value'>&nbsp;<label for='allcolumn'>Exporter toutes les colonnes</label>" +
         '<br/>' +
         "<input type='checkbox' id='includecomments' value='value'>&nbsp;<label for='includecomments'>Inclure les commentaires</label>" +
         '<br/>' +
         '<br/>' +
         "<button class='formbutton exporter' title='Exporter' type='button'><span>Exporter</span></button>"
   );

   $('#popup').show();
}

$('.export_menu').click(exportMenuClic);
function exportMenuClic(event) {
   var el = $(event.target);
   exportPopup(el.attr('id'));
}

$('.popup').delegate('.exporter', 'click', exportDemandes);
function exportDemandes() {
   $('#popup').hide();
   var arrayId = [];

   switch ($('.popupdiv .exporttype').val()) {
      case 'exportall':
         arrayId = getAllDemandes();
         break;
      case 'exportfiltered':
         arrayId = getFilteredDemandes();
         break;
      case 'exportselected':
         arrayId = getSelectedDemandes();
         break;
      default:
         break;
   }

   var allcolumns = $('#allcolumn').prop('checked');
   var includecomments = $('#includecomments').prop('checked');

   if (arrayId.length !== 0) $.download('exportdemande', 'allcolumns=' + allcolumns + '&includecomments=' + includecomments + '&id=' + arrayId);
   else alert("Aucune demande n'est sélectionnée !");
}

$('.popup').delegate('#choix_notice .oui', 'click', verificationdemande);
function verificationdemande() {
   $('#choix_notice_form').submit();
}

$('.popup').delegate('#choix_notice .non', 'click', closepopup);
function verifnoticedemande(demande_num) {
   return ajaxPopup('verifnoticedemande', {demande_num: demande_num});
}

function goToListeDemande() {
   window.location.replace('liste-demandes');
}

$('#chercher-notice button').click(cherchernotice);
function cherchernotice() {
   var value = $('#searched_value').val();
   if (value !== '') {
      $('#search_type').val('');
      $('#chercher-notice').submit();
   }
   return false;
}

$('#ajoutfichier').click(ajoutfichier);
var filecount = $('#filecount').val();
function ajoutfichier() {
   filecount++;
   var html =
      '<div id="divfile' +
      filecount +
      '" class="file inputline">' +
      '<input type="file" id="file' +
      filecount +
      '" name="file' +
      filecount +
      '" /><button delete="#divfile' +
      filecount +
      '" class="supprimerfichier formbutton" title="Supprimer le fichier" type="button"><span>Supprimer</span></button>' +
      '</div>';
   $('#files').append(html);
}

$('#files').delegate('button.supprimerfichier', 'click', supprimerfichier);
function supprimerfichier() {
   if ($('#files .file').size() === 1) {
      var input = $($(this).attr('delete') + ' input');
      input.replaceWith(input.val('').clone(true));
   } else {
      $($(this).attr('delete')).remove();
   }
}

$('#filesup').delegate('button.supprimerfichierup', 'click', supprimerfichierup);
function supprimerfichierup(event) {
   var el = $(event.target);
   if (el.is('span')) {
      el = el.parent('button');
   }
   if (confirm('Êtes-vous sûr de vouloir supprimer définitivement cette pièce justificative dans Cidemis ?')) {
      $('#filestodelete').append("<input type='hidden' name='filestodelete' value='" + el.attr('fileid') + "' />");
      $('#fileup' + el.attr('fileid')).remove();
   }
}

$('#supprimerpremierfichier').click(supprimerpremierfichier);
function supprimerpremierfichier() {
   $('#file0').replaceWith('<input class="required" error_required="Veuillez ajouter un justificatif" type="file" id="file0" name="file0" />');
}

$('#creation-demande .annuler').click(goToListeDemande);
$('.goto_listedemande').click(goToListeDemande);

$('.shadow, .popup-close').click(closepopup);
function closepopup(event) {
   var el = $(event.target);
   el.parents('.popup').hide();
}

$('.popup').delegate('#choix-action .Annuler ', 'click', closepopup);
$('.popup').delegate('.closepopup ', 'click', closepopup);

$('.popup').delegate('#choix-action .Consulter ', 'click', afficherdemande);
function afficherdemande(event) {
   var el = $(event.target);
   if (el.is('span')) {
      el = el.parent('button');
   }
   window.open('afficher-demande?id=' + el.attr('demande'));
}

$('.popup').delegate('#choix-action .Creer', 'click', creationdemande);
function creationdemande() {
   $('#choix-action').attr('action', 'creation-demande').submit();
}

$('#creation-demande .actiondemande').click(actiondemande);
function actiondemande(event) {
   var el = $(event.target);
   if (el.is('span')) {
      el = el.parent('button');
   }
   var action = el.attr('action');
   var submit = true;
   if (el.hasClass('green')) {
      if (el.attr('confirm') !== undefined && !confirm(el.attr('confirm'))) {
         // Si envoie demande à role différent (bouton vert...)
         return;
      }

      if (!verifdemande(action)) {
         submit = false;
      } else {
         if (action === 'valider' && $('#user_role').val() === '2' && !verifnoticedemande($('#id_demande').val())) {
            submit = false;
         }
      }
   }

   if (submit) {
      $('#actiondemande').val(action);
      $('#filecount').val(filecount);
      clearBlurText();
      $('#creation-demande').submit();
   }
}

function verifdemande(action) {
   var valid = true;

   var filetotal =
      $('#filesup div').size() +
      $('#files input:file')
         .filter(function () {
            return this.value.length > 0;
         })
         .size();

   // Si c'est une demande de correction alors il faut remplir le champ "zones" et "commentaire" pour informer sur quelle zone porte la correction
   // Si il n'y a aucun commentaire d'enregistré et que l'utilisateur n'en a pas fourni, on lui demande d'en ajouter un
   if ($('#type_demande').val() === '23' && $('.commentaire').size() === 0 && (!$('#commentaire').val() || $('#commentaire').val() === $('#commentaire').attr('blurText'))) {
      $('#commentaire').addClass('inputerror');
      $('#commentaire_error').html("<span class='errortxt'>Merci d'apporter des précisions concernant votre demande de correction dans le champ «Commentaires»</span>");
      valid = false;
   }

   if (($('#user_role').val() === '2' || $('#user_role').val() === '1') && action !== 'rejeter' && filetotal === 0) {
      var ok = confirm('Êtes-vous sur de ne pas vouloir joindre de justificatifs');
      if (!ok) valid = false;
   }

   if (action === 'precisions' && (!$('#commentaire').val() || $('#commentaire').val() === $('#commentaire').attr('blurText'))) {
      $('#commentaire').addClass('inputerror');
      $('#commentaire_error').html("<span class='errortxt'>Merci de poser votre demande de précisions dans le champ «Commentaires»</span>");
      valid = false;
   }

   if (action === 'refuser' && (!$('#commentaire').val() || $('#commentaire').val() === $('#commentaire').attr('blurText'))) {
      $('#commentaire').addClass('inputerror');
      $('#commentaire_error').html("<span class='errortxt'>Merci d'indiquer la raison de votre refus en remplissant le champ «Commentaires»</span>");
      valid = false;
   }

   if ($('#type_demande').val() === '23' && action === 'valider') {
      if (!$('#zones').val()) {
         $('#zones').addClass('inputerror');
         $('#zones_error').html("<span class='errortxt'>Vous devez choisir une zone afin de valider la demande de correction</span>");
         valid = false;
      }
   }

   if (action === 'creernotice') {
      if (!$('#num_ppn').val()) {
         $('#num_ppn').addClass('inputerror');
         $('#num_ppn_error').html("<span class='errortxt'>Vous devez remplir le PPN de la notice afin de valider la demande de création de notice</span>");
         valid = false;
      } else {
         var test_ppn = verifierppnajax($('#num_ppn').val());
         if (test_ppn) {
            $('#num_ppn').addClass('inputerror');
            $('#num_ppn_error').html("<span class='errortxt'>" + test_ppn + '</span>');
            valid = false;
         }
      }
   }

   // Meme chose si l'on répond à une demande de précision
   if (($('#etat_demande').val() === '27' || $('#etat_demande').val() === '28') && (!$('#commentaire').val() || $('#commentaire').val() === $('#commentaire').attr('blurText'))) {
      $('#commentaire').addClass('inputerror');
      $('#commentaire_error').html("<span class='errortxt'>Merci de répondre à la demande de précision en remplissant le champ «Commentaires»</span>");
      valid = false;
   }

   if (action === 'accepter' && $('#type_demande').val() === '22') {
      if (!$('#num_ISSN').val()) {
         $('#num_ISSN').addClass('inputerror');
         $('#num_ISSN_error').html("<span class='errortxt'>Vous devez attribuer un numéro ISSN</span>");
         valid = false;
      } else {
         var issn = $('#num_ISSN').val();
         var issn_format = /^\d{4}-\d{3}[\dxX]$/;

         if (!issn_format.test(issn)) {
            $('#num_ISSN').addClass('inputerror');
            $('#num_ISSN_error').html("<span class='errortxt'>Le numéro ISSN doit être de la forme XXXX-XXXX</span>");
            valid = false;
         } else if (!isValidISSN(issn)) {
            valid = false;
         }
      }
   }

   if (action === 'rejeter' && (!$('#commentaire').val() || $('#commentaire').val() === $('#commentaire').attr('blurText'))) {
      $('#commentaire').addClass('inputerror');
      $('#commentaire_error').html("<span class='errortxt'>Merci d'indiquer la raison de votre rejet en remplissant le champ «Commentaires»</span>");
      valid = false;
   }

   if ($('#ppn').val() && action !== 'precisions') {
      var verif_notice = verifiernoticeajax($('#ppn').val());
      if (verif_notice) {
         $('#popup .popupcontent').html('<h4>Il y a une erreur empêchant la modification de la notice.</h4>' + '<br/>' + '<p>' + verif_notice + '</p>');
         $('#popup').show();
         valid = false;
      }
   }

   return valid;
}

$('.sidebar-toggler').click(goToListeDemande);

/**********************************************************************

EVENEMENTS

**********************************************************************/

// Affiche à l'écran un message de chargement
function displayDomLoadingElement(domElement, message) {
   if (document.getElementById(domElement)) {
      document.getElementById(domElement).style.display = 'block';
      document.getElementById(domElement).style.color = 'red';
      document.getElementById(domElement).innerHTML = '<span class="blink">' + message + '</span>';
   }
}

// Fait disparaitre de l'écran un message de chargement
function removeDomLoadingElement(domElement) {
   if (document.getElementById(domElement)) {
      document.getElementById(domElement).style.display = 'none';
   }
}

// Désactive les boutons de bas de formulaire
function disabledFormButtonsElement() {
   if (document.getElementById('validationBouton')) {
      document.getElementById('validationBouton').disabled = true;
      document.getElementById('validationBouton').style.background = '#b94a48';
   }
   if (document.getElementById('precisionsBouton')) {
      document.getElementById('precisionsBouton').disabled = true;
      document.getElementById('precisionsBouton').style.background = '#b94a48';
   }
   if (document.getElementById('enregistrementBouton')) {
      document.getElementById('enregistrementBouton').disabled = true;
      document.getElementById('enregistrementBouton').style.background = '#b94a48';
   }
   if (document.getElementById('rejeterBouton')) {
      document.getElementById('rejeterBouton').disabled = true;
      document.getElementById('rejeterBouton').style.background = '#b94a48';
   }
   if (document.getElementById('tableauDeBordBouton')) {
      document.getElementById('tableauDeBordBouton').disabled = true;
      document.getElementById('tableauDeBordBouton').style.background = '#b94a48';
   }
}

// Réactive les boutons de bas de formulaire
function enabledFormButtonsElement() {
   if (document.getElementById('validationBouton')) {
      document.getElementById('validationBouton').disabled = false;
      document.getElementById('validationBouton').style.background = '#7cb81c';
   }
   if (document.getElementById('precisionsBouton')) {
      document.getElementById('precisionsBouton').disabled = false;
      document.getElementById('precisionsBouton').style.background = '#7cb81c';
   }
   if (document.getElementById('enregistrementBouton')) {
      document.getElementById('enregistrementBouton').disabled = false;
      document.getElementById('enregistrementBouton').style.background = '#555555';
   }
   if (document.getElementById('rejeterBouton')) {
      document.getElementById('rejeterBouton').disabled = false;
      document.getElementById('rejeterBouton').style.background = '#7cb81c';
   }
   if (document.getElementById('tableauDeBordBouton')) {
      document.getElementById('tableauDeBordBouton').disabled = false;
      document.getElementById('tableauDeBordBouton').style.background = '#555555';
   }
}

// Ajuste la taille du menu de sélection des colonnes si celui-ci déborde de l'écran
$('#columns_control').mouseenter(function () {
   var el = $('#columns_control');
   var bottom = el[0].getBoundingClientRect().bottom;
   var isVisible = bottom <= $(window).height();
   if (!isVisible) {
      el.height(el.height() - (bottom - $(window).height()));
      el.css('overflow-y', 'auto');
   }
});

$('#fichier_cieps').change(fichierCiepsChange);
function fichierCiepsChange(event) {
   $(event.target);
   $('form#upload-cieps').submit();
}

$('#type_demande_select').change(onTypeDemandeSelectChange);
function onTypeDemandeSelectChange(event) {
   var el = $(event.target);

   // si c'est une demande de création de notice
   if (el.val() === '24') {
      $('#search_type').val(el.val());
      $('label[for="searched_value"]').html('titre');
   } else {
      $('#search_type').val(el.val());
      $('label[for="searched_value"]').html('ppn');
   }
}

$('#code_pays_select').change(onCodePaysSelectChange);
function onCodePaysSelectChange(event) {
   var el = $(event.target);
   $('#code_pays').val(el.val());
}

$('input[blurText],textarea[blurText]').focus(clearTextOnFocus);
function clearTextOnFocus(event) {
   var el = $(event.target);
   if (el.val() === el.attr('blurText')) {
      el.val('');
   }
}

$('input[blurText],textarea[blurText]').blur(addTextOnBlur);
function addTextOnBlur(event) {
   var el = $(event.target);
   if (el.val() === '') {
      el.val(el.attr('blurText'));
   }
}

//Fonction se déclenchant à la validation du formulaire de création
function ajaxPopup(url, params) {
   var error;
   $.ajax({
      type: 'POST',
      url: url,
      data: params,
      async: false,
      success: function (data) {
         if (data !== '') {
            $('#popup .popupcontent').html(data);
            $('#popup').show();
            error = false;
         } else {
            error = true;
         }
      },
   });

   return error;
}

//Fonction se déclenchant à la validation du formulaire de création
$('body').delegate('form.AJAXCreationForm', 'submit', ajaxFormCreationSubmit);
function ajaxFormCreationSubmit(e) {
   displayDomLoadingElement('myDIV', 'Vérification des informations en cours, veuillez patienter...');
   disabledFormButtonsElement();

   e.preventDefault();
   var form = $('#creation-demande')[0];
   var postData = new FormData(form);
   var formURL = form.getAttribute('action');

   $.ajax({
      url: formURL,
      type: 'POST',
      data: postData,
      enctype: 'multipart/form-data',
      processData: false,
      contentType: false,
      success: function (data) {
         if (data === 'LOGOUT') {
            removeDomLoadingElement('myDIV');
            enabledFormButtonsElement();

            deconnecterClick();
         } else {
            if (data.indexOf('popuperreurnotice') > 0) {
               removeDomLoadingElement('myDIV');
               enabledFormButtonsElement();

               $('#' + form.getAttribute('popupid') + ' .popupcontent').html(data);
               $('#' + form.getAttribute('popupid')).show();
            } else {
               e.preventDefault();
               e.stopPropagation();
               e.stopImmediatePropagation();
               $('body').undelegate('form.AJAXCreationForm', 'submit', ajaxFormSubmit);
               goToListeDemande();
            }
         }
      },
      error: function () {
         removeDomLoadingElement('myDIV');
         enabledFormButtonsElement();
         console.log(e);
         console.error('AJAXCreationForm error');
      },
   });
   return false;
}

//Fonction se déclenchant à la validation du formulaire de modification
$('body').delegate('form.AJAXForm', 'submit', ajaxFormSubmit);
function ajaxFormSubmit(e) {
   displayDomLoadingElement('myDIV', 'Contrôle de la notice en cours, veuillez patienter');
   disabledFormButtonsElement();

   var form = $(this);
   var postData = form.serializeArray();
   postData.push({name: 'ajax', value: 'true'});
   var formURL = form.attr('action');

   $.ajax({
      url: formURL,
      type: 'POST',
      data: postData,
      success: function (data) {
         if (data === 'LOGOUT') {
            removeDomLoadingElement('myDIV');
            enabledFormButtonsElement();

            deconnecterClick();
         } else {
            if (data !== 'RESEND_FORM') {
               removeDomLoadingElement('myDIV');
               enabledFormButtonsElement();

               $('#' + form.attr('popupid') + ' .popupcontent').html(data);
               $('#' + form.attr('popupid')).show();
            } else {
               e.preventDefault();
               e.stopPropagation();
               e.stopImmediatePropagation();
               $('body').undelegate('form.AJAXForm', 'submit', ajaxFormSubmit);
               form.submit();
            }
         }
      },
      error: function () {
         removeDomLoadingElement('myDIV');
         enabledFormButtonsElement();

         console.error('ajaxFormSubmit error');
      },
   });

   return false;
}

/**********************************************************************

Mise à jour des options d'affichage

**********************************************************************/

function updateColonnesOptionsAjax(data) {
   $.ajax({
      type: 'POST',
      url: 'optionscolonnes',
      data: data,
      success: function (data) {
         if ($(data).find('option').text() === 'OK') {
            console.info('updateColonnesOptionsAjax OK');
         } else if ($(data).find('.logindiv')) {
            console.info('updateColonnesOptionsAjax LOGIN');
         }
      },
      error: function () {
         if ($(data).find('option').text() === 'OK') {
            console.error('updateColonnesOptionsAjax NOK');
         }
      },
   });
}

function updateUserProfil(user, profil) {
   $.ajax({
      type: 'POST',
      url: 'update-user-profil',
      data: {user: user, profil: profil},
   });
}

function verifierppnajax(ppn) {
   var error;

   $.ajax({
      type: 'POST',
      url: 'verifier-ppn',
      async: false,
      data: {ppn: ppn},
      success: function (data) {
         if (data === 'OK') {
            error = false;
         } else if (data === 'DEMANDE_EXISTANTE') {
            error = 'Une demande de numérotation existe déjà pour le ppn ' + ppn;
         } else if (data === 'AUCUNE_NOTICE') {
            error = "Aucune notice n'existe avec ce ppn";
         } else if (data === 'PPNINCORRECT') {
            error = 'Format du PPN incorrect.';
         } else if (data === 'ISSN_EXISTANT') {
            error = 'Un ISSN a déjà été attribué pour le ppn ' + ppn;
         } else if (data === 'MONOGRAPHIE') {
            error = "Il n'est pas possible de créer une demande sur des notices de monographies";
         }
      },
      error: function () {
         error = true;
      },
   });

   return error;
}

function verifiernoticeajax(ppn) {
   var error;
   $.ajax({
      type: 'POST',
      url: 'verifier-notice',
      async: false,
      data: {ppn: ppn},
      success: function (data) {
         if (data === 'OK') error = false;
         else error = data;
      },
      error: function () {
         error = true;
      },
   });
   return error;
}

function errorISSNAjax(issn, ppn) {
   var error = false;

   $.ajax({
      type: 'POST',
      url: 'verifier-issn',
      async: false,
      data: {issn: issn, ppn: ppn},
      success: function (data) {
         error = data.exist;
      },
      error: function () {
         error = true;
      },
   });

   return error;
}

/**********************************************************************

Document ready definition

**********************************************************************/

$(document).ready(initPage);

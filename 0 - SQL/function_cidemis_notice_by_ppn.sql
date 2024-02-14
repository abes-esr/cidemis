/**
 * Cette fonction retourne une notice pouvant être ajouté dans la table CIDEMIS_NOTICE.
 * Elle est appelé par le trigger UPDATE_NOTICE à chaque changement d'un PPN dans la table DEMANDES de Cidemis
 */
CREATE OR REPLACE FUNCTION CIDEMIS_NOTICE_BY_PPN 
(
    PPN_VALUE IN VARCHAR2
) 
RETURN CIDEMIS_NOTICE_TABLE AS 
	CIDEMIS_NOTICE_ROWS_TMP CIDEMIS_NOTICE_TABLE;
BEGIN
    SELECT
        CIDEMIS_NOTICE(
        	NOTICES.ppn,
            NOTICES.titre_a,
            NOTICES.issn,
            NOTICES.titre_e,
            NOTICES.titre_c,
            NOTICES.titre_h,
            NOTICES.titre_i,
            NOTICES.titre_cle,
            NOTICES.titre_abrege,
            NOTICES.pays,
            NOTICES.date_debut,
            NOTICES.date_fin,
            NOTICES.type_document,
            NOTICES.statut_notice,
            NOTICES.centre_issn,
            NOTICES.issn035,
            NOTICES.frbnf,
            NOTICES.type_ressource_continue,
            NOTICES.source_cataloguage_issn,
            NOTICES.note_identifiants,
            NOTICES.note_generale_catalogueur
        )
    	BULK COLLECT INTO CIDEMIS_NOTICE_ROWS_TMP
    FROM (
    		SELECT 
	            NOTICE.ppn,
	            NOTICE.titre_a,
	            NOTICE.issn,
	            NOTICE.titre_e,
	            NOTICE.titre_c,
	            NOTICE.titre_h,
	            (NOTICE.titre_i_1 || (CASE WHEN NOTICE.titre_i_2 IS NULL THEN '' ELSE (' . ' || NOTICE.titre_i_2) END)) AS titre_i,
	            NOTICE.titre_cle,
	            NOTICE.titre_abrege,
	            NOTICE.pays,
	            REPLACE(SUBSTR(NOTICE.dates,10,4),' ','X') AS date_debut,
	            (CASE WHEN (SUBSTR(NOTICE.dates,14,4)='    ' OR SUBSTR(NOTICE.dates,14,4)='9999') THEN '' ELSE REPLACE(SUBSTR(NOTICE.dates,14,4),' ','X') END) AS date_fin,
	            SUBSTR(NOTICE.label,7,2) AS type_document,
	            SUBSTR(NOTICE.label,6,1) AS statut_notice,
	            NOTICE.centre_issn,
	            NOTICE.issn035,
	            NOTICE.frbnf,
	            SUBSTR(NOTICE.type_ressource_continue,1,1) as type_ressource_continue,
	            NOTICE.source_cataloguage_issn,
	            NOTICE.note_identifiants,
	            NOTICE.note_generale_catalogueur
	        FROM
	            AUTORITES.NOTICESBIBIO X,
	            XMLTABLE('/record'
	                PASSING X.DATA_XML
	                COLUMNS 
	                    ppn                             VARCHAR(9)      PATH        '//controlfield[@tag="001"][1]/text()',
	                    titre_a                         VARCHAR(300)    PATH        '//datafield[@tag="200"][1]/subfield[@code="a"][1]/text()',
	                    issn                            VARCHAR(9)      PATH        '//datafield[@tag="011"][1]/subfield[@code="a"][1]/text()',
	                    titre_e                         VARCHAR(300)    PATH        '//datafield[@tag="200"][1]/subfield[@code="e"][1]/text()',
	                    titre_c                         VARCHAR(300)    PATH        '//datafield[@tag="200"][1]/subfield[@code="c"][1]/text()',
	                    titre_h                         VARCHAR(300)    PATH        '//datafield[@tag="200"][1]/subfield[@code="h"][1]/text()',
	                    titre_i_1                       VARCHAR(300)    PATH        '//datafield[@tag="200"][1]/subfield[@code="i"][1]/text()',
	                    titre_i_2                       VARCHAR(300)    PATH        '//datafield[@tag="200"][1]/subfield[@code="i"][2]/text()',
	                    titre_cle                       VARCHAR(300)    PATH        '//datafield[@tag="530"][1]/subfield[@code="a"][1]/text()',
	                    titre_abrege                    VARCHAR(300)    PATH        '//datafield[@tag="530"][1]/subfield[@code="b"][1]/text()',
	                    pays                            VARCHAR(2)      PATH        '//datafield[@tag="102"][1]/subfield[@code="a"][1]/text()',
	                    dates                           VARCHAR(40)     PATH        '//datafield[@tag="100"][1]/subfield[@code="a"][1]/text()',
	                    label                           VARCHAR(30)     PATH        '//leader[1]/text()',
	                    centre_issn                     VARCHAR(2)      PATH        '//datafield[@tag="802"][1]/subfield[@code="a"][1]/text()',
	                    issn035                         VARCHAR(130)    PATH        'string-join(//datafield[@tag="035"]/subfield[@code="a" and starts-with(text(),"issn")]/text()," / ")',
	                    frbnf                           VARCHAR(130)    PATH        'string-join(//datafield[@tag="035"]/subfield[@code="a" and starts-with(text(),"FRBNF")]/text()," / ")',  
	                    type_ressource_continue         VARCHAR(11)     PATH        '//datafield[@tag="110"][1]/subfield[@code="a"][1]/text()',
	                    source_cataloguage_issn         VARCHAR(50)     PATH        'string-join(//datafield[@tag="801"]/subfield[@code="b" and starts-with(text(),"ISSN")]/text()," / ")',
	                    note_identifiants               VARCHAR(4000)   PATH        'string-join(//datafield[@tag="301"]/subfield[@code="a"]//text(),"---note---")',
	                    note_generale_catalogueur       VARCHAR(4000)   PATH        'string-join(//datafield[@tag="830"]/subfield[@code="a"]//text(),"---note---")'
	                ) AS NOTICE
	        WHERE 
	            X.PPN = PPN_VALUE
    	) NOTICES;

    RETURN CIDEMIS_NOTICE_ROWS_TMP;
    
    EXCEPTION
	WHEN OTHERS THEN
        CIDEMIS_NOTICE_ROWS_TMP.DELETE;
        RETURN CIDEMIS_NOTICE_ROWS_TMP;
END CIDEMIS_NOTICE_BY_PPN;
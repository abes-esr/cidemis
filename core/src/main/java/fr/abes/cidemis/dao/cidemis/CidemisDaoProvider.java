package fr.abes.cidemis.dao.cidemis;


import lombok.Getter;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Getter
@Service
public class CidemisDaoProvider {
    @Resource
    private ICbsUsersDao cbsUsersDao;
    @Resource
    private IRolesDao rolesDao;
    @Resource
    private IUsersRolesDao usersRolesDao;
    @Resource
    private ICommentairesDao commentairesDao;
    @Resource
    private IDemandesDao demandesDao;
    @Resource
    private IPiecesJustificativesDao piecesJustificativesDao;
    @Resource
    private ITagguesDao tagguesDao;
    @Resource
    private IJournalDemandesDao journalDemandesDao;
    @Resource
    private IDefaultTagguesDao defaultTagguesDao;
    @Resource
    private IEtatsDemandesDao etatsDemandesDao;
    @Resource
    private IOptionsDao optionsDao;
    @Resource
    private IOptionsRolesDao optionsRolesDao;
    @Resource
    private ITypesDemandesDao typesDemandesDao;
    @Resource
    private IZoneCorrectionDao zoneCorrectionDao;
    @Resource
    private ICidemisNoticeDao cidemisNoticeDao;
    @Resource
    private IJdbcTemplateDao cidemisTableDao;
    @Resource
    private IToolsDao toolsDao;



}

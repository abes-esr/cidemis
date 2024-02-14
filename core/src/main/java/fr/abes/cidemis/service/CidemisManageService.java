package fr.abes.cidemis.service;

import lombok.Getter;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Getter
@Service
public class CidemisManageService {
    @Resource
    private IDemandesService demande;
    @Resource
    private ICommentairesService commentaires;
    @Resource
    private ITagguesService taggues;
    @Resource
    private IPiecesJustificativesService piecesJustificatives;
    @Resource
    private IToolsService tools;
    @Resource
    private IOptionsService options;
    @Resource
    private IReferenceService reference;
    @Resource
    private IUsersService users;
    @Resource
    private IControleEnvoiMailService controleEnvoiMail;
}

package fr.abes.cidemis.service;

import fr.abes.cidemis.exception.DaoException;
import fr.abes.cidemis.model.cidemis.*;

import java.util.List;

public interface IUsersService {
    boolean save(CbsUsers cbsUsers);

    Boolean updateProfil(CbsUsers cbsUsers, Integer idProfil);

    Boolean delete(CbsUsers cbsUsers);

    CbsUsers findCbsUsers(Integer userNum);

    List<CbsUsers> findAllCbsUsers();

    List<CbsUsers> findCbsUsersByRoles(Roles roles);

    CbsUsers findCbsUsersByCommentaires(Commentaires commentaires);

    CbsUsers findCbsUsersByDemandes(Demandes demandes);

    CbsUsers findCbsUsersByJournalDemandes(JournalDemandes journalDemandes);

    CbsUsers findCbsUsersByPiecesJustificatives(PiecesJustificatives piecesJustificatives);

    CbsUsers findCbsUsersByOptions(Options options);

    CbsUsers findCbsUsersByUserKey(String userkey);

    CbsUsers createUsersCbsFromUserRegistry(RegistryUser registryUser);

    CbsUsers updateUsersCbsFromUserRegistry(RegistryUser registryUser);

    Connexion login(RegistryUser registryUser) throws DaoException;

    Boolean save(Roles roles);

    boolean saveCbsUsersListByRoles(Roles roles);

    Boolean delete(Roles roles);

    Roles findRoles(Integer idRole);

    List<Roles> findAllRoles();

    Roles findRolesByCbsUsers(CbsUsers cbsUsers);

    Boolean save(UsersRoles usersRoles);

    Boolean delete(UsersRoles usersRoles);

    UsersRoles findUsersRoles(Integer idUsersRoles);

    UsersRoles findUsersRolesByUserGroup(String userGroup);

    List<UsersRoles> findAllUsersRoles();

    List<CbsUsers> findCbsUsersToSendEmail(Demandes demande, Roles roles);
}

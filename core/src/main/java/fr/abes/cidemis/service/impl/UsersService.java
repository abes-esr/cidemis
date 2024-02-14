package fr.abes.cidemis.service.impl;

import fr.abes.cidemis.constant.Constant;
import fr.abes.cidemis.dao.cidemis.CidemisDaoProvider;
import fr.abes.cidemis.dao.cidemis.IJdbcTemplateDao;
import fr.abes.cidemis.exception.DaoException;
import fr.abes.cidemis.model.cidemis.*;
import fr.abes.cidemis.service.CidemisManageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class UsersService implements fr.abes.cidemis.service.IUsersService {
    @Autowired
    private CidemisDaoProvider dao;
    @Autowired
    private CidemisManageService service;

    @Autowired
    private IJdbcTemplateDao jdbcTemplateDao;

    @Override
    public boolean save(CbsUsers cbsUsers) {
        if (cbsUsers == null) {
            log.error("Erreur dans le parametre d'entrée dans save, la variable 'cbs_users' vaut 'null'.");
            return false;
        }
        this.dao.getCbsUsersDao().save(cbsUsers);
        log.debug("Sauvegarde : Cbs_Users. User_num:" + cbsUsers.getUserNum().toString() + " OK");
        return true;
    }

    @Override
    public Boolean updateProfil(CbsUsers cbsUsers, Integer idProfil) {
        if (cbsUsers == null) {
            log.error("Erreur dans le parametre d'entrée , la variable 'cbs_users' vaut 'null'.");
            return false;
        }
        cbsUsers.setIdProfil(idProfil);
        this.dao.getCbsUsersDao().save(cbsUsers);
        log.info("Mise à jour: Cbs_Users. User_num:" + cbsUsers.getUserNum().toString() + ", id_profil:"
                + idProfil + " OK");

        return true;
    }

    @Override
    public Boolean delete(CbsUsers cbsUsers) {
        if (cbsUsers == null) {
            log.error("Erreur dans le parametre d'entrée , la variable 'cbs_users' vaut 'null'.");
            return false;
        }

        try {
            this.dao.getCbsUsersDao().delete(cbsUsers);
            return true;
        } catch (Exception ex) {
            log.error("function delete(). Cbs_Users. User_num:" + cbsUsers.getUserNum().toString(), ex);
            return false;
        }
    }

    @Override
    public CbsUsers findCbsUsers(Integer userNum) {
        Optional<CbsUsers> cbsUsers = this.dao.getCbsUsersDao().findById(userNum);

        if (!cbsUsers.isPresent())
            log.warn("La fonction 'findCbsUsers' n'a retournée aucun résultat. User_num:" + userNum);

        return cbsUsers.get();
    }

    @Override
    public List<CbsUsers> findAllCbsUsers() {
        List<CbsUsers> cbsUsersList = this.dao.getCbsUsersDao().findAll();

        if (cbsUsersList.isEmpty())
            log.warn("La fonction 'findAllCbs_Users' n'a retournée aucun résultat.");

        return cbsUsersList;
    }

    @Override
    public List<CbsUsers> findCbsUsersByRoles(Roles roles) {
        List<CbsUsers> cbsUsersList = this.dao.getCbsUsersDao().findCbsUsersByRoles(roles);

        if (cbsUsersList.isEmpty())
            log.warn("La fonction 'findCbsUsersByRoles' n'a retournée aucun résultat. Id_role:"
                    + roles.getIdRole().toString());

        return cbsUsersList;
    }

    @Override
    public CbsUsers findCbsUsersByCommentaires(Commentaires commentaires) {
        Optional<CbsUsers> cbsUsers = this.dao.getCbsUsersDao().findById(commentaires.getCbsUsers().getUserNum());

        if (!cbsUsers.isPresent())
            log.warn(
                    "La fonction 'findCbsUsersByCommentaires' n'a retournée aucun résultat. Id_commentaire:"
                            + commentaires.getIdCommentaire().toString());

        return cbsUsers.get();
    }

    @Override
    public CbsUsers findCbsUsersByDemandes(Demandes demandes) {
        Optional<CbsUsers> cbsUsers = this.dao.getCbsUsersDao().findById(demandes.getCbsUsers().getUserNum());

        if (!cbsUsers.isPresent())
            log.warn("La fonction 'findCbsUsersByDemandes' n'a retournée aucun résultat. Id_demande:"
                    + demandes.getIdDemande().toString());

        return cbsUsers.get();
    }

    @Override
    public CbsUsers findCbsUsersByJournalDemandes(JournalDemandes journalDemandes) {
        Optional<CbsUsers> cbsUsers = this.dao.getCbsUsersDao().findById(journalDemandes.getCbsUsers().getUserNum());

        if (!cbsUsers.isPresent())
            log.warn(
                    "La fonction 'findCbsUsersByJournalDemandes' n'a retournée aucun résultat. Id_journaldemande:"
                            + journalDemandes.getIdJournalDemande().toString());

        return cbsUsers.get();
    }

    @Override
    public CbsUsers findCbsUsersByPiecesJustificatives(PiecesJustificatives piecesJustificatives) {
        Optional<CbsUsers> cbsUsers = this.dao.getCbsUsersDao().findById(piecesJustificatives.getCbsUsers().getUserNum());

        if (!cbsUsers.isPresent())
            log.warn(
                    "La fonction 'findCbsUsersByPiecesJustificatives' n'a retournée aucun résultat. Id_piece:"
                            + piecesJustificatives.getIdPiece().toString());

        return cbsUsers.get();
    }

    @Override
    public CbsUsers findCbsUsersByOptions(Options options) {
        Optional<CbsUsers> cbsUsers = this.dao.getCbsUsersDao().findById(options.getCbsUsers().getUserNum());

        if (!cbsUsers.isPresent())
            log.warn("La fonction 'findCbsUsersByOptions' n'a retournée aucun résultat. Id_option:"
                    + options.getIdOption().toString());

        return cbsUsers.get();
    }

    @Override
    public CbsUsers findCbsUsersByUserKey(String userkey) {
        CbsUsers cbsUsers = this.dao.getCbsUsersDao().findCbsUsersByUserKey(userkey);

        if (cbsUsers == null)
            log.warn(
                    "La fonction 'findCbsUsersByUserKey' n'a retournée aucun résultat. userkey:" + userkey);

        return cbsUsers;
    }

    @Override
    public CbsUsers createUsersCbsFromUserRegistry(RegistryUser registryUser) {
        // On vérifie si l'utilisateur est dans la base XML
        if (registryUser == null) {
            return null;
        } else {
            // On teste la validité du login
            if (!registryUser.getLoginAllowed()) {
                return null;
            } else {
                // On récupère le role de l'utilisateur
                UsersRoles userRole = this.findUsersRolesByUserGroup(registryUser.getUserGroup());
                // On vérifie que l'utilisateur a un role autorisé par
                // l'appliaction
                if (userRole == null) {
                    return null;
                } else {
                    // L'utilisateur est dans la base XML, a un login valide,
                    // mais n'est pas dans la bdd Cidemis
                    // alors on l'ajoute à la bdd Cidemis
                    CbsUsers user = new CbsUsers(registryUser.getUserNum());
                    user.setUserKey(registryUser.getUserKey());
                    user.setUserGroup(registryUser.getUserGroup());
                    user.setShortName(registryUser.getShortName());
                    user.setEditDate(new java.util.Date());
                    user.setRoles(findRoles(userRole.getIdRole().getIdRole()));
                    user.setLibrary(registryUser.getLibrary());
                    user.setIln(registryUser.getIln());
                    Integer ilnRattache = jdbcTemplateDao.getIlnRattache(registryUser.getLibrary());
                    if (ilnRattache != null) {
                        user.setIlnRattache(ilnRattache.toString());
                    } else {
                        user.setIlnRattache(registryUser.getIln());
                    }
                    // On save l'utilisateur dans la bdd Cidemis
                    save(user);
                    user = findCbsUsers(user.getUserNum());
                    return user;
                }
            }
        }
    }

    @Override
    public CbsUsers updateUsersCbsFromUserRegistry(RegistryUser registryUser) {
        // On vérifie si l'utilisateur est dans la base XML
        if (registryUser == null) {
            return null;
        } else {
            // On teste la validité du login
            if (!registryUser.getLoginAllowed()) {
                return null;
            } else {
                // On récupère le role de l'utilisateur
                UsersRoles userRole = this.findUsersRolesByUserGroup(registryUser.getUserGroup());
                // On vérifie que l'utilisateur a un role autorisé par
                // l'appliaction
                if (userRole == null) {
                    return null;
                } else {
                    CbsUsers user = findCbsUsersByUserKey(registryUser.getUserKey());
                    Integer ilnRattache = jdbcTemplateDao.getIlnRattache(registryUser.getLibrary());
                    user.setUserGroup(registryUser.getUserGroup());
                    user.setShortName(registryUser.getShortName());
                    user.setLibrary(registryUser.getLibrary());
                    user.setEditDate(new java.util.Date());
                    user.setRoles(findRoles(userRole.getIdRole().getIdRole()));
                    user.setIln(registryUser.getIln());

                    if (ilnRattache != null) {
                        user.setIlnRattache(ilnRattache.toString());
                    } else {
                        user.setIlnRattache(registryUser.getIln());
                    }
                    save(user);

                    // On save l'utilisateur dans la bdd Cidemis
                    return user;
                }
            }
        }
    }

    @Override
    public Connexion login(RegistryUser registryUser) throws DaoException {
        // On récupère l'utilisateur dans la bdd Cidemis
        CbsUsers user = findCbsUsersByUserKey(registryUser.getUserKey());

        // On vérifie que l'utilisateur est dans la bdd cidemis
        if (user == null) {
            // On essaye de créer l'utilisateur dans la base Cidemis (avec vérif
            // du login et du role)
            user = createUsersCbsFromUserRegistry(registryUser);
        } else {
            // On essaye de mettre à jour l'utisateur dans la bdd Cidemis
            user = updateUsersCbsFromUserRegistry(registryUser);
        }

        // Si on n'a pas pu récupérer d'utilisateur on renvoie null
        if (user == null) {
            return null;
        }

        // On vérifie qu'il a une liste de colonnes définie dans ses options
        // Sinon on lui en créé une en fonction de son rôle
        service.getOptions().verifOptionsColonnesByCbsUsers(user);
        return new Connexion(user, registryUser);
    }

    /**
     * Services de la table Roles
     */
    @Override
    public Boolean save(Roles roles) {
        if (roles == null) {
            log.error("Erreur dans le parametre d'entrée , la variable 'roles' vaut 'null'.");
            return false;
        }

        try {
            this.dao.getRolesDao().save(roles);
            log.info("Sauvegarde: Roles. Id_role:" + roles.getIdRole().toString() + " OK");


            if (!saveCbsUsersListByRoles(roles))
                log.warn(
                        "Erreur dans l'ajout des Cbs_Userss. Roles. Id_role:" + roles.getIdRole().toString() + "'");
            else
                log.info(
                        "Les Cbs_Userss ont bien été ajouté(e)s .Roles. Id_role:" + roles.getIdRole().toString() + "'");

            return true;
        } catch (Exception ex) {
            log.error("function save(). Roles. Id_role:" + roles.getIdRole().toString(), ex);
            return false;
        }
    }

    @Override
    public boolean saveCbsUsersListByRoles(Roles roles) {
        List<CbsUsers> cbsUsersList = roles.getCbsUsersList();
        List<CbsUsers> cbsUsersBDDList = this.dao.getCbsUsersDao().findCbsUsersByRoles(roles);
        Boolean find;

        for (CbsUsers cbs_usersBDD : cbsUsersBDDList) {
            find = false;

            for (CbsUsers cbs_users : cbsUsersList)
                find |= cbs_usersBDD.getUserNum().equals(cbs_users.getUserNum());

            if (!find)
                this.dao.getCbsUsersDao().delete(cbs_usersBDD);
        }

        Boolean success = true;
        for (CbsUsers cbs_users : cbsUsersList)
            success &= save(cbs_users);

        return success;
    }

    @Override
    public Boolean delete(Roles roles) {
        if (roles == null) {
            log.error("Erreur dans le parametre d'entrée , la variable 'roles' vaut 'null'.");
            return false;
        }

        try {
            this.dao.getRolesDao().delete(roles);
            return true;
        } catch (Exception ex) {
            log.error("function delete(). Roles. Id_role:" + roles.getIdRole().toString(), ex);
            return false;
        }
    }

    @Override
    public Roles findRoles(Integer idRole) {
        Optional<Roles> roles = this.dao.getRolesDao().findById(idRole);

        if (!roles.isPresent())
            log.warn("La fonction 'findRoles' n'a retournée aucun résultat. Id_role:" + idRole);

        return roles.get();
    }

    @Override
    public List<Roles> findAllRoles() {
        List<Roles> rolesList = this.dao.getRolesDao().findAll();

        if (rolesList.isEmpty())
            log.warn("La fonction 'findAllRoles' n'a retournée aucun résultat.");

        return rolesList;
    }

    @Override
    public Roles findRolesByCbsUsers(CbsUsers cbsUsers) {
        Optional<Roles> roles = this.dao.getRolesDao().findById(cbsUsers.getRoles().getIdRole());

        if (!roles.isPresent())
            log.warn("La fonction 'findRolesByCbs_Users' n'a retournée aucun résultat. User_num:"
                    + cbsUsers.getUserNum().toString());

        return roles.get();
    }

    @Override
    public Boolean save(UsersRoles usersRoles) {
        if (usersRoles == null) {
            log.error("Erreur dans le parametre d'entrée , la variable 'users_roles' vaut 'null'.");
            return false;
        }
        try {
            this.dao.getUsersRolesDao().save(usersRoles);
            log.info(
                    "Sauvegarde: Users_Roles. Id_users_roles:" + usersRoles.getIdUsersRoles().toString() + " OK");

            return true;
        } catch (Exception ex) {
            log.error(
                    "function save(). Users_Roles. Id_users_roles:" + usersRoles.getIdUsersRoles().toString(), ex);
            return false;
        }
    }

    @Override
    public Boolean delete(UsersRoles usersRoles) {
        if (usersRoles == null) {
            log.error("Erreur dans le parametre d'entrée , la variable 'users_roles' vaut 'null'.");
            return false;
        }
        try {
            this.dao.getUsersRolesDao().delete(usersRoles);
            return true;
        } catch (Exception ex) {
            log.error(
                    "function delete(). Users_Roles. Id_users_roles:" + usersRoles.getIdUsersRoles().toString(), ex);
            return false;
        }
    }

    @Override
    public UsersRoles findUsersRoles(Integer idUsersRoles) {
        Optional<UsersRoles> usersRoles = this.dao.getUsersRolesDao().findById(idUsersRoles);

        if (!usersRoles.isPresent())
            log.warn(
                    "La fonction 'findUsers_Roles' n'a retournée aucun résultat. Id_users_roles:" + idUsersRoles);

        return usersRoles.get();
    }

    @Override
    public UsersRoles findUsersRolesByUserGroup(String userGroup) {
        List<UsersRoles> usersRoles = this.dao.getUsersRolesDao().findUsersRolesByUserGroup(userGroup);
        if (usersRoles.isEmpty())
            log.warn(
                    "La fonction 'findUsers_RolesByUser_Group' n'a retournée aucun résultat. user_group:" + userGroup);
        return usersRoles.get(0);
    }

    @Override
    public List<UsersRoles> findAllUsersRoles() {
        List<UsersRoles> usersRolesList = this.dao.getUsersRolesDao().findAll();

        if (usersRolesList.isEmpty())
            log.warn("La fonction 'findAllUsers_Roles' n'a retournée aucun résultat.");

        return usersRolesList;
    }

    @Override
    public List<CbsUsers> findCbsUsersToSendEmail(Demandes demande, Roles roles) {
        List<CbsUsers> cbsUsersList = new ArrayList<>();
        if (roles.getIdRole().equals(Constant.ROLE_CATALOGUEUR)) {
            //si on envoie le mail au catalogueur, il s'agit du créateur de la demande
            cbsUsersList.add(demande.getCbsUsers());
        } else {
            //si on envoie le mail au responsable CR on récupère l'utilisateur du groupe cbs crcat dont l'iln rattaché correspond à l'iln rattache du centre issn de la demande
            if (roles.getIdRole().equals(Constant.ROLE_RESPONSABLE_CR))
                cbsUsersList.addAll(dao.getCbsUsersDao().findAllByIlnRattacheAndUserGroup(dao.getCrIlnDao().findAllByCr(demande.getCr()).getIln(), "crcat"));
        }
        return cbsUsersList;
    }
}

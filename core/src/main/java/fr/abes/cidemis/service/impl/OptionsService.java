package fr.abes.cidemis.service.impl;

import fr.abes.cidemis.dao.cidemis.CidemisDaoProvider;
import fr.abes.cidemis.exception.DaoException;
import fr.abes.cidemis.model.cidemis.CbsUsers;
import fr.abes.cidemis.model.cidemis.Options;
import fr.abes.cidemis.model.cidemis.OptionsRoles;
import fr.abes.cidemis.model.cidemis.Roles;
import fr.abes.cidemis.service.IOptionsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class OptionsService implements IOptionsService {
    @Autowired
    private CidemisDaoProvider dao;

    @Override
    public Boolean save(Options options) {
        if (options == null) {
            log.error("Erreur dans le parametre d'entrée , la variable 'options' vaut 'null'.");
            return false;
        }
        try {
            this.dao.getOptionsDao().save(options);
            log.info("Sauvegarde: Options. Id_option:" + options.getIdOption().toString() + " OK");

            return true;
        } catch (Exception ex) {
            log.error("function save(). Options. Id_option:" + options.getIdOption().toString(), ex);
            return false;
        }
    }

    @Override
    public Boolean delete(Options options) {
        if (options == null) {
            log.error("Erreur dans le parametre d'entrée , la variable 'options' vaut 'null'.");
            return false;
        }
        try {
            this.dao.getOptionsDao().delete(options);
            return true;
        } catch (Exception ex) {
            log.error("function delete(). Options. Id_option:" + options.getIdOption().toString(), ex);
            return false;
        }
    }

    @Override
    public Options findOptions(Integer idOption) {
        Optional<Options> options = this.dao.getOptionsDao().findById(idOption);

        if (!options.isPresent())
            log.debug("La fonction 'findOptions' n'a retournée aucun résultat. Id_option:" + idOption);

        return options.get();
    }

    @Override
    public List<Options> findAllOptions() {
        List<Options> optionsList = this.dao.getOptionsDao().findAll();

        if (optionsList.isEmpty())
            log.debug("La fonction 'findAllOptions' n'a retournée aucun résultat.");

        return optionsList;
    }

    @Override
    public List<Options> findOptionsByCbsUsers(CbsUsers cbsUsers) {
        List<Options> optionsList = this.dao.getOptionsDao().findOptionsByCbsUsers(cbsUsers);

        if (optionsList.isEmpty())
            log.debug("La fonction 'findOptionsByCbs_Users' n'a retournée aucun résultat. User_num:"
                    + cbsUsers.getUserNum().toString());

        return optionsList;
    }

    @Override
    public List<Options> findOptionsColonnesByCbsUsers(CbsUsers cbsUsers)  {
        List<Options> optionsList = this.dao.getOptionsDao().findOptionsColonnesByCbsUsers(cbsUsers);

        return optionsList;
    }


    @Override
    public void verifOptionsColonnesByCbsUsers(CbsUsers cbsUsers) throws DaoException {
        List<Options> optionsUser = this.findOptionsColonnesByCbsUsers(cbsUsers);
        List<OptionsRoles> optionsDefaut = this.findOptionsRolesByRoles(cbsUsers.getRoles());

        if (optionsUser.isEmpty()) {
            createListOptionsFromDefault(optionsDefaut, cbsUsers);
        } else {
            updateListOptions(optionsUser, optionsDefaut, cbsUsers);
        }
    }

    /**
     * Sauvegarde des options de l'utilisateur à partir des options par défaut pour son role
     * @param optionsDefaut
     * @param cbsUsers
     */
    private void createListOptionsFromDefault(List<OptionsRoles> optionsDefaut, CbsUsers cbsUsers) {
        for (OptionsRoles option_defaut : optionsDefaut) {
            Options option = new Options(option_defaut);
            option.setCbsUsers(cbsUsers);
            save(option);
        }
    }

    private void updateListOptions(List<Options> optionsUser, List<OptionsRoles> optionsDefaut, CbsUsers cbsUsers) {
        deleteOptionsUserFromDefault(optionsDefaut, optionsUser);
        createOptionsUserFromDefault(optionsUser, optionsDefaut, cbsUsers);
    }

    private void deleteOptionsUserFromDefault(List<OptionsRoles> optionsDefaut, List<Options> optionsUser) {
        Iterator<Options> it = optionsUser.iterator();
        Options optionUser;
        OptionsRoles optionsRoles;
        // On recherche les options "en trop" dans la config de l'user. On
        // supprime celles que l'on trouve
        while (it.hasNext()) {
            optionUser = it.next();
            optionsRoles = null;

            for (OptionsRoles opt_def : optionsDefaut) {
                if (optionUser.getLibOption().equals(opt_def.getLibOption())) {
                    optionsRoles = opt_def;
                    break;
                }
            }

            if (optionsRoles == null) {
                delete(optionUser);
            }
        }
    }

    private void createOptionsUserFromDefault(List<Options> optionsUser, List<OptionsRoles> optionsDefaut, CbsUsers cbsUsers) {
        Options optionUser;
        // On recherche les options "manquantes" dans la config de l'user.
        // On ajoute celles qui manquent
        for (OptionsRoles opt_def : optionsDefaut) {
            optionUser = null;

            for (Options opt : optionsUser) {
                if (opt_def.getLibOption().equals(opt.getLibOption())) {
                    optionUser = opt;
                    break;
                }
            }

            if (optionUser == null) {
                optionUser = new Options(opt_def);
                optionUser.setCbsUsers(cbsUsers);
                save(optionUser);
            }
        }
    }

    @Override
    public Boolean save(OptionsRoles optionsRoles) {
        if (optionsRoles == null) {
            log.error("Erreur dans le parametre d'entrée , la variable 'options_roles' vaut 'null'.");
            return false;
        }
        try {
            this.dao.getOptionsRolesDao().save(optionsRoles);
            log.info("Sauvegarde: Options_Roles. Id_options_roles:"
                    + optionsRoles.getIdOptionsRoles().toString() + " OK");
            return true;
        } catch (Exception ex) {
            log.error(
                    "function save(). Options_Roles. Id_options_roles:" + optionsRoles.getIdOptionsRoles().toString(),
                    ex);
            return false;
        }
    }

    @Override
    public Boolean delete(OptionsRoles optionsRoles) {
        if (optionsRoles == null) {
            log.error("Erreur dans le parametre d'entrée , la variable 'options_roles' vaut 'null'.");
            return false;
        }

        try {
            this.dao.getOptionsRolesDao().delete(optionsRoles);
            return true;
        } catch (Exception ex) {
            log.error(
                    "function delete(). Options_Roles. Id_options_roles:" + optionsRoles.getIdOptionsRoles().toString(),
                    ex);
            return false;
        }
    }

    @Override
    public OptionsRoles findOptionsRoles(Integer idOptionsRoles) {
        Optional<OptionsRoles> optionsRoles = this.dao.getOptionsRolesDao().findById(idOptionsRoles);

        if (!optionsRoles.isPresent())
            log.debug(
                    "La fonction 'findOptions_Roles' n'a retournée aucun résultat. Id_options_roles:" + idOptionsRoles);

        return optionsRoles.get();
    }

    @Override
    public List<OptionsRoles> findAllOptionsRoles() {
        List<OptionsRoles> optionsRolesList = this.dao.getOptionsRolesDao().findAll();

        if (optionsRolesList.isEmpty())
            log.debug("La fonction 'findAllOptions_Roles' n'a retournée aucun résultat.");

        return optionsRolesList;
    }

    @Override
    public List<OptionsRoles> findOptionsRolesByRoles(Roles roles) {
        List<OptionsRoles> optionsRolesList = this.dao.getOptionsRolesDao().findAllByRoles(roles);

        if (optionsRolesList.isEmpty())
            log.debug("La fonction 'findOptionsRolesByRoles' n'a retournée aucun résultat. Idrole:"
                    + roles.getIdRole().toString());

        return optionsRolesList;
    }

}

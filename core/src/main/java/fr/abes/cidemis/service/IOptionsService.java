package fr.abes.cidemis.service;

import fr.abes.cidemis.exception.DaoException;
import fr.abes.cidemis.model.cidemis.CbsUsers;
import fr.abes.cidemis.model.cidemis.Options;
import fr.abes.cidemis.model.cidemis.OptionsRoles;
import fr.abes.cidemis.model.cidemis.Roles;

import java.util.List;

public interface IOptionsService {
    Boolean save(Options options);

    Boolean delete(Options options);

    Options findOptions(Integer idOption);

    List<Options> findAllOptions();

    List<Options> findOptionsByCbsUsers(CbsUsers cbsUsers);

    List<Options> findOptionsColonnesByCbsUsers(CbsUsers cbsUsers) throws DaoException;

    void verifOptionsColonnesByCbsUsers(CbsUsers cbsUsers) throws DaoException;

    Boolean save(OptionsRoles optionsRoles);

    Boolean delete(OptionsRoles optionsRoles);

    OptionsRoles findOptionsRoles(Integer idOptionsRoles);

    List<OptionsRoles> findAllOptionsRoles();

    List<OptionsRoles> findOptionsRolesByRoles(Roles roles);
}

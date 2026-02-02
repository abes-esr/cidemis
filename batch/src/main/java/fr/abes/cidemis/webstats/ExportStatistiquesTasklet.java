package fr.abes.cidemis.webstats;

import fr.abes.cidemis.webstats.correspondence.etat.DemandeEtat;
import fr.abes.cidemis.webstats.correspondence.type.DemandeType;
import fr.abes.cidemis.webstats.correspondence.typepublication.DemandeTypePublication;
import fr.abes.cidemis.webstats.correspondence.typesupport.DemandeTypeSupport;
import fr.abes.cidemis.webstats.correspondence.zone.Zone;
import fr.abes.cidemis.webstats.statsetablissement.demandescorrection.EtabDemandesCorrection;
import fr.abes.cidemis.webstats.statsetablissement.demandesdonewithcriteria.EtabDemandesDoneWithCriteria;
import fr.abes.cidemis.webstats.statsetablissement.demandesissnactivity.EtabDemandesISSNActivity;
import fr.abes.cidemis.webstats.statsetablissement.demandesissnstate.EtabDemandesISSNState;
import fr.abes.cidemis.webstats.statsetablissement.demandesissnwithcriteriaactivity.EtabDemandesISSNWithCriteriaActivity;
import fr.abes.cidemis.webstats.statsetablissement.demandesissnwithcriteriadtate.EtabDemandesISSNWithCriteriaState;
import fr.abes.cidemis.webstats.statsgeneral.demandesdonedelay.DemandesDoneDelay;
import fr.abes.cidemis.webstats.statsgeneral.demandesdonewithcriteria.DemandesDoneWithCriteria;
import fr.abes.cidemis.webstats.statsgeneral.demandesissn.DemandesISSN;
import fr.abes.cidemis.webstats.statsgeneral.demandesissnwithcriteria.DemandesISSNWithCriteria;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ExportStatistiquesTasklet implements Tasklet, StepExecutionListener {

    protected JdbcTemplate jdbcTemplate;

    @Value("${path.statistiques}")
    private String uploadPath;

    private Integer annee;
    private Integer mois;

    public ExportStatistiquesTasklet(@Qualifier("cidemisJdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void beforeStep(StepExecution stepExecution) {
        annee = (Integer) stepExecution.getJobExecution().getExecutionContext().get("annee");
        mois = (Integer) stepExecution.getJobExecution().getExecutionContext().get("mois");

    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        return stepExecution.getExitStatus();
    }

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
        // Exports des référentiels
        DemandeType demandeType = new DemandeType(jdbcTemplate);
        demandeType.generate(getFileName("correspondence_demandeType.csv"), getDate());

        DemandeEtat demandeEtat = new DemandeEtat(jdbcTemplate);
        demandeEtat.generate(getFileName("correspondence_demandeEtat.csv"), getDate());

        Zone zone = new Zone(jdbcTemplate);
        zone.generate(getFileName("correspondence_zone.csv"), getDate());

        DemandeTypeSupport typeSupport = new DemandeTypeSupport(jdbcTemplate);
        typeSupport.generate(getFileName("correspondence_demandeTypeSupport.csv"), getDate());

        DemandeTypePublication typePublication = new DemandeTypePublication(jdbcTemplate);
        typePublication.generate(getFileName("correspondence_demandeTypePublication.csv"), getDate());

        // Exports des statistiques généralistes
        DemandesISSN demandesISSN = new DemandesISSN(jdbcTemplate);
        demandesISSN.generate(getFileName("general_demandesISSN.csv"), getDate());

        DemandesISSNWithCriteria demandesISSNWithCriteria = new DemandesISSNWithCriteria(jdbcTemplate);
        demandesISSNWithCriteria.generate(getFileName("general_demandesISSNWithCriteria.csv"), getDate());

        DemandesDoneWithCriteria demandesDoneWithCriteria = new DemandesDoneWithCriteria(jdbcTemplate);
        demandesDoneWithCriteria.generate(getFileName("general_demandesDoneWithCriteria.csv"), getDate());

        DemandesDoneDelay demandesDoneDelay = new DemandesDoneDelay(jdbcTemplate);
        demandesDoneDelay.generate(getFileName("general_demandesDoneDelay.csv"), getDate());

        // Exports des statistiques établissement
        EtabDemandesISSNState demandesISSNEtab = new EtabDemandesISSNState(jdbcTemplate);
        demandesISSNEtab.generate(getFileName("etab_demandesISSNState.csv"), getDate());

        EtabDemandesISSNActivity demandesISSNActivity = new EtabDemandesISSNActivity(jdbcTemplate);
        demandesISSNActivity.generate(getFileName("etab_demandesISSNActivity.csv"), getDate());

        EtabDemandesISSNWithCriteriaState demandesISSNWithCriteriaEtabState = new EtabDemandesISSNWithCriteriaState(jdbcTemplate);
        demandesISSNWithCriteriaEtabState.generate(getFileName("etab_demandesISSNWithCriteriaState.csv"), getDate());

        EtabDemandesISSNWithCriteriaActivity demandesISSNWithCriteriaEtabActivity = new EtabDemandesISSNWithCriteriaActivity(jdbcTemplate);
        demandesISSNWithCriteriaEtabActivity.generate(getFileName("etab_demandesISSNWithCriteriaActivity.csv"), getDate());

        EtabDemandesCorrection demandesCorrectionEtab = new EtabDemandesCorrection(jdbcTemplate);
        demandesCorrectionEtab.generate(getFileName("etab_demandesCorrection.csv"), getDate());

        EtabDemandesDoneWithCriteria demandesDoneWithCriteriaEtab = new EtabDemandesDoneWithCriteria(jdbcTemplate);
        demandesDoneWithCriteriaEtab.generate(getFileName("etab_demandesDoneWithCriteria.csv"), getDate());

        return RepeatStatus.FINISHED;
    }

    /**
     * Je ne fois pas enlever un mois, car ici je fais le mois et l'année passé -01, et donc j'aurai les stats de cette date + 1 mois
     * @return the current date
     * @throws ParseException
     */
    private Date getDate() throws ParseException {
        SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd");
        return formater.parse(this.annee + "-" + this.mois + "-01"); }

    private String getFileName(String fileName) {
        return uploadPath + this.annee + ((this.mois < 10) ? '0' + this.mois.toString() : this.mois.toString()) + "_" + fileName;
    }

}

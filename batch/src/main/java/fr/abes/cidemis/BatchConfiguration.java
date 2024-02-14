package fr.abes.cidemis;

import fr.abes.cidemis.ajoutRefus.MajSudocTasklet;
import fr.abes.cidemis.ajoutRefus.SelectDemandesRefusTasklet;
import fr.abes.cidemis.constant.Constant;
import fr.abes.cidemis.mailing.EnvoiMailTasklet;
import fr.abes.cidemis.mailing.SelectDemandesTasklet;
import fr.abes.cidemis.webstats.ExportStatistiquesTasklet;
import fr.abes.cidemis.webstats.VerifierParamsTasklet;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersIncrementer;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.*;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.persistence.EntityManagerFactory;

@Slf4j
@Configuration
@EnableBatchProcessing
public class BatchConfiguration {
    @Autowired
    protected JobBuilderFactory jobs;

    @Autowired
    protected StepBuilderFactory stepBuilderFactory;

    @Autowired
    @Qualifier("cidemisJdbcTemplate")
    protected JdbcTemplate jdbcTemplate;


    @Bean
    public BatchConfigurer configurer(EntityManagerFactory entityManagerFactory){
        return new CidemisBatchConfigurer(entityManagerFactory);
    }

    // Job d'export des statistiques mensuelles
    @Bean
    public Job jobExportStatistiques() {
        return jobs
                .get(Constant.SPRING_BATCH_JOB_EXPORT_STATISTIQUES_NAME).incrementer(incrementer())
                .start(stepVerifierParams()).on(Constant.FAILED).end()
                .from(stepVerifierParams()).on(Constant.COMPLETED).to(stepExportStatistiques())
                .build().build();
    }

    @Bean
    public Job jobMailing() {
        return jobs
                .get(Constant.SPRING_BATCH_JOB_MAILING).incrementer(incrementer())
                .start(stepSelectDemandes()).on(Constant.NODEMANDE).end()
                .from(stepSelectDemandes()).on(Constant.COMPLETED).to(stepEnvoiMail())
                .build().build();
    }

    @Bean
    public Job jobRefus() {
        return jobs
                .get(Constant.SPRING_BATCH_JOB_REFUS).incrementer(incrementer())
                .start(stepSelectDemandesRefus()).on(Constant.NODEMANDE).end()
                .from(stepSelectDemandesRefus()).on(Constant.COMPLETED).to(stepMajSudoc())
                .build().build();
    }

    // Steps pour exports statistiques
    @Bean
    public Step stepVerifierParams() {
        return stepBuilderFactory
                .get("stepVerifierParams").allowStartIfComplete(true)
                .tasklet(verifierParamsTasklet())
                .build();
    }

    @Bean
    public Step stepExportStatistiques() {
        return stepBuilderFactory
                .get("stepExportStatistiques").allowStartIfComplete(true)
                .tasklet(exportStatistiquesTasklet())
                .build();
    }

    //Steps pour mailing CIEPS
    @Bean
    @JobScope
    public Step stepSelectDemandes() {
        return stepBuilderFactory.get("stepSelectDemande").allowStartIfComplete(true)
                .tasklet(selectDemandesTasklet())
                .build();
    }

    @Bean
    @JobScope
    public Step stepEnvoiMail() {
        return stepBuilderFactory.get("stepEnvoiMail").allowStartIfComplete(true)
                .tasklet(envoiMailTasklet())
                .build();
    }

    //Steps pour job Ajout des commentaires de refus
    @Bean
    public Step stepSelectDemandesRefus() {
        return stepBuilderFactory.get("stepSelectDemandeRefus").allowStartIfComplete(true)
                .tasklet(selectDemandesRefusTasklet())
                .build();
    }

    @Bean
    public Step stepMajSudoc() {
        return stepBuilderFactory.get("stepMajSudoc").allowStartIfComplete(true)
                .tasklet(majSudocTasklet())
                .build();
    }




    //tasklet Statistiques
    @Bean
    public VerifierParamsTasklet verifierParamsTasklet() { return new VerifierParamsTasklet(); }

    @Bean
    public ExportStatistiquesTasklet exportStatistiquesTasklet() { return new ExportStatistiquesTasklet(jdbcTemplate); }

    //tasklets envoi Mail CIEPS
    @Bean
    public SelectDemandesTasklet selectDemandesTasklet() { return new SelectDemandesTasklet(); }

    @Bean
    public EnvoiMailTasklet envoiMailTasklet() { return new EnvoiMailTasklet(); }

    //tasklets ajout commentaires refus
    @Bean
    public Tasklet selectDemandesRefusTasklet() { return new SelectDemandesRefusTasklet(); }

    @Bean
    public Tasklet majSudocTasklet() { return new MajSudocTasklet(); }

    // ------------------ INCREMENTER ------------------
    protected JobParametersIncrementer incrementer() {
        return new TimeIncrementer();
    }

}

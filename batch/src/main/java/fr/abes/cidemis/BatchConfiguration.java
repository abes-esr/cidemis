package fr.abes.cidemis;

import fr.abes.cidemis.ajoutRefus.MajSudocTasklet;
import fr.abes.cidemis.ajoutRefus.SelectDemandesRefusTasklet;
import fr.abes.cidemis.constant.Constant;
import fr.abes.cidemis.mailing.DemandesDto;
import fr.abes.cidemis.mailing.EnvoiMailTasklet;
import fr.abes.cidemis.mailing.SelectDemandesTasklet;
import fr.abes.cidemis.webstats.ExportStatistiquesTasklet;
import fr.abes.cidemis.webstats.VerifierParamsTasklet;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersIncrementer;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.support.DefaultBatchConfiguration;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.dao.Jackson2ExecutionContextStringSerializer;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
@EnableBatchProcessing(
        dataSourceRef = "cidemisDataSource",
        transactionManagerRef = "cidemisTransactionManager",
        executionContextSerializerRef = "batchExecutionContextSerializer"
)
public class BatchConfiguration extends DefaultBatchConfiguration {
    @Autowired
    @Qualifier("cidemisJdbcTemplate")
    protected JdbcTemplate jdbcTemplate;

    @Bean
    public Jackson2ExecutionContextStringSerializer batchExecutionContextSerializer() {
        return new Jackson2ExecutionContextStringSerializer(DemandesDto.class.getName());
    }

    @Override
    protected int getMaxVarCharLength() {
        return 1000;
    }

    @Override
    protected boolean getValidateTransactionState() {
        return false;
    }

    // Job d'export des statistiques mensuelles
    @Bean
    public Job jobExportStatistiques(JobRepository jobRepository, Step stepVerifierParams, Step stepExportStatistiques) {
        return new JobBuilder(Constant.SPRING_BATCH_JOB_EXPORT_STATISTIQUES_NAME, jobRepository)
                .incrementer(incrementer())
                .start(stepVerifierParams).on(Constant.FAILED).end()
                .from(stepVerifierParams).on(Constant.COMPLETED).to(stepExportStatistiques)
                .end()
                .build();
    }

    @Bean
    public Job jobMailing(JobRepository jobRepository, Step stepSelectDemandes, Step stepEnvoiMail) {
        return new JobBuilder(Constant.SPRING_BATCH_JOB_MAILING, jobRepository)
                .incrementer(incrementer())
                .start(stepSelectDemandes).on(Constant.NODEMANDE).end()
                .from(stepSelectDemandes).on(Constant.COMPLETED).to(stepEnvoiMail)
                .end()
                .build();
    }

    @Bean
    public Job jobRefus(JobRepository jobRepository, Step stepSelectDemandesRefus, Step stepMajSudoc) {
        return new JobBuilder(Constant.SPRING_BATCH_JOB_REFUS, jobRepository)
                .incrementer(incrementer())
                .start(stepSelectDemandesRefus).on(Constant.NODEMANDE).end()
                .from(stepSelectDemandesRefus).on(Constant.COMPLETED).to(stepMajSudoc)
                .end()
                .build();
    }

    // Steps pour exports statistiques
    @Bean
    public Step stepVerifierParams(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("stepVerifierParams", jobRepository)
                .allowStartIfComplete(true)
                .tasklet(verifierParamsTasklet(), transactionManager)
                .build();
    }

    @Bean
    public Step stepExportStatistiques(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("stepExportStatistiques", jobRepository)
                .allowStartIfComplete(true)
                .tasklet(exportStatistiquesTasklet(), transactionManager)
                .build();
    }

    //Steps pour mailing CIEPS
    @Bean
    @JobScope
    public Step stepSelectDemandes(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("stepSelectDemande", jobRepository)
                .allowStartIfComplete(true)
                .tasklet(selectDemandesTasklet(), transactionManager)
                .build();
    }

    @Bean
    @JobScope
    public Step stepEnvoiMail(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("stepEnvoiMail", jobRepository)
                .allowStartIfComplete(true)
                .tasklet(envoiMailTasklet(), transactionManager)
                .build();
    }

    //Steps pour job Ajout des commentaires de refus
    @Bean
    public Step stepSelectDemandesRefus(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("stepSelectDemandeRefus", jobRepository)
                .allowStartIfComplete(true)
                .tasklet(selectDemandesRefusTasklet(), transactionManager)
                .build();
    }

    @Bean
    public Step stepMajSudoc(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("stepMajSudoc", jobRepository)
                .allowStartIfComplete(true)
                .tasklet(majSudocTasklet(), transactionManager)
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

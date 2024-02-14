package fr.abes.cidemis;

import fr.abes.cidemis.constant.Constant;
import fr.abes.cidemis.mailing.DemandesDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.BatchConfigurer;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.explore.support.JobExplorerFactoryBean;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.ExecutionContextSerializer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.dao.Jackson2ExecutionContextStringSerializer;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.batch.support.DatabaseType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@Slf4j
public class CidemisBatchConfigurer implements BatchConfigurer {
    private final EntityManagerFactory entityManagerFactory;

    private PlatformTransactionManager transactionManager;

    private JobRepository jobRepository;

    private JobLauncher jobLauncher;

    private JobExplorer jobExplorer;

    @Autowired
    private DataSource cidemisDataSource;

    public CidemisBatchConfigurer(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }


    public ExecutionContextSerializer customSerializer() {
        return new Jackson2ExecutionContextStringSerializer(DemandesDto.class.getName());
    }

    @Override
    public JobRepository getJobRepository() throws Exception {
        return this.jobRepository;
    }

    @Override
    public PlatformTransactionManager getTransactionManager() throws Exception {
        return this.transactionManager;
    }

    @Override
    public JobLauncher getJobLauncher() throws Exception {
        return this.jobLauncher;
    }

    @Override
    public JobExplorer getJobExplorer() throws Exception {
        return this.jobExplorer;
    }

    @PostConstruct
    public void initialize() {
        try {
            log.warn(Constant.SPRING_BATCH_FORCING_USAGE_JPA_TRANSACTION_MANAGER);
            if (this.entityManagerFactory == null) {
                log.error(Constant.SPRING_BATCH_ENTITY_MANAGER_FACTORY_NULL);
            } else {
                this.transactionManager = new JpaTransactionManager(this.entityManagerFactory);
            }
            // jobRepository:
            JobRepositoryFactoryBean factory = new JobRepositoryFactoryBean();
            factory.setDataSource(cidemisDataSource);
            factory.setTransactionManager(transactionManager);
            factory.setDatabaseType(DatabaseType.ORACLE.getProductName());
            factory.setIsolationLevelForCreate("ISOLATION_DEFAULT");
            factory.setTablePrefix("BATCH_");
            factory.setMaxVarCharLength(1000);
            factory.setValidateTransactionState(false);
            factory.setSerializer(customSerializer());
            this.jobRepository = factory.getObject();

            // jobLauncher:
            SimpleJobLauncher jobLauncherParam = new SimpleJobLauncher();
            jobLauncherParam.setJobRepository(getJobRepository());
            jobLauncherParam.setTaskExecutor(new SyncTaskExecutor());
            jobLauncherParam.afterPropertiesSet();
            this.jobLauncher = jobLauncherParam;

            // jobExplorer:
            JobExplorerFactoryBean jobExplorerFactory = new JobExplorerFactoryBean();
            jobExplorerFactory.setDataSource(cidemisDataSource);
            jobExplorerFactory.setTablePrefix("BATCH_");
            jobExplorerFactory.afterPropertiesSet();
            jobExplorerFactory.setSerializer(customSerializer());
            this.jobExplorer = jobExplorerFactory.getObject();
        }
        catch (Exception ex) {
            throw new IllegalStateException(Constant.SPRING_BATCH_INITIALIZATION_FAILED, ex);
        }
    }
}

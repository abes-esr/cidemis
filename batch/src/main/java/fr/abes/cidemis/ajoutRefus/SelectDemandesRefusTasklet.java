package fr.abes.cidemis.ajoutRefus;

import fr.abes.cidemis.constant.Constant;
import fr.abes.cidemis.model.cidemis.Demandes;
import fr.abes.cidemis.service.CidemisManageService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.Date;
import java.util.List;

@Slf4j
public class SelectDemandesRefusTasklet implements Tasklet, StepExecutionListener {
    @Value("#{new java.text.SimpleDateFormat('dd/MM/yyyy').parse('${date.borneInf}')}")
    private Date borneInf;
    @Value("#{new java.text.SimpleDateFormat('dd/MM/yyyy').parse('${date.borneSup}')}")
    private Date borneSup;
    @Autowired
    @Getter
    private CidemisManageService service;

    List<Demandes> demandes;

    @Override
    public void beforeStep(StepExecution stepExecution) {

    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        if (stepExecution.getExitStatus().equals(ExitStatus.COMPLETED)) {
            stepExecution.getJobExecution().getExecutionContext().put("demandes", this.demandes);
        }
        return stepExecution.getExitStatus();
    }

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
        log.info(Constant.ENTER_EXECUTE_FROM_SELECTDEMANDESREFUSTASKLET);
        demandes = service.getDemande().findDemandesForUpdateRefus(borneInf, borneSup);
        if (demandes.isEmpty()) {
            log.warn(Constant.NO_DEMANDE_TO_PROCESS);
            stepContribution.setExitStatus(new ExitStatus(Constant.NODEMANDE));
            return RepeatStatus.FINISHED;
        }
        return RepeatStatus.FINISHED;
    }
}

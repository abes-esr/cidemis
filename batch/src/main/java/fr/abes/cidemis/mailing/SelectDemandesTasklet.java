package fr.abes.cidemis.mailing;

import fr.abes.cidemis.constant.Constant;
import fr.abes.cidemis.model.cidemis.Demandes;
import fr.abes.cidemis.service.CidemisManageService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@JobScope
public class SelectDemandesTasklet implements Tasklet, StepExecutionListener {
    @Autowired
    @Getter
    private CidemisManageService service;
    @Autowired
    private DemandeMapper mapper;
    Map<String, List<DemandesDto>> mapDemandesByEmail;

    @Override
    public void beforeStep(StepExecution stepExecution) {

    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        if (stepExecution.getExitStatus().equals(ExitStatus.COMPLETED)) {
            stepExecution.getJobExecution().getExecutionContext().put("demandes", this.mapDemandesByEmail);
        }
        return stepExecution.getExitStatus();
    }

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) {
        log.info(Constant.ENTER_EXECUTE_FROM_SELECTDEMANDESTASKLET);
        List<Demandes> demandes = service.getDemande().findDemandesMailingCiepsForMailing();
        if (demandes.isEmpty()) {
            log.warn(Constant.NO_DEMANDE_TO_PROCESS);
            stepContribution.setExitStatus(new ExitStatus(Constant.NODEMANDE));
            return RepeatStatus.FINISHED;
        }
        List<DemandesDto> demandesdto = mapper.mapList(demandes, DemandesDto.class);
        this.mapDemandesByEmail = new HashMap<>();
        List<DemandesDto> demandesByEmail;

        // On classe les demandes par adresse e-mail
        for (DemandesDto demande : demandesdto) {
            if (this.mapDemandesByEmail.containsKey(demande.getEmail())) {
                this.mapDemandesByEmail.get(demande.getEmail()).add(demande);
            } else {
                demandesByEmail = new ArrayList<>();
                demandesByEmail.add(demande);
                mapDemandesByEmail.put(demande.getEmail(), demandesByEmail);
            }
        }
        return RepeatStatus.FINISHED;
    }
}

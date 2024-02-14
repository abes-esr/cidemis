package fr.abes.cidemis.webstats;

import fr.abes.cidemis.LogTime;
import fr.abes.cidemis.constant.Constant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

@Slf4j
public class VerifierParamsTasklet implements Tasklet, StepExecutionListener {
    private Integer annee;
    private Integer mois;

    @Override
    public void beforeStep(StepExecution stepExecution) {
        LogTime.logDebutTraitement(stepExecution);
        if (System.getProperty(Constant.ANNEE) != null && System.getProperty("mois") != null) {
            this.annee = Integer.parseInt(System.getProperty(Constant.ANNEE));
            this.mois = Integer.parseInt(System.getProperty("mois"));
        }
        else {
            //cas où le batch est appelé sans paramètre
            //on calcule le mois et l'année en fonction de la date courante
            Calendar dateJour = Calendar.getInstance();
            this.annee = dateJour.get(Calendar.YEAR);
            this.mois = dateJour.get(Calendar.MONTH);
        }
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        Integer currentMonth = (mois == 1) ? 12 : mois - 1;
        stepExecution.getJobExecution().getExecutionContext().put("dateDebut",new GregorianCalendar(annee, currentMonth, 1).getTime());
        stepExecution.getJobExecution().getExecutionContext().put("dateFin", new GregorianCalendar(annee, currentMonth, Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH)).getTime());
        stepExecution.getJobExecution().getExecutionContext().put(Constant.ANNEE, annee);
        stepExecution.getJobExecution().getExecutionContext().put(Constant.MOIS, mois);
        return stepExecution.getExitStatus();
    }

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
        if ((mois <= 0) && (mois > 12)) {
            log.error(Constant.ERROR_MONTH_RANGE);
            stepContribution.setExitStatus(ExitStatus.FAILED);
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String date = format.format(new Date());

        if (annee < Integer.parseInt(date.substring(0,4))) {
            log.error(Constant.ERROR_YEAR_RANGE);
            stepContribution.setExitStatus(ExitStatus.FAILED);
        }
        stepContribution.setExitStatus(ExitStatus.COMPLETED);
        return RepeatStatus.FINISHED;
    }
}

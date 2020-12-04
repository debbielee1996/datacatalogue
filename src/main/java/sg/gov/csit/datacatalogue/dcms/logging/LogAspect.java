package sg.gov.csit.datacatalogue.dcms.logging;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j

//todo: Log into a json file

public class LogAspect {
    @Pointcut("within(sg.gov.csit.datacatalogue..*)")
    public void pointcutDataCatalogue(){}

    @Around("pointcutDataCatalogue()")
    public Object logAll(ProceedingJoinPoint joinPoint) throws Throwable{
        logStart(joinPoint);

        final long start = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        final long timeTaken = System.currentTimeMillis() - start;

        logEnd(joinPoint, timeTaken);
        return result;
    }

    private static void logStart(ProceedingJoinPoint joinPoint){
        String loggedMessage = new JSONObject()
                .put(LogEnum.TYPE.name(), LogEnum.START.name())
                .put(LogEnum.PACKAGE.name(), LogHelper.getPackageName(joinPoint))
                .put(LogEnum.CLASS.name(), LogHelper.getClassName(joinPoint))
                .put(LogEnum.METHOD.name(), LogHelper.getMethodName(joinPoint))
                .put(LogEnum.PARAMETER.name(), LogHelper.getParameters(joinPoint))
                .toString();
        log.info(loggedMessage);
    }
    private static void logEnd(ProceedingJoinPoint joinPoint, long timeTaken){
        String loggedMessage = new JSONObject()
                .put(LogEnum.TYPE.name(), LogEnum.END.name())
                .put(LogEnum.PACKAGE.name(), LogHelper.getPackageName(joinPoint))
                .put(LogEnum.CLASS.name(), LogHelper.getClassName(joinPoint))
                .put(LogEnum.METHOD.name(), LogHelper.getMethodName(joinPoint))
                .put(LogEnum.TIMETAKEN.name(), timeTaken)
                .toString();
        log.info(loggedMessage);
    }
}

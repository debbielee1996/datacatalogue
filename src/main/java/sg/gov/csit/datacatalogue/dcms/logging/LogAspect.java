package sg.gov.csit.datacatalogue.dcms.logging;

import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.implementation.bytecode.Throw;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
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
        long start = System.currentTimeMillis();
        String className = getClassName(joinPoint);
        String methodName = getMethodName(joinPoint);

        logStart(className,methodName);
        Object result = joinPoint.proceed();
        long timeTaken = System.currentTimeMillis() - start;
        logEnd(className,methodName, timeTaken);

        return result;
    }

    private static void logStart(String className, String methodName){
        log.info("START: " + className + " " + methodName);
    }
    private static void logEnd(String className, String methodName, long timeTaken){
        log.info("END: " + className + " " + methodName + " Time taken: " + timeTaken);
    }
    private static String getClassName(ProceedingJoinPoint joinPoint){
        return joinPoint.getTarget().getClass().getCanonicalName();
    }
    private static String getMethodName(ProceedingJoinPoint joinPoint){
        return joinPoint.getSignature().getName();
    }

}

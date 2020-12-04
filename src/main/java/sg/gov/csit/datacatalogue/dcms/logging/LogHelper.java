package sg.gov.csit.datacatalogue.dcms.logging;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.CodeSignature;
import org.json.JSONObject;

public class LogHelper {
    public static String getPackageName(ProceedingJoinPoint joinPoint){
        try{
            return joinPoint.getTarget().getClass().getPackageName();
        }catch(NullPointerException npe){
            return "null";
        }
    }
    public static String getClassName(ProceedingJoinPoint joinPoint){
        try{
            return joinPoint.getTarget().getClass().getSimpleName();
        }catch(NullPointerException npe){
            return "null";
        }
    }
    public static String getMethodName(ProceedingJoinPoint joinPoint){
        try{
            return joinPoint.getSignature().getName();
        }catch(NullPointerException npe){
            return "null";
        }
    }
    public static JSONObject getParameters(ProceedingJoinPoint joinPoint){
        try{
            JSONObject jsonObject = new JSONObject();
            CodeSignature codeSignature = (CodeSignature) joinPoint.getSignature();
            Object[] signatureArgs = joinPoint.getArgs();

            for(int i=0;i<codeSignature.getParameterNames().length;i++){
                String paramName = codeSignature.getParameterNames()[i];
                Object paramValue = signatureArgs[i];
                jsonObject.put(paramName,paramValue);
            }

            return jsonObject;
        }catch(Exception ex){
            return null;
        }
    }
}

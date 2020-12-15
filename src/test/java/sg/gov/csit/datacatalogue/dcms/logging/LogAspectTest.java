package sg.gov.csit.datacatalogue.dcms.logging;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;
import org.springframework.aop.framework.AopProxy;
import org.springframework.aop.framework.DefaultAopProxyFactory;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class LogAspectTest {
    private final LogAspect logAspect = new LogAspect();
    private ServiceFake serviceFake;

    @BeforeEach
    public void setUp(){
        AspectJProxyFactory aspectJProxyFactory = new AspectJProxyFactory(new ServiceFake());
        aspectJProxyFactory.addAspect(logAspect);
        DefaultAopProxyFactory proxyFactory = new DefaultAopProxyFactory();
        AopProxy aopProxy = proxyFactory.createAopProxy(aspectJProxyFactory);
        serviceFake = (ServiceFake)aopProxy.getProxy();
    }

    @Test
    public void logAll_ShouldLogStartPackageName(){
        //arrange
        Logger testLogger = (Logger) LoggerFactory.getLogger(LogAspect.class);
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        testLogger.addAppender(listAppender);

        String expectedType = "\"TYPE\":\"START\"";
        String expectedPackage="\"PACKAGE\":\"sg.gov.csit.datacatalogue.dcms.logging\"";

        //act
        serviceFake.testing();

        //assert
        String loggedMessage = listAppender.list.get(0).getMessage();
        assertThat(loggedMessage.contains(expectedType)).isTrue();
        assertThat(loggedMessage.contains(expectedPackage)).isTrue();
    }

    @Test
    public void logAll_ShouldLogStartClassName(){
        //arrange
        Logger testLogger = (Logger) LoggerFactory.getLogger(LogAspect.class);
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        testLogger.addAppender(listAppender);

        String expectedType = "\"TYPE\":\"START\"";
        String expectedClass ="\"CLASS\":\"ServiceFake\"";

        //act
        serviceFake.testing();

        //assert
        String loggedMessage = listAppender.list.get(0).getMessage();
        assertThat(loggedMessage.contains(expectedType)).isTrue();
        assertThat(loggedMessage.contains(expectedClass)).isTrue();
    }

    @Test
    public void logAll_ShouldLogStartMethodName(){
        //arrange
        Logger testLogger = (Logger) LoggerFactory.getLogger(LogAspect.class);
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        testLogger.addAppender(listAppender);

        String expectedType = "\"TYPE\":\"START\"";
        String expectedMethod ="\"METHOD\":\"testing\"";

        //act
        serviceFake.testing();

        //assert
        String loggedMessage = listAppender.list.get(0).getMessage();
        assertThat(loggedMessage.contains(expectedType)).isTrue();
        assertThat(loggedMessage.contains(expectedMethod)).isTrue();
    }

    @Test
    public void logAll_ShouldLogMethodPrimitiveParameters(){
        //arrange
        Logger testLogger = (Logger) LoggerFactory.getLogger(LogAspect.class);
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        testLogger.addAppender(listAppender);

        String expectedType = "\"TYPE\":\"START\"";
        String expectedParameter ="\"PARAMETER\":{\"number\":1234,\"word\":\"testingWord\"}";

        //act
        serviceFake.testingParameter(1234,"testingWord");

        //assert
        String loggedMessage = listAppender.list.get(0).getMessage();
        assertThat(loggedMessage.contains(expectedType)).isTrue();
        assertThat(loggedMessage.contains(expectedParameter)).isTrue();
    }

    @Test
    public void logAll_ShouldLogMethodListParameters(){
        //arrange
        Logger testLogger = (Logger) LoggerFactory.getLogger(LogAspect.class);
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        testLogger.addAppender(listAppender);

        List<String> mockList = Arrays.asList("ab","cd");
        String expectedType = "\"TYPE\":\"START\"";
        String expectedParameter ="\"PARAMETER\":{\"mockList\":\"[ab, cd]\"}";

        //act
        serviceFake.testingParameterList(mockList);

        //assert
        String loggedMessage = listAppender.list.get(0).getMessage();
        assertThat(loggedMessage.contains(expectedType)).isTrue();
        assertThat(loggedMessage.contains(expectedParameter)).isTrue();
    }

    @Test
    public void logAll_ShouldLogEndPackageName(){
        //arrange
        Logger testLogger = (Logger) LoggerFactory.getLogger(LogAspect.class);
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        testLogger.addAppender(listAppender);

        String expectedType = "\"TYPE\":\"END\"";
        String expectedPackage="\"PACKAGE\":\"sg.gov.csit.datacatalogue.dcms.logging\"";

        //act
        serviceFake.testing();

        //assert
        String loggedMessage = listAppender.list.get(1).getMessage();
        assertThat(loggedMessage.contains(expectedType)).isTrue();
        assertThat(loggedMessage.contains(expectedPackage)).isTrue();
    }

    @Test
    public void logAll_ShouldLogEndClassName(){
        //arrange
        Logger testLogger = (Logger) LoggerFactory.getLogger(LogAspect.class);
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        testLogger.addAppender(listAppender);

        String expectedType = "\"TYPE\":\"END\"";
        String expectedClass ="\"CLASS\":\"ServiceFake\"";

        //act
        serviceFake.testing();

        //assert
        String loggedMessage = listAppender.list.get(1).getMessage();
        assertThat(loggedMessage.contains(expectedType)).isTrue();
        assertThat(loggedMessage.contains(expectedClass)).isTrue();
    }

    @Test
    public void logAll_ShouldLogEndMethodName(){
        //arrange
        Logger testLogger = (Logger) LoggerFactory.getLogger(LogAspect.class);
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        testLogger.addAppender(listAppender);

        String expectedType = "\"TYPE\":\"END\"";
        String expectedMethod ="\"METHOD\":\"testing\"";

        //act
        serviceFake.testing();

        //assert
        String loggedMessage = listAppender.list.get(1).getMessage();
        assertThat(loggedMessage.contains(expectedType)).isTrue();
        assertThat(loggedMessage.contains(expectedMethod)).isTrue();
    }

    @Test
    public void logAll_ShouldLogEndTimeTaken() throws Exception{
        //arrange
        Logger testLogger = (Logger) LoggerFactory.getLogger(LogAspect.class);
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        testLogger.addAppender(listAppender);

        //act
        serviceFake.testing();

        //assert
        JSONObject loggedMessage = new JSONObject(listAppender.list.get(1).getMessage());
        assertThat(loggedMessage.has("TIMETAKEN")).isTrue();
        assertThat(loggedMessage.get("TIMETAKEN") instanceof Integer).isTrue();
    }

    @Test
    public void logAll_ShouldProceedWithOriginalMethodCallAfterLogging(){
        //arrange
        String expectedResult = "Hello World";

        //act
        String result = serviceFake.testing();

        //assert
        assertThat(expectedResult).isEqualTo(result);
    }
}

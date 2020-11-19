package sg.gov.csit.datacatalogue.dcms.logging;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;
import org.springframework.aop.framework.AopProxy;
import org.springframework.aop.framework.DefaultAopProxyFactory;

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
    public void logAll_ShouldLogStartClassName(){
        //arrange
        Logger testLogger = (Logger) LoggerFactory.getLogger(LogAspect.class);
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        testLogger.addAppender(listAppender);

        //act
        serviceFake.testing();

        //assert
        String loggedMessage = listAppender.list.get(0).getMessage();
        assertThat(loggedMessage.contains("START: ")).isTrue();
        assertThat(loggedMessage.contains("ServiceFake")).isTrue();
    }

    @Test
    public void logAll_ShouldLogStartMethodName(){
        //arrange
        Logger testLogger = (Logger) LoggerFactory.getLogger(LogAspect.class);
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        testLogger.addAppender(listAppender);

        //act
        serviceFake.testing();

        //assert
        String loggedMessage = listAppender.list.get(0).getMessage();
        assertThat(loggedMessage.contains("START: ")).isTrue();
        assertThat(loggedMessage.contains("testing")).isTrue();
    }

    @Test
    public void logAll_ShouldLogEndClassName(){
        //arrange
        Logger testLogger = (Logger) LoggerFactory.getLogger(LogAspect.class);
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        testLogger.addAppender(listAppender);

        //act
        serviceFake.testing();

        //assert
        String loggedMessage = listAppender.list.get(1).getMessage();
        assertThat(loggedMessage.contains("END: ")).isTrue();
        assertThat(loggedMessage.contains("ServiceFake")).isTrue();
    }

    @Test
    public void logAll_ShouldLogEndMethodName(){
        //arrange
        Logger testLogger = (Logger) LoggerFactory.getLogger(LogAspect.class);
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        testLogger.addAppender(listAppender);

        //act
        serviceFake.testing();

        //assert
        String loggedMessage = listAppender.list.get(1).getMessage();
        assertThat(loggedMessage.contains("END: ")).isTrue();
        assertThat(loggedMessage.contains("testing")).isTrue();
    }

    @Test
    public void logAll_ShouldLogEndTimeTaken(){
        //arrange
        Logger testLogger = (Logger) LoggerFactory.getLogger(LogAspect.class);
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        testLogger.addAppender(listAppender);

        //act
        serviceFake.testing();

        //assert
        String loggedMessage = listAppender.list.get(1).getMessage();
        assertThat(loggedMessage.contains("END: ")).isTrue();
        assertThat(loggedMessage.contains("Time taken:")).isTrue();
        assertThat(Character.isDigit(loggedMessage.charAt(loggedMessage.length()-1))).isTrue();
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

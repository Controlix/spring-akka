package be.ict.mb.spring.akka.demo;

import java.util.concurrent.Executor;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import akka.actor.ActorSystem;
import be.ict.mb.spring.akka.boot.SpringExtension;

@Configuration
@EnableAsync
public class DemoConfiguration implements AsyncConfigurer {
	
    private final ApplicationContext applicationContext;
    
    DemoConfiguration(ApplicationContext applicationContext) {
    	this.applicationContext = applicationContext;
    }
    
    @Bean
    public ActorSystem actorSystem() {
        ActorSystem system = ActorSystem.create("akka-spring-demo");
        SpringExtension.SPRING_EXTENSION_PROVIDER.get(system).initialize(applicationContext);
        return system;
    }
    
    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(50);
        executor.initialize();
		return executor;
    }

}

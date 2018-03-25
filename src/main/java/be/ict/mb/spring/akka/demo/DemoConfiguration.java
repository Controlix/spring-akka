package be.ict.mb.spring.akka.demo;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import akka.actor.ActorSystem;
import be.ict.mb.spring.akka.boot.SpringExtension;

@Configuration
public class DemoConfiguration {
	
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
    
    
}

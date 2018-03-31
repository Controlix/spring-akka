package be.ict.mb.spring.akka.boot;

import org.springframework.context.ApplicationContext;

import akka.actor.Actor;
import akka.actor.IndirectActorProducer;

public class SpringActorProducer implements IndirectActorProducer {
	 
    private ApplicationContext applicationContext;
    private String beanActorName;
	private Object[] args;
 
    public SpringActorProducer(ApplicationContext applicationContext, String beanActorName, Object ... args) {
        this.applicationContext = applicationContext;
        this.beanActorName = beanActorName;
        this.args = args;
    }
 
    @Override
    public Actor produce() {
        return (Actor) applicationContext.getBean(beanActorName, args);
    }
 
    @Override
    public Class<? extends Actor> actorClass() {
        return (Class<? extends Actor>) applicationContext.getType(beanActorName);
    }
}
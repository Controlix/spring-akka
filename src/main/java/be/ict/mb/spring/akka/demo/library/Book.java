package be.ict.mb.spring.akka.demo.library;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import akka.actor.AbstractActor;
import lombok.ToString;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

@ToString
@Slf4j
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class Book extends AbstractActor {

	int id;
	String title;
	String author;
	
	public Book(int id, String title, String author) {
		this.id = id;
		this.title = title;
		this.author = author;
	}

	@Override
	public Receive createReceive() {
		return receiveBuilder()
				.match(GetBookDetails.class, g -> getSender().tell(bookDetails(), getSelf()))
				.build();
	}

	private BookDetails bookDetails() {
		log.info("Create book details {}", this);
		return new BookDetails(id, title, author);
	}
	
	@Value
	public static class GetBookDetails {
	}
	
}

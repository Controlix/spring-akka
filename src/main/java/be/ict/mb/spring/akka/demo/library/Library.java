package be.ict.mb.spring.akka.demo.library;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import be.ict.mb.spring.akka.demo.library.Book.GetBookDetails;
import lombok.Singular;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class Library extends AbstractActor {
	
	private Collection<ActorRef> books = new ArrayList<>();
	private Map<Integer, BookDetails> bookDetails = Collections.synchronizedMap(new HashMap<>());
	
	@Override
	public void preStart() throws Exception {
		getContext().actorSelection("/user/book-*").tell(new GetBookDetails(), getSelf());
	}

	@Override
	public Receive createReceive() {
		return receiveBuilder()
				.match(AddBook.class, this::add)
				.match(ListBooks.class, lb -> getSender().tell(new AllBooks(allBooks()), getSelf()))
				.match(BookDetails.class, this::update)
				.build();
	}
	
	private Collection<BookDetails> allBooks() {
		log.info("List all books {}", bookDetails.keySet());
		return Collections.unmodifiableCollection(bookDetails.values());
	}

	private void add(AddBook addBook) {
		log.info("Add book {}", addBook);
		this.books.add(addBook.getBook());
		addBook.getBook().tell(new GetBookDetails(), getSelf());
	}
	
	private void update(BookDetails bookDetails) {
		log.info("Update book details {}", bookDetails);
		this.bookDetails.put(bookDetails.getId(), bookDetails);
	}

	@Value
	public static class AddBook {
		ActorRef book;
	}
	
	@Value
	public static class ListBooks {
	}
	
	@Value
	public static class AllBooks {
		@Singular Collection<BookDetails> bookDetails;
	}
	
	@Value
	private static class BookDetail {
		String title;
		String author;
	}
}

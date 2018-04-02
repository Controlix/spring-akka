package be.ict.mb.spring.akka.demo.library;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.persistence.query.PersistenceQuery;
import akka.persistence.query.journal.leveldb.javadsl.LeveldbReadJournal;
import akka.stream.ActorMaterializer;
import be.ict.mb.spring.akka.demo.library.Book.GetBookDetails;
import lombok.Singular;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class Library extends AbstractActor {

	private Map<UUID, BookDetails> bookDetails = Collections.synchronizedMap(new HashMap<>());

	@Override
	public void preStart() throws Exception {
		log.info("Read all persistent ids");
		LeveldbReadJournal queries = PersistenceQuery.get(getContext().getSystem()).getReadJournalFor(LeveldbReadJournal.class,
				LeveldbReadJournal.Identifier());
		queries.currentPersistenceIds().runForeach(System.out::println, ActorMaterializer.create(getContext().getSystem()));
	}

	@Override
	public Receive createReceive() {
		return receiveBuilder().match(AddBook.class, this::add)
				.match(ListBooks.class, lb -> getSender().tell(new AllBooks(allBooks()), getSelf()))
				.match(BookDetails.class, this::update).build();
	}

	private Collection<BookDetails> allBooks() {
		log.info("List all books {}", bookDetails.keySet());
		return Collections.unmodifiableCollection(bookDetails.values());
	}

	private void add(AddBook addBook) {
		log.info("Add book {}", addBook);
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
		@Singular
		Collection<BookDetails> bookDetails;
	}

	@Value
	private static class BookDetail {
		String title;
		String author;
	}
}

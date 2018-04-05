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
import be.ict.mb.spring.akka.boot.SpringExtension;
import be.ict.mb.spring.akka.demo.library.Book.GetBookDetails;
import be.ict.mb.spring.akka.demo.library.Book.InitializeBook;
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
		queries.currentPersistenceIds().runForeach(id -> {
			System.out.println(id);
			try {
				ActorRef book = getContext().actorOf(SpringExtension.SPRING_EXTENSION_PROVIDER.get(getContext().getSystem()).props("book", UUID.fromString(id)), "book-" + id);
				book.tell(new GetBookDetails(), self());
			} catch (IllegalArgumentException e) {
				log.error("Not a valid UUID", e);
			}
		}, ActorMaterializer.create(getContext().getSystem()));
	}

	@Override
	public Receive createReceive() {
		return receiveBuilder().match(AddBook.class, ab -> sender().tell(new BookId(add(ab)), self()))
				.match(ListBooks.class, lb -> getSender().tell(new AllBooks(allBooks()), self()))
				.match(BookDetails.class, this::update).build();
	}

	private Collection<BookDetails> allBooks() {
		log.info("List all books {}", bookDetails.keySet());
		return Collections.unmodifiableCollection(bookDetails.values());
	}

	private UUID add(AddBook addBook) {
		log.info("Add book {}", addBook);
		UUID id = UUID.randomUUID();
		ActorRef book = getContext().actorOf(SpringExtension.SPRING_EXTENSION_PROVIDER.get(getContext().getSystem()).props("book", id), "book-" + id);
		book.tell(new InitializeBook(id, addBook.getTitle(), addBook.getAuthor()), null);
		book.tell(new GetBookDetails(), self());
		return id;
	}

	private void update(BookDetails bookDetails) {
		log.info("Update book details {}", bookDetails);
		this.bookDetails.put(bookDetails.getId(), bookDetails);
	}

	@Value
	public static class AddBook {
		String title;
		String author;
	}


	@Value
	public static class BookId {
		UUID id;
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

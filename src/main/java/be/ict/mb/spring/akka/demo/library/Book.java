package be.ict.mb.spring.akka.demo.library;

import java.io.Serializable;
import java.util.UUID;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import akka.persistence.AbstractPersistentActor;
import akka.persistence.Recovery;
import akka.persistence.SnapshotSelectionCriteria;
import lombok.ToString;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ToString(exclude = {"unititialized", "initialized"})
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class Book extends AbstractPersistentActor {

	private String persistenceId;
	private BookDetails details;

	private Receive initialized = receiveBuilder().match(GetBookDetails.class, g -> getSender().tell(details, getSelf())).build();
	private Receive unititialized = receiveBuilder()
			.match(InitializeBook.class, i -> {
				BookCreated bookCreated = new BookCreated(i.getId(), i.getTitle(), i.getAuthor());
				persist(bookCreated, event -> {
					initialize(event);
					getContext().getSystem().eventStream().publish(event);
				});
				})
			.build();

	public Book(UUID id) {
		this.persistenceId = id.toString();
	}

	@Override
	public String persistenceId() {
		log.info("Persistence id = {}", persistenceId);
		return persistenceId;
	}

	@Override
	public Receive createReceiveRecover() {
		return receiveBuilder()
				.match(BookCreated.class, this::initialize)
				.build();
	}

	@Override
	public Receive createReceive() {
		return unititialized;
	}

	@Override
	public Recovery recovery() {
		return Recovery.create(SnapshotSelectionCriteria.none());
	}

	private void initialize(BookCreated bookCreated) {
		log.info("Initialize book with {}", bookCreated);
		this.details = new BookDetails(bookCreated.getId(), bookCreated.getTitle(), bookCreated.getAuthor());
		getContext().become(initialized);
	}

	@Value
	public static class InitializeBook {
		UUID id;
		String title;
		String author;
	}

	@Value
	public static class GetBookDetails {
	}

	@Value
	public static class BookCreated implements Serializable {
		private static final long serialVersionUID = -2629712388247815453L;
		
		UUID id;
		String title;
		String author;
	}
}

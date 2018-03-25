package be.ict.mb.spring.akka.demo.writer;

import java.io.Serializable;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import akka.persistence.AbstractPersistentActor;
import akka.persistence.Recovery;
import akka.persistence.SnapshotSelectionCriteria;
import lombok.Value;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class Writer extends AbstractPersistentActor {

	private StringBuilder buffer = new StringBuilder();
	

	@Override
	public String persistenceId() {
		return "writer-id-1";
	}

	@Override
	public Receive createReceiveRecover() {
		return receiveBuilder()
				.match(TextWritten.class, this::update)
				.build();
	}
	
	@Override
	public Receive createReceive() {
		return receiveBuilder()
				.match(Write.class, req -> {
					TextWritten textWritten = new TextWritten(req.text);
					persist(textWritten, event -> {
						update(event);
						getContext().getSystem().eventStream().publish(textWritten);
					});
				})
				.match(Show.class, req -> getSender().tell(new Text(buffer.toString()), getSelf()))
				.build();
	}
	
	private void update(TextWritten event) {
		this.buffer.append(event.text);
	}
	
	@Override
	public Recovery recovery() {
		return Recovery.create(SnapshotSelectionCriteria.none());
	}
	
	@Value
	public static class TextWritten implements Serializable {
		private static final long serialVersionUID = -6293714054253889786L;
		
		String text;
	}

	@Value
	public static class Show {
	}

	@Value
	public static class Text {
		String content;
	}

	@Value
	public static class Write {
		String text;
	}
}

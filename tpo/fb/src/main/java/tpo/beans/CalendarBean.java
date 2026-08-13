package tpo.beans;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.openfaces.component.timetable.AbstractTimetableEvent;
import org.openfaces.component.timetable.ReservedTimeEvent;
import org.openfaces.component.timetable.TimetableChangeEvent;
import org.openfaces.component.timetable.TimetableEvent;
import org.openfaces.component.timetable.TimetableResource;
import org.openfaces.util.Faces;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

@Repository("CalendarBean")
@Scope("request")
public class CalendarBean extends DayTableBean {
	private static int eventIdCounter = 0;

	List<AbstractTimetableEvent> events = new ArrayList<AbstractTimetableEvent>();
	private List<TimetableResource> resources = new ArrayList<TimetableResource>();

	public CalendarBean() {
		Color green = new Color(41, 142, 1);
		Color blue = new Color(2, 105, 220);
		Color orange = new Color(232, 65, 2);

		Resource andrew = new Resource("Andrew", "#0269dc");

		// today
		events.add(new TimetableEvent(generateEventId(), todayAt(9, 0),
				todayAt(19, 30), "WORK", "Usual day, nothing special", blue,
				andrew.getId()));
		events.add(new TimetableEvent(generateEventId(), todayAt(20, 0),
				todayAt(23, 0), "ALEX BIRTHDAY PARTY",
				"Dress-code: Harry Potter", blue, andrew.getId()));

		// yesterday
		events.add(new TimetableEvent(generateEventId(), yesterdayAt(9, 0),
				yesterdayAt(15, 30), "WORK", "Short day, will visit a doctor",
				blue, andrew.getId()));
		events.add(new TimetableEvent(generateEventId(), yesterdayAt(16, 30),
				yesterdayAt(17, 30), "DOCTOR CONSULTATION",
				"Once-a-year visit", blue, andrew.getId()));
		events.add(new ReservedTimeEvent(generateEventId(), andrew.getId(),
				yesterdayAt(18, 30), yesterdayAt(19, 30)));

		// tomorrow
		events.add(new ReservedTimeEvent(generateEventId(), andrew.getId(),
				tomorrowAt(6, 0), tomorrowAt(8, 30)));
		events.add(new TimetableEvent(generateEventId(), tomorrowAt(9, 0),
				tomorrowAt(19, 30), "WORK", "As usual", blue, andrew.getId()));
		events.add(new TimetableEvent(generateEventId(), tomorrowAt(20, 30),
				tomorrowAt(23, 30), "WENDY",
				"Table is reserved at Potato House", blue, andrew.getId()));

		resources.add(new TimetableResource(andrew, andrew.getId(), andrew
				.getName()));
	}

	private String generateEventId() {
		return String.valueOf(eventIdCounter++);
	}

	public List<AbstractTimetableEvent> getEvents() {
		Date startTime = Faces.var("startTime", Date.class);
		Date endTime = Faces.var("endTime", Date.class);

		List<AbstractTimetableEvent> result = retrieveEventsForPeriod(
				startTime, endTime);
		return result;
	}

	private List<AbstractTimetableEvent> retrieveEventsForPeriod(
			Date startTime, Date endTime) {
		List<AbstractTimetableEvent> result = new ArrayList<AbstractTimetableEvent>();
		for (AbstractTimetableEvent event : events) {
			if (event.getStart().before(endTime)
					&& event.getEnd().after(startTime))
				result.add(event);
		}
		return result;
	}

	public List<TimetableResource> getResources() {
		return resources;
	}

	public void removeEvent(List<AbstractTimetableEvent> events, String id) {
		events.remove(eventById(events, id));
	}

	public void addEvent(List<AbstractTimetableEvent> events,
			TimetableEvent event) {
		event.setId(generateEventId());
		if (event.getColor() == null) {
			event.setColor(new Color(0, 0x6e, 0xbb));
		}
		events.add(event);
	}

	public void updateEvent(List<AbstractTimetableEvent> events,
			TimetableEvent editedEvent) {
		TimetableEvent event = (TimetableEvent) eventById(events,
				editedEvent.getId());
		event.setName(editedEvent.getName());
		event.setStart(editedEvent.getStart());
		event.setEnd(editedEvent.getEnd());
		event.setDescription(editedEvent.getDescription());
		event.setResourceId(editedEvent.getResourceId());
		event.setColor(editedEvent.getColor());

	}

	public void processTimetableChanges(TimetableChangeEvent tce) {
		TimetableEvent[] addedEvents = tce.getAddedEvents();
		for (TimetableEvent event : addedEvents) {
			addEvent(events, event);
		}

		TimetableEvent[] editedEvents = tce.getChangedEvents();
		for (TimetableEvent event : editedEvents) {
			updateEvent(events, event);
		}

		String[] removedEventIds = tce.getRemovedEventIds();
		for (String eventId : removedEventIds) {
			removeEvent(events, eventId);
		}
	}

	public void remove() {
		TimetableEvent event = getEvent();
		removeEvent(events, event.getId());
	}

	private TimetableEvent getEvent() {
		return Faces.var("event", TimetableEvent.class);
	}

}

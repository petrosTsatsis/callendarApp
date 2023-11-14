package gr.dit.hua.calendar;

import biweekly.Biweekly;
import biweekly.ICalendar;
import biweekly.component.VEvent;
import biweekly.property.DateEnd;
import biweekly.property.DateStart;
import biweekly.property.Description;
import biweekly.property.Summary;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Calendar {

    private List<VEvent> events;

    private List<VEvent> sortedEvents;

    public Calendar(){}

    public void displayEvents(List<VEvent> events, String message){

        System.out.println("\n" + message + "\n");

        for (VEvent event : events) {

            Summary summary = event.getSummary();
            DateStart dtStart = event.getDateStart();
            DateEnd dtEnd = event.getDateEnd();
            Description description = event.getDescription();

            System.out.println("--------------------");
            System.out.println("Title: " + (summary != null ? summary.getValue() : ""));
            System.out.println("Start Date: " + (dtStart != null ? dtStart.getValue() : ""));
            System.out.println("End Date: " + (dtEnd != null ? dtEnd.getValue() : ""));
            System.out.println("Description: " + (description != null ? description.getValue() : ""));
            System.out.println("--------------------");
        }

    }

    // view the events from the current day
    public void displayDayEvents(String filePath) throws IOException {
        String content = readFile(filePath);
        ICalendar ical = Biweekly.parse(content).first();
        LocalDateTime dateTimeNow = LocalDateTime.now();
        events = new ArrayList<>();

        if (ical != null) {
            System.out.println("Events in the iCal file:");

            for (VEvent event : ical.getEvents()) {
                LocalDateTime eventStartDateTime = convertDateStartToLocalDateTime(event.getDateStart());

                if (eventStartDateTime.toLocalDate().isEqual(dateTimeNow.toLocalDate())) {
                    events.add(event);
                }
            }
            sortedEvents = EventLists.sortByStartDate(events);

            displayEvents(sortedEvents, "Today's events : ");
        } else {
            System.out.println("Invalid or empty iCal file.");
        }
    }

    // view the events from the current day till the end of the week
    public void displayWeekEvents(String filePath) throws IOException {
        String content = readFile(filePath);
        ICalendar ical = Biweekly.parse(content).first();
        LocalDateTime dateTimeNow = LocalDateTime.now();
        events = new ArrayList<>();

        if (ical != null) {

            LocalDateTime startOfWeek = dateTimeNow.with(dateTimeNow.getDayOfWeek()).withHour(0).withMinute(0).withSecond(0).withNano(0);
            LocalDateTime endOfWeek = dateTimeNow.with(DayOfWeek.SUNDAY).plusDays(1).withHour(23).withMinute(59).withSecond(59);

            for (VEvent event : ical.getEvents()) {
                LocalDateTime eventStartDateTime = convertDateStartToLocalDateTime(event.getDateStart());

                if (eventStartDateTime.isAfter(startOfWeek) && eventStartDateTime.isBefore(endOfWeek) ) {
                    events.add(event);
                }
            }
            sortedEvents = EventLists.sortByStartDate(events);

            displayEvents(sortedEvents, "Week's events :");
        } else {
            System.out.println("Invalid or empty iCal file.");
        }

    }

    // view the events from the current month
    public void displayMonthEvents(String filePath) throws IOException {
        String content = readFile(filePath);
        ICalendar ical = Biweekly.parse(content).first();
        LocalDateTime dateTimeNow = LocalDateTime.now();
        events = new ArrayList<>();


        if (ical != null) {
            LocalDateTime startOfMonth = dateTimeNow.withDayOfMonth(dateTimeNow.getDayOfMonth()).withHour(0).withMinute(0).withSecond(0).withNano(0);
            LocalDateTime endOfMonth = dateTimeNow.withDayOfMonth(dateTimeNow.getMonth().length(dateTimeNow.toLocalDate().isLeapYear())).withHour(23).withMinute(59).withSecond(59);

            for (VEvent event : ical.getEvents()) {
                LocalDateTime eventStartDateTime = convertDateStartToLocalDateTime(event.getDateStart());

                if (eventStartDateTime.isAfter(startOfMonth) && eventStartDateTime.isBefore(endOfMonth) ) {
                    events.add(event);
                }
            }
            sortedEvents = EventLists.sortByStartDate(events);

            displayEvents(sortedEvents, "Month's events :");
        }else {
            System.out.println("Invalid or empty iCal file.");
        }
    }

    // read the ical file
    private static String readFile(String filePath) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(filePath));
        return new String(encoded);
    }

    // change the format from DateStart to LocalDateTime
    static LocalDateTime convertDateStartToLocalDateTime(DateStart dateStart) {
        Date date = dateStart.getValue();
        return LocalDateTime.ofInstant(date.toInstant(), java.time.ZoneId.systemDefault());
    }
}

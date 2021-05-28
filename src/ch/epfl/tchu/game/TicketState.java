package ch.epfl.tchu.game;

import javafx.beans.property.SimpleStringProperty;

public class TicketState {

    private final SimpleStringProperty ticketFirstName;
    private final SimpleStringProperty doneTicket;

    public TicketState(String ticketName, String done){
        this.ticketFirstName = new SimpleStringProperty(ticketName);
        this.doneTicket = new SimpleStringProperty(done);
    }

    public SimpleStringProperty getTicketFirstName() {
        return ticketFirstName;
    }

    public SimpleStringProperty doneTicketProperty() {
        return doneTicket;
    }
}

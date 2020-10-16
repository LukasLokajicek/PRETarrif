package cz.lokajicek.lukas.pretariffreader.model;

import lombok.Data;
import lombok.ToString;

import java.time.LocalTime;

@Data
@ToString
public class StateHour implements Comparable<StateHour> {

    private final State state;
    private final LocalTime hour;
    private final Integer order;


    @Override
    public int compareTo(StateHour o) {
        return order.compareTo(o.order);
    }
}

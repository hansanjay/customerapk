package com.org.tsd.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.org.tsd.exception.ApplicationException;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Subscription extends Record {
    private int id;
    private int customerId;
    private int quantity;
    private int distributorId;
    private int productId;
    private int type;
    private int status;
    private Integer parentId;
    private boolean permanent;
    private boolean visible;

    private String dayOfWeek;
    private String dayOfMonth;

    private Date start;
    private Date stop;
    private Date pause;
    private Date resume;

    private Product product;
    
    public Subscription(Subscription s, Map<String,Object> m) {
        super();
        this.customerId = s.getCustomerId();
        quantity = s.getQuantity();
        distributorId = s.getDistributorId();
        productId = s.getProduct().getId();
        type = s.type;
        status = 1;
        parentId = s.id;
        dayOfWeek = s.getDayOfWeek();
        dayOfMonth = s.getDayOfMonth();
        permanent = false;
        visible = true;
        setProperties(m, true);
    }

    public boolean isActiveOn(Date date) {
        Date s1 = new Date(start.getTime() - 86400000);
        Date s2 = (stop != null) ? new Date(stop.getTime() + 86400000) : null;
        Date r = (resume != null) ? new Date(resume.getTime() + 86400000) : null;
        Date p = (pause != null) ? new Date(pause.getTime()) : null;

        boolean active = !s1.after(setToMidnight(date)) && (s2 == null || s2.after(date));
        boolean notPaused = (pause == null) || (p.after(date) && r.before(date));

        return active && notPaused;
    }
    
    public void setDayOfWeek(String dayOfWeek) throws ApplicationException {
        if (dayOfWeek == null || dayOfWeek.matches("^[1-7](,[1-7]){0,6}$")) {
            this.dayOfWeek = dayOfWeek;
        } else {
            throw new ApplicationException(0, "Invalid day of week value", HttpStatus.BAD_REQUEST);
        }
    }

    public void setDayOfMonth(String dayOfMonth) throws ApplicationException {
        if (dayOfMonth == null || dayOfMonth.matches("^([1-9]|[12][0-9]|3[01])(,([1-9]|[12][0-9]|3[01])){0,30}$")) {
            this.dayOfMonth = dayOfMonth;
        } else {
            throw new ApplicationException(0, "Invalid day of month value", HttpStatus.BAD_REQUEST);
        }
    }

    @JsonIgnore
    @Override
    public List<String> getEditableFields() {
        List<String> fields = new ArrayList<>(List.of("stop", "pause", "resume", "quantity", "status"));
        switch (type) {
            case 2 -> fields.add("dayOfWeek");
            case 3 -> fields.add("dayOfMonth");
        }
        return fields;
    }

}

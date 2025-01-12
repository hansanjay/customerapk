package com.org.tsd.models;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class Credentials {
	
    private String clientId;
    private String clientSecret;
    private String type;
    private Date issuedOn;
    private Date expiry;

    private static final DateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm X");

    public Credentials() {
    }

    public Credentials(Map<String, Object> data) {
        this.clientId = (String) data.get("clientId");
        this.clientSecret = (String) data.get("clientSecret");
        this.type = (String) data.get("type");
        setIssuedOn(data.get("issuedOn"));
        setExpiry(data.get("expiry"));
    }

    public Credentials(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getIssuedOn() {
        return issuedOn;
    }

    public void setIssuedOn(Object issuedOn) {
        if (issuedOn != null) {
            try {
                if (issuedOn instanceof String) {
                    this.issuedOn = SDF.parse((String) issuedOn);
                } else if (issuedOn instanceof Date) {
                    this.issuedOn = (Date) issuedOn;
                } else {
                    this.issuedOn = null;
                }
            } catch (ParseException e) {
                throw new IllegalArgumentException("Invalid date format for issuedOn: " + issuedOn);
            }
        } else {
            this.issuedOn = null;
        }
    }

    public Date getExpiry() {
        return expiry;
    }

    public void setExpiry(Object expiry) {
        if (expiry != null) {
            try {
                if (expiry instanceof String) {
                    this.expiry = SDF.parse((String) expiry);
                } else if (expiry instanceof Date) {
                    this.expiry = (Date) expiry;
                } else {
                    this.expiry = null;
                }
            } catch (ParseException e) {
                throw new IllegalArgumentException("Invalid date format for expiry: " + expiry);
            }
        } else {
            this.expiry = null;
        }
    }

    @Override
    public String toString() {
        return "Credentials{" +
                "clientId='" + clientId + '\'' +
                ", clientSecret='" + clientSecret + '\'' +
                ", type='" + type + '\'' +
                ", issuedOn=" + issuedOn +
                ", expiry=" + expiry +
                '}';
    }
}

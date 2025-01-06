package com.rbs.lre.gitlab.model;

import java.util.Date;

public class Metadata {
    private Date lastUpdatedDate;

    public Metadata(Date lastUpdatedDate) {
        this.lastUpdatedDate = lastUpdatedDate;
    }

    public Date getLastUpdatedDate() { return lastUpdatedDate; }
    public void setLastUpdatedDate(Date lastUpdatedDate) { this.lastUpdatedDate = lastUpdatedDate; }
}


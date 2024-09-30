package com.backend.owlfinance.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
// @Table(name="") REAL DB TABLE NAME
public class DummyEntity {

    @Id
    private String dummyId;
    private String dummyName;

    public String getDummyId() {
        return dummyId;
    }

    public String getDummyName() {
        return dummyName;
    }

    public void setDummyId(String dummyId) {
        this.dummyId = dummyId;
    }

    public void setDummyName(String dummyName) {
        this.dummyName = dummyName;
    }
}

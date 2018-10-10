package io.aesy.dropwizard.entity;

import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;

@Entity
@Table(name = "my_entity")
public class MyEntity {
    @Id
    @Column(name = "id", nullable = false)
    private final int id;

    @NotEmpty
    @Column(name = "name", nullable = false)
    private final String name;

    public MyEntity(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
package com.nix.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
//import com.nix.api.rest.json.JsonRole;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.NaturalId;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;

@Data
@EqualsAndHashCode(exclude = {"id"})
@AllArgsConstructor
@NoArgsConstructor
//@JsonDeserialize(using = JsonRole.Deserializer.class)
//@JsonSerialize(using = JsonRole.Serializer.class)
@Entity
@Table(name = "PERSON_ROLE", schema = "PUBLIC")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ROLE_ID")
    private long id;

    @Column(name = "NAME", unique = true)
    @NaturalId
    @NotEmpty
    private String name;
}

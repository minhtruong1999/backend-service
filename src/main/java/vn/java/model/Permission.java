package vn.java.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.*;


@Setter
@Getter
@Entity
@Table(name = "tbl_permission")
public class Permission extends AbstractEntity<Integer> {

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @OneToMany(mappedBy = "permission")
    private Set<RoleHasPermission> permissions = new HashSet<>();
}

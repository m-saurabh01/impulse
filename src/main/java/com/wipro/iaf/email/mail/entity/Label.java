package com.wipro.iaf.email.mail.entity;

import javax.persistence.*;

import com.wipro.iaf.email.user.entity.User;
import lombok.Data;

@Entity
@Table(name = "labels", indexes = {
    @Index(name = "idx_label_user", columnList = "user_id")
})
@Data
public class Label {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, length = 7)
    private String color; // Hex color like #ff5733

    public Label() {}

    public Label(User user, String name, String color) {
        this.user = user;
        this.name = name;
        this.color = color;
    }
}

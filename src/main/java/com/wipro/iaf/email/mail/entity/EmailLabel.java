package com.wipro.iaf.email.mail.entity;

import javax.persistence.*;
import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "email_labels")
@IdClass(EmailLabel.EmailLabelId.class)
@Data
public class EmailLabel {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "email_id")
    private Email email;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "label_id")
    private Label label;

    @Id
    @Column(name = "user_id")
    private Long userId;

    public EmailLabel() {}

    public EmailLabel(Email email, Label label, Long userId) {
        this.email = email;
        this.label = label;
        this.userId = userId;
    }

    @Data
    @EqualsAndHashCode
    public static class EmailLabelId implements Serializable {
        private Long email;
        private Long label;
        private Long userId;
    }
}

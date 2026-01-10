package com.wipro.iaf.email.mail.entity;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailRecipientId implements Serializable {

    private static final long serialVersionUID = 1L;
    
	private Long email;
    private Long user;

    
}


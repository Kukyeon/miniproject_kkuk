package com.kkuk.jap.kkuk.user;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
@SequenceGenerator(
		name = "USERS_SEQ_GENERATOR",
		sequenceName = "USERS_SEQ",
		initialValue = 1,
		allocationSize = 1
		)

public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "USERS_SEQ_GENERATOR")
	private Long id;
	
	@Column(name = "username" ,unique = true)
	private String username;
	
	private String password;
	
	@Column(name = "email" ,unique = true)
	private String email;
	
	
}

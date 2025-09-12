package com.kkuk.jap.kkuk.post;

import java.time.LocalDateTime;

import com.kkuk.jap.kkuk.user.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "post")
@SequenceGenerator(
		name = "POST_SEQ_GENERATOR",
		sequenceName = "POST_SEQ",
		initialValue = 1,
		allocationSize = 1
		)

public class Post {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE , generator = "POST_SEQ_GENERATOR")
    private Integer id;
	
	@Column(length = 200)
    private String title;
    
	@Column(length = 500)
    private String content;

    private LocalDateTime createdate;
	
    @ManyToOne
    private User writer;
}

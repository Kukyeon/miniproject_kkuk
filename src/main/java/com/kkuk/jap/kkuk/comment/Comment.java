package com.kkuk.jap.kkuk.comment;

import java.time.LocalDateTime;

import com.kkuk.jap.kkuk.post.Post;
import com.kkuk.jap.kkuk.user.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(name = "comment")
@SequenceGenerator(
		name = "COMMENT_SEQ_GENERATOR",
		sequenceName = "COMMENT_SEQ",
		initialValue = 1,
		allocationSize = 1
		)
public class Comment {

	 	@Id 
	 	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "COMMENT_SEQ_GENERATOR")
	    private Integer id;
	 	
	 	@Column(length = 500)
	    private String content;

	    @ManyToOne 
	    private User user;

	    @ManyToOne
	    private Post post;

	    private LocalDateTime createdate;
	
}

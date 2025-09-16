package com.kkuk.jap.kkuk.comment;

import java.time.LocalDateTime;
import java.util.Set;

import com.kkuk.jap.kkuk.post.Post;
import com.kkuk.jap.kkuk.user.User;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "comments")
@SequenceGenerator(
		name = "COMMENTS_SEQ_GENERATOR",
		sequenceName = "COMMENT_SEQ",
		initialValue = 1,
		allocationSize = 1
		)
public class Comment {

	 	@Id 
	 	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "COMMENTS_SEQ_GENERATOR")
	    private Integer id;
	 	
	 	@Column(length = 500)
	    private String content;

	 	private LocalDateTime createdate;
	 	
	 	@ManyToOne
	    private Post post;
	 	
	    @ManyToOne 
	    private User writer;

	    private LocalDateTime modifydate;

	    @ManyToMany
		Set<User> voter;
		
		@ManyToMany
		Set<User> novoter;
	
}

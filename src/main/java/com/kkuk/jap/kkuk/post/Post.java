package com.kkuk.jap.kkuk.post;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import com.kkuk.jap.kkuk.comment.Comment;
import com.kkuk.jap.kkuk.user.User;


import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
    private String subject;
    
	@Column(length = 500)
    private String content;

    private LocalDateTime createdate;
	
    @ManyToOne
    private User writer;
    
    private LocalDateTime modifydate;
    
    private Integer hit = 0; // 작성글 조회수
    
    @ManyToMany
	Set<User> voter;
	
	@ManyToMany
	Set<User> novoter;
	
	@OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE)
	private List<Comment> commentList;
	
	 @ManyToOne
	 @JoinColumn(name = "user_id")
	 private User user;
}

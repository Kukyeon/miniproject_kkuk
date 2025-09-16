package com.kkuk.jap.kkuk.comment;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kkuk.jap.kkuk.DataNotFoundException;
import com.kkuk.jap.kkuk.post.Post;
import com.kkuk.jap.kkuk.user.User;

@Service
public class CommentService {

	@Autowired
	private CommentRepository commentRepository;
	
	public Comment create(Post post, String content, User writer) {
		Comment comment = new Comment();
		comment.setContent(content);
		comment.setCreatedate(LocalDateTime.now());
		comment.setPost(post);
		comment.setWriter(writer);
		commentRepository.save(comment);
		
		return comment;
	}
	
	public Comment getComment(Integer id) {
		Optional<Comment> _comment = commentRepository.findById(id);
		
		if(_comment.isPresent()) {
			return _comment.get();
		}else {
			throw new DataNotFoundException("해당 답변이 존재하지않습니다.");
		}
	}
	
	public void modify(Comment comment, String content) {
		comment.setContent(content);
		comment.setModifydate(LocalDateTime.now());
		commentRepository.save(comment);
	}
	
	public void delete(Comment comment) {
		commentRepository.delete(comment);
	}
	
	public void vote(Comment comment, User user) {
		comment.getVoter().add(user);
		commentRepository.save(comment);
	}
	
	public void novote(Comment comment, User user) {
		comment.getNovoter().add(user);
		commentRepository.save(comment);
	}
}

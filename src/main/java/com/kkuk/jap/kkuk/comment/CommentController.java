package com.kkuk.jap.kkuk.comment;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import com.kkuk.jap.kkuk.post.Post;
import com.kkuk.jap.kkuk.post.PostService;
import com.kkuk.jap.kkuk.user.User;
import com.kkuk.jap.kkuk.user.UserService;

import jakarta.validation.Valid;

@RequestMapping("/comment")
@Controller
public class CommentController {

	@Autowired
	private PostService postService;
	
	@Autowired
	private CommentService commentService;
	
	@Autowired
	private UserService userService;
	
	@PreAuthorize("isAuthenticated()")
	@PostMapping(value = "/create/{id}")
	public String createComment(Model model, @PathVariable("id")Integer id, Principal principal, 
			@Valid CommentForm commentForm, BindingResult bindingResult) {
		Post post = postService.getPost(id);
		User user = userService.getUser(principal.getName());
		if(bindingResult.hasErrors()) {
			model.addAttribute("post", post);
			return "post_detail";
		}
		Comment comment = commentService.create(post, commentForm.getContent(), user);
		return String.format("redirect:/post/detail/%s#comment_%s", id, comment.getId());
	}
	
	@GetMapping(value = "/modify/{id}")
	public String commentModify(CommentForm commentForm, @PathVariable("id")Integer id, Principal principal) {
		Comment comment = commentService.getComment(id);
		if(!comment.getWriter().getUsername().equals(principal.getName())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제 권한이 없습니다.");
		}
		commentForm.setContent(comment.getContent());
		return "comment_form";
	}
	
	@PostMapping(value = "/modify/{id}")
	public String commentModify(@Valid CommentForm commentForm, BindingResult bindingResult,
			@PathVariable("id")Integer id, Principal principal) {
		if(bindingResult.hasErrors()) {
			return "comment_form";
		}
		Comment comment = commentService.getComment(id);
		if(!comment.getWriter().getUsername().equals(principal.getName())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
		}
		commentService.modify(comment, commentForm.getContent());
		
		return String.format("redirect:/post/detail/%s", comment.getPost().getId());
	}
	
	@GetMapping(value = "/delete/{id}")
	public String commentDelete(Principal principal, @PathVariable("id")Integer id) {
		Comment comment = commentService.getComment(id);
		
		if(!comment.getWriter().getUsername().equals(principal.getName())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제 권한이 없습니다.");
		}
		commentService.delete(comment);
		return String.format("redirect:/post/detail/%s", comment.getPost().getId());
	}
	
	@GetMapping(value = "/vote/{id}")
	public String commentVote(@PathVariable("id")Integer id, Principal principal) {
		Comment comment = commentService.getComment(id);
		User user = userService.getUser(principal.getName());
		commentService.vote(comment, user);
		return String.format("redirect:/post/detail/%s", comment.getPost().getId());
	}
	
	@GetMapping(value = "/novote/{id}")
	public String commentNotVote(@PathVariable("id")Integer id, Principal principal) {
		Comment comment = commentService.getComment(id);
		User user = userService.getUser(principal.getName());
		commentService.novote(comment, user);
		return String.format("redirect:/post/detail/%s", comment.getPost().getId());
	}
}

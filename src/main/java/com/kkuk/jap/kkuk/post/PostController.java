package com.kkuk.jap.kkuk.post;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;


import com.kkuk.jap.kkuk.comment.CommentForm;
import com.kkuk.jap.kkuk.user.User;
import com.kkuk.jap.kkuk.user.UserService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;



@Controller
@RequestMapping("/post")
public class PostController {

	@Autowired
	private PostService postService;
	
	@Autowired
	private UserService userService;
	
	@GetMapping(value = "/list")
	public String list(Model model, @RequestParam(value = "page", defaultValue = "0")int page,
			@RequestParam(value = "kw", defaultValue = "")String kw) {
		
		int pageSize = 10;
		
		Page<Post> paging = postService.getPagePost(page, kw);
		model.addAttribute("postPage", paging);
		model.addAttribute("kw", kw);
		return "post_list";
	}
	
	@GetMapping(value = "/detail/{id}")
	public String detail(Model model, @PathVariable("id") Integer id, CommentForm commentForm) {
		
		postService.hit(id);
		
		Post post = postService.getPost(id);
		model.addAttribute("post", post);
		return "post_detail";
	}
	
	@GetMapping(value = "/create")
	public String postCreate(PostForm postForm) {
		return "post_form";
	}
	
	@PostMapping(value = "/create")
	public String postCreate(@Valid PostForm postForm, BindingResult bindingResult,  Principal principal) {
		
		if(bindingResult.hasErrors()) {
			return "post_form";
		}
		
		User user = userService.getUser(principal.getName());
		
		postService.create(postForm.getSubject(), postForm.getContent(), user);
		
		return "redirect:/post/list";
	}
	
	@GetMapping(value = "/modify/{id}")
	public String postModify(PostForm postForm, @PathVariable("id")Integer id, Principal principal) {
		Post post = postService.getPost(id);
		
		if(!post.getWriter().getUsername().equals(principal.getName())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이없습니다.");
		}
		
		postForm.setSubject(post.getSubject());
		postForm.setContent(post.getContent());
		
		return "post_form";
	}
	
	@PostMapping(value = "/modify/{id}")
	public String postModify(@Valid PostForm postForm, BindingResult bindingResult,
			Principal principal, @PathVariable("id")Integer id) {
		
		if(bindingResult.hasErrors()) {
			return "post_form";
		}
		
		Post post = postService.getPost(id);
		
		if(!post.getWriter().getUsername().equals(principal.getName())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
		}
		
		postService.modify(post, postForm.getSubject(), postForm.getContent());
		
		return String.format("redirect:/post/detail/%s", id);
	}
	
	@GetMapping(value = "/delete/{id}")
	public String postDelete(Principal principal, @PathVariable("id")Integer id) {
		Post post = postService.getPost(id);
		if(!post.getWriter().getUsername().equals(principal.getName())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제 권한이 없습니다.");
		}
		postService.delete(post);
		return "redirect:/";
	}
	
	@GetMapping(value = "/vote/{id}")
	public String postVote(@PathVariable("id")Integer id, Principal principal) {
		Post post = postService.getPost(id);
		User user = userService.getUser(principal.getName());
		postService.vote(post, user);
		return String.format("redirect:/post/detail/%s", id);
	}
	
	@GetMapping(value = "/novote/{id}")
	public String postNotVote(@PathVariable("id")Integer id, Principal principal) {
		Post post = postService.getPost(id);
		User user = userService.getUser(principal.getName());
		postService.novote(post, user);
		return String.format("redirect:/post/detail/%s", id);
	}
}

package com.kkuk.jap.kkuk.post;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
	
//	@GetMapping(value = "/list")
//	public String list(Model model, @RequestParam(value = "page", defaultValue = "0")int page,
//			@RequestParam(value = "kw", defaultValue = "")String kw) {
//		
//		int pageSize = 10;
//		
//		Page<Post> paging = postService.getPagePost(page, kw, null);
//		model.addAttribute("postPage", paging);
//		model.addAttribute("kw", kw);
//		return "post_list";
//	}
	@GetMapping(value = "/free/list")
	public String freeList(Model model, @RequestParam(value = "page", defaultValue = "0")int page,
									@RequestParam(value = "kw", defaultValue = "")String kw,
									@RequestParam(value = "boardType", defaultValue = "FREE") BoardType bt) {
		
		int pageSize = 10;
		
		Page<Post> paging = postService.getPagePost(page, kw, bt);
		model.addAttribute("postPage", paging);
		model.addAttribute("kw", kw);
		model.addAttribute("bt", bt);
		return "post_list";
	}
	
	@GetMapping(value = "/notice/list")
	public String noticeList(Model model, @RequestParam(value = "page", defaultValue = "0")int page,
									@RequestParam(value = "kw", defaultValue = "")String kw,
									@RequestParam(value = "boardType", defaultValue = "NOTICE") BoardType bt) {
		
		int pageSize = 10;
		
		Page<Post> paging = postService.getPagePost(page, kw, bt);
		model.addAttribute("postPage", paging);
		model.addAttribute("kw", kw);
		model.addAttribute("bt", bt);
		return "post_list";
	}
	
	
	@GetMapping(value = "/free/detail/{id}") // 게시글 상세
	public String freeDetail(Model model, @PathVariable("id") Integer id, CommentForm commentForm) {
		
		postService.hit(id); // 조회수
		
		Post post = postService.getPost(id);
		model.addAttribute("post", post);
		model.addAttribute("bt", BoardType.FREE);
		return "post_detail";
	}
	
	@GetMapping(value = "/notice/detail/{id}") // 게시글 상세
	public String noticeDetail(Model model, @PathVariable("id") Integer id, CommentForm commentForm) {
		
		postService.hit(id); // 조회수
		
		Post post = postService.getPost(id);
		model.addAttribute("post", post);
		model.addAttribute("bt", BoardType.NOTICE);
		return "post_detail";
	}
	
	@GetMapping(value = "/free/create") // 게시글 작성 띄워주는
	public String freeCreate(PostForm postForm, Model model) {
		postForm.setBt(BoardType.FREE); 
	    model.addAttribute("bt", BoardType.FREE);
		
		return "post_form";
	}
	
	@PostMapping(value = "/free/create") // 게시글 작성 보내기
	public String freeCreate(@Valid PostForm postForm, BindingResult bindingResult,  Principal principal) {
		
		if(bindingResult.hasErrors()) {
			return "post_form";
		}
		
		User user = userService.getUser(principal.getName());
		
		postService.create(postForm.getSubject(), postForm.getContent(), user, postForm.getBt());
		
		return "redirect:/post/free/list";
	}
	
	@GetMapping(value = "/notice/create") // 게시글 작성 띄워주는
	public String noticeCreate(PostForm postForm, Model model) {
		
		postForm.setBt(BoardType.NOTICE); // 기본값 설정
	    model.addAttribute("bt", BoardType.NOTICE);
		return "post_form";
	}
	
	@PostMapping(value = "/notice/create") // 게시글 작성 보내기
	public String noticeCreate(@Valid PostForm postForm, BindingResult bindingResult,  Principal principal) {
		
		if(bindingResult.hasErrors()) {
			return "post_form";
		}
		
		User user = userService.getUser(principal.getName());
		
		postService.create(postForm.getSubject(), postForm.getContent(), user, postForm.getBt());
		
		return "redirect:/post/notice/list";
	}
	
	@GetMapping(value = "/free/modify/{id}") // 수정 띄워주기
	public String freeModify(PostForm postForm, @PathVariable("id")Integer id, Principal principal) {
		Post post = postService.getPost(id);
		
		if(!post.getWriter().getUsername().equals(principal.getName())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이없습니다.");
		}
		
		postForm.setSubject(post.getSubject());
		postForm.setContent(post.getContent());
		postForm.setBt(post.getBt());
		
		return "post_form";
	}
	
	@PostMapping(value = "/free/modify/{id}") // 수정내역 보내기
	public String freeModify(@Valid PostForm postForm, BindingResult bindingResult,
			Principal principal, @PathVariable("id")Integer id) {
		
		if(bindingResult.hasErrors()) {
			return "post_form";
		}
		
		Post post = postService.getPost(id);
		
		if(!post.getWriter().getUsername().equals(principal.getName())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
		}
		
		postService.modify(post, postForm.getSubject(), postForm.getContent());
		
		return String.format("redirect:/post/free/detail/%s", id);
	}
	
	@GetMapping(value = "/notice/modify/{id}") // 수정 띄워주기
	public String noticeModify(PostForm postForm, @PathVariable("id")Integer id, Principal principal) {
		Post post = postService.getPost(id);
		
		if(!post.getWriter().getUsername().equals(principal.getName())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이없습니다.");
		}
		
		postForm.setSubject(post.getSubject());
		postForm.setContent(post.getContent());
		postForm.setBt(post.getBt());
		
		return "post_form";
	}
	
	@PostMapping(value = "/notice/modify/{id}") // 수정내역 보내기
	public String noticeModify(@Valid PostForm postForm, BindingResult bindingResult,
			Principal principal, @PathVariable("id")Integer id) {
		
		if(bindingResult.hasErrors()) {
			return "post_form";
		}
		
		Post post = postService.getPost(id);
		
		if(!post.getWriter().getUsername().equals(principal.getName())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
		}
		
		postService.modify(post, postForm.getSubject(), postForm.getContent());
		
		return String.format("redirect:/post/notice/detail/%s", id);
	}
	
	@GetMapping(value = "/free/delete/{id}")
	public String freeDelete(Principal principal, @PathVariable("id")Integer id) {
		Post post = postService.getPost(id);
		if(!post.getWriter().getUsername().equals(principal.getName())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제 권한이 없습니다.");
		}
		postService.delete(post);
		return "redirect:/post/free/list";
	}
	
	@GetMapping(value = "/notice/delete/{id}")
	public String noticeDelete(Principal principal, @PathVariable("id")Integer id) {
		Post post = postService.getPost(id);
		if(!post.getWriter().getUsername().equals(principal.getName())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제 권한이 없습니다.");
		}
		postService.delete(post);
		return "redirect:/post/notice/list";
	}
	
	@GetMapping(value = "/free/vote/{id}")
	public String freeVote(@PathVariable("id")Integer id, Principal principal) {
		Post post = postService.getPost(id);
		User user = userService.getUser(principal.getName());
		postService.vote(post, user);
		return String.format("redirect:/post/free/detail/%s", id);
	}
	
	@GetMapping(value = "/free/novote/{id}")
	public String freeNotVote(@PathVariable("id")Integer id, Principal principal) {
		Post post = postService.getPost(id);
		User user = userService.getUser(principal.getName());
		postService.novote(post, user);
		return String.format("redirect:/post/free/detail/%s", id);
	}
	
	@GetMapping(value = "/notice/vote/{id}")
	public String noticeVote(@PathVariable("id")Integer id, Principal principal) {
		Post post = postService.getPost(id);
		User user = userService.getUser(principal.getName());
		postService.vote(post, user);
		return String.format("redirect:/post/notice/detail/%s", id);
	}
	
	@GetMapping(value = "/notice/novote/{id}")
	public String noticeNotVote(@PathVariable("id")Integer id, Principal principal) {
		Post post = postService.getPost(id);
		User user = userService.getUser(principal.getName());
		postService.novote(post, user);
		return String.format("redirect:/post/notice/detail/%s", id);
	}
	
	
	
}

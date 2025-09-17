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

import com.kkuk.jap.kkuk.post.BoardType;
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
    @PostMapping(value = "/create/{bt}/{id}")  // boardType과 Id를 함께 받도록 수정
    public String createComment(Model model,
                                @PathVariable("bt") String btStr,
                                @PathVariable("id") Integer id,
                                Principal principal,
                                @Valid CommentForm commentForm,
                                BindingResult bindingResult) {
        BoardType bt = BoardType.from(btStr); // 변환
        Post post = postService.getPost(id);
        User user = userService.getUser(principal.getName());

        if (bindingResult.hasErrors()) {
            model.addAttribute("post", post);
            model.addAttribute("boardType", bt);  // boardType도 다시 보내줘야 함
            return "post_detail";
        }

        Comment comment = commentService.create(post, commentForm.getContent(), user);
        return String.format("redirect:/post/%s/detail/%s#comment_%s", bt.name().toLowerCase(), id, comment.getId());
    }
    
    @PreAuthorize("isAuthenticated()")
    @PostMapping(value = "/create/{id}") // 아이디 값으로 댓글
    public String createComment(Model model, @PathVariable("id") Integer id, Principal principal, 
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
    
    @GetMapping("/{bt}/modify/{id}")
    public String commentModify(CommentForm commentForm,
                                 @PathVariable("bt") String btStr,
                                 @PathVariable("id") Integer id,
                                 Principal principal,
                                 Model model) {
        BoardType bt = BoardType.from(btStr); 

        Comment comment = commentService.getComment(id);

        if (!comment.getWriter().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정 권한이 없습니다.");
        }

        commentForm.setContent(comment.getContent());
        model.addAttribute("bt", bt);
        return "comment_form";
    }
    
    @PostMapping("/{bt}/modify/{id}")
    public String commentModify(@Valid CommentForm commentForm,
                                 BindingResult bindingResult,
                                 @PathVariable("bt") String btStr,
                                 @PathVariable("id") Integer id,
                                 Principal principal) {
        BoardType bt = BoardType.from(btStr);

        if (bindingResult.hasErrors()) {
            return "comment_form";
        }

        Comment comment = commentService.getComment(id);
        if (!comment.getWriter().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정 권한이 없습니다.");
        }

        commentService.modify(comment, commentForm.getContent());
        return String.format("redirect:/post/%s/detail/%s", bt.name().toLowerCase(), comment.getPost().getId());
    }
    
    @GetMapping("{bt}/delete/{id}")
    public String commentDelete(Principal principal,
                                 @PathVariable("bt") String btStr,
                                 @PathVariable("id") Integer id) {
        BoardType bt = BoardType.from(btStr); 

        Comment comment = commentService.getComment(id);

        if (!comment.getWriter().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제 권한이 없습니다.");
        }

        commentService.delete(comment);
        return String.format("redirect:/post/%s/detail/%s", bt.name().toLowerCase(), comment.getPost().getId());
    }
    
    @GetMapping("/{bt}/vote/{id}")
    public String commentVote(@PathVariable("bt") String btStr,
                               @PathVariable("id") Integer id,
                               Principal principal) {
        BoardType bt = BoardType.from(btStr);

        Comment comment = commentService.getComment(id);
        User user = userService.getUser(principal.getName());
        commentService.vote(comment, user);
        return String.format("redirect:/post/%s/detail/%s", bt.name().toLowerCase(), comment.getPost().getId());
    }

    @GetMapping("/{bt}/novote/{id}")
    public String commentNotVote(@PathVariable("bt") String btStr,
                                  @PathVariable("id") Integer id,
                                  Principal principal) {
        BoardType bt = BoardType.from(btStr); // 변환

        Comment comment = commentService.getComment(id);
        User user = userService.getUser(principal.getName());
        commentService.novote(comment, user);
        return String.format("redirect:/post/%s/detail/%s", bt.name().toLowerCase(), comment.getPost().getId());
    }
}

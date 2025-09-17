package com.kkuk.jap.kkuk.post;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.kkuk.jap.kkuk.DataNotFoundException;
import com.kkuk.jap.kkuk.comment.Comment;
import com.kkuk.jap.kkuk.user.User;


import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostService {

	@Autowired
	private final PostRepository postRepository;
	
	public List<Post> getList(){
		return postRepository.findAll();
	}
	
	public Post getPost(Integer id) {
		Optional<Post> pOptional = postRepository.findById(id);
		
		if(pOptional.isPresent()) {
			return pOptional.get();
		}else {
			throw new DataNotFoundException("post not found");
		}
	}
	public void create(String subject, String content, User user, BoardType bt) { // 글쓰기
		Post post = new Post();
		post.setSubject(subject);
		post.setContent(content);
		post.setCreatedate(LocalDateTime.now());
		post.setWriter(user);
		post.setBt(bt);
		postRepository.save(post);
	}
	
	public void modify(Post post, String subject, String content) {//글 수정하기
		post.setSubject(subject);
		post.setContent(content);
		post.setModifydate(LocalDateTime.now());
		postRepository.save(post);
	}
	
	public void delete(Post post) { // 삭제하기
		this.postRepository.delete(post);
	}
	
	public void hit(Integer id) { // 조회수 증가
		postRepository.updateHit(id);
	}
	
	public void vote(Post post, User user) { // 추천 비추천
		post.getVoter().add(user);
		postRepository.save(post);
	}
	
	public void novote(Post post, User user) { // 추천 비추천
		post.getNovoter().add(user);
		postRepository.save(post);
	}
	
	public Page<Post> getPagePost(int page, String kw, BoardType bt) {
	    int size = 10;
	    int startRow = page * size;
	    int endRow = startRow + size;

	    List<Post> postList;
	    int totalCount;

	    if (kw == null || kw.trim().isEmpty()) {
	        postList = postRepository.findPostWithPaging(bt.toString(), startRow, endRow);
	        // 게시판 목록 총 개수 계산
	        totalCount = (int) postRepository.count(); // 모든 게시글 대상
	    } else {
	        postList = postRepository.searchPostWithPaging(bt.toString(), kw, startRow, endRow);
	        totalCount = postRepository.countSearchResult(bt.toString(), kw);
	    }

	    return new PageImpl<>(postList, PageRequest.of(page, size), totalCount);
	}
//	public Page<Post> getPagePost(int page, String kw){
//		
//		int size = 10;
//		int startRow = page * size;
//		int endRow = startRow + size;
//		
//		List<Post> pagePostList = postRepository.findPostWithPaging(startRow, endRow);
//		
//		List<Post> searchPostList = postRepository.searchPostWithPaging(kw, startRow, endRow);
//		int totalSearchPost = postRepository.countSearchResult(kw);
//		
//		Page<Post> pageingList = new PageImpl<>(searchPostList, PageRequest.of(page, size), totalSearchPost);
//		return pageingList;
//	}
	
	private Specification<Post> search(String kw){
		
		return new Specification<Post>() {
			private static final long SerialVersionUID = 1L;
			
			@Override
			public Predicate toPredicate(Root<Post> p, CriteriaQuery<?> query, CriteriaBuilder cb) {
				// TODO Auto-generated method stub
				
				query.distinct(true); // distinct -> 중복제거
				Join<Post, User> u1 = p.join("writer", JoinType.LEFT); //question + siteUser left 테이블 조인
				Join<Post, Comment> c = p.join("commentList", JoinType.LEFT); //question + answer left 테이블 조인
				Join<Comment, User> u2 = c.join("writer", JoinType.LEFT); // answer + siteUser left 테이블 조인
				
				return cb.or(cb.like(p.get("subject"), "%" + kw + "%"), // 질문 제목에서 검색어 조회
						cb.like(p.get("content"), "%" + kw + "%"), // 질문 내용에서 검색어 조회
						cb.like(u1.get("username"), "%" + kw + "%"), // 질문 작성자에서 검색어 조회
						cb.like(c.get("content"), "%" + kw + "%"), // 답변 내용에서 검색어 조회
						cb.like(u2.get("username"), "%" + kw + "%") // 답변 작성자에서 검색어 조회
						)
					
					;
					
			}
		};
	}
}

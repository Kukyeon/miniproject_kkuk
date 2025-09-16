package com.kkuk.jap.kkuk.post;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;



import jakarta.transaction.Transactional;


public interface PostRepository extends JpaRepository<Post, Integer>{

	public Post findBySubject(String subject);
	
	public Post findBySubjectAndContent(String subject, String content);
	
	List<Post> findBySubjectLike(String keyword);
	
	Page<Post> findAllByOrderByCreatedateDesc(Pageable pageable);
	
	
	
	@Modifying
    @Transactional
    @Query(value = "UPDATE Post SET hit = hit + 1 WHERE id = :id", nativeQuery = true)
    public void updateHit(@Param("id") Integer id);
	
	@Query(
	         value = "SELECT * FROM ( " +
	                 " SELECT p.*, ROWNUM rnum FROM ( " +
	                 "   SELECT * FROM post ORDER BY createdate DESC " +
	                 " ) p WHERE ROWNUM <= :endRow " +
	                 ") WHERE rnum > :startRow",
	         nativeQuery = true)
		List<Post> findPostWithPaging(@Param("startRow") int startRow,
		                                       @Param("endRow") int endRow);
	
	Page<Post> findAll(Specification<Post> spec, Pageable pageable);
	
	//검색을 조회하는 페이징
	@Query(value = 
		       "SELECT * FROM ( " +
		       "   SELECT p.*, ROWNUM rnum FROM ( " +
		       "       SELECT DISTINCT p.* " +
		       "       FROM post p " +
		       "       LEFT OUTER JOIN users u1 ON p.writer_id = u1.id " +
		       "       LEFT OUTER JOIN comments c ON c.post_id = p.id " +
		       "       LEFT OUTER JOIN users u2 ON c.writer_id = u2.id " +
		       "       WHERE p.subject LIKE '%'||:kw||'%' " +
		       "          OR p.content LIKE '%'||:kw||'%' " +
		       "          OR u1.username LIKE '%'||:kw||'%' " +
		       "          OR c.content LIKE '%'||:kw||'%' " +
		       "          OR u2.username LIKE '%'||:kw||'%' " +
		       "       ORDER BY p.createdate DESC " +
		       "   ) p WHERE ROWNUM <= :endRow " +
		       ") WHERE rnum > :startRow", 
		       nativeQuery = true)
	
	List<Post> searchPostWithPaging(@Param("kw") String kw,
											@Param("startRow") int startRow,
            								@Param("endRow") int endRow);
	
	//검색 결과 총 갯수 반환
	@Query(value = 
		       "       SELECT COUNT(DISTINCT p.id) " +
		       "       FROM post p " +
		       "       LEFT OUTER JOIN users u1 ON p.writer_id = u1.id " +
		       "       LEFT OUTER JOIN comments c ON c.post_id = p.id " +
		       "       LEFT OUTER JOIN users u2 ON c.writer_id = u2.id " +
		       "       WHERE p.subject LIKE '%'||:kw||'%' " +
		       "          OR p.content LIKE '%'||:kw||'%' " +
		       "          OR u1.username LIKE '%'||:kw||'%' " +
		       "          OR c.content LIKE '%'||:kw||'%' " +
		       "          OR u2.username LIKE '%'||:kw||'%' ", 
		       nativeQuery = true)
	int countSearchResult(@Param("kw") String kw);
	
}

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
	
	//boardType 게시글 리스트 조회 / 페이징
	@Query("SELECT p FROM Post p WHERE p.bt = :bt ORDER BY p.createdate DESC")
    Page<Post> findByBoardType(@Param("bt") BoardType bt, Pageable pageable);
	

    // boardType 게시판/검색어 페이징
	@Query("SELECT p FROM Post p WHERE p.bt = :bt AND (p.subject LIKE CONCAT('%', :kw, '%') OR p.content LIKE CONCAT('%', :kw, '%')) ORDER BY p.createdate DESC")
    Page<Post> findByBoardTypeAndKeyword(@Param("bt") BoardType bt, @Param("kw") String kw, Pageable pageable);
	
	@Modifying
    @Transactional
    @Query(value = "UPDATE Post SET hit = hit + 1 WHERE id = :id", nativeQuery = true)
    public void updateHit(@Param("id") Integer id);
	
	@Query(
		    value = "SELECT * FROM ( " +
		            " SELECT p.id, p.subject, p.content, p.createdate, p.modifydate, p.hit, p.writer_id, p.bt, p.user_id, ROWNUM rnum " +
		            " FROM ( " +
		            "   SELECT * FROM post WHERE bt = :bt ORDER BY createdate DESC " +
		            " ) p WHERE ROWNUM <= :endRow " +
		            ") WHERE rnum > :startRow",
		    nativeQuery = true)
		List<Post> findPostWithPaging(@Param("bt") String bt,
		                              @Param("startRow") int startRow,
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
			   "       WHERE p.bt = :bt " +  // 게시판 타입 조건
			   "         AND (p.subject LIKE '%'||:kw||'%' " +
			   "          OR p.content LIKE '%'||:kw||'%' " +
			   "          OR u1.username LIKE '%'||:kw||'%' " +
			   "          OR c.content LIKE '%'||:kw||'%' " +
			   "          OR u2.username LIKE '%'||:kw||'%') " +
			   "       ORDER BY p.createdate DESC " +
			   "   ) p WHERE ROWNUM <= :endRow " +
			   ") WHERE rnum > :startRow", 
			   nativeQuery = true)
	List<Post> searchPostWithPaging(@Param("bt") String bt,   // BoardType를 String으로
			                               @Param("kw") String kw,
			                               @Param("startRow") int startRow,
			                               @Param("endRow") int endRow);

	
	//검색 결과 총 갯수 반환
	@Query(value = 
			   "SELECT COUNT(DISTINCT p.id) " +
			   "FROM post p " +
			   "LEFT OUTER JOIN users u1 ON p.writer_id = u1.id " +
			   "LEFT OUTER JOIN comments c ON c.post_id = p.id " +
			   "LEFT OUTER JOIN users u2 ON c.writer_id = u2.id " +
			   "WHERE p.bt = :bt " +  // 게시판 타입 조건
			   "  AND (p.subject LIKE '%'||:kw||'%' " +
			   "    OR p.content LIKE '%'||:kw||'%' " +
			   "    OR u1.username LIKE '%'||:kw||'%' " +
			   "    OR c.content LIKE '%'||:kw||'%' " +
			   "    OR u2.username LIKE '%'||:kw||'%')", 
			   nativeQuery = true)
	int countSearchResult(@Param("bt") String bt, @Param("kw") String kw);

	
}

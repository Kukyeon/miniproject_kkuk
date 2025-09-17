package com.kkuk.jap.kkuk.post;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostForm {


	
	@NotEmpty(message = "제목은 필수작성입니다.")
	@Size(max = 200, message = "제목은 최대 200글자 입니다.")
	private String subject;
	
	@NotEmpty(message = "내용은 필수 작성입니다.")
	@Size(max = 499, message = "내용은 최대 500글자 까지입니다.")
	private String content;
	
	private BoardType bt;
	
}

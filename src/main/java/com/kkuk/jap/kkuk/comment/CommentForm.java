package com.kkuk.jap.kkuk.comment;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentForm {

	@NotEmpty(message = "내용은 필수항목입니다.")
	@Size(min = 1)
	private String content;
	
	
}

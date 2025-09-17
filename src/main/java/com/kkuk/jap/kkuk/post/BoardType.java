package com.kkuk.jap.kkuk.post;

public enum BoardType {
	
	NOTICE,
	FREE;
	
	 public static BoardType from(String str) {
	        for (BoardType b : values()) {
	            if (b.name().equalsIgnoreCase(str)) {
	                return b;
	            }
	        }
	        throw new IllegalArgumentException("Invalid BoardType: " + str);
	    }
}

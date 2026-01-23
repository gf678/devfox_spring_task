package com.littlestar.task.domain;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@Data
public class PostForm {
    private Long postId;       // 수정/삭제 시 식별자
    private String title;      // 제목
    private String content;    // 내용
    private List<MultipartFile> images; // 이미지 업로드용
}
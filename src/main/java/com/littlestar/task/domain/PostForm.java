package com.littlestar.task.domain;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

// 投稿登録および編集時にクライアントから受け取るDTO
@Data
public class PostForm {

    // 投稿ID（編集時に使用、新規登録時はnull）
    private Long postId;

    // 投稿タイトル
    private String title;

    // 投稿内容
    private String content;

    // アップロードする画像ファイルのリスト（multipart/form-data）
    private List<MultipartFile> images;
}
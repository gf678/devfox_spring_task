package com.littlestar.task.service;

import com.littlestar.task.entity.Image;
import com.littlestar.task.entity.Post;
import com.littlestar.task.entity.User;
import org.springframework.web.multipart.MultipartFile;

// 画像ファイルのアップロードおよび物理的保存を担当するサービスインターフェース
public interface ImageService {

    // クライアントから送信された画像ファイルをサーバーの指定パスに保存
    String savePostImage(MultipartFile file, Post post, int order);
    Image saveProfileImage(MultipartFile file, User user);
}
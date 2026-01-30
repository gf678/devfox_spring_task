package com.littlestar.task.service;

import org.springframework.web.multipart.MultipartFile;

// 画像ファイルのアップロードおよび物理的保存を担当するサービスインターフェース
public interface ImageService {

    // クライアントから送信された画像ファイルをサーバーの指定パスに保存
    String saveImage(MultipartFile file);
}
package com.littlestar.task.Controller;

import com.littlestar.task.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class ImageController {

    // 1. 画像サービスBeanの登録
    private final ImageService imageService;

    // 2. クライアントから画像を受け取り、サーバーに保存してアクセス可能なURLを返却
    @PostMapping("/api/image/upload")
    public String uploadImage(@RequestParam("file") MultipartFile file) {

        // 2-1. 画像サービスを呼び出して物理ストレージにファイルを保存
        String imageUrl = imageService.saveImage(file);

        // 2-2. URLが正常に生成された場合は返却、失敗した場合は "error" を返してフロントで例外処理
        return (imageUrl != null) ? imageUrl : "error";
    }
}

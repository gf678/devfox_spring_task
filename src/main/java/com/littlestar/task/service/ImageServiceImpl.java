package com.littlestar.task.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

// サーバーのローカルディレクトリに画像ファイルを物理的に保存するサービス実装
@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {

    // ファイルが実際に保存されるサーバー内部の絶対パス
    private final String uploadPath = "/home/uploads/";

    // 画像ファイルを保存し、DBに記録するWebアクセスパスを返す
    @Override
    public String saveImage(MultipartFile file) {
        // アップロードされたファイルが空の場合は処理を中断し、nullを返す
        if (file == null || file.isEmpty()) {
            return null;
        }

        // 一意のファイル名を生成 (UUIDを使用)
        // 元のファイル名から拡張子を抽出
        String originalName = file.getOriginalFilename();
        String extension = "";
        if (originalName != null && originalName.contains(".")) {
            extension = originalName.substring(originalName.lastIndexOf("."));
        }

        // ファイル名の重複を避けるためにランダムなUUIDと拡張子を結合
        String savedName = UUID.randomUUID() + extension;

        // 保存フォルダの確認および作成
        // 指定されたアップロードパスにフォルダが存在しない場合、下位フォルダまで一度に作成
        File folder = new File(uploadPath);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        // ファイルの物理的保存
        try {
            // transferTo()を使用して、メモリまたは一時ディレクトリにあるファイルを実際のパスに移動
            file.transferTo(new File(uploadPath + savedName));

            // クライアントがWebブラウザからアクセス可能な仮想パスを返す
            // 物理パスは /home/uploads/ だが、外部公開は /uploads/ に設定
            return "/uploads/" + savedName;
        } catch (IOException e) {
            // 保存に失敗した場合はランタイム例外をスローし、トランザクションのロールバックなどを誘導
            throw new RuntimeException("画像の保存に失敗しました: " + e.getMessage());
        }
    }
}

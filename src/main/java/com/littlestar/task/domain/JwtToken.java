package com.littlestar.task.domain;

import lombok.Builder;
import lombok.Data;

// クライアントに発行するJWTトークン情報を保持するDTO
@Data
@Builder
public class JwtToken {

    // トークンの種類（例: "Bearer"）
    private String grantType;

    // 実際のAPIリクエストで認証に使用するアクセストークン
    private String accessToken;

    // アクセストークンが期限切れになった際に再発行に使用するリフレッシュトークン
    private String refreshToken;
}

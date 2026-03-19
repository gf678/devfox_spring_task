package com.littlestar.task.Exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "P001", "投稿が見つかりません。"),
    COMMENT_NOT_FOUND1(HttpStatus.NOT_FOUND, "C001","コメントが存在しません。"),
    COMMENT_NOT_FOUND2(HttpStatus.NOT_FOUND, "C002","親コメントが存在しません。"),
    BOARD_NOT_FOUND(HttpStatus.NOT_FOUND, "B001", "掲示板が見つかりません。"),
    USER_NOT_FOUND1(HttpStatus.NOT_FOUND, "U001", "ユーザーが見つかりません。"),
    USER_NOT_FOUND2(HttpStatus.NOT_FOUND, "U002", "存在しないユーザーです。"),
    UNAUTHORIZED1(HttpStatus.UNAUTHORIZED, "A001", "ログインが必要です。"),
    UNAUTHORIZED2(HttpStatus.UNAUTHORIZED, "A002", "無効なトークンです。"),
    UNAUTHORIZED3(HttpStatus.UNAUTHORIZED, "A003", "トークンの有効期限が切れました。"),
    FORBIDDEN1(HttpStatus.FORBIDDEN , "A002" , "投稿の修正／削除権限がありません。"),
    FORBIDDEN2(HttpStatus.FORBIDDEN , "A003" , "最高管理者の権限は変更できません。");
    
    private final HttpStatus status;
    private final String code;
    private final String message;

    ErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }

    public HttpStatus getStatus() { return status; }
    public String getCode() { return code; }
    public String getMessage() { return message; }
}

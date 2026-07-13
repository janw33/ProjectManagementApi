package com.janwypych.ProjectManagementApi.exceptions.comment;

public class CommentNotFoundException extends RuntimeException{
    public CommentNotFoundException() {super("Comment not found");}
}

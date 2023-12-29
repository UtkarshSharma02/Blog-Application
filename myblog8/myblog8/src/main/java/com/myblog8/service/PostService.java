package com.myblog8.service;

import com.myblog8.payload.PostDto;

import java.util.List;

public interface PostService {
    public PostDto createPost(PostDto postDto);
    public PostDto updatePost(PostDto postDto,Long id);
    public PostDto retrieveById(Long id);
    public List<PostDto> retrieveAll(int pageNo,int pageSize,String sortBy, String sortDirect);

    public void deleteById(Long id);
}
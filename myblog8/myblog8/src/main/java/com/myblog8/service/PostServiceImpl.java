package com.myblog8.service;


import com.myblog8.exception.PostNotFound;
import com.myblog8.util.EmailService;
import com.myblog8.util.SmsService;
import org.modelmapper.ModelMapper;
import com.myblog8.entity.Post;
import com.myblog8.payload.PostDto;
import com.myblog8.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostServiceImpl implements PostService {
    @Autowired
    private PostRepository postRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private EmailService emailService;

    @Autowired
    private SmsService smsService;

    @Override
    public PostDto createPost(PostDto postDto){
            Post post = mapToEntity(postDto);
            Post savedPost=postRepository.save(post);
            emailService.sendEmail("tarungautam98@gmail.com", "Test", "Test Email Sending");
            smsService.sendSms("+918223086877", "Account created so sending sms");
            PostDto dto = mapToDto(savedPost);
            return dto;
    }

    @Override
    public PostDto retrieveById(Long id){
        Post post = postRepository.findById(id).orElseThrow(
                ()->new PostNotFound("Post not found with id:"+id)
        );
        PostDto dto = mapToDto(post);
        return dto;
    }
    @Override
    public List<PostDto> retrieveAll(int pageNo,int pageSize,String sortBy, String sortDirect){
        //condtion?output1:output2
        Sort sort = sortDirect.equalsIgnoreCase(Sort.Direction.ASC.name())?
                Sort.by(sortBy).ascending():
                Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNo,pageSize,sort);
        Page page = postRepository.findAll(pageable);
        List<Post> posts = page.getContent();
        List<PostDto> dto = posts.stream().map(n->mapToDto(n)).collect(Collectors.toList());
        return dto;
    }

    @Override
    public PostDto updatePost(PostDto postDto,Long id){
        //Fetch post by id
            Post post = postRepository.findById(id).orElseThrow(()->new PostNotFound("Post not found with id:"+id));
        //Update info of dto to post
            post.setName(postDto.getName());
            post.setDescription(postDto.getDescription());
        //save the updated post back in repository
            Post savedPost=postRepository.save(post);
            // converting back entity to dto
            PostDto dto = mapToDto(savedPost);
            return dto;
    }

    @Override
    public void deleteById(Long id){
        postRepository.deleteById(id);
    }

    public Post mapToEntity(PostDto postDto){
        Post post = modelMapper.map(postDto,Post.class);
        return post;
    }
    public PostDto mapToDto(Post post){
        PostDto postDto = modelMapper.map(post,PostDto.class);
        return postDto;
    }
}

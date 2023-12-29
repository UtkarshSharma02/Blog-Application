package com.myblog8;


import com.myblog8.entity.Post;
import com.myblog8.exception.PostNotFound;
import com.myblog8.payload.PostDto;
import com.myblog8.repository.PostRepository;
import com.myblog8.service.PostServiceImpl;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(classes={PostServiceImplTests.class})
public class PostServiceImplTests {
    @Mock
    private PostRepository postRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private PostServiceImpl postService;

    private PostDto postDto;
    private Post post;

    @BeforeEach
    public void setUp() {
        postDto = new PostDto();
        postDto.setId(1L);
        postDto.setName("my blog");
        postDto.setDescription("my description");

        post = new Post();
        post.setId(1L);
        post.setName("my blog");
        post.setDescription("my description");
    }

    @AfterEach
    public void tearDown() {
        postDto = null;
        post = null;
    }

    @Test
    @Order(1)
    public void testCreatePost() {
        when(modelMapper.map(postDto, Post.class)).thenReturn(post);
        when(postRepository.save(post)).thenReturn(post);
        when(modelMapper.map(post, PostDto.class)).thenReturn(postDto);

        PostDto result = postService.createPost(postDto);

        assertEquals(postDto, result);
    }

    @Test
    @Order(2)
    public void testRetrieveById_WhenPostExists() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(modelMapper.map(post, PostDto.class)).thenReturn(postDto);

        PostDto result = postService.retrieveById(1L);

        assertEquals(postDto, result);
    }

    @Test
    @Order(3)
    public void testRetrieveById_WhenPostDoesNotExist() {
        when(postRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(PostNotFound.class, () -> postService.retrieveById(1L));
    }

    @Test
    @Order(4)
    public void testRetrieveAll() {
        int pageNo = 0;
        int pageSize = 10;
        String sortBy = "name";
        String sortDirect = "asc";

        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(sortBy).ascending());
        Page<Post> page = new PageImpl<>(Collections.emptyList());
        when(postRepository.findAll(pageable)).thenReturn(page);

        List<PostDto> result = postService.retrieveAll(pageNo, pageSize, sortBy, sortDirect);

        assertTrue(result.isEmpty());
    }

    @Test
    @Order(5)
    public void testUpdatePost_WhenPostExists() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(postRepository.save(post)).thenReturn(post);
        when(modelMapper.map(post, PostDto.class)).thenReturn(postDto);

        PostDto result = postService.updatePost(postDto, 1L);

        assertEquals(postDto, result);
    }

    @Test
    @Order(6)
    public void testUpdatePost_WhenPostDoesNotExist() {
        when(postRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(PostNotFound.class, () -> postService.updatePost(postDto, 1L));
    }

    @Test
    @Order(7)
    public void testDeleteById() {
        Long postId = 1L;

        postService.deleteById(postId);

        verify(postRepository, times(1)).deleteById(postId);
    }
}


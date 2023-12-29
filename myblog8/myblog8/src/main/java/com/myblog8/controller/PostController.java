package com.myblog8.controller;

import com.myblog8.payload.PostDto;
import com.myblog8.service.PostService;
import com.myblog8.util.EmailService;
import com.myblog8.util.SmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/post")
@CrossOrigin(origins = "http://localhost:3000")
public class PostController {

    @Autowired
    private PostService postService;



    //http://localhost:8080/api/post

//    @PreAuthorize("hasAnyRole('ADMIN','WRITER')")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<?> createPost(@Valid @RequestBody PostDto postDto, BindingResult result){
        if(result.hasErrors()){
                return new ResponseEntity<>(result.getFieldError().getDefaultMessage(),HttpStatus.INTERNAL_SERVER_ERROR); //202
        }
        PostDto dto = postService.createPost(postDto);

        return new ResponseEntity<>(dto, HttpStatus.CREATED); //201
    }

    //http://localhost:8080/api/post/1

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<PostDto> updatePost(@RequestBody PostDto postDto,@PathVariable("id") Long id){
        PostDto dto = postService.updatePost(postDto,id);
        return new ResponseEntity<>(dto,HttpStatus.OK);  //200
    }

    //http://localhost:8080/api/post/1
    @GetMapping("/{id}")
    public ResponseEntity<PostDto> retrieveById(@PathVariable("id") Long id){
        PostDto dto = postService.retrieveById(id);
        return new ResponseEntity<>(dto,HttpStatus.OK);  //200
    }

    //http://localhost:8080/api/post/pageNo=1&pageSize=2&sortBy="id"&sortDirect="asc"
    @GetMapping
    public ResponseEntity<List<PostDto>> retrieveAll(
            @RequestParam(value="pageNo",defaultValue="0",required=false) int pageNo,
            @RequestParam(value="pageSize",defaultValue="3",required=false) int pageSize,
            @RequestParam(value="sortBy",defaultValue="id",required=false) String sortBy,
            @RequestParam(value="sortDirect",defaultValue="asc",required=false) String sortDirect
    ){
        List<PostDto> dto = postService.retrieveAll(pageNo,pageSize,sortBy,sortDirect);
        return new ResponseEntity<>(dto,HttpStatus.OK);
    }


    //http://localhost:8080/api/post/3


//    @PreAuthorize("hasRole('ADMIN')")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteById(@PathVariable("id") Long id){
        postService.deleteById(id);
        return new ResponseEntity<>("Post has been deleted with id:"+id,HttpStatus.OK);
    }
}

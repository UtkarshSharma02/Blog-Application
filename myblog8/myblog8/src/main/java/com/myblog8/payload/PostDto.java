package com.myblog8.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
public class PostDto {
    private Long id;

    @NotEmpty
    @Size(min=2,message="Length of name must be atleast 2 characters")
    private String name;

    @NotEmpty
    @Size(min=3,message="Length of description must be atleast 3 characters")
    private String description;
}

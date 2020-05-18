package com.example.myappapialbums.data;

import lombok.Data;

@Data
public class AlbumEntity {
    private long id;
    private String albumId;
    private String userId; 
    private String name;
    private String description; 

}

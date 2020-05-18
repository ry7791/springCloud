package com.example.myappapialbums.service;


import com.example.myappapialbums.data.AlbumEntity;

import java.util.List;

public interface AlbumsService {
    List<AlbumEntity> getAlbums(String userId);
}

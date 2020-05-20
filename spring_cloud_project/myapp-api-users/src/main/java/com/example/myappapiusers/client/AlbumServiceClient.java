package com.example.myappapiusers.client;

import com.example.myappapiusers.model.AlbumResponseModel;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.List;

@FeignClient(name = "albums-ws", fallback = AlbumsFallback.class)
public interface AlbumServiceClient {

    @GetMapping("/users/{id}/albums")
    List<AlbumResponseModel> getAlbums(@PathVariable String id);
}
@Component
class AlbumsFallback implements AlbumServiceClient{
    @Override
    public List<AlbumResponseModel> getAlbums(String id) {
            return new ArrayList<>();
        }
}

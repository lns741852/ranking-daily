package com.song.rerank.utils;

import com.song.rerank.enums.AnonymousPaths;

import java.util.stream.Stream;

public class EnumUtils {


    public  static String[] getAnonymousPaths(){
        return Stream.of(AnonymousPaths.values()).map(AnonymousPaths::getPath).toArray(String[]::new);
    }
}

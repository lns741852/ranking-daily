package com.song.rerank.mapper;



import java.util.List;

public interface BaseMapper<D, E> {

    /**
     * DTO轉Entity
     * @param dto /
     * @return /
     */
    E toEntity(D dto);

    /**
     * Entity轉DTO
     * @param entity /
     * @return /
     */
    D toDto(E entity);

    /**
     * DTO集合轉Entity集合
     * @param dtoList /
     * @return /
     */
    List <E> toEntity(List<D> dtoList);

    /**
     * Entity集合轉DTO集合
     * @param entityList /
     * @return /
     */
    List <D> toDto(List<E> entityList);
}
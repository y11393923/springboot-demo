package com.zyy.dao;

import com.zyy.entity.Goods;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;


public interface GoodsDao extends JpaRepository<Goods,Integer>{

    public Goods findById(int id);

    @Modifying
    @Transactional
    @Query(value = "update goods set number=number-:num where id=:id",nativeQuery = true)
    public int updateById(@Param("id") int id, @Param("num") int num);
}

package com.zyy.dao2;

import com.zyy.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Author:zhouyuyang
 * @Description:
 * @Date: Created in 10:08 2019/5/29
 */
public interface UserDao2 extends JpaRepository<User,Integer>{
}

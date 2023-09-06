package com.yj2025.sample.service;

import com.yj2025.sample.jdbc.entity.TestUser;
import com.yj2025.sample.jdbc.repository.TestUserRepository;
import com.yj2025.sample.jpa.entity.JpaUser;
import com.yj2025.sample.jpa.repository.JpaUserRepository;
import com.yj2025.sample.mybatis.entity.User;
import com.yj2025.sample.mybatis.mapper.UserMapper;
import com.yj2025.dynamic.tenant.Tenant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;

@Service
@Transactional
public class UserService {

    @Autowired
    private JpaUserRepository jpaUserRepository;
    @Autowired
    private TestUserRepository testUserRepository;
    @Autowired
    private UserMapper userMapper;

    public Integer addByJpa(Integer count) {
        System.out.println(TransactionSynchronizationManager.isActualTransactionActive());
        for (int i = 0; i < count; i++) {
            JpaUser user = new JpaUser();
            user.setCode("code" + i);
            user.setName("张三丰");
            user.setEmail("张三丰@qq.com");
            user.setEntCode("ent001");
            jpaUserRepository.save(user);
        }
        return count;
    }

    public List<JpaUser> findListByJpa(String entCode) {
        return jpaUserRepository.findList(entCode);
    }

    @Tenant("#{#entCode}")
    public Integer addByJdbc(String entCode, Integer count) {
        for (int i = 0; i < count; i++) {
            TestUser user = new TestUser();
            user.setCode("code" + i);
            user.setName("张三丰");
            user.setEmail("张三丰@qq.com");
            user.setEntCode(entCode);
            testUserRepository.save(user);
        }
        return count;
    }

    @Tenant("#{#entCode}")
    public List<TestUser> findListByJdbc(String entCode, String codeLike) {
        return testUserRepository.findList(entCode, codeLike);
    }

    public Integer addByMybatis(Integer count) {
        for (int i = 0; i < count; i++) {
            User user = new User();
            user.setCode("code" + i);
            user.setName("张三丰");
            user.setEmail("张三丰@qq.com");
            user.setEntCode("ent001");
            userMapper.insert(user);
        }
        return count;
    }

    public Page<User> findPageByMybatis(String entCode) {
        return userMapper.findByOrigin(entCode, PageRequest.of(0, 200));
    }
}

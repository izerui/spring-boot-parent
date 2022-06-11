package com.yj2025.jpa.service;

import com.yj2025.jpa.entity.User;
import com.yj2025.jpa.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public void add() {
        System.out.println(TransactionSynchronizationManager.isActualTransactionActive());
        userRepository.deleteAll();
        for (int i = 0; i < 20; i++) {
            User user = new User();
            user.setCode("code" + i);
            user.setName("张三丰");
            user.setEmail("张三丰@qq.com");
            userRepository.save(user);
        }
    }
}

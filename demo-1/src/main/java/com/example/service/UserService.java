package com.example.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.entity.User;
import com.example.mapper.RoleMapper;
import com.example.mapper.UserMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    private final UserMapper userMapper;
    private final RoleMapper roleMapper;
    private final SqlSessionFactory sqlSessionFactory;
    private final PasswordEncoder passwordEncoder;
    
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userMapper.getAllUsers();
    }

    @Transactional(readOnly = true)
	public User findByUserId(String userId) {
		User user = userMapper.findByUserId(userId);

		List<String> roles = roleMapper.getRolesByUserId(userId);
		user.setRoles(roles);

		return user;
	}
    
	public void createUser(User user, List<String> roles) {
		if (!user.getPassword().isEmpty()) {
			user.setPassword(passwordEncoder.encode(user.getPassword()));
		}

		userMapper.insertUser(user);
		
		Long usersId = user.getId();
		for (String role : roles) {
			int roleId = roleMapper.getRoleId(role);
			Map<String, Object> param = new HashMap<String, Object>();
			param.put("usersId", usersId);
			param.put("roleId", roleId);
			roleMapper.insertUserRoles(param);
		}
	}

    public User updateUser(User user) {
    	String userId = user.getUserId();
        User existing = userMapper.findByUserId(userId);
		if (existing != null) {
			existing.setUserName(user.getUserName());
			
			if (!user.getPassword().isEmpty()) {
				existing.setPassword(passwordEncoder.encode(user.getPassword()));
			}

			userMapper.updateUser(existing);
		}
        return existing;
    }

    public void deleteUser(User user) {
    	userMapper.deleteUser(user);
    }
    
    public void updateRefreshToken(String userId, String refreshToken) {
    	userMapper.updateRefreshToken(userId, refreshToken);
    }
    
    public int batchInsertUsers() {
        SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH, false);

        int dataCount = 10000;
        int batchSize = 1000;
		try {
			List<User> userList = new ArrayList<User>();
			for (int i = 1; i <= 10_000; i++) {
				User user = new User();
				user.setUserId("users" + i);
				user.setPassword("pass" + i);
				user.setUserName("users" + i + "@mail.com");
				userList.add(user);
			}

			for (int i = 0; i < dataCount; i += batchSize) {
				int end = Math.min(i + batchSize, userList.size());
				List<User> subList = userList.subList(i, end);
				userMapper.insertUsers(subList);
				sqlSession.flushStatements(); // 쿼리 실행
				sqlSession.clearCache();
			}
			sqlSession.commit();
		} catch (Exception e) {
            sqlSession.rollback();
            throw new RuntimeException("Batch insert failed", e);
        } finally {
            sqlSession.close();
        }
        
        return dataCount;
    }
}
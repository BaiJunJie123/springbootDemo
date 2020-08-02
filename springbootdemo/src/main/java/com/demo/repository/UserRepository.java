package com.demo.repository;

import com.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Integer> {

	User findTopByUsername(String username);

	@Query(nativeQuery = true,value="select count(c.id) from mall_user c where c.username = :username")
	int findCount(String username);

	@Modifying
	@Query(nativeQuery = true,value="update mall_user u set u.job_num = :jobNum where u.id = :id ")
	int updateUserjobNum(int jobNum,int id);

	User findOneById(int id);
}

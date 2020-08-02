package com.demo.repository;

import com.demo.model.Job;
import com.demo.model.Tixian;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Map;

public interface TixianRepository extends JpaRepository<Tixian, Integer> {

	@Query(nativeQuery = true, value = "select sum(c.money) from mall_tixian c where c.user_id = :userId and c.check_status = 1")
	Double findAllUserMoney(int userId);

	@Query(nativeQuery = true, value = "select DATE_FORMAT(c.create_at,'%Y-%m-%d'),c.money,( CASE c.check_status WHEN 0 THEN '审核中' WHEN 1 THEN '已打款' ELSE '驳回' END ),c.zfb_account from mall_tixian c where c.user_id = :userId order by c.id desc")
	List<Object> findUserTiXianInfo(int userId);

	@Query(nativeQuery = true, value = "select count(c.id) from mall_tixian c where  c.check_status = :checkStatus")
	public int findAllUserTiXianPageCount(int checkStatus);

	@Query(nativeQuery = true, value = "select c.id,c.user_id,c.money,c.check_status,c.create_at,c.update_at,c.zfb_account,c.user_name from mall_tixian c where c.check_status = :checkStatus  order by c.id desc limit :limitStart,:countPerpage")
	public List<Tixian> findAllUserTiXianPage(int checkStatus, int limitStart, int countPerpage);
}

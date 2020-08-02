package com.demo.repository;

import com.demo.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Integer> {

	@Query(nativeQuery = true, value = "select DATE_FORMAT(c.create_at,'%Y-%m-%d'),c.money,( CASE c.vip_type WHEN 1 THEN 'VIP' WHEN 2 THEN 'SVIP' ELSE '区域负责人' END ),( CASE c.check_status WHEN 0 THEN '审核中' WHEN 1 THEN '审核通过' ELSE '审核驳回' END ) from mall_order c where c.user_id = :userId order by c.id desc")
	List<Object> findAllByUserIdinfo(int userId);

	@Query(nativeQuery = true, value = "select c.id,c.vip_type,c.money,c.user_id,c.user_name,DATE_FORMAT(c.create_at,'%Y-%m-%d') as 'create_at',c.update_at,c.check_status  from mall_order c  where c.check_status = :checkStatus order by c.id desc limit :limitStart,:countPerpage")
	List<Order> findAllOrdercheck(int checkStatus, int limitStart, int countPerpage);

	@Query(nativeQuery = true, value = "select count(c.id) from mall_order c where c.check_status = :checkstatus")
	int findcheckOrderCount(int checkstatus);

}

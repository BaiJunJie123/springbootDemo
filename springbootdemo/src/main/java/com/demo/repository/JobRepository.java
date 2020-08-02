package com.demo.repository;

import com.demo.model.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface JobRepository extends JpaRepository<Job, Integer> {

	@Query(nativeQuery = true, value = "SELECT count(c.id) FROM mall_job c where   c.type = :type and c.id not IN (SELECT j.job_id FROM mall_finish_job j WHERE j.check_status = 0 OR j.check_status = 1 )")
	public int findCount(int type);


	@Query(nativeQuery = true, value = "select c.id,c.name,c.url,c.ok_number,c.type,c.create_at,c.update_at,c.number,c.version,c.money,c.boss_money from mall_job c where c.type = :type and c.ok_number > 0 and c.id not IN (SELECT j.job_id FROM mall_finish_job j WHERE j.check_status = 0 OR j.check_status = 1 )  order by c.id desc limit :limitStart,:countPerpage")
	public List<Job> findAllByPage(int type, int limitStart, int countPerpage);

	@Modifying
	@Query(nativeQuery = true, value = "update mall_job m set m.ok_number = :okNumber where m.version = :version and m.id = :jobId ")
	public int updateByjJobId(int okNumber, int version, int jobId);
}

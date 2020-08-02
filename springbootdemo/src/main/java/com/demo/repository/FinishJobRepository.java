package com.demo.repository;

import com.demo.model.FinishJob;
import org.hibernate.query.NativeQuery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.lang.annotation.Native;
import java.util.List;
import java.util.Map;

public interface FinishJobRepository extends JpaRepository<FinishJob, Integer> {

	List<FinishJob> findAllByJobTypeAndUserId(int jobType, int userId);

	@Query(nativeQuery = true, value = "select count(c.id) from mall_finish_job c where c.check_status = :checkstatus and c.user_id = :userId")
	public int findCount(int checkstatus, int userId);

	@Query(nativeQuery = true, value = "select c.id,c.job_id,c.user_id,c.check_status,c.job_type,c.create_at,c.update_at,c.file_id from mall_finish_job c where c.check_status = :checkstatus and c.user_id = :userId  order by c.id desc limit :limitStart,:countPerpage")
	List<FinishJob> findAllByPage(int checkstatus, int userId, int limitStart, int countPerpage);

	@Query(nativeQuery = true, value = "select count(c.id) from mall_finish_job c where c.check_status = :checkstatus")
	int findFinishJob(int checkstatus);

	//任务审核
	@Query(nativeQuery = true, value = "select c.id as 'id',c.user_id as 'userId',f.context_path as 'path',j.money as 'money' from mall_finish_job c,mall_file f,mall_job j where c.file_id = f.id and c.job_id = j.id and  c.check_status = :checkstatus order by c.id desc limit :limitStart,:countPerpage")
	List<Map<String,Object>> findcheckJobPage(int checkstatus, int limitStart, int countPerpage);

	@Query(nativeQuery = true, value="select j.file_id from mall_finish_job j where j.check_status in (1,2) ")
	List<Integer> findFileIdByFindishJob();
}

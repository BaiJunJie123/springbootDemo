package com.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Table(name = "mall_finish_job")
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FinishJob {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) // 自增长策略
	private int id;

	@Column(name = "job_id")
	private int jobId;

	@Column(name = "user_id")
	private int userId;

	@Column(name = "check_status")
	private int checkStatus;

	@Column(name = "job_type")
	private int jobType;

	@Column(name = "create_at")
	private Date createAt = new Date();

	@Column(name = "update_at")
	private Date updateAt = new Date();

	@Column(name = "file_id")
	private int fileId;

}

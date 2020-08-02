package com.demo.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "mall_tixian")
@Entity
public class Tixian {

	@Id // 主键
	@GeneratedValue(strategy = GenerationType.IDENTITY) // 自增长策略
	private int id;

	@Column(name = "user_id")
	private int userId;

	private Double money;

	@Column(name = "check_status")
	private int checkStatus;

	@Column(name = "create_at")
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date createAt = new Date();

	@Column(name = "update_at")
	private Date updateAt = new Date();

	@Column(name = "zfb_account")
	private String zfbAccount;

	@Column(name = "user_name")
	private String userName;
}

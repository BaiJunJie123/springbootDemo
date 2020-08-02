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
@Table(name = "mall_order")
@Entity
public class Order {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) // 自增长策略
	private int id;

	@Column(name = "vip_type")
	private int vipType;

	private Double money;

	@Column(name = "user_id")
	private int userId;

	@Column(name = "user_name")
	private String userName;

	@Column(name = "create_at")
	@JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd")
	private Date createAt = new Date();

	@Column(name = "update_at")
	private Date updateAt = new Date();

	@Column(name = "check_status")
	private int checkStatus;
}

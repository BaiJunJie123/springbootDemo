package com.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Table(name = "mall_job")
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Job {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) // 自增长策略
	private int id;

	private String name;

	private String url;

	@Column(name = "ok_number")
	private  int okNumber;

	private int type;

	@Column(name = "create_at")
	private Date createAt = new Date();

	@Column(name = "update_at")
	private Date updateAt = new Date();

	private int number;

	private int version;

	private Double money;

	@Column(name = "boss_money")
	private Double bossMoney;

}

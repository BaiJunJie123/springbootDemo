package com.demo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "mall_user")
@Entity
public class User implements Serializable {

	@Id // 主键
	@GeneratedValue(strategy = GenerationType.IDENTITY) // 自增长策略
	private int id;

	private String username;

	//@JsonIgnore  // 返回前端不显示密码
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)   // 仅限写入额权限
	private String password;

	private String image;

	@Column(name = "is_admin")
	private int isAdmin;

	@Column(name = "vip_type")
	private int vipType;

	@Column(name = "create_date")
	private Date createDate = new Date();

	@Column(name = "update_date")
	private Date updateDate = new Date();

	@Column(name = "parent_id")
	private int parentId;

	@Column(name = "job_num")
	private int jobNum;

	private Double money;

	@Transient
	private List<Role> roles;

	public final Boolean isAdmin(){
		if(isAdmin == 0){
			return  true;
		}else {
			return false;
		}
	}
}

package com.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.access.prepost.PreInvocationAuthorizationAdviceVoter;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "mall_role")
@Entity
public class Role {

	@Id // 主键
	@GeneratedValue(strategy = GenerationType.IDENTITY) // 自增长策略
	@Column(name = "role_id")
	private int roleId;

	private String authority;
	private String character;

	@Column(name = "user_id")
	private int userId;
}

package com.demo.model

import java.util.*
import javax.persistence.*

@Entity
@Table(name = "mall_file")
class File {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 自增长策略
    // 自增长策略
    var id = 0L

    @Column(name = "job_id")
    var jobId = 0

    @Column(name = "user_id")
    var userId: Long = 0L

    var title = ""

    var ext = ""

    var path = ""

    var size: Int = 0

    var mime = ""

    var width: Int = 0

    var height: Int = 0

    var image = false

    @Column(name = "created_at")
    var createdAt = Date()

    @Column(name = "updated_at")
    var updatedAt = Date()

    @Column(name = "context_path")
    var contextPath = ""
}

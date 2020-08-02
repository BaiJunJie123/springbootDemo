package com.demo.util

/***
 * 分页类
 */
class Page<T> {

    constructor(recordCount: Int, curentPageIndex: Int) {
        this.recordCount = recordCount
        this.curentPageIndex = curentPageIndex
    }

    // 当前页
    var curentPageIndex = 1
        set(value) {
            field = value
            prePageIndex = value - 1
            nextPageIndex = value + 1
            firstPage = value == 1
            lastPage = value == pageCount
            limitStart = prePageIndex * countPerpage
        }

    // sql 开始位置
    var limitStart = 0

    // 每页条数
    val countPerpage = 10

    //总页
    var pageCount = 0

    // 总条数
    var recordCount = 0
        set(value) {
            pageCount = if (value % countPerpage == 0) {
                value / countPerpage
            } else {
                (value / countPerpage) + 1
            }
            field = value
        }

    // 上一页序号
    var prePageIndex = 0

    // 下一页序号
    var nextPageIndex = 0

    // 是否是第一页
    var firstPage = false

    // 是否是最后一页
    var lastPage = false

    // 数据集合
    var list: List<T>? = null

}

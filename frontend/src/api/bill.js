import request from '@/utils/request'

/**
 * 账单相关 API
 */

/**
 * 获取账单列表
 * @param {Object} params - 请求参数
 * @param {number} params.userId - 用户 ID
 * @param {string} [params.date] - 日期（可选）
 */
export function getBillList(params) {
  return request({
    url: '/bill/list',
    method: 'get',
    params
  })
}

/**
 * 获取账单详情
 * @param {number} id - 账单 ID
 */
export function getBillById(id) {
  return request({
    url: `/bill/${id}`,
    method: 'get'
  })
}

/**
 * 添加账单
 * @param {Object} data - 账单数据
 */
export function addBill(data) {
  return request({
    url: '/bill/add',
    method: 'post',
    data
  })
}

/**
 * 更新账单
 * @param {Object} data - 账单数据
 */
export function updateBill(data) {
  return request({
    url: '/bill/update',
    method: 'put',
    data
  })
}

/**
 * 删除账单
 * @param {number} id - 账单 ID
 */
export function deleteBill(id) {
  return request({
    url: `/bill/delete/${id}`,
    method: 'delete'
  })
}

/**
 * 按类型获取账单
 * @param {Object} params - 请求参数
 * @param {number} params.userId - 用户 ID
 * @param {number} params.type - 类型（1-收入，2-支出）
 * @param {string} params.start - 开始日期
 * @param {string} params.end - 结束日期
 */
export function getBillsByType(params) {
  return request({
    url: '/bill/by-type',
    method: 'get',
    params
  })
}

/**
 * 批量删除账单（软删除）
 * @param {Object} data - 请求数据
 * @param {number[]} data.ids - ID 列表
 */
export function batchDeleteBill(data) {
  return request({
    url: '/bill/batch/delete',
    method: 'put',
    data
  })
}

/**
 * 搜索账单
 * @param {Object} params - 请求参数
 * @param {number} params.userId - 用户 ID
 * @param {string} params.keyword - 搜索关键字
 */
export function searchBill(params) {
  return request({
    url: '/bill/search',
    method: 'get',
    params
  })
}

/**
 * 月度统计
 * @param {Object} params - 请求参数
 * @param {number} params.userId - 用户 ID
 * @param {number} params.year - 年份
 * @param {number} params.month - 月份
 */
export function statMonth(params) {
  return request({
    url: '/bill/stat/month',
    method: 'get',
    params
  })
}

/**
 * 分类统计
 * @param {Object} params - 请求参数
 * @param {number} params.userId - 用户 ID
 * @param {number} params.type - 类型（1-收入，2-支出）
 * @param {string} params.start - 开始日期
 * @param {string} params.end - 结束日期
 */
export function statCategory(params) {
  return request({
    url: '/bill/stat/category',
    method: 'get',
    params
  })
}

/**
 * 趋势统计
 * @param {Object} params - 请求参数
 * @param {number} params.userId - 用户 ID
 * @param {number} params.days - 天数
 */
export function statTrend(params) {
  return request({
    url: '/bill/stat/trend',
    method: 'get',
    params
  })
}

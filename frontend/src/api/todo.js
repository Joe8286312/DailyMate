import request from '@/utils/request'

/**
 * 待办事项相关 API
 */

/**
 * 获取待办事项列表
 * @param {Object} params - 请求参数
 * @param {number} params.userId - 用户 ID
 * @param {string} [params.date] - 日期（可选）
 */
export function getTodoList(params) {
  return request({
    url: '/todo/list',
    method: 'get',
    params
  })
}

/**
 * 获取待办事项详情
 * @param {number} id - 待办事项 ID
 */
export function getTodoById(id) {
  return request({
    url: `/todo/${id}`,
    method: 'get'
  })
}

/**
 * 添加待办事项
 * @param {Object} data - 待办事项数据
 */
export function addTodo(data) {
  return request({
    url: '/todo/add',
    method: 'post',
    data
  })
}

/**
 * 更新待办事项
 * @param {Object} data - 待办事项数据
 */
export function updateTodo(data) {
  return request({
    url: '/todo/update',
    method: 'put',
    data
  })
}

/**
 * 删除待办事项
 * @param {number} id - 待办事项 ID
 */
export function deleteTodo(id) {
  return request({
    url: `/todo/delete/${id}`,
    method: 'delete'
  })
}

/**
 * 按状态获取待办事项
 * @param {Object} params - 请求参数
 * @param {number} params.userId - 用户 ID
 * @param {number} params.status - 状态（0-未完成，1-已完成）
 */
export function getTodosByStatus(params) {
  return request({
    url: '/todo/by-status',
    method: 'get',
    params
  })
}

/**
 * 批量更新状态
 * @param {Object} data - 请求数据
 * @param {number[]} data.ids - ID 列表
 * @param {number} data.status - 新状态
 */
export function batchUpdateStatus(data) {
  return request({
    url: '/todo/batch/finish',
    method: 'put',
    data
  })
}

/**
 * 批量删除（软删除）
 * @param {Object} data - 请求数据
 * @param {number[]} data.ids - ID 列表
 */
export function batchDelete(data) {
  return request({
    url: '/todo/batch/delete',
    method: 'put',
    data
  })
}

/**
 * 搜索待办事项
 * @param {Object} params - 请求参数
 * @param {number} params.userId - 用户 ID
 * @param {string} params.keyword - 搜索关键字
 */
export function searchTodo(params) {
  return request({
    url: '/todo/search',
    method: 'get',
    params
  })
}

/**
 * 获取未完成数量
 * @param {Object} params - 请求参数
 * @param {number} params.userId - 用户 ID
 */
export function getUnfinishedCount(params) {
  return request({
    url: '/todo/unfinished-count',
    method: 'get',
    params
  })
}

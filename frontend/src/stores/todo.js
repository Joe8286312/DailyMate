import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import {
  getTodoList as getTodoListApi,
  addTodo as addTodoApi,
  updateTodo as updateTodoApi,
  deleteTodo as deleteTodoApi,
  batchUpdateStatus,
  batchDelete,
  searchTodo
} from '@/api/todo'

export const useTodoStore = defineStore('todo', () => {
  // 状态
  const todos = ref([])
  const loading = ref(false)
  const filter = ref('all') // all, done, undone
  const searchKeyword = ref('')

  // 计算属性
  const filteredTodos = computed(() => {
    let result = [...todos.value]
    
    // 按筛选条件过滤
    if (filter.value === 'done') {
      result = result.filter(todo => todo.status === 1)
    } else if (filter.value === 'undone') {
      result = result.filter(todo => todo.status === 0)
    }
    
    // 按关键字搜索
    if (searchKeyword.value) {
      const keyword = searchKeyword.value.toLowerCase()
      result = result.filter(todo =>
        todo.title?.toLowerCase().includes(keyword) ||
        todo.content?.toLowerCase().includes(keyword)
      )
    }
    
    return result
  })

  const totalCount = computed(() => todos.value.length)
  const doneCount = computed(() => todos.value.filter(t => t.status === 1).length)
  const undoneCount = computed(() => todos.value.filter(t => t.status === 0).length)

  // 方法
  /**
   * 获取待办事项列表
   */
  async function fetchTodoList (params) {
    loading.value = true
    try {
      const res = await getTodoListApi(params)
      todos.value = res.data || res || []
    } catch (error) {
      console.error('获取待办事项失败:', error)
      todos.value = []
    } finally {
      loading.value = false
    }
  }

  /**
   * 添加待办事项
   */
  async function addTodo (data) {
    const res = await addTodoApi(data)
    const newTodo = res.data || res
    if (newTodo) {
      todos.value.unshift(newTodo)
    }
    return newTodo
  }

  /**
   * 更新待办事项
   */
  async function updateTodo (data) {
    const res = await updateTodoApi(data)
    const updatedTodo = res.data || res
    if (updatedTodo) {
      const index = todos.value.findIndex(t => t.id === updatedTodo.id)
      if (index !== -1) {
        todos.value[index] = updatedTodo
      }
    }
    return updatedTodo
  }

  /**
   * 删除待办事项
   */
  async function deleteTodo (id) {
    await deleteTodoApi(id)
    todos.value = todos.value.filter(t => t.id !== id)
  }

  /**
   * 批量更新状态
   */
  async function batchUpdateStatusAction (ids, status) {
    await batchUpdateStatus({ ids, status })
    todos.value = todos.value.map(todo =>
      ids.includes(todo.id) ? { ...todo, status } : todo
    )
  }

  /**
   * 批量删除
   */
  async function batchDeleteAction (ids) {
    await batchDelete({ ids })
    todos.value = todos.value.filter(t => !ids.includes(t.id))
  }

  /**
   * 搜索待办事项
   */
  async function searchTodoAction (params) {
    loading.value = true
    try {
      const res = await searchTodo(params)
      todos.value = res.data || res || []
    } catch (error) {
      console.error('搜索待办事项失败:', error)
    } finally {
      loading.value = false
    }
  }

  /**
   * 设置筛选条件
   */
  function setFilter (value) {
    filter.value = value
  }

  /**
   * 设置搜索关键字
   */
  function setSearchKeyword (value) {
    searchKeyword.value = value
  }

  /**
   * 清空搜索
   */
  function clearSearch () {
    searchKeyword.value = ''
  }

  return {
    // 状态
    todos,
    loading,
    filter,
    searchKeyword,
    // 计算属性
    filteredTodos,
    totalCount,
    doneCount,
    undoneCount,
    // 方法
    fetchTodoList,
    addTodo,
    updateTodo,
    deleteTodo,
    batchUpdateStatusAction,
    batchDeleteAction,
    searchTodoAction,
    setFilter,
    setSearchKeyword,
    clearSearch
  }
})

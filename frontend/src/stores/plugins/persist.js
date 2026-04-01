/**
 * Pinia 持久化插件
 * 自动将 store 状态持久化到 localStorage
 */

export const piniaPersistPlugin = ({ store }) => {
  // 从 localStorage 恢复状态
  const storedState = localStorage.getItem(`pinia_${store.$id}`)
  if (storedState) {
    try {
      const parsedState = JSON.parse(storedState)
      // 只恢复 $state 中的属性
      Object.keys(parsedState).forEach(key => {
        if (key in store.$state) {
          store.$state[key] = parsedState[key]
        }
      })
    } catch (error) {
      console.error(`恢复 store "${store.$id}" 状态失败:`, error)
      localStorage.removeItem(`pinia_${store.$id}`)
    }
  }

  // 订阅状态变化并保存到 localStorage
  store.$subscribe((mutation, state) => {
    try {
      // 只保存 $state 中的属性
      const stateToSave = {}
      Object.keys(state).forEach(key => {
        if (key in store.$state) {
          stateToSave[key] = state[key]
        }
      })
      localStorage.setItem(`pinia_${store.$id}`, JSON.stringify(stateToSave))
    } catch (error) {
      console.error(`保存 store "${store.$id}" 状态失败:`, error)
    }
  })
}

/**
 * 浏览器系统通知工具
 * 使用 Web Notifications API 实现 Windows 系统通知
 */

// 通知权限状态
let permission = 'default'

/**
 * 请求通知权限
 */
export async function requestNotificationPermission() {
  if (!('Notification' in window)) {
    console.warn('当前浏览器不支持系统通知')
    return false
  }

  if (Notification.permission === 'granted') {
    permission = 'granted'
    return true
  }

  if (Notification.permission !== 'denied') {
    const result = await Notification.requestPermission()
    permission = result
    return result === 'granted'
  }

  return false
}

/**
 * 检查通知权限
 */
export function checkNotificationPermission() {
  if (!('Notification' in window)) {
    return 'unsupported'
  }
  permission = Notification.permission
  return permission
}

/**
 * 发送系统通知
 * @param {Object} options - 通知选项
 * @param {string} options.title - 通知标题
 * @param {string} options.body - 通知内容
 * @param {string} [options.icon] - 通知图标
 * @param {string} [options.badge] - 角标图标
 * @param {Function} [options.onClick] - 点击回调
 * @param {Function} [options.onClose] - 关闭回调
 */
export function sendSystemNotification({
  title,
  body,
  icon = '/logo.png',
  badge = '/logo.png',
  onClick,
  onClose
}) {
  console.log('[Notification] 尝试发送通知:', { title, body })

  if (!('Notification' in window)) {
    console.warn('[Notification] 当前浏览器不支持系统通知')
    return null
  }

  console.log('[Notification] 当前权限状态:', Notification.permission)

  if (Notification.permission !== 'granted') {
    console.warn('[Notification] 未获得通知权限，尝试请求权限...')
    Notification.requestPermission().then(permission => {
      console.log('[Notification] 权限请求结果:', permission)
      if (permission === 'granted') {
        sendSystemNotification({ title, body, icon, badge, onClick, onClose })
      }
    })
    return null
  }

  try {
    const notification = new Notification(title, {
      body,
      icon,
      badge,
      lang: 'zh-CN',
      requireInteraction: false,
      tag: 'dailymate-notification',
      renotify: false
    })

    console.log('[Notification] 通知创建成功:', notification)

    // 点击通知
    notification.onclick = (event) => {
      console.log('[Notification] 通知被点击')
      event.preventDefault()
      window.focus()
      onClick?.(notification)
      notification.close()
    }

    // 关闭通知
    notification.onclose = () => {
      console.log('[Notification] 通知被关闭')
      onClose?.(notification)
    }

    // 显示通知
    notification.onshow = () => {
      console.log('[Notification] 通知已显示')
    }

    // 错误处理
    notification.onerror = (error) => {
      console.error('[Notification] 通知错误:', error)
    }

    // 自动关闭（5 秒后）
    setTimeout(() => {
      notification.close()
    }, 5000)

    return notification
  } catch (error) {
    console.error('[Notification] 创建通知失败:', error)
    return null
  }
}

/**
 * 发送待办提醒通知
 */
export function sendTodoNotification(todo) {
  return sendSystemNotification({
    title: '📋 待办事项提醒',
    body: `${todo.title} - ${todo.endTime ? '截止时间：' + new Date(todo.endTime).toLocaleString() : ''}`,
    icon: '/logo.png',
    onClick: () => {
      window.location.href = '/todos'
    }
  })
}

/**
 * 发送账单提醒通知
 */
export function sendBillNotification(bill) {
  const typeText = bill.type === 1 ? '收入' : '支出'
  return sendSystemNotification({
    title: '💰 账单提醒',
    body: `${typeText}：¥${bill.amount} - ${bill.category}${bill.remark ? '（' + bill.remark + '）' : ''}`,
    icon: '/logo.png',
    onClick: () => {
      window.location.href = '/bills'
    }
  })
}

/**
 * 发送通用消息通知
 */
export function sendMessageNotification(message) {
  const typeIcons = {
    1: '📢',
    2: '📋',
    3: '💰',
    4: '💬'
  }
  const typeIcon = typeIcons[message.type] || '📬'

  return sendSystemNotification({
    title: `${typeIcon} ${message.title}`,
    body: message.content,
    icon: '/logo.png',
    onClick: () => {
      // 打开消息抽屉
      const event = new CustomEvent('open-message-drawer')
      window.dispatchEvent(event)
    }
  })
}

/**
 * 播放通知声音
 */
export function playNotificationSound() {
  try {
    const audio = new Audio('data:audio/wav;base64,UklGRl9vT19XQVZFZm10IBAAAAABAAEAQB8AAEAfAAABAAgAZGF0YU')
    audio.volume = 0.3
    audio.play().catch(() => {})
  } catch (e) {
    // 忽略播放错误
  }
}

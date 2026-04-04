/**
 * 图片压缩工具
 * 使用 Canvas API 进行图片压缩，无需额外依赖
 */

/**
 * 压缩图片
 * @param {File|Blob} file - 图片文件
 * @param {Object} options - 压缩选项
 * @param {number} options.maxWidth - 最大宽度（默认 1920px）
 * @param {number} options.maxHeight - 最大高度（默认 1080px）
 * @param {number} options.quality - 压缩质量（0.1-1.0，默认 0.8）
 * @param {string} options.outputType - 输出类型（image/jpeg, image/png, image/webp）
 * @returns {Promise<string>} - 返回 Base64 格式
 */
export function compressImage (file, options = {}) {
  const {
    maxWidth = 1920,
    maxHeight = 1080,
    quality = 0.8,
    outputType = 'image/jpeg'
  } = options

  return new Promise((resolve, reject) => {
    const reader = new FileReader()
    
    reader.onload = (e) => {
      const img = new Image()
      
      img.onload = () => {
        // 计算缩放比例
        let width = img.width
        let height = img.height
        
        if (width > maxWidth || height > maxHeight) {
          const ratio = Math.min(maxWidth / width, maxHeight / height)
          width = Math.floor(width * ratio)
          height = Math.floor(height * ratio)
        }

        // 创建 Canvas
        const canvas = document.createElement('canvas')
        canvas.width = width
        canvas.height = height
        
        const ctx = canvas.getContext('2d')
        
        // 对于 PNG 图片，如果转换为 JPEG，需要白色背景
        if (outputType === 'image/jpeg') {
          ctx.fillStyle = '#FFFFFF'
          ctx.fillRect(0, 0, width, height)
        }
        
        ctx.drawImage(img, 0, 0, width, height)
        
        // 压缩并输出
        try {
          const base64 = canvas.toDataURL(outputType, quality)
          resolve(base64)
        } catch (error) {
          reject(new Error('图片压缩失败：' + error.message))
        }
      }
      
      img.onerror = () => {
        reject(new Error('图片加载失败'))
      }
      
      img.src = e.target.result
    }
    
    reader.onerror = () => {
      reject(new Error('文件读取失败'))
    }
    
    reader.readAsDataURL(file)
  })
}

/**
 * 获取文件大小（格式化）
 * @param {number} bytes - 字节数
 * @returns {string} - 格式化后的大小
 */
export function formatFileSize (bytes) {
  if (bytes === 0) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return (bytes / Math.pow(k, i)).toFixed(2) + ' ' + sizes[i]
}

/**
 * 获取图片信息
 * @param {string} base64 - Base64 字符串
 * @returns {Promise<Object>} - 返回图片信息 { width, height, size, type }
 */
export function getImageInfo (base64) {
  return new Promise((resolve, reject) => {
    const img = new Image()
    
    img.onload = () => {
      // 计算 Base64 大小
      const size = Math.round((base64.length * 3) / 4)
      resolve({
        width: img.width,
        height: img.height,
        size: size,
        type: base64.split(';')[0].split(':')[1]
      })
    }
    
    img.onerror = () => {
      reject(new Error('图片信息获取失败'))
    }
    
    img.src = base64
  })
}

/**
 * 智能压缩图片
 * 根据目标大小自动调整压缩质量
 * @param {File} file - 图片文件
 * @param {Object} options - 选项
 * @param {number} options.targetSize - 目标大小（KB，默认 500KB）
 * @param {number} options.maxWidth - 最大宽度
 * @param {number} options.maxHeight - 最大高度
 * @returns {Promise<string>} - Base64 字符串
 */
export async function smartCompressImage (file, options = {}) {
  const {
    targetSize = 500, // KB
    maxWidth = 1920,
    maxHeight = 1080
  } = options

  // 如果文件小于目标大小，直接返回
  if (file.size / 1024 <= targetSize) {
    return compressImage(file, { maxWidth, maxHeight, quality: 0.9 })
  }

  // 逐步降低质量直到满足大小要求
  const qualities = [0.9, 0.8, 0.7, 0.6, 0.5, 0.4, 0.3]
  
  for (const quality of qualities) {
    const compressed = await compressImage(file, {
      maxWidth,
      maxHeight,
      quality,
      outputType: 'image/jpeg'
    })
    
    // 计算压缩后的大小
    const sizeInKB = (compressed.length * 3) / 4 / 1024
    
    if (sizeInKB <= targetSize) {
      return compressed
    }
  }

  // 如果所有质量都不满足，返回最低质量的结果
  return compressImage(file, {
    maxWidth,
    maxHeight,
    quality: 0.3,
    outputType: 'image/jpeg'
  })
}

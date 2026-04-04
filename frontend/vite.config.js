import { defineConfig, loadEnv } from 'vite'
import vue from '@vitejs/plugin-vue'
import { fileURLToPath, URL } from 'node:url'

/**
 * Vite 配置文件
 * @param {Object} param0 - 配置参数
 * @param {string} param0.mode - 当前模式
 * @param {string} param0.command - 命令
 */
export default defineConfig(({ mode, command }) => {
  // 加载环境变量
  const env = loadEnv(mode, process.cwd(), '')
  const isBuild = command === 'build'

  return {
    plugins: [vue()],

    resolve: {
      alias: {
        '@': fileURLToPath(new URL('./src', import.meta.url))
      }
    },

    css: {
      preprocessorOptions: {
        scss: {
          additionalData: '@use "@/styles/variables.scss" as *;'
        }
      }
    },

    server: {
      port: 3000,
      host: '0.0.0.0',
      open: false,
      proxy: {
        '/api': {
          target: 'http://localhost:8080',
          changeOrigin: true,
          secure: false,
          ws: true
        }
      },
      hmr: {
        overlay: true
      }
    },

    build: {
      // 生产环境构建配置
      outDir: 'dist',
      assetsDir: 'static',
      sourcemap: !isBuild || env.VITE_SOURCE_MAP === 'true',
      minify: 'terser',
      chunkSizeWarningLimit: 2000,
      // 指定根目录
      rollupOptions: {
        input: {
          main: fileURLToPath(new URL('./index.html', import.meta.url))
        },
        output: {
          manualChunks: {
            'vendor-vue': ['vue', 'vue-router', 'pinia'],
            'vendor-element': ['element-plus', '@element-plus/icons-vue'],
            'vendor-utils': ['axios', 'dayjs']
          },
          // 静态资源分类
          chunkFileNames: 'static/js/[name]-[hash].js',
          entryFileNames: 'static/js/[name]-[hash].js',
          assetFileNames: 'static/[ext]/[name]-[hash].[ext]'
        }
      },

      // Terser 压缩配置
      terserOptions: {
        compress: {
          // 生产环境移除 console 和 debugger
          drop_console: env.VITE_DROP_CONSOLE === 'true',
          drop_debugger: true,
          pure_funcs: env.VITE_DROP_CONSOLE === 'true' ? ['console.log'] : []
        },
        format: {
          comments: false
        }
      }
    },

    // 优化依赖预构建
    optimizeDeps: {
      include: [
        'vue',
        'vue-router',
        'pinia',
        'axios',
        'element-plus',
        '@element-plus/icons-vue',
        'dayjs',
        'vue-i18n'
      ],
      exclude: []
    },

    // 日志级别
    logLevel: 'info',

    // 清除控制台
    clearScreen: true
  }
})

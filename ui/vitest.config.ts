import { defineConfig } from 'vitest/config'
import vue from '@vitejs/plugin-vue'
import vuetify from 'vite-plugin-vuetify'
import { fileURLToPath } from 'url'

export default defineConfig({
  plugins: [
    vue(),
    vuetify({ autoImport: true }) as any
  ],
  test: {
    environment: 'jsdom',
    globals: true,
    setupFiles: ['./src/test/setup.ts'],
    deps: {
      optimizer: {
        web: {
          include: ['vuetify']
        }
      }
    },
    css: {
      modules: {
        classNameStrategy: 'stable'
      }
    }
  },
  resolve: {
    alias: {
      'vuetify/styles': 'vuetify/styles/main.sass',
      '\\.(css|less|scss|sass)$': fileURLToPath(new URL('./src/test/mocks/styleMock.js', import.meta.url)),
      'vuetify/lib': 'vuetify/lib',
      'vuetify/lib/components': 'vuetify/lib/components'
    }
  },
  optimizeDeps: {
    include: ['vuetify']
  },
  build: {
    rollupOptions: {
      external: ['vuetify/lib/components/*/V*.css']
    }
  },
  ssr: {
    noExternal: ['vuetify']
  }
}) 
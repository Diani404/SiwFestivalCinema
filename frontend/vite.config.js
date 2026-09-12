import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// npm run build -> il bundle finisce in src/main/resources/static/react e viene servito da Spring Boot
export default defineConfig({
  plugins: [react()],
  base: '/react/',
  build: {
    outDir: '../src/main/resources/static/react',
    emptyOutDir: true,
    rollupOptions: {
      output: {
        entryFileNames: 'program.js',
        chunkFileNames: 'program-[name].js',
        assetFileNames: 'program.[ext]'
      }
    }
  },
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
})

import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// Build output goes straight into Spring Boot's static resources,
// so the FE is served by the BE at http://localhost:8080/
export default defineConfig({
  plugins: [react()],
  build: {
    outDir: '../src/main/resources/static',
    emptyOutDir: true,
  },
})

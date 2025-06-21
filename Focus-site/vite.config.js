import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

export default defineConfig({
  plugins: [react()],
  optimizeDeps: {
    exclude: ['lucide-react'],
  },
  build: {
    // Enable code splitting for better performance
    rollupOptions: {
      output: {
        manualChunks: {
          vendor: ['react', 'react-dom'],
          motion: ['framer-motion'],
          icons: ['lucide-react'],
          animations: ['aos'],
        },
      },
    },
    // Enable compression
    cssCodeSplit: true,
    // Optimize for modern browsers
    target: 'es2015',
    // Enable source maps for debugging
    sourcemap: false,
  },
  server: {
    // Enable hot reload
    hmr: true,
    // Optimize dev server
    host: true,
  },
  // CSS optimization
  css: {
    devSourcemap: true,
  },
  // Enable preview optimizations
  preview: {
    port: 3000,
    strictPort: true,
  },
});
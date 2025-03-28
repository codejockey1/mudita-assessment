import { createVuetify } from 'vuetify'
import * as components from 'vuetify/components'
import * as directives from 'vuetify/directives'
import { vi } from 'vitest'

// Mock CSS imports
vi.mock('*.css', () => ({}))
vi.mock('*.scss', () => ({}))
vi.mock('*.sass', () => ({}))
vi.mock('*.less', () => ({}))
vi.mock('vuetify/styles', () => ({}))
vi.mock('@mdi/font/css/materialdesignicons.css', () => ({}))

// Mock Vuetify component CSS files
vi.mock('vuetify/lib/components/*/V*.css', () => ({}))

// Mock ResizeObserver
class ResizeObserver {
  observe() {}
  unobserve() {}
  disconnect() {}
}

// Assign mock to window
window.ResizeObserver = ResizeObserver

// Mock IntersectionObserver
const mockIntersectionObserver = vi.fn();
mockIntersectionObserver.mockImplementation(() => ({
  root: null,
  rootMargin: '',
  thresholds: [],
  disconnect: () => null,
  observe: () => null,
  takeRecords: () => [],
  unobserve: () => null,
}));

window.IntersectionObserver = mockIntersectionObserver;

// Create Vuetify instance
export const vuetify = createVuetify({
  components,
  directives,
  theme: {
    defaultTheme: 'light'
  }
})

// Mock CSS
const style = document.createElement('style')
document.head.appendChild(style)

// Mock adoptedStyleSheets
Object.defineProperty(document, 'adoptedStyleSheets', {
  value: [],
  writable: true
}) 
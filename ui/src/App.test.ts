import { describe, it, expect, vi } from 'vitest'
import { mount } from '@vue/test-utils'
import { vuetify } from './test/setup'
import App from './App.vue'

describe('App', () => {
  const mountComponent = () => {
    return mount(App, {
      global: {
        plugins: [vuetify]
      }
    })
  }

  it('renders the title correctly', () => {
    const wrapper = mountComponent()
    const title = wrapper.find('[data-test="app-title"]')
    expect(title.exists()).toBe(true)
    expect(title.text()).toBe('Task Analyzer')
  })

  it('shows server error when API call fails', async () => {
    // Mock fetch to simulate API failure
    global.fetch = vi.fn().mockRejectedValue(new Error('Network error'))

    const wrapper = mountComponent()
    const taskForm = wrapper.findComponent({ name: 'TaskForm' })
    
    // Trigger the submit event
    await taskForm.vm.$emit('submit', ['Task 1'])
    
    // Wait for all promises to resolve and component to update
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 0))
    
    // Check for error message
    const errorMessage = wrapper.find('[data-test="error-message"]')
    expect(errorMessage.exists()).toBe(true)
    expect(errorMessage.text()).toBe('An error occurred submitting your request. Please try again later.')
  })

  it('shows server error when API returns non-200 status', async () => {
    // Mock fetch to simulate API error response
    global.fetch = vi.fn().mockResolvedValue({
      ok: false,
      json: () => Promise.resolve({})
    })

    const wrapper = mountComponent()
    const taskForm = wrapper.findComponent({ name: 'TaskForm' })
    
    // Trigger the submit event
    await taskForm.vm.$emit('submit', ['Task 1'])
    
    // Wait for all promises to resolve and component to update
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 0))
    
    // Check for error message
    const errorMessage = wrapper.find('[data-test="error-message"]')
    expect(errorMessage.exists()).toBe(true)
    expect(errorMessage.text()).toBe('An error occurred submitting your request. Please try again later.')
  })
})
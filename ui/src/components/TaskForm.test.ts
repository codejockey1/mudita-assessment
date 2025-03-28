import { describe, it, expect } from 'vitest'
import { mount } from '@vue/test-utils'
import { vuetify } from '../test/setup'
import TaskForm from './TaskForm.vue'

describe('TaskForm', () => {
  const mountComponent = () => {
    return mount(TaskForm, {
      global: {
        plugins: [vuetify]
      }
    })
  }

  it('renders correctly', () => {
    const wrapper = mountComponent()
    const textarea = wrapper.find('textarea')
    const submitButton = wrapper.find('button')
    expect(textarea.exists()).toBe(true)
    expect(submitButton.exists()).toBe(true)
    expect(submitButton.text()).toBe('Plan')
  })

  it('validates empty input', async () => {
    const wrapper = mountComponent()
    const form = wrapper.find('form')
    await form.trigger('submit')
    
    const errorMessage = wrapper.find('.v-messages__message')
    expect(errorMessage.exists()).toBe(true)
    expect(errorMessage.text()).toBe('Please enter at least one task')
  })

  it('validates non-comma-separated input', async () => {
    const wrapper = mountComponent()
    const textarea = wrapper.find('textarea')
    await textarea.setValue('Task 1 Task 2')
    const form = wrapper.find('form')
    await form.trigger('submit')
    
    const errorMessage = wrapper.find('.v-messages__message')
    expect(errorMessage.exists()).toBe(true)
    expect(errorMessage.text()).toBe('Invalid format: tasks must be comma-separated')
  })

  it('emits submit event with parsed tasks when valid input is provided', async () => {
    const wrapper = mountComponent()
    const textarea = wrapper.find('textarea')
    await textarea.setValue('Task 1, Task 2, Task 3')
    const form = wrapper.find('form')
    await form.trigger('submit')
    
    const emitted = wrapper.emitted('submit')
    expect(emitted).toBeTruthy()
    expect(emitted![0]).toEqual([['Task 1', 'Task 2', 'Task 3']])
  })

  it('handles loading state correctly', async () => {
    const wrapper = mountComponent()
    const component = wrapper.vm as any
    
    // Test setting loading state
    component.setLoading(true)
    await wrapper.vm.$nextTick()
    const submitButton = wrapper.find('button')
    expect(submitButton.attributes('disabled')).toBe('')
    
    // Test removing loading state
    component.setLoading(false)
    await wrapper.vm.$nextTick()
    expect(submitButton.attributes('disabled')).toBeFalsy()
  })

  it('trims whitespace from tasks', async () => {
    const wrapper = mountComponent()
    const textarea = wrapper.find('textarea')
    await textarea.setValue('  Task 1  ,  Task 2  ,  Task 3  ')
    const form = wrapper.find('form')
    await form.trigger('submit')
    
    const emitted = wrapper.emitted('submit')
    expect(emitted).toBeTruthy()
    expect(emitted![0]).toEqual([['Task 1', 'Task 2', 'Task 3']])
  })

  it('filters out empty tasks', async () => {
    const wrapper = mountComponent()
    const textarea = wrapper.find('textarea')
    await textarea.setValue('Task 1,,,Task 2,,Task 3')
    const form = wrapper.find('form')
    await form.trigger('submit')
    
    const emitted = wrapper.emitted('submit')
    expect(emitted).toBeTruthy()
    expect(emitted![0]).toEqual([['Task 1', 'Task 2', 'Task 3']])
  })
})
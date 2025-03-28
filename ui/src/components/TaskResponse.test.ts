import { describe, it, expect } from 'vitest'
import { mount } from '@vue/test-utils'
import { vuetify } from '../test/setup'
import TaskResponse from './TaskResponse.vue'

interface Response {
  analysis: string
  imageUrl: string
}

describe('TaskResponse', () => {
  const mountComponent = (props = {}) => {
    return mount(TaskResponse, {
      props: {
        response: null,
        ...props
      },
      global: {
        plugins: [vuetify]
      }
    })
  }

  it('does not render when response is null', () => {
    const wrapper = mountComponent({ response: null })
    expect(wrapper.find('[data-test="response-card"]').exists()).toBe(false)
  })

  it('renders correctly when response is provided', async () => {
    const mockResponse: Response = {
      analysis: 'Test analysis\nwith multiple lines',
      imageUrl: 'https://example.com/image.jpg'
    }

    const wrapper = mountComponent({ response: mockResponse })
    await wrapper.vm.$nextTick()

    // Check card exists
    const card = wrapper.find('[data-test="response-card"]')
    expect(card.exists()).toBe(true)
    expect(card.classes()).toContain('mt-4')
    expect(card.classes()).toContain('pa-4')
    
    // Check image
    const img = wrapper.find('[data-test="response-image"]')
    expect(img.exists()).toBe(true)
    expect(img.classes()).toContain('mb-4')
    expect(img.attributes('style')).toContain('width: 100%')
    expect(img.attributes('style')).toContain('height: auto')
    expect(img.attributes('style')).toContain('max-height: 600px')
    
    // Check analysis text
    const analysis = wrapper.find('[data-test="response-analysis"]')
    expect(analysis.exists()).toBe(true)
    expect(analysis.text()).toBe(mockResponse.analysis)
    expect(analysis.classes()).toContain('text-body-1')
    expect(analysis.classes()).toContain('white-space-pre-line')
  })
})
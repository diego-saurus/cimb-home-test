import { mount } from '@vue/test-utils'
import { describe, expect, it } from 'vitest'

import EmptyState from '@/components/atoms/EmptyState.vue'

const UIconStub = {
	template: '<svg data-testid="icon" :data-icon="name" />',
	props: ['name'],
}

function mountEmpty(props: Record<string, unknown> = {}) {
	return mount(EmptyState, {
		props,
		global: {
			stubs: { UIcon: UIconStub },
		},
	})
}

describe('EmptyState', () => {
	it('renders the default title, description, and icon when no props are provided', () => {
		const wrapper = mountEmpty()

		expect(wrapper.text()).toContain('Tidak ada data yang cocok')
		expect(wrapper.text()).toContain('Coba sesuaikan pencarian anda atau hapus filter.')
		expect(wrapper.find('[data-testid="icon"]').exists()).toBe(true)
	})

	it('renders a custom title and description', () => {
		const wrapper = mountEmpty({
			title: 'Belum ada panggilan',
			description: 'Panggilan masuk akan muncul di sini.',
		})

		expect(wrapper.text()).toContain('Belum ada panggilan')
		expect(wrapper.text()).toContain('Panggilan masuk akan muncul di sini.')
		expect(wrapper.text()).not.toContain('Tidak ada data yang cocok')
	})

	it('forwards the icon prop to the rendered icon', () => {
		const wrapper = mountEmpty({ icon: 'i-ph:phone-x' })

		const icon = wrapper.find('[data-testid="icon"]')
		expect(icon.exists()).toBe(true)
		expect(icon.attributes('data-icon')).toBe('i-ph:phone-x')
	})
})

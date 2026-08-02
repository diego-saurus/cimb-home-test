import { mount } from '@vue/test-utils'
import { CalendarDate } from '@internationalized/date'
import { describe, expect, it } from 'vitest'
import { defineComponent, h, inject } from 'vue'

import { useCallMonitoringFilter } from '@/composables/useCallMonitoringFilter'
import {
	CallMonitoringFilterKey,
	type CallMonitoringFilterContext,
} from '@/providers/callMonitoringFilter'

function mountFilterHost() {
	let api: CallMonitoringFilterContext | undefined
	let injected: CallMonitoringFilterContext | undefined
	const Child = defineComponent({
		setup() {
			injected = inject(CallMonitoringFilterKey)
			return () => h('span', { 'data-testid': 'child' })
		},
	})
	const Host = defineComponent({
		setup() {
			api = useCallMonitoringFilter()
			return () => h('div', [h(Child)])
		},
	})
	const wrapper = mount(Host)
	return { wrapper, get api() { return api! }, get injected() { return injected } }
}

describe('useCallMonitoringFilter', () => {
	it('starts with no dates and sentiment "all"', () => {
		const { api } = mountFilterHost()
		expect(api.startDate.value).toBeNull()
		expect(api.endDate.value).toBeNull()
		expect(api.sentiment.value).toBe('all')
	})

	it('allows mutating the filter refs', () => {
		const { api } = mountFilterHost()
		const start = new CalendarDate(2025, 1, 10)
		const end = new CalendarDate(2025, 1, 20)

		api.startDate.value = start
		api.endDate.value = end
		api.sentiment.value = 'BELOW_70'

		expect(api.startDate.value?.year).toBe(2025)
		expect(api.startDate.value?.month).toBe(1)
		expect(api.startDate.value?.day).toBe(10)
		expect(api.endDate.value?.year).toBe(2025)
		expect(api.endDate.value?.month).toBe(1)
		expect(api.endDate.value?.day).toBe(20)
		expect(api.sentiment.value).toBe('BELOW_70')
	})

	it('reset() restores all filter values to defaults', () => {
		const { api } = mountFilterHost()
		api.startDate.value = new CalendarDate(2025, 5, 1)
		api.endDate.value = new CalendarDate(2025, 5, 31)
		api.sentiment.value = 'AT_OR_ABOVE_70'

		api.reset()

		expect(api.startDate.value).toBeNull()
		expect(api.endDate.value).toBeNull()
		expect(api.sentiment.value).toBe('all')
	})

	it('provides the same context that descendant components inject', () => {
		const { api, injected } = mountFilterHost()
		expect(injected).toBeDefined()
		expect(injected?.startDate).toBe(api.startDate)
		expect(injected?.endDate).toBe(api.endDate)
		expect(injected?.sentiment).toBe(api.sentiment)
		expect(injected?.reset).toBe(api.reset)
	})

	it('reset() invoked from a descendant clears the shared filter state', () => {
		const { api, injected } = mountFilterHost()
		api.sentiment.value = 'BELOW_70'

		injected!.reset()

		expect(api.sentiment.value).toBe('all')
	})
})

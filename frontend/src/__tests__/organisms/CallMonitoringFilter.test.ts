import { mount, flushPromises } from '@vue/test-utils'
import { createMemoryHistory, createRouter } from 'vue-router'
import { afterEach, describe, expect, it, vi } from 'vitest'
import { defineComponent, h, provide, ref } from 'vue'

import CallMonitoringFilter from '@/components/organisms/CallMonitoringFilter.vue'
import {
	CallMonitoringFilterKey,
	type CallMonitoringFilterContext,
} from '@/providers/callMonitoringFilter'

vi.mock('@vueuse/core', async () => {
	const actual = await vi.importActual<typeof import('@vueuse/core')>('@vueuse/core')
	const { ref } = await import('vue')
	return {
		...actual,
		useBreakpoints: () => ({
			sm: ref(false),
			md: ref(false),
			lg: ref(false),
			xl: ref(false),
			'2xl': ref(false),
		}),
	}
})

const DateRangeInputStub = {
	template: '<div data-testid="date-range" />',
	props: ['modelValue', 'startDate', 'endDate', 'triggerPlaceholder'],
}

const SentimentFilterStub = {
	template: '<div data-testid="sentiment-filter" />',
	props: ['modelValue'],
}

const UButtonStub = {
	template: '<button data-testid="reset-btn" :data-label="label" :disabled="disabled ? \'\' : null" @click="$emit(\'click\')" />',
	props: ['label', 'icon', 'variant', 'color', 'size', 'class', 'disabled'],
	emits: ['click'],
}

interface MountResult {
	wrapper: ReturnType<typeof mount>
	ctx: CallMonitoringFilterContext
	router: ReturnType<typeof createRouter>
	findBtn: () => ReturnType<ReturnType<typeof mount>['find']>
}

async function mountHost(initialQuery: Record<string, string> = {}): Promise<MountResult> {
	const startDate = ref(null)
	const endDate = ref(null)
	const sentiment = ref<'all' | 'BELOW_70' | 'AT_OR_ABOVE_70'>('all')
	const reset = () => {
		startDate.value = null
		endDate.value = null
		sentiment.value = 'all'
	}
	const ctx: CallMonitoringFilterContext = { startDate, endDate, sentiment, reset }

	const router = createRouter({
		history: createMemoryHistory(),
		routes: [{ path: '/', component: { template: '<div />' } }],
	})

	const Host = defineComponent({
		setup() {
			provide(CallMonitoringFilterKey, ctx)
			return () => h(CallMonitoringFilter)
		},
	})

	const wrapper = mount(Host, {
		global: {
			plugins: [router],
			stubs: {
				DateRangeInput: DateRangeInputStub,
				SentimentFilter: SentimentFilterStub,
				UButton: UButtonStub,
			},
		},
	})

	if (Object.keys(initialQuery).length > 0) {
		await router.replace({ path: '/', query: initialQuery })
		await router.isReady()
	}
	await flushPromises()

	return {
		wrapper,
		ctx,
		router,
		findBtn: () => wrapper.find('[data-testid="reset-btn"]'),
	}
}

afterEach(() => {
	vi.restoreAllMocks()
})

describe('CallMonitoringFilter', () => {
	it('renders the date range and sentiment controls', async () => {
		const { wrapper } = await mountHost()
		expect(wrapper.find('[data-testid="date-range"]').exists()).toBe(true)
		expect(wrapper.find('[data-testid="sentiment-filter"]').exists()).toBe(true)
	})

	it('hides the reset button when nothing is set and the URL search is empty', async () => {
		const { findBtn } = await mountHost()
		expect(findBtn().exists()).toBe(false)
	})

	it('enables the reset button when sentiment differs from "all"', async () => {
		const { ctx, findBtn } = await mountHost()
		ctx.sentiment.value = 'BELOW_70'
		await flushPromises()

		const btn = findBtn()
		expect(btn.exists()).toBe(true)
		expect(btn.attributes('disabled')).toBeUndefined()
	})

	it('enables the reset button when the URL search query is non-empty', async () => {
		const { findBtn } = await mountHost({ s: 'agus' })
		const btn = findBtn()
		expect(btn.exists()).toBe(true)
		expect(btn.attributes('disabled')).toBeUndefined()
	})

	it('keeps the reset button enabled (with non-empty label) on small viewports once a filter is set', async () => {
		const { ctx, findBtn } = await mountHost()
		ctx.sentiment.value = 'BELOW_70'
		await flushPromises()

		const btn = findBtn()
		expect(btn.attributes('data-label')).toBe('Reset Filter')
	})

	it('clicking reset clears the filter refs and the URL search', async () => {
		const { ctx, router, findBtn } = await mountHost({ s: 'agus' })
		ctx.sentiment.value = 'BELOW_70'
		await flushPromises()

		expect(router.currentRoute.value.query.s).toBe('agus')
		expect(ctx.sentiment.value).toBe('BELOW_70')

		await findBtn().trigger('click')
		await flushPromises()

		expect(ctx.sentiment.value).toBe('all')
		expect(router.currentRoute.value.query.s).toBeUndefined()
	})
})

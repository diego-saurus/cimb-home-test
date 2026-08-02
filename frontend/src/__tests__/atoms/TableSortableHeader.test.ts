import { mount, flushPromises } from '@vue/test-utils'
import type { Column } from '@tanstack/vue-table'
import { describe, expect, it } from 'vitest'
import { defineComponent, h, provide, ref } from 'vue'

import TableSortableHeader from '@/components/atoms/TableSortableHeader.vue'
import { SortingKey, type SortingContext } from '@/providers/sorting'

interface SortingRefs {
	sortBy: { value: string }
	sortDirection: { value: 'asc' | 'desc' }
	toggleSortDirection: () => void
}

const UButtonStub = {
	template: '<button data-testid="sort-btn" :data-icon="trailingIcon" :data-label="label" :disabled="disabled" @click="$emit(\'click\')" />',
	props: ['label', 'trailingIcon', 'ui', 'variant', 'color', 'disabled'],
	emits: ['click'],
}

function mountHeader(columnId: string, label = 'Caller Name', initialDirection: 'asc' | 'desc' = 'desc') {
	const sortBy = ref('callTimestamp')
	const sortDirection = ref<'asc' | 'desc'>(initialDirection)
	const toggleSortDirection = () => {
		sortDirection.value = sortDirection.value === 'asc' ? 'desc' : 'asc'
	}

	const fakeColumn = { id: columnId } as unknown as Column<unknown, unknown>

	const ctx: SortingContext = { sortBy, sortDirection, toggleSortDirection }

	const Host = defineComponent({
		setup() {
			provide(SortingKey, ctx)
			return () => h(TableSortableHeader, { column: fakeColumn, label })
		},
	})

	const wrapper = mount(Host, {
		global: { stubs: { UButton: UButtonStub } },
	})

	return {
		wrapper,
		state: { sortBy, sortDirection, toggleSortDirection } satisfies SortingRefs,
		btn: () => wrapper.find('[data-testid="sort-btn"]'),
	}
}

describe('TableSortableHeader', () => {
	it('shows the descending icon when this column is the active sort and direction is desc', () => {
		const { btn } = mountHeader('callTimestamp', 'Timestamp')
		expect(btn().attributes('data-icon')).toBe('i-ph:sort-ascending')
	})

	it('shows the ascending icon when this column is the active sort and direction is asc', async () => {
		const { state, btn } = mountHeader('callTimestamp', 'Timestamp', 'asc')
		await flushPromises()
		expect(state.sortDirection.value).toBe('asc')
		expect(btn().attributes('data-icon')).toBe('i-ph:sort-descending')
	})

	it('shows the ascending placeholder icon when this column is not the active sort', () => {
		const { btn } = mountHeader('callerName', 'Caller')
		expect(btn().attributes('data-icon')).toBe('i-ph:sort-descending')
	})

	it('clicking the active column toggles direction and keeps the sort key', async () => {
		const { state, btn } = mountHeader('callTimestamp', 'Timestamp')
		expect(state.sortDirection.value).toBe('desc')

		await btn().trigger('click')

		expect(state.sortBy.value).toBe('callTimestamp')
		expect(state.sortDirection.value).toBe('asc')
	})

	it('clicking a different column switches the sort key without toggling direction', async () => {
		const { state, btn } = mountHeader('callerName', 'Caller', 'asc')

		await btn().trigger('click')

		expect(state.sortBy.value).toBe('callerName')
		expect(state.sortDirection.value).toBe('asc')
	})

	it('renders the provided label on the button', () => {
		const { btn } = mountHeader('callTimestamp', 'Caller Name')
		expect(btn().attributes('data-label')).toBe('Caller Name')
	})
})

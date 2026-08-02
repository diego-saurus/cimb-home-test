import { mount } from '@vue/test-utils'
import { describe, expect, it } from 'vitest'
import { defineComponent, h, inject } from 'vue'

import { useSortingState } from '@/composables/useSortingState'
import { SortingKey, type SortingContext } from '@/providers/sorting'

function mountSortingHost() {
  let api: SortingContext | undefined
  let injected: SortingContext | undefined
  const Child = defineComponent({
    setup() {
      injected = inject(SortingKey)
      return () => h('span', { 'data-testid': 'child' })
    },
  })
  const Host = defineComponent({
    setup() {
      api = useSortingState()
      return () => h('div', [h(Child)])
    },
  })
  const wrapper = mount(Host)
  return {
    wrapper,
    get api() {
      return api!
    },
    get injected() {
      return injected
    },
  }
}

describe('useSortingState', () => {
  it('initializes sortBy with "callTimestamp" and direction "desc"', () => {
    const { api } = mountSortingHost()
    expect(api.sortBy.value).toBe('callTimestamp')
    expect(api.sortDirection.value).toBe('desc')
  })

  it('toggles sort direction from desc to asc and back', () => {
    const { api } = mountSortingHost()

    api.toggleSortDirection()
    expect(api.sortDirection.value).toBe('asc')

    api.toggleSortDirection()
    expect(api.sortDirection.value).toBe('desc')
  })

  it('allows consumers to change the sort key', () => {
    const { api } = mountSortingHost()
    api.sortBy.value = 'callerName'
    expect(api.sortBy.value).toBe('callerName')
  })

  it('provides the same context that descendant components inject', () => {
    const { api, injected } = mountSortingHost()
    expect(injected).toBeDefined()
    expect(injected?.sortBy).toBe(api.sortBy)
    expect(injected?.sortDirection).toBe(api.sortDirection)
    expect(injected?.toggleSortDirection).toBe(api.toggleSortDirection)
  })

  it('injected consumer toggle flips the same shared state', () => {
    const { api, injected } = mountSortingHost()
    injected!.toggleSortDirection()
    expect(api.sortDirection.value).toBe('asc')
  })
})

import { useRouteQuery } from '@vueuse/router'
import { computed, provide, ref, watch } from 'vue'

import type { CallRecord } from '@/types/call'

import { SortingKey } from '@/providers/sorting'

export function useTableState() {
  const search = useRouteQuery<string>('s', '')
  const page = useRouteQuery<number>('page', 1, { transform: Number })

  const sortBy = ref<keyof CallRecord>('callTimestamp')
  const sortDirection = ref<'asc' | 'desc'>('desc')

  const pageIndex = computed(() => page.value - 1)

  const toggleSortDirection = () => (sortDirection.value = sortDirection.value === 'asc' ? 'desc' : 'asc')

  watch([search, sortBy, sortDirection], () => {
    page.value = 1
  })

  provide(SortingKey, {
    sortBy,
    sortDirection,
    toggleSortDirection,
  })

  return {
    search,
    sortBy,
    sortDirection,
    page,
    pageIndex,
    toggleSortDirection,
  }
}

export type TableState = ReturnType<typeof useTableState>

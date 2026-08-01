import { provide, ref } from 'vue'

import type { CallRecord } from '@/types/call'

import { SortingKey } from '@/providers/sorting'

export function useSortingState() {
  const sortBy = ref<keyof CallRecord>('callTimestamp')
  const sortDirection = ref<'asc' | 'desc'>('desc')

  const toggleSortDirection = () => (sortDirection.value = sortDirection.value === 'asc' ? 'desc' : 'asc')

  provide(SortingKey, {
    sortBy,
    sortDirection,
    toggleSortDirection,
  })

  return {
    sortBy,
    sortDirection,
    toggleSortDirection,
  }
}

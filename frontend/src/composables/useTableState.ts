import { refDebounced } from '@vueuse/core'
import { useRouteQuery } from '@vueuse/router'
import { computed, watch } from 'vue'

import { useSortingState } from './useSortingState'

export function useTableState() {
  const search = useRouteQuery<string>('s', '')
  const debouncedSearch = refDebounced(search)

  const page = useRouteQuery<number>('page', 1, { transform: Number })
  const pageIndex = computed(() => page.value - 1)

  const { sortBy, sortDirection, toggleSortDirection } = useSortingState()

  watch([search, sortBy, sortDirection], () => {
    page.value = 1
  })

  return {
    search,
    sortBy,
    sortDirection,
    debouncedSearch,
    page,
    pageIndex,
    toggleSortDirection,
  }
}

export type TableState = ReturnType<typeof useTableState>


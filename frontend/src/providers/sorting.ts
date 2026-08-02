import { inject, type InjectionKey, type Ref } from 'vue'

export interface SortingContext {
  sortBy: Ref<string>
  sortDirection: Ref<'asc' | 'desc'>
  toggleSortDirection: () => void
}

export const SortingKey: InjectionKey<SortingContext> = Symbol()

export function useSortingContext() {
  const context = inject(SortingKey)

  if (!context) throw new Error('SortingKey not found')

  return context
}

<script lang="ts">
import type { Column, RowData } from '@tanstack/vue-table'

import { computed } from 'vue'

import { useSortingContext } from '@/providers/sorting'
</script>

<template>
  <UButton
    :label
    :trailingIcon="isDesc ? 'i-lucide-arrow-down-wide-narrow' : 'i-lucide-arrow-up-narrow-wide'"
    :ui="{
      base: 'group px-1 py-1',
      trailingIcon: ['group-hover:opacity-100  transition-opacity', isCurrentlySort ? 'opacity-100' : 'opacity-0'],
    }"
    variant="ghost"
    color="neutral"
    @click="handleSort"
  />
</template>

<script setup lang="ts" generic="TData extends RowData, TValue = unknown">
const props = defineProps<{ column: Column<TData, TValue>; label: string }>()
const { sortBy, sortDirection, toggleSortDirection } = useSortingContext()

const isCurrentlySort = computed(() => props.column.id === sortBy.value)
const isDesc = computed(() => isCurrentlySort.value && sortDirection.value === 'desc')

const handleSort = () => {
  if (isCurrentlySort.value) toggleSortDirection()

  sortBy.value = props.column.id
}
</script>

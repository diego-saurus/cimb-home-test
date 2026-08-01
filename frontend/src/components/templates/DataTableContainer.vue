<script lang="ts">
import type { TableColumn } from '@nuxt/ui'

import { computed } from 'vue'

import type { TableState } from '@/composables/useTableState'
import type { Page } from '@/types/page'

import TableToolbar from '@/components/organisms/TableToolbar.vue'

interface Props<T> {
  tableState: TableState
  columns: TableColumn<T>[]
  title: string
  description: string

  data?: Page<T>
  isLoading?: boolean
}
</script>

<template>
  <UContainer class="py-8">
    <header class="mb-6">
      <h1 class="text-2xl font-semibold text-highlighted">{{ title }}</h1>
      <p class="mt-1 text-sm text-muted">
        {{ description }}
      </p>
    </header>

    <section class="py-4 px-4 ring ring-default rounded-t-lg">
      <TableToolbar>
        <slot name="toolbar" />
      </TableToolbar>
    </section>

    <section class="relative">
      <div class="overflow-hidden ring ring-default bg-default rounded-b-lg">
        <UTable
          class="flex-1"
          :columns
          :pagination="{
            pageIndex,
            pageSize: 5,
          }"
          :loading="isLoading"
          :data="data?.content"
          :ui="{
            th: 'border-l first:border-l-0 last:border-r-0 border-r border-default',
            td: 'px-6',
          }"
        >
          <template #empty>
            <EmptyState
              title="No matching recordings"
              description="Try adjusting your search or clearing filters."
              icon="i-lucide-phone-off"
            />
          </template>
        </UTable>

        <div class="flex items-center flex-col sm:flex-row justify-between gap-4 px-4 py-3.5 border-t border-muted">
          <div class="flex-1 text-sm">
            Showing <span class="text-secondary font-medium"> {{ rangeStart }}-{{ rangeEnd }} </span> of
            <span class="text-secondary font-medium">
              {{ data?.totalElements ?? 0 }}
            </span>
            entries
          </div>

          <UPagination
            v-model:page="page"
            :items-per-page="data?.size"
            :total="data?.totalElements"
            :edges="true"
            :sibling-count="1"
            :ui="{
              first: 'hidden',
              last: 'hidden',
            }"
          >
          </UPagination>
        </div>
      </div>
    </section>
  </UContainer>
</template>

<script setup lang="ts" generic="T">
const props = defineProps<Props<T>>()
const { pageIndex, page } = props.tableState

const rangeStart = computed(() => (props.data?.pageable.offset ?? 0) + 1)
const rangeEnd = computed(() => (props.data?.pageable.offset ?? 0) + (props.data?.numberOfElements ?? 0))
</script>

<script lang="ts">
import type { TableColumn } from '@nuxt/ui'

import { useQuery } from '@pinia/colada'
import { computed, h, resolveComponent, toValue, watch } from 'vue'

import type { CallRecord } from '@/types/call'
import type { Page } from '@/types/page'

import TableSortableHeader from '@/components/atoms/TableSortableHeader.vue'
import CallMonitoringFilter from '@/components/organisms/CallMonitoringFilter.vue'
import DataTableContainer from '@/components/templates/DataTableContainer.vue'
import { useCallMonitoringFilter } from '@/composables/useCallMonitoringFilter'
import { useTableState } from '@/composables/useTableState'
import { dateLong } from '@/lib/formatter/date'
import { percentage } from '@/lib/formatter/number'
import { satellite } from '@/lib/satellite'
import { unwrap } from '@/lib/utils'
</script>

<template>
  <UMain>
    <DataTableContainer
      title="Call Monitoring"
      description="View and process customer call monitoring data to identify calls that require attention."
      :data
      :columns
      :table-state
      :isLoading="isPlaceholderData || isPending"
    >
      <template v-slot:toolbar>
        <CallMonitoringFilter />
      </template>
    </DataTableContainer>
  </UMain>
</template>

<script setup lang="ts">
const UBadge = resolveComponent('UBadge')

const tableState = useTableState()
const { startDate, endDate, sentiment } = useCallMonitoringFilter()
const { debouncedSearch, sortBy, sortDirection, pageIndex, page } = tableState

const params = computed(() => {
  const sentimentValue = sentiment.value === 'all' ? undefined : sentiment.value

  return {
    search: toValue(debouncedSearch),
    sortBy: toValue(sortBy),
    direction: toValue(sortDirection),
    page: toValue(pageIndex),
    startDate: toValue(startDate),
    endDate: toValue(endDate),
    sentiment: sentimentValue,
  }
})

watch([startDate, endDate], () => {
  page.value = 1
})

const { data, isPlaceholderData, isPending } = useQuery({
  key: () => ['call-monitorings', params.value],
  query: ({ signal }) =>
    satellite.get<Page<CallRecord>>('/call-monitoring', { signal, params: params.value }).then(unwrap),
  placeholderData: (prev) => prev,
})

const columns = computed<TableColumn<CallRecord>[]>(() => [
  {
    id: 'rowNumber',
    header: 'No.',
    enableSorting: false,
    cell: ({ row }) => pageIndex.value * (data?.value?.size ?? 5) + row.index + 1,
  },
  {
    accessorKey: 'callId',
    header: ({ column }) => h(TableSortableHeader<CallRecord>, { column, label: 'Call ID' }),
    meta: { class: { td: 'font-mono text-xs' } },
  },
  {
    accessorKey: 'callTimestamp',
    header: ({ column }) => h(TableSortableHeader<CallRecord>, { column, label: 'Call Timestamp' }),
    cell: ({ getValue }) => dateLong.format(new Date(getValue<string>())),
  },
  {
    accessorKey: 'csAgentName',
    header: ({ column }) => h(TableSortableHeader<CallRecord>, { column, label: 'CS Name' }),
  },
  {
    accessorKey: 'customerName',
    header: ({ column }) => h(TableSortableHeader<CallRecord>, { column, label: 'Nama Nasabah' }),
  },
  {
    accessorKey: 'sentimentScore',
    header: ({ column }) => h(TableSortableHeader<CallRecord>, { column, label: 'Sentiment Score Nasabah' }),
    cell: ({ row }) => {
      const score = row.getValue('sentimentScore') as number
      const color = score < 70 ? 'error' : score < 85 ? 'warning' : 'success'
      return h(UBadge, { color, variant: 'subtle', label: percentage(score / 100) })
    },
  },
])
</script>

<script setup lang="ts">
import type { TableColumn } from '@nuxt/ui'

import { getPaginationRowModel } from '@tanstack/vue-table'
import { h, resolveComponent } from 'vue'
import { ref } from 'vue'

import type { CallRecord } from '@/types/call'

import { percentage } from '@/lib/formatter/number'

const UBadge = resolveComponent('UBadge')

const pagination = ref({ pageIndex: 0, pageSize: 5 })

const columns: TableColumn<CallRecord>[] = [
  {
    id: 'rowNumber',
    header: 'No.',
    enableSorting: false,
    cell: ({ row }) => row.index + 1,
  },
  {
    accessorKey: 'callId',
    header: 'Call ID',
    meta: { class: { td: 'font-mono text-xs' } },
  },
  {
    accessorKey: 'callTimestamp',
    header: 'Call Timestamp',
  },
  {
    accessorKey: 'csName',
    header: 'CS Name',
  },
  {
    accessorKey: 'customerName',
    header: 'Nama Nasabah',
  },
  {
    accessorKey: 'sentimentScore',
    header: 'Sentiment Score Nasabah',
    cell: ({ row }) => {
      const score = row.getValue('sentimentScore') as number
      const color = score < 70 ? 'error' : score < 85 ? 'warning' : 'success'
      return h(UBadge, { color, variant: 'subtle', label: percentage(score / 100) })
    },
  },
]
</script>

<template>
  <div class="overflow-hidden rounded-lg ring ring-default bg-default">
    <UTable
      :data="[]"
      :columns="columns"
      v-model:pagination="pagination"
      :pagination-options="{ getPaginationRowModel: getPaginationRowModel() }"
      class="flex-1"
    >
      <template #empty>
        <EmptyState
          title="No matching recordings"
          description="Try adjusting your search or clearing filters."
          icon="i-lucide-phone-off"
        />
      </template>
    </UTable>
  </div>
</template>

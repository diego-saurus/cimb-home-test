<script setup lang="ts">
import MonitoringFilters from '@/components/organisms/MonitoringFilters.vue'
import MonitoringTable from '@/components/organisms/MonitoringTable.vue'
</script>

<template>
  <UMain>
    <UContainer class="py-8">
      <header class="mb-6">
        <h1 class="text-2xl font-semibold text-highlighted">Call Monitoring</h1>
        <p class="mt-1 text-sm text-muted">
          View and process customer call monitoring data to identify calls that require attention.
        </p>
      </header>

      <section class="mb-4">
        <MonitoringFilters />
      </section>

      <section class="relative">
        <MonitoringTable />
      </section>
    </UContainer>
  </UMain>
</template>

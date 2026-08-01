<template>
  <div class="flex flex-col gap-3 lg:flex-row lg:flex-wrap lg:items-center lg:justify-between">
    <SearchInput v-model="search" />

    <slot />
  </div>
</template>

<script setup lang="ts">
import { useRouteQuery } from '@vueuse/router'

const search = useRouteQuery('s', '')
</script>

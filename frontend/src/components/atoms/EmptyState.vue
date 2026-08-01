<template>
  <div class="flex flex-col items-center justify-center gap-3 py-16 text-center">
    <UIcon :name="icon" class="size-10 text-muted" />
    <div class="space-y-1">
      <p class="text-base font-medium text-highlighted">{{ title }}</p>
      <p class="text-sm text-muted">{{ description }}</p>
    </div>
  </div>
</template>

<script setup lang="ts">
export interface EmptyStateProps {
  title?: string
  description?: string
  icon?: string
}

withDefaults(defineProps<EmptyStateProps>(), {
  title: 'Tidak ada data yang cocok',
  description: 'Coba sesuaikan pencarian anda atau hapus filter.',
  icon: 'i-ph:phone-x',
})
</script>

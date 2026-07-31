<script setup lang="ts">
interface Props {
  placeholder?: string
}

const props = withDefaults(defineProps<Props>(), {
  placeholder: 'Search...',
})

const input = defineModel<string>()
</script>

<template>
  <UInput v-model="input" icon="i-lucide-search" class="w-full sm:max-w-sm" :placeholder>
    <template v-if="input" #trailing>
      <UButton
        color="neutral"
        variant="ghost"
        icon="i-lucide-x"
        size="xs"
        aria-label="Clear search"
        @click="input = ''"
      />
    </template>
  </UInput>
</template>

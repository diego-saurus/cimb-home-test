<script lang="ts">
import type { CalendarProps } from '@nuxt/ui'

import { getLocalTimeZone, type DateValue } from '@internationalized/date'

import { dateMedium } from '@/lib/formatter/date'

type Props = CalendarProps & { triggerPlaceholder?: string }
</script>

<template>
  <UPopover>
    <UButton color="neutral" variant="subtle" icon="i-lucide-calendar">
      {{ value ? dateMedium.format(value.toDate(getLocalTimeZone())) : triggerPlaceholder }}
    </UButton>

    <template #content>
      <UCalendar v-model="value" v-bind="$attrs" class="p-2" />
    </template>
  </UPopover>
</template>

<script setup lang="ts">
withDefaults(defineProps<Props>(), {
  triggerPlaceholder: 'Select a date',
})

const value = defineModel<DateValue>()
</script>
